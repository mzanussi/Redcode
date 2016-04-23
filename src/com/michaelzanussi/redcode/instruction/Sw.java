package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.Cell;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>sw</tt> stores the word 
 * from register <tt>rt</tt> at <tt>address</tt>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Sw extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Sw() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "sw";
		
		// Override defaults.
		op = 0x2b;
		
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
			throw new NullPointerException("Sw.encode error: Encode requires a lexer.");
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
		
		// Retrieve the rs and rt registers.
		int s = process.getRegister(rs);
		int t = process.getRegister(rt);
		
		// Calculate the destination address (register) where we'll 
		// store the contents of rt. The source address (register) 
		// is the contents of register rs plus an offset (immediate).
		int address = s + immediate;
		
		// Store the 32-bit quantity (word) at register rt
		// into address (rs + immediate)
		try {
			// return the cell at memory pointed to by address
			Cell cell = rvm.getMemory(address);
			// set the word to rt.
			cell.setWord(t);
			// set process group and PID for this cell.
			cell.setProcessGroup(process.getProcessGroup());
			cell.setPID(process.getPID());
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
