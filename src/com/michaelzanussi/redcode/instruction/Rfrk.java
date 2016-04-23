package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>rfrk</tt> creates a new process
 * on the remote RVM specified by the executing process's socket. If the remote
 * RVM already has a process group with the same ID as the executing process,
 * the new, remote process is created in that process group. If the remote RVM
 * does not have such a process group, a new process group with the same ID as
 * the currently executing process group is created on the remote RVM and the new
 * remote process is created in that group. Attempting to execute <tt>rfrk</tt>
 * when there is not valid socket available (e.g., before executing <tt>open</tt>
 * or after an unsuccessful <tt>open</tt>) is an illegal operation error for the
 * executing process. <p>
 * 
 * A DCoreWars extended instruction, it replaces the MIPS <tt>lwl</tt> instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Rfrk extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Rfrk() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "rfrk";
		
		// Override defaults.
		op = 0x22;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstructionUnsigned();
		
		return name + " $" + rt;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Rfrk.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		rt = parseRegister(lexer.nextToken());
		
		// Now that all the fields have been parsed, create the instruction.
		createInstruction();
		
		// Return the 32-bit instruction.
		return instruction;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#exec(com.michaelzanussi.redcode.rvm.WarriorProcess, com.michaelzanussi.redcode.rvm.RVM)
	 */
	public void exec(WarriorProcess process, RVM rvm) {

		// DCorewars (distributed Corewars) not implemented (illegal instruction)
		// do nothing

		// Increment PC.
		process.incrementPC();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.IFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rt;
	}
		
}
