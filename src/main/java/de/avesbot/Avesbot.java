package de.avesbot;

import de.avesbot.callable.CommandCallable;
import de.avesbot.db.Database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import de.avesbot.runnable.ExitRunnable;
import de.avesbot.db.StatementManager;
import de.avesbot.runnable.ActiveGuildsRunnable;
import de.avesbot.runnable.LeaveGuildRunnable;
import de.avesbot.runnable.ServerSocketRunnable;
import de.avesbot.util.DatabaseKeepAlive;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import net.dv8tion.jda.api.entities.Activity;

/**
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
		var keepAliveInterval = Integer.parseInt(properties.getProperty("db_keep_alive_interval"));
		var serverSocketAddress = getSocketAddress();
		var serverSocketPort = Integer.parseInt(properties.getProperty("socketPort", "55555"));
		
		// initialize thread pool executor to handle all incoming discord events
		stpe = new ScheduledThreadPoolExecutor(4);
		stpe.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); var socket = new ServerSocket(serverSocketPort, 4, serverSocketAddress)) {
			stpe.execute(new ServerSocketRunnable(socket));
			// Setup database connection
			Database.init(properties.getProperty("db_host"), properties.getProperty("db_user"), properties.getProperty("db_pass"), properties.getProperty("db_name"));
			stmntManager = new StatementManager();
			
			// initialize dice simulator
			diceSimulator = new DiceSimulator();
			
			// Setup discord functions ans event listeners
			jda = buildJDA();
			
			if (Arrays.asList(args).contains("--resetCommands")) {
				resetCommands();
			}
			if (Arrays.asList(args).contains("--updateCommands")) {
				updateCommands();
			}
			
			// execute a database keep alive request at a fixed rate to keep db connection alive
			stpe.scheduleAtFixedRate(new DatabaseKeepAlive(), 0, keepAliveInterval, TimeUnit.HOURS);
			stpe.schedule(() -> getJda().getGuilds().forEach(guild -> stmntManager.insertGuild(guild)), 1000, TimeUnit.MILLISECONDS);
			
			// prompt for shell inputs as long as thread pool is not shut down and is not ordered to exit
			String input;
			String[] messageParts;
			String[] pars;
			String order = "";
			do {
				input = br.readLine();
				
				if(input != null) {
					messageParts = input.split(" ");
					pars = Arrays.copyOfRange(messageParts, 1, messageParts.length);
					order = messageParts.length > 0 ? messageParts[0] : "";
					
					switch(order) {
						case "active" -> stpe.submit(new ActiveGuildsRunnable());
						case "exit" -> stpe.submit(new ExitRunnable());
						case "leave" ->
							stpe.submit(new LeaveGuildRunnable(pars));
						default ->
							System.out.println("Unknown command!");
					}
				}
				
			} while(!stpe.isShutdown() && !order.equals("exit"));
			
			// close DB connection after shutdown
			Database.close();
		} catch (SQLException | IOException ex) {
			Logger.getLogger(Avesbot.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
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
				Logger.getLogger(Avesbot.class.getName()).log(Level.SEVERE, null, ioe);
			}
		}
		properties.putIfAbsent("control_user", "000000000000000000");
		properties.putIfAbsent("db_host", "localhost");
		properties.putIfAbsent("db_user", "root");
		properties.putIfAbsent("db_pass", "root");
		properties.putIfAbsent("db_name", "aves");
		properties.putIfAbsent("db_keep_alive_interval", "6");
		properties.putIfAbsent("max_dice", "70");
		properties.putIfAbsent("application_id", "000000000000000000");
		properties.putIfAbsent("token", "");
		properties.putIfAbsent("website_host", "");
	}

	private static JDA buildJDA() {
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.addEventListeners(new MessageListener());
		builder.addEventListeners(new BroadcastListener());
		builder.addEventListeners(new JoinListener());
		builder.setActivity(Activity.customStatus("observing adventurers"));
		return builder.build();
	}
	
	/**
	 * Get application properties.
	 * @return the properties
	 */
	public static Properties getProperties() {
		return properties;
	}
	
	/**
	 * Get the db statement manager.
	 * @return the statement manager
	 */
	public static StatementManager getStatementManager() {
		return stmntManager;
	}
	
	/**
	 * An Executor to handle all incoming requests.
	 * @return the thread pool executor
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

	public static InputStream getResourceStream(String path) {
		return Avesbot.class.getResourceAsStream(path);
	}
	
	private static void resetCommands() {
		jda.retrieveCommands().queue(cmdList -> cmdList.forEach(c -> c.delete().queue(v -> System.out.println("Deleted Command " + c.getFullCommandName()))));
		CommandBook.getInstance().getAvailableSlashCommands()
				.forEach(command -> jda.upsertCommand(command).queue(
				cmd -> System.out.println("Command " + cmd.getFullCommandName() + " registered"),
				err -> System.err.println(err.getMessage())));
	}
	
	private static void updateCommands() {
		CommandBook.getInstance().getRegisteredCommands().stream()
				.map(CommandCallable::toCommandData)
				.filter(Objects::nonNull)
				.forEach(cmd -> jda.upsertCommand(cmd).queue(
				command -> System.out.println("Command " + command.getFullCommandName() + " registered"),
				err -> System.err.println(err.getMessage())));
	}
	
	private static InetAddress getSocketAddress() {
		try {
			return properties.containsKey("socket") ? InetAddress.getByName(properties.getProperty("socket", "localhost")) : InetAddress.getLoopbackAddress();
		} catch (UnknownHostException ex) {
			Logger.getLogger(Avesbot.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
}