package de.avesbot;

import de.avesbot.db.Database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import de.avesbot.runnable.ExitRunnable;
import de.avesbot.db.StatementManager;
import de.avesbot.runnable.ActiveGuildsRunnable;
import de.avesbot.runnable.LeaveGuildRunnable;
import de.avesbot.util.DatabaseKeepAlive;
import net.dv8tion.jda.api.entities.Activity;

/**
 * Bot invite Link
 * https://discord.com/api/oauth2/authorize?client_id=693247128482480178&permissions=1073743872&redirect_uri=https%3A%2F%2Favesbot.de%2Fhowto&scope=identify%20guilds%20applications.commands%20bot
 * @author nitrout
 */
public class Avesbot {
	
	private static String token;
	private static Properties properties;
	private static JDA jda;
	private static StatementManager stmntManager;
	private static DiceSimulator diceSimulator;
	private static ScheduledThreadPoolExecutor stpe;
	private static final File CONFIG = new File("aves.conf");

	/**
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		// load application properties
		properties = new Properties();
		loadProperties();
		
		token = properties.getProperty("token", "");
		int keepAliveInterval = Integer.parseInt(properties.getProperty("db_keep_alive_interval"));
		
		// initialize thread pool executor to handle all incoming discord events
		stpe = new ScheduledThreadPoolExecutor(1);
		stpe.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			
			// Setup database connection
			Database.init(properties.getProperty("db_host"), properties.getProperty("db_user"), properties.getProperty("db_pass"), properties.getProperty("db_name"));
			stmntManager = new StatementManager();
			
			// initialize dice simulator
			diceSimulator = new DiceSimulator();
			
			// Setup discord functions ans event listeners
			JDABuilder builder = JDABuilder.createDefault(token);
			builder.addEventListeners(new MessageListener());
			builder.addEventListeners(new BroadcastListener());
			builder.addEventListeners(new JoinListener());
			builder.setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Forging destiny"));
			jda = builder.build();
			
			if(Stream.of(args).anyMatch(arg -> arg.equals("--regCmd"))) {
				HashSet<CommandData> commands = new HashSet<>();
				CommandBook.getInstance().getRegisteredCommands().forEach((Class c) -> {
					try {
						CommandData command = (CommandData)c.getField("COMMAND").get(null);
						if(command != null) {
							commands.add(command);
		}
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
						System.err.println(ex.getMessage());
					}
				});
				commands.forEach(command -> {
					jda.upsertCommand(command).queue(
						cmd -> {
							System.out.println("Command "+cmd.getName()+" registered");
						}, err -> {
							System.err.println(err.getMessage());
						});
				});
			}
			
			// execute a database keep alive request at a fixed rate to keep db connection alive
			stpe.scheduleAtFixedRate(new DatabaseKeepAlive(), 0, keepAliveInterval, TimeUnit.HOURS);
			stpe.schedule(() -> {
				getJda().getGuilds().forEach(guild -> {
					stmntManager.insertGuild(guild);
				});
			}, 1000, TimeUnit.MILLISECONDS);
			
			// prompt for shell inputs as long as thread pool is not shut down and is not orderes to exit
			String input;
			String[] messageParts = {};
			String[] pars = {};
			String order = "";
			do {
				input = br.readLine();
				
				if(input != null) {
					messageParts = input.split(" ");
					pars = Arrays.copyOfRange(messageParts, 1, messageParts.length);
					order = messageParts.length > 0 ? messageParts[0] : "";
					
					switch(order) {
						case "active" : stpe.submit(new ActiveGuildsRunnable()); break;
						case "exit" : stpe.submit(new ExitRunnable()); break;
						case "leave" : stpe.submit(new LeaveGuildRunnable(pars)); break;
					}
				}
				
			} while(!stpe.isShutdown() && !order.equals("exit"));
			
			// close DB connection after shutdown
			Database.close();
		}
		catch(LoginException | SQLException | IOException le) {
			System.err.println(le.getMessage());
		}
		finally {
			// ensure to close api connection and thread pool executor
			if(getJda() != null)
				getJda().shutdown();
			stpe.shutdown();
			
			try(FileOutputStream fos = new FileOutputStream(CONFIG)) {
				properties.store(fos, "");
			} catch (IOException ex) {
				Logger.getLogger(Avesbot.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * Load properties from config file aves.conf or set up a default config file.
	 */
	private static void loadProperties() {
		
		if(CONFIG.isFile() && CONFIG.canRead()) {
			try(FileInputStream fis = new FileInputStream(CONFIG)) {
				properties.load(fis);
			}
			catch(IOException ioe) {
				System.err.println(ioe.getMessage());
			}
		}
		properties.computeIfAbsent("control_user", (obj) -> "000000000000000000");
		properties.computeIfAbsent("db_host", (obj) -> "localhost");
		properties.computeIfAbsent("db_user", (obj) -> "root");
		properties.computeIfAbsent("db_pass", (obj) -> "root");
		properties.computeIfAbsent("db_name", (obj) -> "aves");
		properties.computeIfAbsent("db_keep_alive_interval", (obj) -> "6");
		properties.computeIfAbsent("max_dice", (obj) -> "70");
		properties.computeIfAbsent("application_id", (obj) -> "000000000000000000");
		properties.computeIfAbsent("token", (obj) -> "");
		properties.computeIfAbsent("website_host", (obj) -> "");
	}
	
	/**
	 * Get application properties.
	 * @return 
	 */
	public static Properties getProperties() {
		return properties;
	}
	
	/**
	 * Get the db statement manager.
	 * @return 
	 */
	public static StatementManager getStatementManager() {
		return stmntManager;
	}
	
	/**
	 * An Executor to handle all incoming requests.
	 * @return 
	 */
	public static ScheduledThreadPoolExecutor getThreadPoolExecutor() {
		return stpe;
	}
	
	/**
	 * The dice simulator used to roll all Dices.
	 * @return the diceSimulator
	 */
	public static DiceSimulator getDiceSimulator() {
		return diceSimulator;
	}

	/**
	 * The Discord Java API base class.
	 * @return the jda
	 */
	public static JDA getJda() {
		return jda;
	}
}