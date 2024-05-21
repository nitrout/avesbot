/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package de.avesbot;

import de.avesbot.callable.CommandCallable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nitrout
 */
public class CommandBookTest {
	
	public CommandBookTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of getInstance method, of class CommandBook.
	 */
	@Test
	public void testGetInstance() {
		System.out.println("getInstance");
		CommandBook expResult = null;
		CommandBook result = CommandBook.getInstance();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getAvailableSlashCommands method, of class CommandBook.
	 */
	@Test
	public void testGetAvailableSlashCommands() {
		System.out.println("getAvailableSlashCommands");
		Set<CommandData> expResult = null;
		List<CommandData> result = CommandBook.getInstance().getAvailableSlashCommands();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getCommand method, of class CommandBook.
	 */
	@Test
	public void testGetCommand() {
		System.out.println("getCommand");
		SlashCommandInteractionEvent event = null;
		CommandBook instance = null;
		Optional<CommandCallable> expResult = null;
		Optional<CommandCallable> result = instance.getCommand(event);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getRegisteredCommands method, of class CommandBook.
	 */
	@Test
	public void testGetRegisteredCommands() {
		System.out.println("getRegisteredCommands");
		CommandBook instance = null;
		Collection<Class<? extends CommandCallable>> expResult = null;
		Collection<Class<? extends CommandCallable>> result = instance.getRegisteredCommands();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
	
}
