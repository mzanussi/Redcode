package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>open</tt> opens connection to the
 * remote RVM whose address is given by <tt>rt</tt>. If the connection is
 * successful, 1 is stored in <tt>rt</tt>, otherwise 0 is stored in <tt>rt</tt>.
 * When a successful connection is made, the socket of the executing process
 * is initialized to a record of the remote RVM. Attempting to open a new
 * connection when the process already has an open socket is an illegal
 * operation for the executing process. Attempting to open a connection to a
 * non-existent RVM is not illegal, but does cause the open operation to fail. <p>
 * 
 * A DCoreWars extended instruction, it replaces the MIPS <tt>lbu</tt> instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Open extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Open() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "open";
		
		// Override defaults.
		op = 0x24;
		
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
			throw new NullPointerException("Open.encode error: Encode requires a lexer.");
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
