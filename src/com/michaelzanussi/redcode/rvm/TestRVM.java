package com.michaelzanussi.redcode.rvm;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.michaelzanussi.redcode.Assembler;
import com.michaelzanussi.redcode.BadInstructionException;
import com.michaelzanussi.redcode.RedcodeInstruction;
import com.michaelzanussi.redcode.WarFileFilter;
import com.michaelzanussi.redcode.rvm.ProcessGroup;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * Test driver for the assembler and RVM.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (19 April 2016) 
 */
public class TestRVM extends JFrame {
	
	private RVM rvm;
	
	private int players;
	private int curPlayer;
	
	private JButton fileButton;
	private JButton regButton;
	private JButton dumpButton;
	private JButton coreButton;
	private JButton stepButton;
	private JTextArea top;
	private JTextArea bot;
	
	private File filename = null;
		
	private StringBuilder topBuffer = new StringBuilder();
	private StringBuilder botBuffer = new StringBuilder();
	
	/**
	 * Because: It is strongly recommended that all serializable
	 * classes explicitly declare serialVersionUID values.
	 */
	private static final long serialVersionUID = 7944791108359313741L;

	public TestRVM() {
		
		// Set the window title.
		super("RVM & Redcode Tester");
				
		rvm = new RVM(250);
		
		players = 0;
		curPlayer = 0;
		
		// The container.
		Container container = getContentPane();
		container.setLayout(new FlowLayout());

		// Top text area.
		top = new JTextArea(25, 85);
		top.setLineWrap(true);
		top.setWrapStyleWord(true);
		top.setEditable(false);
		container.add(new JScrollPane(top), BorderLayout.NORTH);
		
		// **********************************************************
		// Select the warrior.
		// **********************************************************
		fileButton = new JButton("Open Warrior");
		fileButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						JFileChooser file = new JFileChooser();
						File curDir = new File("/Users/michael/Documents/Dev/Java/Redcode/test");
					    WarFileFilter filter = new WarFileFilter();
					    filter.addExtension("war");
					    filter.setDescription("DCoreWars Warrior Scripts");
					    file.setFileFilter(filter);	
						file.setCurrentDirectory(curDir);
						file.setFileSelectionMode(JFileChooser.FILES_ONLY);
						int choice = file.showOpenDialog(TestRVM.this);
						if (choice != JFileChooser.CANCEL_OPTION) {
							filename = file.getSelectedFile();
						}
						
						if (filename == null) {
							return;
						}
						
						// If no program loaded yet, set current player to 1.
						if (players == 0) {
							curPlayer = 0;
						}
						
						players++;
						
						printTop("Loading " + filename + " (player #" + players + ")\n");
						rvm.loadProgram(filename, players);
						
						List<ProcessGroup> pgs = rvm.getProcessGroups();
						ProcessGroup pg = pgs.get(players-1);
						List<WarriorProcess> wps = pg.getProcesses();
						WarriorProcess wp = wps.get(0);
						Assembler asm = wp.getAssembler();
						List<Integer> iset = asm.getInstructions();
						printTop("Load successful. Instructions loaded: " + iset.size() + "\n\n");
						printCore();
						
						printNextInstruction();
						printTop("\n");
						
						dumpButton.setEnabled(true);
						regButton.setEnabled(true);
						stepButton.setEnabled(true);
						
					}
				}
		);
		
		// **********************************************************
		// Dump the current warrior
		// **********************************************************
		dumpButton = new JButton("Binary Dump");
		dumpButton.setEnabled(false);
		dumpButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						ProcessGroup pg = rvm.getProcessGroups().get(curPlayer);
						WarriorProcess wp = pg.getProcesses().get(0);
						Assembler asm = wp.getAssembler();
						List<Integer> instructions = asm.getInstructions();
						printTop("\nDumping warrior...\n");
						for (Integer instruction : instructions) {
							try {
								RedcodeInstruction rci = asm.getRedcodeInstruction(instruction);
								printTop(rci + "\n");
							} catch (BadInstructionException e) {
								e.printStackTrace();
							}
						}
						printTop("End dump.\n");
					}
				}
		);
		
		// **********************************************************
		// Register dump
		// **********************************************************
		regButton = new JButton("Register Dump");
		regButton.setEnabled(false);
		regButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						registerDump();
					}
				}
		);
		
		// **********************************************************
		// core dump
		// **********************************************************
		coreButton = new JButton("Core Dump");
		coreButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						printCore();
					}
				}
		);
		
		// **********************************************************
		// Step
		// **********************************************************
		stepButton = new JButton("Step");
		stepButton.setEnabled(false);
		stepButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						step();
					}
				}
		);
		
		container.add(fileButton);
		container.add(dumpButton);
		container.add(regButton);
		container.add(coreButton);
		container.add(stepButton);
		
		// Bottom text area.
		bot = new JTextArea(25, 85);
		bot.setLineWrap(true);
		bot.setWrapStyleWord(true);
		bot.setEditable(false);
		container.add(new JScrollPane(bot), BorderLayout.SOUTH);
		
		setSize(1050, 900);
	    setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		
	} 
	
	public void error(String text) {
		JOptionPane.showMessageDialog(null, text, "Exception", JOptionPane.ERROR_MESSAGE);
	}
	
	private void step() {
		// Get the number of the Player whose process will be executed.
		int player = rvm.getNext();	
		// Retrieve the Player's process group.
		ProcessGroup pg = rvm.getProcessGroups().get(player);
		rvm.exec();
		// Return the proc #, pc, and RCI of the instruction just executed,
		// and displays that information to the interface.
		int thisProcNo = pg.getLastProcNo(); 
		printTop("EXECUTED player #" + (player + 1) + " process:"+ thisProcNo /*+ "\tPC:" + thisPC + "\tInstruction:"*/ /*+ thisRci*/ + "\n");
		// Now dump the current state of this processes registers.
		registerDump();
		// Set the current play to the number of the Player whose
		// process will be executed next time Step is run.
		curPlayer = rvm.getNext();
		printNextInstruction();
		printCore();
	}
	
	private void printNextInstruction() {
		int count = 0;
		int nextPlayer = curPlayer;
		ProcessGroup pg = rvm.getProcessGroups().get(nextPlayer);
		int next = pg.getNext();
		while (count < pg.getProcesses().size()) {
			WarriorProcess process = pg.getProcesses().get(next);
			if (process.isRunnable()) {
				int nextPC = process.getPC();
				Cell cell = rvm.getMemory(nextPC);
				Integer nextInst = cell.getInstruction();
				RedcodeInstruction nextRci = process.getInstruction(nextInst);
				printTop("Player #" + pg.getPlayer() + " READY -> pc:" + nextPC + " inst:" + nextRci + "\n");
				break;
			} else {
				count++;
				nextPlayer = (nextPlayer == pg.getProcesses().size() - 1 ? 0 : nextPlayer + 1);
			}
		}
		
		
	}
	
	private void printCore() {
		printBot("Core size: " + rvm.memsize() + "\n");
		printBot(rvm.toString() + "\n");
	}
	
	/**
	 * Dumps the register contents to the bottom pane.
	 */
	private void registerDump() {
		ProcessGroup pg = rvm.getProcessGroups().get(curPlayer);
		WarriorProcess wp = pg.getProcesses().get(0);
		printTop("hi: " + wp.getHI() + "\t");
		printTop("lo: " + wp.getLO() + "\t");
		printTop("pc: " + wp.getPC() + "\n");
		
		for (int i = 0; i < 8; i++) {
			printTop("0" + i + ": " + wp.getRegister(i) + "\t");
		}
		printTop("\n");
		for (int i = 8; i < 16; i++) {
			if (i < 10) {
				printTop("0" + i + ": " + wp.getRegister(i) + "\t");				
			} else {
				printTop(i + ": " + wp.getRegister(i) + "\t");				
			}
		}
		printTop("\n");
		for (int i = 16; i < 24; i++) {
			printTop(i + ": " + wp.getRegister(i) + "\t");				
		}
		printTop("\n");
		for (int i = 24; i < 32; i++) {
			printTop(i + ": " + wp.getRegister(i) + "\t");				
		}
		printTop("\n\n");
		
	}
	
	/**
	 * Print some text in the top pane.
	 * 
	 * @param text the text to print.
	 */
	private void printTop(String text) {
		topBuffer.append(text);
		top.setText(topBuffer.toString());		
	}
	
	/**
	 * Print some text in the bottom pane.
	 * 
	 * @param text the text to print.
	 */
	private void printBot(String text) {
		botBuffer.append(text);
		bot.setText(botBuffer.toString());		
	}
	
	public static void main(String[] args) {
		TestRVM testRVM = new TestRVM();
		testRVM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
