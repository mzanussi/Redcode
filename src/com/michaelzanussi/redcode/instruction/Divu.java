package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.RFormat;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;

/**
 * An R-format instruction type, <tt>divu</tt> divides register <tt>rs</tt> 
 * by register <tt>rt</tt>, leaving the quotient in register <tt>lo</tt>
 * and the remainder in register <tt>hi</tt>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Divu extends RFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Divu() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "divu";
		
		// Override defaults.
		funct = 0x1b;
		
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
			throw new NullPointerException("Divu.encode error: Encode requires a lexer.");
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
		int s = process.getRegister(rs);
		int t = process.getRegister(rt);
		
		// The LO (quotient) and HI (remainder) registers.
		int lo = 0;
		int hi = 0;
		
		if (t != 0) {
			// Divide register rs by register rt, leaving the quotient in
			// register LO and the remainder in register HI. Note that if an
			// operand is negative, the remainder is unspecified by the MIPS
			// architecture and depends on the convention of the machine, in
			// this case the RVM. We'll allow negative operands and the
			// corresponding results of division with negative operands.
			// $LO = $s / $t
			lo = Math.abs(s / t);
			// $HI = $s % $t
			hi = Math.abs(s % t);
		}
		
		// Put the results into registers LO and HI.
		process.setLO(lo);
		process.setHI(hi);
		
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
