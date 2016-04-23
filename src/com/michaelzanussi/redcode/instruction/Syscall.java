package com.michaelzanussi.redcode.instruction;

import com.michaelzanussi.redcode.RFormat;
import com.michaelzanussi.redcode.rvm.RVM;
import com.michaelzanussi.redcode.rvm.WarriorProcess;
import com.michaelzanussi.redcode.Lexer;
import com.michaelzanussi.redcode.ParsingException;

import java.util.Random;

/**
 * An R-format instruction type, <tt>syscall</tt> puts the system call ID into
 * register <tt>rs</tt> (<tt>$1</tt>) and the argument to the call into register
 * <tt>imm</tt> (<tt>$2</tt>). The result of the call, if any, is placed in
 * register <tt>$1</tt>. <p>
 * 
 * The system call instruction supports special "OS-like" calls that provide
 * useful services to assembly language programs. The calls work by putting a
 * "system call ID" value int register <tt>$1</tt> and the argument to the call
 * in register <tt>$2</tt> and then executing the <tt>syscall</tt> instruction.
 * The result of the instruction (if any) is placed into register <tt>$1</tt>.
 * While it may take multiple instructions to fill the registers for the system
 * call, actually executing the system call is an atomic operation. <p>
 * 
 * A DCoreWars extended instruction, it replaces the MIPS <tt>syscall</tt>
 * instruction.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004)
 */
public class Syscall extends RFormat {

	/**
	 * No-arg constructor.
	 */
	public Syscall() {

		// Set defaults.
		super();

		// Set instruction name.
		name = "syscall";

		// Override defaults.
		funct = 0x0c;

	}

	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#decode(int)
	 */
	public String decode(int instruction) {

		// Store the 32-bit instruction.
		this.instruction = instruction;

		// Breakup the instruction into its component parts.
		breakupInstruction();

		return name;

	}

	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#encode(com.michaelzanussi.redcode.Lexer)
	 */
	public int encode(Lexer lexer) throws ParsingException {

		// Was a lexer passed to the encoder?
		if (lexer == null) {
			throw new NullPointerException("Syscall.encode error: Encode requires a lexer.");
		}

		// Now that all the fields have been parsed, create the instruction.
		createInstruction();

		// Return the 32-bit instruction.
		return instruction;

	}

	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.AbstractRedcodeInstruction#exec(com.michaelzanussi.redcode.rvm.WarriorProcess, com.michaelzanussi.redcode.rvm.RVM)
	 */
	public void exec(WarriorProcess process, RVM rvm) {

		// Switch on register 1, the call id.
		switch (process.getRegister(1)) {

		case 0: // HALT
			process.kill();
			break;
		case 1: // Random Number between 0 and RVM memory size.
			Random rand = new Random();
			int value = rand.nextInt(rvm.memsize());
			// Store result in register $1.
			process.setRegister(1, value);
			break;
		case 2: // Memsize - memory size of RVM.
			// Store result in register $1.
			process.setRegister(1, rvm.memsize());
			break;
		case 3: // Netsize - number of hosts on current network.
			// (for future use)
			// Store result in register $1.
			process.setRegister(1, 1);
			break;
		case 4: // GetPID - PID of current process.
			// Store result in register $1.
			process.setRegister(1, process.getPID());
			break;
		case 5: // GetPGID - Player id of current process group.
			// Store result in register $1.
			process.setRegister(1, process.getProcessGroup().getPlayer());
			break;
		case 6: // GetNPG - number of runnable process groups.
			// Store result in register $1.
			process.setRegister(1, rvm.getNPG());
			break;
		case 7: // GetNPlayers - number of players in game.
			// Store result in register $1.
			process.setRegister(1, rvm.getNPlayers());
			break;
		case 8: { // Score(PGID) - current score for specified process group.
			// argument is stored in $2
			int arg = process.getRegister(2);
			int score = rvm.score(arg);
			// Store result in register $1.
			process.setRegister(1, score);
			break;
		}
		case 9: { // RemoteProc(RPGID)
			// (for future use)
			// Store result in register $1.
			process.setRegister(1, 0);
			break;
		}
		case 10: { // RemoteScore(RPGID)
			// (for future use)
			// Store result in register $1.
			process.setRegister(1, 0);
			break;
		}
		case 11: { // GetNProcs(PGID) - the number of runnable processes
			// in the specified process group.
			// argument is stored in $2
			int arg = process.getRegister(2);
			int np = rvm.getNProcs(arg);
			// Store result in register $1.
			process.setRegister(1, np);
			break;
		}
		case 12: // RMemsize
			// (for future use)
			// Store result in register $1.
			process.setRegister(1, 0);
			break;
		default: // Invalid - halt
			process.kill();
			break;

		}

		// Increment PC.
		process.incrementPC();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.RFormat#toString()
	 */
	public String toString() {
		return super.toString() + "\t" + name;
	}

}
