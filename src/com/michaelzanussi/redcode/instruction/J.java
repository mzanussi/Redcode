package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.JFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * A J-format instruction type, <tt>j</tt> unconditionally jumps to the 
 * instruction at target (i.e., relative addressing).
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class J extends JFormat {
	
	/**
	 * No-arg constructor.
	 */
	public J() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "j";
		
		// Override defaults.
		op = 0x02;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstruction();
		
		return name + " " + target;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("J.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		target = parseTarget(lexer.nextToken());
		
		// Now that all the fields have been parsed, create the instruction.
		createInstruction();
		
		// Return the 32-bit instruction.
		return instruction;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#exec(com.michaelzanussi.redcode.rvm.WarriorProcess, com.michaelzanussi.redcode.rvm.RVM)
	 */
	public void exec(WarriorProcess process, RVM rvm) {
		
		// Get the current PC and calculate the new value by adding
		// target to it. Set PC to the newly calculated value. 
		int ntarget = process.getPC();
		ntarget += target;
		process.setPC(ntarget);
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.JFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " " + target;
	}
		
}
