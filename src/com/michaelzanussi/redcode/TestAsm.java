package com.michaelzanussi.redcode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.michaelzanussi.redcode.rvm.ProcessGroup;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * Test driver for the loader/assembler.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class TestAsm extends JFrame {
	
	private Assembler assembler;
	
	private JButton fileButton;
	private JButton regButton;
	private JButton dumpButton;
	private JButton execButton;
	private JComboBox<Integer> instrBox;
	private JTextArea top;
	private JTextArea bot;
	private File filename = null;
	private JTextField[] registers = new JTextField[32];
	private JTextField pcTextField;
	private JTextField hiTextField;
	private JTextField loTextField;
	private JTextField field;
	
	private StringBuilder topBuffer = new StringBuilder();
	private StringBuilder botBuffer = new StringBuilder();
	
	private RVM rvm;
	private WarriorProcess wp;
	
	/**
	 * Because: It is strongly recommended that all serializable
	 * classes explicitly declare serialVersionUID values.
	 */
	private static final long serialVersionUID = -8918572274433972992L;

	public TestAsm() {
		
		// Set the window title.
		super("Loader Redcode Assembly Simulator");
				
		assembler = new Assembler();

		// Fake WarriorProcess for testing instructions that touch
		// the registers.
		rvm = new RVM(100);
		
		// The container.
		Container container = getContentPane();
		container.setLayout(new FlowLayout());

		// Top text area.
		top = new JTextArea(19, 85);
		top.setLineWrap(true);
		top.setWrapStyleWord(true);
		top.setEditable(false);
		container.add(new JScrollPane(top), BorderLayout.NORTH);
		
		// **********************************************************
		// Select the warrior.
		// **********************************************************
		fileButton = new JButton(" Open Warrior ");
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
						int choice = file.showOpenDialog(TestAsm.this);
						if (choice != JFileChooser.CANCEL_OPTION) {
							filename = file.getSelectedFile();
							fileButton.setEnabled(false);
						}
						
						if (filename == null) {
							printTop("No file selected.\n");
							return;
						}
						printTop("Loading " + filename + "\n");
						try {
							// Load the warrior.
							assembler.load(filename);
							// create a process
							ProcessGroup pg = new ProcessGroup(1, rvm);
							wp = new WarriorProcess(assembler, pg);
							// Get the instruction set.
							List<Integer> iset = assembler.getInstructions();
							printTop("Load successful. Instructions loaded: " + iset.size() + "\n");
							dumpButton.setEnabled(true);
							regButton.setEnabled(true);
							execButton.setEnabled(true);
							field.setEnabled(true);
							field.setText("");
							// Load the comboBox.
							instrBox.removeAllItems();
							for (int i = 0; i < iset.size(); i++) {
								Integer in = iset.get(i);
								instrBox.addItem(in);
							}
							instrBox.setEnabled(true);
						} catch (NullPointerException e) {
							error(e.getMessage());
						} catch (FileNotFoundException e) {
							error(e.getMessage());
						} catch (ParsingException e) {
							error(e.getMessage());
						} catch (BadInstructionException e) {
							error(e.getMessage());
						}
						
					}
				}
		);
		
		// **********************************************************
		// Dump the current warrior
		// **********************************************************
		dumpButton = new JButton(" Binary Redcode Dump ");
		dumpButton.setEnabled(false);
		dumpButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						List<Integer> iset = assembler.getInstructions();
						printTop("\nDumping warrior...\n");
						for (int i = 0; i < iset.size(); i++) {
							Integer in = iset.get(i);
							// From the word, get the instruction object.
							RedcodeInstruction rci = null;
							try {
								rci = assembler.getRedcodeInstruction(in/*.intValue()*/);								
							} catch (BadInstructionException e) {
								error(e.getMessage());								
							}
							printTop(rci.toString() + "\n");
						}
						printTop("End dump.\n");
					}
				}
		);
		
		// **********************************************************
		// ComboBox
		// **********************************************************
		instrBox = new JComboBox<Integer>();
		instrBox.setMaximumRowCount(5);
		instrBox.setEnabled(false);
		instrBox.setEditable(false);
		instrBox.addItemListener(
				new ItemListener() {
					public void itemStateChanged(ItemEvent event) {
						if (event.getStateChange() == ItemEvent.SELECTED) {
							// Get the instruction.
							int instruction = (Integer)instrBox.getSelectedItem();
							topBuffer.append("\nThe current instruction is " + instruction + ". Test instruction:\n");
							// From the instruction, get the instruction object.
							RedcodeInstruction rci = null;
							try {
								rci = assembler.getRedcodeInstruction(instruction);								
							} catch (BadInstructionException e) {
								error(e.getMessage());								
							}
							// Decode it.
							String strInstruction = rci.decode(instruction);
							topBuffer.append("1. Decode()     : " + strInstruction + "\n");
							// Treat decoded as fresh instruction to encode.
							Assembler bob = new Assembler();
							try {
								bob.load(strInstruction);
							} catch (BadInstructionException | ParsingException e) {
								e.printStackTrace();
							}
							int nInstruction = bob.getInstruction(0);
							topBuffer.append("2. Encode()     : " + nInstruction + "\n");
							// Decode it.
							String strInstruction2 = rci.decode(nInstruction);
							topBuffer.append("3. Decode()     : " + strInstruction2 + "\n");
							top.setText(topBuffer.toString());
						}
					}
				}
		);

		// **********************************************************
		// Execute the instruction.
		// **********************************************************
		execButton = new JButton(" <- Execute ");
		execButton.setEnabled(false);
		execButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						// Fetch the current instruction.
						int temp = (Integer)instrBox.getSelectedItem();
						RedcodeInstruction rci = null;
						try {
							rci = assembler.getRedcodeInstruction(temp);								
						} catch (BadInstructionException e) {
							error(e.getMessage());								
						}
						String spaz = rci.decode(temp);
						botBuffer.append("\nExecute: " + spaz + "\n");
						rci.exec(wp, rvm);
						regs();
						bot.setText(botBuffer.toString());
					}
				}
		);
		
		// **********************************************************
		// Register dump
		// **********************************************************
		regButton = new JButton(" Register Dump ");
		regButton.setEnabled(false);
		regButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						registerDump();
					}
				}
		);
		
		// **********************************************************
		// Execute manual instruction
		// **********************************************************
		JLabel jf = new JLabel("Input an instruction here, then press the Enter key: ");
		
		field = new JTextField(50);
		field.setForeground(Color.BLUE);
		field.setText("Open a warrior to enable instruction field...");
		field.setEnabled(false);
		field.addActionListener(
				new ActionListener() {
					public void actionPerformed( ActionEvent event ) {
						
						String text = event.getActionCommand();
						if (text.length() == 0)
							return;
						field.setText(null);
						Assembler bob = new Assembler();
						try {
							bob.load(text);
						} catch (BadInstructionException | ParsingException e) {
							e.printStackTrace();
						}
						int instruction = bob.getInstruction(0);
						printBot("Instruction word: " + instruction + "\n");
						// Fetch the current instruction.
						RedcodeInstruction rci = null;
						try {
							rci = assembler.getRedcodeInstruction(instruction);								
						} catch (BadInstructionException e) {
							error(e.getMessage());
						}
						String spaz = rci.decode(instruction);
						printBot("Execute: " + spaz + "\n");
						rci.exec(wp, rvm);
						regs();
						
					}
				}
		);
		
		field.requestFocus();

		container.add(new JLabel("          "));

		container.add(fileButton);
		container.add(dumpButton);
		container.add(instrBox);
		container.add(execButton);
		container.add(regButton);
		container.add(jf);
		container.add(field);

		container.add(new JLabel("          "));

		container.add(new JLabel("                                                                 "));
		
		container.add(new JLabel("HI:"));
		hiTextField = new JTextField(10);
		hiTextField.setForeground(Color.BLUE);
		hiTextField.setText("0");
		hiTextField.setEditable(false);
		container.add(hiTextField);
		
		container.add(new JLabel("LO:"));
		loTextField = new JTextField(10);
		loTextField.setForeground(Color.BLUE);
		loTextField.setText("0");
		loTextField.setEditable(false);
		container.add(loTextField);
		
		container.add(new JLabel("PC:"));
		pcTextField = new JTextField(10);
		pcTextField.setForeground(Color.BLUE);
		//pcTextField.setBackground(Color.CYAN);
		pcTextField.setText("0");
		pcTextField.setEditable(false);
		container.add(pcTextField);
		
		container.add(new JLabel("                                                                 "));
		
		for (int i = 0; i < registers.length; i++) {
			if (i == 0 || i == 8 || i == 16 || i == 24) {
				container.add(new JLabel("          "));
			}
			container.add(new JLabel(i < 9 ? "0" + (i+1) + ":" : "" + (i+1) + ":"));
			JTextField temp = new JTextField(6);
			temp.setForeground(Color.BLUE);
			temp.setText("0");
			temp.setEditable(false);
			registers[i] = temp;
			container.add(registers[i]);
			if (i == 7 || i == 15 || i == 23 || i == 31) {
				container.add(new JLabel("          "));
			}
		}
		
		// Bottom text area.
		bot = new JTextArea(19, 85);
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
	
	private void regs() {
		Integer prevPC = new Integer(pcTextField.getText());
		Integer currPC = wp.getPC();
		if (prevPC.intValue() == currPC.intValue()) {
			pcTextField.setForeground(Color.BLUE);
			pcTextField.setText(prevPC.toString());
		} else {
			pcTextField.setForeground(Color.RED);
			pcTextField.setText(currPC.toString());
		}
		
		Integer prevHI = new Integer(hiTextField.getText());
		Integer currHI = wp.getHI();
		if (prevHI.intValue() == currHI.intValue()) {
			hiTextField.setForeground(Color.BLUE);
			hiTextField.setText(prevHI.toString());
		} else {
			hiTextField.setForeground(Color.RED);
			hiTextField.setText(currHI.toString());
		}

		Integer prevLO = new Integer(loTextField.getText());
		Integer currLO = wp.getLO();
		if (prevLO.intValue() == currLO.intValue()) {
			loTextField.setForeground(Color.BLUE);
			loTextField.setText(prevLO.toString());
		} else {
			loTextField.setForeground(Color.RED);
			loTextField.setText(currLO.toString());
		}

		for (int i = 0; i < 32; i++) {
			Integer prev = new Integer(registers[i].getText());
			Integer curr = wp.getRegister(i);
			if (prev.intValue() == curr.intValue()) {
				registers[i].setForeground(Color.BLUE);
				registers[i].setText(prev.toString());
			} else {
				registers[i].setForeground(Color.RED);
				registers[i].setText(curr.toString());
			}
		}
	}

	/**
	 * Dumps the register contents to the bottom pane.
	 */
	private void registerDump() {
		printBot("Register Dump...\n");
		printBot("hi: " + wp.getHI() + "\t");
		printBot("lo: " + wp.getLO() + "\t");
		printBot("pc: " + wp.getPC() + "\n");
		
		for (int i = 0; i < 8; i++) {
			printBot("0" + i + ": " + wp.getRegister(i) + "\t");
		}
		botBuffer.append("\n");
		for (int i = 8; i < 16; i++) {
			if (i < 10) {
				printBot("0" + i + ": " + wp.getRegister(i) + "\t");				
			} else {
				printBot(i + ": " + wp.getRegister(i) + "\t");				
			}
		}
		printBot("\n");
		for (int i = 16; i < 24; i++) {
			printBot(i + ": " + wp.getRegister(i) + "\t");				
		}
		printBot("\n");
		for (int i = 24; i < 32; i++) {
			printBot(i + ": " + wp.getRegister(i) + "\t");				
		}
		printBot("\n\n");

		regs();
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
		TestAsm tl = new TestAsm();
		tl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
