package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.RFormat;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;

/**
 * An R-format instruction type, <tt>xor</tt> puts the logical XOR of registers 
 * <tt>rs</tt> and <tt>rt</tt> into register <tt>rd</tt>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Xor extends RFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Xor() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "xor";
		
		// Override defaults.
		funct = 0x26;
		
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
			throw new NullPointerException("Xor.encode error: Encode requires a lexer.");
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

		// $d = $s ^ $t
	    int d = s ^ t;
	    
		// Put the result into register rd.
	    process.setRegister(rd, d);
	    
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
