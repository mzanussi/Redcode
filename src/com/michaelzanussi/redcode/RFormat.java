package com.michaelzanussi.redcode;

/**
 * The R-format instruction type (arithmetic instruction format) is one of the three 
 * base classes for the Redcode instruction set. <p>
 * 
 * In the R-format instruction, the <code>createInstruction()</code> method builds a 
 * 32-bit word based on the fields: <code>op</code>, <code>rs</code>, <code>rt</code>, 
 * <code>rd</code>, <code>shamt</code>, and <code>funct</code>. The 
 * <code>breakupInstruction()</code> method reverses this procedure and produces the 
 * human-readable Redcode assembly based on a 32-bit word instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public abstract class RFormat extends AbstractRedcodeInstruction {

	/**
	 * The instruction name.
	 */
	protected String name;
	
	/**
	 * The opcode (6-bits).
	 */
	protected byte op;
	
	/**
	 * The source register (5-bits).
	 */
	protected byte rs;
	
	/**
	 * Normally a source register (5-bits).
	 */
	protected byte rt;
	
	/**
	 * The destination register (5-bits).
	 */
	protected byte rd;
	
	/**
	 * The shift amount (5-bits).
	 */
	protected byte shamt;
	
	/**
	 * The function (6-bits). See also <code>op</code>.
	 */
	protected byte funct;
	
	/**
	 * The 32-bit instruction.
	 */
	protected int instruction;

	/**
	 * No-arg constructor.
	 */
	public RFormat() {
		
		name = null;
		op = 0x00;
		rs = 0x00;
		rt = 0x00;
		rd = 0x00;
		shamt = 0x00;
		funct = 0x00;
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
 			if (j == 6 || j == 11 || j == 16 || j == 21 || j == 26) {
 				temp += " " + inst.charAt(j);
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

	// Bit masks used exclusively by the R-format createInstruction() and
	// breakupInstruction() methods. FLAG5 gives the 5 low-order bits, FLAG6 
	// gives the 6 low-order bits.
	private static final int FLAG5 = 0x1f;
	private static final int FLAG6 = 0x3f;
	
	/**
	 * Break-up the 32-bit integer instruction into its component parts.
	 */
	protected void breakupInstruction() {
		
		int temp = instruction;
		
		funct = (byte)(temp & FLAG6);
		
		temp >>>= 6;
		
		shamt = (byte)(temp & FLAG5);
		
		temp >>>= 5;
		
		rd = (byte)(temp & FLAG5);
		
		temp >>>= 5;
		
		rt = (byte)(temp & FLAG5);
		
		temp >>>= 5;
		
		rs = (byte)(temp & FLAG5);
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#createInstruction()
	 */
	protected void createInstruction() {
		
		instruction = 0;
		
		// We only want the low order 6 bits.
		int xop = op & FLAG6;
		instruction |= xop;
		
		instruction <<= 5;
		
		// We only want the low order 5 bits.
		int trs = rs & FLAG5;
		instruction |= trs;
		
		instruction <<= 5;
		
		// We only want the low order 5 bits.
		int trt = rt & FLAG5;
		instruction |= trt;
		
		instruction <<= 5;
		
		// We only want the low order 5 bits.
		int trd = rd & FLAG5;
		instruction |= trd;

		instruction <<= 5;
		
		// We only want the low order 5 bits.
		int tshamt = shamt & FLAG5;
		instruction |= tshamt;

		instruction <<= 6;
		
		// We only want the low order 6 bits.
		int tfunct = funct & FLAG6;
		instruction |= tfunct;
		
	}
	
}
