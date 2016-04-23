package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>rsw</tt> writes the contents
 * of register <tt>rt</tt> to the address <tt>rs</tt> on the remote RVM specified
 * by the executing process's socket. If the operation succeeds, a 1 is stored in
 * register <tt>rt</tt>, otherwise a 0 is. Attempting to execute <tt>rsw</tt> when
 * there is no open socket available (e.g., before executing <tt>open</tt> or after
 * an unsuccessful <tt>open</tt>) is an illegal operation for the executing
 * process. <p>
 *  
 * Executing this instruction actually causes the specified word to be buffered in
 * an incoming data buffer for the remote machine. All such buffers are of finite,
 * bounded length. If a buffer is full when this instruction is executed, the word
 * is dropped (not inserted into the incoming data buffer) and this instruction
 * fails (causing 0 to be stored in <tt>rt</tt>).
 * 
 * A DCoreWars extended instruction, it replaces the MIPS <tt>lhu</tt> instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Rsw extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Rsw() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "rsw";
		
		// Override defaults.
		op = 0x25;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstruction();
				
		return name + " $" + rs + ", $" + rt;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Rsw.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		rs = parseRegister(lexer.nextToken());
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
		return super.toString() + "\t" + name + " $" + rs + ", $" + rt;
	}
		
}
