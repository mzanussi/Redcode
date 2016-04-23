package com.michaelzanussi.redcode;

import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * The interface for the Redcode assembly instruction set. It specifically
 * provides support for I-, J-, and R-format instructions both for a large 
 * subset of the MIPS instruction set and a custom set of MIPS extensions. 
 * Methods are provided to encode the assembly into object code, decode 
 * object code back into assembly, and an execute method which actually 
 * performs the execution of the instruction.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public interface RedcodeInstruction {
	
	/**
	 * Decode object code (a 32-bit word) into plain-text assembly language.
	 * 
	 * @param instruction the 32-bit word format instruction to decode.
	 * @return the resultant assembly language.
	 */
	public String decode(int instruction);
	
	/**
	 * Encode plain-text assembly language into object code (a 32-bit word). 
	 * Labels, comments, and pseudoinstructions are not supported.
	 * 
	 * @param lexer the lexer containing the assembly language.
	 * @return the encoded object code in 32-bit word format.
	 * @throws ParsingException If an error is encountered while parsing.
	 */
	public int encode(Lexer lexer) throws ParsingException;
	
	/**
	 * Execute the current instruction.
	 * 
	 * @param process the WarriorProcess (register access)
	 * @param rvm the RVM (memory access)
	 */
	public void exec(WarriorProcess process, RVM rvm);
	
}
