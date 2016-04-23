package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>andi</tt> puts the logical AND of registers 
 * <tt>rs</tt> and the zero-extended immediate into register <tt>rt</tt>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Andi extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Andi() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "andi";
		
		// Override defaults.
		op = 0x0c;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstructionUnsigned();
		
		return name + " $" + rt + ", $" + rs + ", " + immediate;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Andi.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		rt = parseRegister(lexer.nextToken());
		rs = parseRegister(lexer.nextToken());
		immediate = parseImmediateUnsigned(lexer.nextToken());
		
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
		
		// $t = $s & C
		int t = s & immediate;
		
		// Put the result into register rt.
		process.setRegister(rt, t);
		
		// Increment PC.
		process.incrementPC();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.IFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rt + ", $" + rs + ", " + immediate;
	}
		
}
