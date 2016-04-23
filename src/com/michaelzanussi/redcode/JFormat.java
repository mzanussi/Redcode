package com.michaelzanussi.redcode;

/**
 * The J-format instruction type (jump instruction format) is one of the 
 * four base classes for the Redcode instruction set. <p>
 * 
 * In the J-format instruction, the <code>createInstruction()</code> method builds 
 * a 32-bit word based on the fields: <code>op</code> and a 26-bit target address. The 
 * <code>breakupInstruction()</code> method reverses this procedure and produces the 
 * human-readable Redcode assembly based on a 32-bit word instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public abstract class JFormat extends AbstractRedcodeInstruction {

	/**
	 * The instruction name.
	 */
	protected String name;
	
	/**
	 * The instruction, as represented by an 32-bit integer (word).
	 */
	protected int instruction;
	
	/**
	 * The opcode (6-bits).
	 */
	protected byte op;
	
	/**
	 * Target address for J-format instructions, a 26-bit value.
	 * Combines the <code>rs</code>, <code>rd</code>, <code>rt</code>,
	 * <code>shamt</code> and <code>funct</code> fields. 
	 */
	protected int target;
	
	/**
	 * No-arg constructor.
	 */
	public JFormat() {
		
		name = null;
		op = 0x00;
		instruction = 0;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		// Convert the instruction to its binary equivalent.
		String inst = Integer.toBinaryString(instruction);

		// Add leading zeroes, if necessary.
 		for (int i = inst.length(); i < 32; i++) {
			inst = "0" + inst;
		}
		
 		// Now subdivide the segments.
 		String temp = "";
 		for (int j = 0; j < 32; j++) {
 			if (j == 6) {
 				temp += "     " + inst.charAt(j);
 			} else {
 				temp += inst.charAt(j);
 			}
 		}
 		inst = temp;
		
		// Right-align the instruction (integer).
 		String ni = Integer.toString(instruction);
		for (int i = ni.length(); i < 11; i++) {
			ni = " " + ni;
		}
		
		return ni + "\t" + inst;
		
	}
	
	// Bit masks used exclusively by the J-format createInstruction() and
	// breakupInstruction() methods. FLAG6 gives the 6 low-order bits, 
	// FLAG26 gives the 26 low-order bits.
	private static final int FLAG6 = 0x3f;
	private static final int FLAG26 = 0x3ffffff;
	
	/**
	 * Break-up the 32-bit integer instruction into its component parts.
	 */
	protected void breakupInstruction() {
	
		int temp = instruction;
	
		// Get the low-order 26-bits.
		target = (int)(temp & FLAG26);
		
		// If the sign-bit is turned on, sign-extend the
		// high-order 6-bits.
		if ((target & 0x2000000) == 0x2000000) {
			target |= 0xfc000000;
			
		}
		
	}

	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#createInstruction()
	 */
	protected void createInstruction() {
		
		instruction = 0;
		
		// We only want the low order 6 bits.
		int xop = op & FLAG6;
		instruction |= xop;
		
		instruction <<= 26;
		
		// We only want the low order 26 bits.
		int xtarget = target & FLAG26;
		instruction |= xtarget;
		
	}
	
}
