package com.michaelzanussi.redcode.instruction;

import java.util.List;

import com.michaelzanussi.redcode.IFormat;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.rvm.Cell;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;

/**
 * An I-format instruction type, <tt>frk</tt> creates a new process within the
 * same process group as the executing process. The new process is initialized
 * and its PC is set from <tt>rt</tt>. <p>
 * 
 * A DCoreWars extended instruction, it replaces the MIPS <tt>lb</tt> instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class Frk extends IFormat {
	
	/**
	 * No-arg constructor.
	 */
	public Frk() {
		
		// Set defaults.
		super();
		
		// Set instruction name.
		name = "frk";
		
		// Override defaults.
		op = 0x20;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {
		
		// Store the 32-bit instruction.
		this.instruction = instruction;
		
		// Breakup the instruction into its component parts.
		breakupInstructionUnsigned();
		
		return name + " $" + rt;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Frk.encode error: Encode requires a lexer.");
		}

		// Retrieve the registers.
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
		
		// Retrieve the rt register.
		// rt holds the PC for the new process.
		int t = process.getRegister(rt);
		
		if (t < 0 || t > rvm.memsize()) {
			// PC has run off the end of RVM RAM.
			process.kill();
		} else {
			// PC is okay.
			
			// Create a new process within the same process group as the 
			// executing process. The new process is initialized as
			// specified by the rules and its PC is set from rs.
			WarriorProcess newProcess = new WarriorProcess(process.getAssembler(), process.getProcessGroup());
			newProcess.setPC(t);
			
			// get the instruction set.
			List<Integer> iset = newProcess.getInstructions();
			
			// Load the program into memory
			for (Integer instruction : iset) {
				try {
					Cell cell = rvm.getMemory(t);
					cell.setProcessGroup(newProcess.getProcessGroup());
					cell.setPID(newProcess.getPID());
					cell.setInstruction(instruction);
					t++;
				} catch (IndexOutOfBoundsException e) {
					process.kill();
				}
			}
			
		}

		// Increment PC.
		process.incrementPC();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.IFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name + " $" + rt;
	}
		
}
