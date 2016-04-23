package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.Cell;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>lw</tt> loads the 32-bit quantity (word) 
 * at <tt>address</tt> (register) into register <tt>rt</tt>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Lw extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Lw() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "lw";
		
		// Override defaults.
		op = 0x23;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstruction();
		
		return name + " $" + rt + ", " + immediate + "($" + rs + ")";
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Lw.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		rt = parseRegister(lexer.nextToken());
		immediate = parseImmediate(lexer.nextToken());
		rs = parseLoadStore(lexer);
		
		// Now that all the fields have been parsed, create the instruction.
		createInstruction();
		
		// Return the 32-bit instruction.
		return instruction;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#exec(com.michaelzanussi.redcode.rvm.WarriorProcess, com.michaelzanussi.redcode.rvm.RVM)
	 */
	public void exec(WarriorProcess process, RVM rvm) {
		
		// Retrieve the rs register.
		int s = process.getRegister(rs);
		
		// Calculate the source address (register) from which rt will 
		// be populated with. The source address (register) is the 
		// contents of register rs plus an offset (immediate).
		int address = s + immediate;
		
		// Load the 32-bit quantity (word) at address (rs + immediate)
		try {
			// return the cell at memory pointed to by address
			Cell cell = rvm.getMemory(address);
			// get the word stored there
			Integer word = cell.getWord();
			// Put the result into register rt.
			process.setRegister(rt, word);
		} catch (IndexOutOfBoundsException e) {
			process.kill();
		}
		
		// Increment PC.
		process.incrementPC();			
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.IFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rt + ", " + immediate + "($" + rs + ")";
	}
		
}
