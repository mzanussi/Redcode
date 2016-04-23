package com.michaelzanussi.redcode;

import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * The <tt>AbstractRedcodeInstruction</tt> class provides a minimal implementation 
 * of the <tt>RedcodeInstruction</tt> interface. The <code>decode()</code>, 
 * <code>encode()</code>, <code>exec()</code> and <code>createInstruction()</code>
 * methods are deferred to subclasses for implementation. The methods implemented
 * in this abstract class relate solely to parsing the plain-text Redcode assembly
 * language into their constituent parts such as register values and immediate
 * values, and performing range checking on those parts.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public abstract class AbstractRedcodeInstruction implements RedcodeInstruction {
	
	/**
	 * No-arg constructor.
	 */
	public AbstractRedcodeInstruction() {
		
	}

	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.RedcodeInstruction#decode(int)
	 */
	public abstract String decode(int instruction);
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.RedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public abstract int encode(Lexer lexer) throws ParsingException;
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.RedcodeInstruction#exec(com.michaelzanussi.redcode.rvm.WarriorProcess, com.michaelzanussi.redcode.rvm.RVM)
	 */
	public abstract void exec(WarriorProcess process, RVM rvm);
	
	/**
	 * Create the object code instruction from the fields of each instruction
	 * format. <p>
	 * 
	 * Defer to subclass for implementation.
	 */
	protected abstract void createInstruction();
	
	/**
	 * Parses the current token for an address or an immediate value. The 
	 * address/immediate value is a 16-bit signed value (sign-extended), 
	 * stored as a 32-bit <code>integer</code>, with a valid range of -32768
	 * to 32767 (-2^15 to 2^15-1).
	 * 
	 * @param token the token to parse.
	 * @return the address/immediate value.
	 * @throws NumberFormatException If the resultant token cannot be converted
	 * to an <code>integer</code> value.
	 * @throws IllegalArgumentException If the address/immediate value is not in the range
	 * of -32768 through 32767.
	 */
	protected int parseImmediate(Token token) {

		// Initialize the output.
		int imm = 0;
		
		// Attempt to convert the argument to a short value.
		try {
			String strToken = token.getToken();
			imm = Integer.parseInt(strToken);
		}
		catch (NumberFormatException e) {
			// This seems redundant, but we want to control the error message.
			throw new NumberFormatException("'" + token.getToken() + "' is not a valid address/immediate argument.");
		}
		
		// Verify the address/immediate is within the correct range of -32768 to 32767.
		if (imm < -32768 || imm > 32767) {
			throw new IllegalArgumentException("Address/immediate value out of range: '" + token.getToken() + "'.");
		}
		
		return imm;
		
	}

	/**
	 * Parses the current token for an address or an immediate value. The 
	 * address/immediate value is a 16-bit unsigned value (zero-extended), 
	 * stored as a 32-bit <code>integer</code>, with a valid range of 0 to
	 * 65535 (0 to 2^16-1).
	 * 
	 * @param token the token to parse.
	 * @return the address/immediate value.
	 */
	protected int parseImmediateUnsigned(Token token) {

		// Initialize the output.
		int imm = 0;
		
		// Attempt to convert the argument to a short value.
		try {
			String strToken = token.getToken();
			imm = Integer.parseInt(strToken);
		}
		catch (NumberFormatException e) {
			// This seems redundant, but we want to control the error message.
			throw new NumberFormatException("'" + token.getToken() + "' is not a valid address/immediate argument.");
		}
		
		// Verify the address/immediate is within the correct range of 0 to 65535.
		if (imm < 0 || imm > 65535) {
			throw new IllegalArgumentException("Address/immediate value out of range: '" + token.getToken() + "'.");
		}
		
		return imm;
		
	}

	/**
	 * Parses for a load/store register argument by extracting the register
	 * from within a parenthetical value. The register is retrieved via a
	 * call to the <code>parseRegister()</code> method, which enforces a
	 * valid range of 0 to 31. 
	 * 
	 * @param lexer the lexer.
	 * @return the register.
	 * @throws ParsingException If <code>(</code>, <code>$</code>, or 
	 * <code>)</code> is not encountered while parsing the input.
	 */
	protected byte parseLoadStore(Lexer lexer) throws ParsingException {
		
		// The first token should be an open parenthesis.
		Token token = lexer.nextToken();
		String strToken = token.getToken();
		if (!strToken.equals("(")) {
			throw new ParsingException("'(' expected, received: " + strToken);
		}
		
		// Retrieve the register value.
		token = lexer.nextToken();
		byte reg = parseRegister(token);
		
		// The last token should be a closed parenthesis.
		token = lexer.nextToken();
		strToken = token.getToken();
		if (!strToken.equals(")")) {
			throw new ParsingException("')' expected, received: " + strToken);
		}
		
		return reg;
		
	}
	
	/**
	 * Parses the current token for a register argument. That is, it extracts
	 * the <code>$</code> and converts the remaining token into a byte value, the
	 * register. The valid range for register values is 0 to 31.
	 * 
	 * @param token the token to parse.
	 * @return the register.
	 * @throws ParsingException If <code>$</code> is not the first character
	 * of the token.
	 */
	protected byte parseRegister(Token token) throws ParsingException {
		
		// Convert the token to a string.
		String strToken = token.getToken();
		
		// Check if this is a register argument by looking at the first
		// character, which should be a '$'.
		if (strToken.charAt(0) != '$') {
			throw new ParsingException("'" + strToken + "' is not a valid register argument.");
		}
		
		byte reg = 0x00;
		
		// Attempt to convert the argument to a byte value.
		try {
			reg = (byte)Integer.parseInt(strToken.substring(1, strToken.length()));
		}
		catch (NumberFormatException e) {
			// This seems a bit redundant, but we want to control the error message.
			throw new NumberFormatException("'" + strToken + "' is not a valid register argument.");		
		}
		
		// Verify the register is within the correct range of 0 to 31.
		if (reg < 0 || reg > 31) {		
			throw new IllegalArgumentException("Register value out of range: '" + strToken + "'.");
		}
		
		return reg;
		
	}
	
	/**
	 * Parses the current token for a shift amount argument, converting it into
	 * a <code>byte</code> value. The valid range for shift amount values is 0 to 31.
	 * 
	 * @param token the token to parse.
	 * @return the shift amount value.
	 */
	protected byte parseShiftAmount(Token token) {

		// Initialize the shift amount value.
		byte shift = 0x00;
		
		// Attempt to convert the argument to a byte value.
		try {
			shift = (byte)Integer.parseInt(token.getToken());
		}
		catch (NumberFormatException e) {
			// This seems redundant, but I wanted to control the error message.
			throw new NumberFormatException("'" + token.getToken() + "' is not a valid shift amount value.");
		}
		
		// Verify the shift amount is within the correct range of 0 to 31.
		if (shift < 0 || shift > 31) {
			throw new IllegalArgumentException("Shift amount value out of range: '" + token.getToken() + "'.");
		}
		
		return shift;
		
	}
	
	/**
	 * Parses the current token for a target address, as used by the J instruction. 
	 * The target address is a signed 26-bit value (sign-extended), and is stored as 
	 * an <code>integer</code>, with a valid range of -33554432 to 33554431.
	 * 
	 * @param token the token to parse.
	 * @return the target address.
	 * @throws NumberFormatException If the resultant token cannot be converted
	 * to an <code>integer</code> value.
	 * @throws IllegalArgumentException If the target address is not in the range
	 * of -33554432 through 33554431 (2^25).
	 */
	protected int parseTarget(Token token) {

		// Initialize the target address.
		int target = 0;
		
		// Attempt to convert token to an integer.
		try {
			target = Integer.parseInt(token.getToken());
		}
		catch (NumberFormatException e) {
			// This seems redundant, but I wanted to control the error message.
			throw new NumberFormatException("'" + token.getToken() + "' is not a valid target address.");
		}
		
		// Verify the target address is within the correct range of -33554432 
		// to 33554431.
		if (target < -33554432 || target > 33554431) {
			throw new IllegalArgumentException("Target address out of range: '" + token.getToken() + "'.");
		}
		
		return target;
		
	}
	
}
