package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>bgez</tt> conditionally branches the number 
 * of instructions specified by the offset if register <tt>rs</tt> is greater 
 * than or equal to 0 (i.e., relative addressing).
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Bgez extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Bgez() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "bgez";
		
		// Override defaults.
		op = 0x01;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstruction();
		
		return name + " $" + rs + ", " + immediate;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Bgez.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		rs = parseRegister(lexer.nextToken());
		rt = 1;	// necessary for encoding because bltz shares same opcode
		immediate = parseImmediate(lexer.nextToken());
		
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
		
		// Conditionally branch the number of instructions specified by the
		// immediate (i.e., offset) if register rs is greater than or
		// equal to 0.
		if (s >= 0) {
			// Get the current PC, increment it by the offset, then
			// write it back.
			int pc = process.getPC();
			pc += immediate;
			process.setPC(pc);
		} else {
			// Increment PC.
			process.incrementPC();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.IFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rs + ", " + immediate;
	}
		
}
