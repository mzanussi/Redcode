package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.RFormat;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;

/**
 * An R-format instruction type, <tt>slt</tt> sets register <tt>rd</tt> to 1 
 * if register <tt>rs</tt> is less than <tt>rt</tt>, and to 0 otherwise.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Slt extends RFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Slt() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "slt";
		
		// Override defaults.
		funct = 0x2a;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstruction();
		
		return name + " $" + rd + ", $" + rs + ", $" + rt;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Slt.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
		rd = parseRegister(lexer.nextToken());
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
		
		// Retrieve the rs and rt registers.
		int s = process.getRegister(rs);
		int t = process.getRegister(rt);
		
		// $d = ($s < $t)
		if (s < t) {
			process.setRegister(rd, 1);
		} else {
			process.setRegister(rd, 0);
		}
		
		// Increment PC.
		process.incrementPC();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.RFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rd + ", $" + rs + ", $" + rt;
	}
		
}
