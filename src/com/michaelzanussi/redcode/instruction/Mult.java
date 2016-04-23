package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.RFormat;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;

/**
 * An R-format instruction type, <tt>mult</tt> multiplies registers <tt>rs</tt> 
 * and <tt>rt</tt>, leaving the low-order word of the product in register
 * <tt>lo</tt> and the high-order word in register <tt>hi</tt>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Mult extends RFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Mult() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "mult";
		
		// Override defaults.
		funct = 0x18;
		
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
			throw new NullPointerException("Mult.encode error: Encode requires a lexer.");
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
		
		// Retrieve the rs and rt registers.
		long s = process.getRegister(rs);
		long t = process.getRegister(rt);
		
		// Multiply registers rs and rt.
		long product = s * t;
		
		// $LO = ((s * t ) << 32 ) >>> 32
		long lo = (product << 32) >>> 32;
		
		// $HI = (s * t) >> 32
		long hi = product >> 32;
		
		// Put the results into registers LO and HI.
		process.setLO((int)lo);
		process.setHI((int)hi);
		
		// Increment PC.
		process.incrementPC();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.RFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rs + ", $" + rt;
	}
		
}
