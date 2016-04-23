package com.michaelzanussi.redcode;

/**
 * The I-format instruction type (transfer, branch, immediate format) is one of the 
 * four base classes for the Redcode instruction set. <p>
 * 
 * In the I-format instruction, the <code>createInstruction()</code> method builds 
 * a 32-bit word based on the fields: <code>op</code>, <code>rs</code>, 
 * <code>rt</code>, and a 16-bit address/immediate value. The <code>breakupInstruction()</code>
 * and <code>breakupInstructionUnsigned()</code> methods reverse this procedure and 
 * produces the human-readable Redcode assembly based on a 32-bit word instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public abstract class IFormat extends AbstractRedcodeInstruction {

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
	 * The address/immediate value, a 16-bit value. Combines the <code>rd</code>, 
	 * <code>shamt</code> and <code>funct</code> fields. Also referred to as offset.
	 */
	protected int immediate;
	
	/**
	 * The 32-bit instruction.
	 */
	protected int instruction;

	/**
	 * No-arg constructor.
	 */
	public IFormat() {
		
		name = null;
		op = 0x00;
		rs = 0x00;
		rt = 0x00;
		immediate = 0x00;
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
		
 		// Now subdivide into segments.
 		String temp = "";
 		for (int j = 0; j < 32; j++) {
 			if (j == 6 || j == 11) {
 				temp += " " + inst.charAt(j);
 			} else if (j == 16) {
 				temp += "   " + inst.charAt(j);
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

	// Bit masks used exclusively by the I-format createInstruction(), 
	// breakupInstruction() and breakupInstructionUnsigned() methods.
	// FLAG5 gives the 5 low-order bits, FLAG6 gives the 6 low-order,
	// and FLAG16 gives the 16 low-order bits.
	protected static final int FLAG5 = 0x1f;
	protected static final int FLAG6 = 0x3f;
	protected static final int FLAG16 = 0xffff;
	
	/**
	 * Break-up the 32-bit integer instruction into its component parts,
	 * where immediate is a signed value (sign-extended).
	 */
	protected void breakupInstruction() {
		
		int temp = instruction;

		// Get the low-order 16-bits.
		immediate = (int)(temp & FLAG16);
		
		// If the sign-bit is turned on, sign-extend the
		// high-order 16-bits.
		if ((immediate & 0x8000) == 0x8000) {
			immediate |= 0xffff0000;
			
		}
		
		temp >>>= 16;
		
		rt = (byte)(temp & FLAG5);
		
		temp >>>= 5;
		
		rs = (byte)(temp & FLAG5);
		
	}
	
	/**
	 * Break-up the 32-bit integer instruction into its component parts,
	 * where immediate is an unsigned value (zero-extended).
	 */
	protected void breakupInstructionUnsigned() {
		
		int temp = instruction;
		
		immediate = (int)(temp & FLAG16);
		
		temp >>>= 16;
		
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
		int xrs = rs & FLAG5;
		instruction |= xrs;
		
		instruction <<= 5;
		
		// We only want the low order 5 bits.
		int xrt = rt & FLAG5;
		instruction |= xrt;
		
		instruction <<= 16;
		
		// Insert the immediate/address value.
		int imm = immediate & FLAG16;
		instruction |= imm;

	}
	
}
