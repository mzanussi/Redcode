package com.michaelzanussi.redcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The Assembler class is responsible for loading the Redcode program (aka warrior) 
 * and assembling it into individual binary instructions (<tt>Integer</tt>). Methods
 * allow consumers to retrieve an instruction or all instructions, plus a method
 * to decode an instruction into a RedcodeInstruction. The heavy lifting for encoding
 * and decoding is done by the individual Redcode instructions.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (15 April 2016) 
 */
public class Assembler {
	
	private List<Integer> program;	// the binary program image
	
	// The mnemonic table, used for looking up instruction names.
	private Map<Integer, String> mtable;
	
	/**
	 * No-arg constructor.
	 */
	public Assembler() {
		// Instantiate the program image, an array of RedcodeInstruction objects.
		program = new ArrayList<Integer>();
		
		mtable = new HashMap<Integer, String>();
		loadMnemonicTable();
	}
	
	/**
	 * Returns a Redcode instruction at a given index. The instruction is an
	 * <tt>Integer</tt> object containing the 32-bit word Redcode instruction
	 * encoded by the assembler.
	 * 
	 * @return an <tt>Integer</tt> containing the Redcode instruction. 
	 */
	public Integer getInstruction(int index) {
		return program.get(index);
	}
	
	/**
	 * Returns the Redcode instruction set. The instruction set is a series
	 * of <tt>Integer</tt> objects containing the 32-bit word Redcode instructions
	 * encoded by the assembler. This instruction set is to be loaded into
	 * the RVM memory in contiguous memory, and must fit without overlapping other
	 * warriors or without falling off the end of the RVM's memory.
	 * 
	 * @return a <tt>List</tt> containing the Redcode instruction set. 
	 */
	public List<Integer> getInstructions() {
		return program;
	}
	
	/**
	 * Returns the RedcodeInstruction associated with the 32-bit word passed
	 * to the method. If not object exists, <code>null</code> is returned.<p>
	 * 
	 * In order to return the correct instruction object, we need to find out
	 * exactly which instruction we're working with, and that can be derived
	 * from the <code>op</code> and the <code>funct</code> fields. There is 
	 * a special case when <code>op</code> is 1, in which case we'll have to
	 * look at the <code>rt</code> field as well. Using these fields, we'll
	 * build a 32-bit word which will act as the key into the mnemonic table
	 * loaded when the app is launched, and retrieve the instruction name.
	 * From there, a new instruction object can be instantiated and
	 * populated.
	 * 
	 * @param instruction the 32-bit word instruction just fetched.
	 * @return the instruction object, or <code>null</code> if it doesn't
	 * exist.
	 * @throws BadInstructionException If a bad instruction is encountered.
	 */
	public RedcodeInstruction getRedcodeInstruction(Integer instruction) throws BadInstructionException {
		
		// Extract the 'op' field. We'll use this field to figure out
		// how to build the hash key.
		int ins = instruction >> 26;
		
		// If the 'op' field wasn't used in this instruction, use 'funct'.
		// If the 'op' field is equal to 1, this is the special case
		// of the 'bgez' and 'bltz' instructions which use the 'rt'
		// field in addition to 'op'. If 'op' is any value other than 1 or 
		// 0, we'll use 'op'.
		switch (ins) {
			case 0:
				// Use the 'funct' field and zero out the
				// high-order 26 bits.
				ins = instruction & 0x3f;
				break;
			case 1:
				// The 'bgez' and 'bltz' special cases.
				ins <<= 10;
				int spec = instruction >> 16;
				spec = spec & 0x1f;
				ins |= spec;
				ins <<= 16;
				break;
			default:
				// Use the 'op' field and zero out the
				// low-order 26 bits.
				ins = instruction & 0xfc000000;
				break;
		}
		
		// Get the instruction name from the mnemonic table
		String name = mtable.get(ins);

		if (name == null) {
			// no such instruction exists
			return null;
		}
		
		// Convert the instruction name to a (hopefully) valid class name.
		name = getClass(name);
		
		// Create a new instruction object.
		RedcodeInstruction rci = instantiateRedcodeInstruction(name);

		// Now decode the instruction.
		rci.decode(instruction);
		
		return rci;
		
	}
	
	/**
	 * Loads the specified Redcode assembly language file (the warrior) and
	 * processes it into 32-bit word instructions. Instructions are loaded
	 * into a <tt>List</tt>.
	 * 
	 * @param input the warrior file to process.
	 * @throws BadInstructionException If a bad instruction is encountered.
	 * @throws FileNotFoundException If the warrior file cannot be located.
	 * @throws NullPointerException If no warrior was specified.
	 * @throws ParsingExcepion If problems occur while parsing the warrior.
	 */
	public void load(File file) throws FileNotFoundException, BadInstructionException, ParsingException {
		
		// Make sure file isn't null.
		if (file == null) {
			throw new NullPointerException("File must be specified.");
		}
		
		// Open up the warrior file for processing.
		BufferedReader buffer = null;
		try {
			buffer = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File not found: " + file);
		}
		
		// Attach the input buffer to the lexer.
		Lexer lexer = new RedcodeLexer(buffer);
		
		// While there are still tokens to process...
		while (lexer.hasMoreTokens()) {
			
			// Retrieve the instruction name.
			Token token = lexer.nextToken();
			String strToken = token.getToken();
			
			// Empty file?
			if (strToken.length() == 0) {
				return;
			}
			
			// Convert the instruction name to a (hopefully) valid class name.
			strToken = getClass(strToken);
			
			// Create a new instruction object.
			RedcodeInstruction rci = instantiateRedcodeInstruction(strToken);
			
			// Encode instruction into a 32-bit word.
			Integer word = rci.encode(lexer);
			
			// Add the instruction to the array.
			program.add(word);
			
		}
		
	}
	
	public void load(String warr) throws BadInstructionException, ParsingException {
		
		// Make sure program isn't null.
		if (warr == null) {
			throw new NullPointerException("Program must be specified.");
		}
		
		// Open up the warrior file for processing.
		StringReader buffer = new StringReader(warr);
		
		// Attach the input buffer to the lexer.
		Lexer lexer = new RedcodeLexer(buffer);
		
		// While there are still tokens to process...
		while (lexer.hasMoreTokens()) {
			
			// Retrieve the instruction name.
			Token token = lexer.nextToken();
			String strToken = token.getToken();
			
			// Empty file?
			if (strToken.length() == 0) {
				return;
			}
			
			// Convert the instruction name to a (hopefully) valid class name.
			strToken = getClass(strToken);
			
			// Create a new instruction object.
			RedcodeInstruction rci = instantiateRedcodeInstruction(strToken);
			
			// Encode instruction into a 32-bit word.
			Integer word = rci.encode(lexer);
			
			// Add the instruction to the array.
			program.add(word);
			
		}
		
	}
	
	/**
	 * This method loads the mnemonic table, which is made up of the 
	 * instruction, the instruction format, the opcode, and the <code>funct</code>.
	 * or <code>rt</code> code. The resultant table is returned to the calling method.
	 * 
	 * @return the mnemonic table.
	 * @throws IllegalArgumentException If the opcode or alt fall outside the acceptable
	 * range of 0 to 63.
	 */
	private void loadMnemonicTable() {
		
		// The master mnemonic table. Must live in project root.
		File file = new File("mnemonic.tbl");
		
		// Open the mnemonic table file.
		BufferedReader buf = null;
		
		// Parse the file and load the mnemonic table.
		String input = null;
		try {
			
			buf = new BufferedReader(new FileReader(file));

			while ((input = buf.readLine()) != null) {
				
				// Tokenize the current input line.
				StringTokenizer st = new StringTokenizer(input);
				
				while (st.hasMoreTokens()) {
					
					// Get the instruction.
					String m = st.nextToken();
					m = m.toLowerCase();
					
					// Get the opcode and verify it is within the correct
					// range of 0 to 63 (2^6).
					String op = st.nextToken();
					int o = Integer.parseInt(op);
					if (o < 0 || o > 63) {
						buf.close();
						throw new IllegalArgumentException("Opcode out of range: '" + o + "'.");
					}
					
					// Get the alt (funct/rt) and verify if is within the correct
					// range of 0 to 63 (2^6).
					String funct = st.nextToken();
					int a = Integer.parseInt(funct);
					if (a < 0 || a > 63) {
						buf.close();
						throw new IllegalArgumentException("Funct/rt out of range: '" + a + "'.");
					}
					
					int key = 0;
					
					key |= o;
					
					if (o == 1) {
						key <<= 10;
						key |= (a & 0x1f);
						key <<= 16;
					} else {
						key <<= 26;
						key |= a;
					}
					
					// Add to the mnemonic table.
					mtable.put(key, m);
					
				}
				
			}
			
			buf.close();
			
		} catch (IOException e) {
			
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
			
		} 
		
	}
	
	/**
	 * Creates the fully-qualified class name so that it can be dynamically
	 * instantiated to create the instruction object. To be fully-qualified,
	 * the package name must be prepended to the instruction name.
	 * 
	 * @param name the instruction name.
	 * @return the fully-qualified class name.
	 */
	private String getClass(String name) {
		// Convert the instruction name to a (hopefully) valid class name.
		// First, convert instruction to lowercase to assure consistency.
		// Then, convert the first character to uppercase and finally prepend
		// the package name.
		name = name.toLowerCase();
		String firstChar = name.substring(0, 1).toUpperCase();
		name = firstChar + name.substring(1);
		name = this.getClass().getPackage().getName() + ".instruction." + name;
		
		return name;
	}
	
	/**
	 * Instantiates an instruction object based on the fully-qualified instruction
	 * name passed to the method.
	 * 
	 * @param instruction the fully-qualified instruction name.
	 * @return the instruction object.
	 * @throws BadInstructionException If a bad instruction is encountered.
	 */
	private RedcodeInstruction instantiateRedcodeInstruction(String instruction) throws BadInstructionException {
		// Attempt to instantiate the instruction object!
		RedcodeInstruction rci = null;
		try {
			rci = (RedcodeInstruction)Class.forName(instruction).newInstance();
		} catch (ClassNotFoundException e) {
			throw new BadInstructionException("Invalid instruction encountered. No such class exists: " + instruction);
		} catch (IllegalAccessException e) {
			throw new BadInstructionException("Invalid instruction encountered. Illegal access: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new BadInstructionException("Invalid instruction encountered. Instantiation error: " + e.getMessage());			
		}
		return rci;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer rci : program) {
			sb.append(rci + "\n");
		}
		return sb.toString();
	}

	/**
	 * a test harness
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Assembler asm = new Assembler();
			asm.load(new File("test/add.war"));
			System.out.println(asm);
			for (Integer inst : asm.getInstructions()) {
				RedcodeInstruction rci = asm.getRedcodeInstruction(inst);
				System.out.println(rci);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BadInstructionException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
		
	}
	
}
