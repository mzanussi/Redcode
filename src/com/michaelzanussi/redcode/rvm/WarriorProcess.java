package com.michaelzanussi.redcode.rvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.michaelzanussi.redcode.Assembler;
import com.michaelzanussi.redcode.BadInstructionException;
import com.michaelzanussi.redcode.ParsingException;
import com.michaelzanussi.redcode.RedcodeInstruction;

/**
 * A process, one for each Redcode warrior. The process includes the
 * registers and the Redcode program, as well as a unique id for the
 * process and a pointer to the process group to which it belongs.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (8 April 2016)
 */
public class WarriorProcess {

	private int registers[];
	private int pc;
	private int hi;
	private int lo;
	
	private ProcessGroup pg;		// process group this process belongs to
	private boolean alive;			// is the process still alive?
	private Assembler assembler;	// the Redcode assembler
	private int pid;				// process id
	
	/**
	 * FRK uses this constructor.
	 * 
	 * @param assembler
	 * @param pg
	 */
	public WarriorProcess(Assembler assembler, ProcessGroup pg) {
		
		registers = new int[32];
		for (int i = 0; i < registers.length; i++) {
			registers[i] = 0;
		}
		pc = 0;
		hi = 0;
		lo = 0;
		
		this.pg = pg;
		alive = true;
		
		this.assembler = assembler;

		// Process now has the instruction set, 
		// so set the PID for this process and
		// add the process to the process group.
		pid = this.pg.getNumberOfProcesses() + 1;
		this.pg.addProcess(this);
		
	}
	
	/**
	 * @param file
	 * @param pg
	 */
	public WarriorProcess(File file, ProcessGroup pg) {

		registers = new int[32];
		for (int i = 0; i < registers.length; i++) {
			registers[i] = 0;
		}
		pc = 0;
		hi = 0;
		lo = 0;
		
		this.pg = pg;
		alive = true;

		// Check if a Redcode program was specified.
		if (file == null) {
			throw new NullPointerException("No program was specified.");
		}
		
		// Load the Redcode program.
		assembler = new Assembler();
		try {
			assembler.load(file);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (BadInstructionException e) {
			e.printStackTrace();
		}
		
		// Process now has the instruction set, 
		// so set the PID for this process and
		// add the process to the process group.
		pid = this.pg.getNumberOfProcesses() + 1;
		this.pg.addProcess(this);
		
	}
	
	/**
	 * @return
	 */
	public Assembler getAssembler() {
		return assembler;
	}
	
	/**
	 * @return
	 */
	public int getPID() {
		return pid;
	}
	
	/**
	 * @return
	 */
	public ProcessGroup getProcessGroup() {
		return pg;
	}
	
	/**
	 * @return
	 */
	public List<Integer> getInstructions() {
		return assembler.getInstructions();
	}
	
	/**
	 * 
	 */
	public void kill() {
		alive = false;
	}
	
	/**
	 * @param instruction
	 * @return
	 */
	public RedcodeInstruction getInstruction(Integer instruction) {
		RedcodeInstruction rci = null;
		try {
			rci = assembler.getRedcodeInstruction(instruction);
		} catch (BadInstructionException e) {
			e.printStackTrace();
		}
		
		return rci;
	}
	
	/**
	 * @return
	 */
	public boolean isRunnable() {
		return alive;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public int getRegister(int index) {
		// Check index bounds.
		if (index < 0 || index > 31) {
			throw new ArrayIndexOutOfBoundsException("Invalid register " + index);
		}
		// Return the value at register point to by index.
		return registers[index];
	}
	
	/**
	 * @param index
	 * @param value
	 */
	public void setRegister(int index, Integer value) {
		// Check index bounds.
		if (index < 0 || index > 31) {
			throw new ArrayIndexOutOfBoundsException("Invalid register " + index);
		}
		// Set register at index to value.
		registers[index] = value;
		// Register $0 always contains 0, regardless of 
		// what data is written to it, so just set to 0.
		registers[0] = 0;
	}
	
	/**
	 * @return
	 */
	public int getPC() {
		return pc;
	}
	
	/**
	 * @param pc
	 */
	public void setPC(int pc) {
		this.pc = pc;
	}
	
	/**
	 * 
	 */
	public void incrementPC() {
		pc++;
	}
	
	/**
	 * @return
	 */
	public int getHI() {
		return hi;
	}
	
	/**
	 * @param hi
	 */
	public void setHI(int hi) {
		this.hi = hi;
	}
	
	/**
	 * @return
	 */
	public int getLO() {
		return lo;
	}
	
	/**
	 * @param lo
	 */
	public void setLO(int lo) {
		this.lo = lo;
	}
	
	/**
	 * 
	 */
	public void dump() {
		System.out.print("PC    " + "HI  " + "LO  " + "00 " + "01 " + "02 " + "03 " + "04 " + "05 " + "06 " + "07 " + "08 " + "09 ");
		for (int i = 10; i < registers.length; i++) {
			System.out.print(i + " ");
		}
		System.out.print("RBL?");
		
		System.out.println();
		String str = new Integer(pc).toString();
		while (str.length() < 6) {
			str += " ";
		}
		System.out.print(str);

		str = new Integer(hi).toString();
		while (str.length() < 4) {
			str += " ";
		}
		System.out.print(str);

		str = new Integer(lo).toString();
		while (str.length() < 4) {
			str += " ";
		}
		System.out.print(str);

		for (int i = 0; i < registers.length; i++) {
			str = ((Integer) registers[i]).toString();
			while (str.length() < 3) {
				str += " ";
			}
			System.out.print(str);
		}
		System.out.print(isRunnable());
		System.out.println();

	}
	
}
