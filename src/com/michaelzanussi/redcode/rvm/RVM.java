package com.michaelzanussi.redcode.rvm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A Redcode Virtual Machine.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (8 April 2016)
 */
public class RVM {
	
	private Cell memory[];				// RVM RAM
	private List<ProcessGroup> groups;	// process groups
	private int next;					// next process group to execute
	
	private static final int MAX_RAM = 65535;
	
	/**
	 * No-arg constructor. Default size is MAX_RAM.
	 */
	public RVM() {
		this(MAX_RAM);
	}
	
	/**
	 * @param size
	 */
	public RVM(int size) {
		
		// Valid RAM size is between 1 and MAX_RAM.
		if (size < 1 || size > MAX_RAM) {
			throw new IndexOutOfBoundsException("Invalid RAM size: " + size);
		}
		
		// Create the RVM memory.
		memory = new Cell[size];
		
		// Initialize memory.
		for (int i = 0; i < size; i++) {
			memory[i] = new Cell();
		}
		
		// Create an empty process group.
		groups = new ArrayList<ProcessGroup>();
		
		// The next process group to execute.
		next = 0;
		
	}
	
	/**
	 * Load the specified program into the RVM for the specified
	 * player. First, find the process group for this player. If
	 * no process group exists, create a new one and add to the
	 * group array. Next, create the process and load the Redcode
	 * program into the process. Determine if the program can
	 * fit into the RVM memory, and if so, determine the start PC.
	 * Beginning at the PC, load the program into RVM memory.
	 * 
	 * @param file the Redcode program to load
	 * @param player the player this program belongs to
	 */
	public void loadProgram(File file, Integer player) {
		
		if (player < 1 || player > 9) {
			throw new IllegalArgumentException("Illegal player number: " + player + ". Value must be between 1 and 9.");
		}
		
		// Return the process group for this player. With a limited
		// number of players, this data structure is adequate.
		ProcessGroup pg = null;
		for (ProcessGroup group : groups) {
			if (group.getPlayer() == player) {
				pg = group;
				break;
			}
		}
		
		// No process group exists yet for this player, 
		// so create a new one.
		if (pg == null) {
			pg = new ProcessGroup(player, this);
			groups.add(pg);
		}
		
		// Create a process for this Redcode program.
		// The process will add itself to the group.
		WarriorProcess process = new WarriorProcess(file, pg);
		
		// get the instruction set.
		List<Integer> iset = process.getInstructions();
		
		// analyze blocks; update block list. want to locate
		// contiguous blocks of memory where the program will 
		// fit without overlapping another program and will
		// not run off the end of the memory.
		int blocksize = 0;
		int start = 0;
		boolean inBlock = false;
		List<Integer> blocks = new ArrayList<Integer>();
		for (int i = 0; i < memory.length; i++ ) {
			if (memory[i].getProcessGroup() == null) {
				// no owner
				if (inBlock) {
					blocksize++;
				} else {
					inBlock = true;
					start = i;
					blocksize = 1;
				}
			} else {
				// someone owns the cell
				if (inBlock) {
					inBlock = false;
					if (blocksize >= iset.size()) {
						// valid block, so save off block to array.
						for (int j = start; j <= (start + blocksize - iset.size()); j++) {
							blocks.add(j);
						}
					}
				} else {
					// not in a block, so do nothing
				}
			}
		}
		
		if (inBlock) {
			if (blocksize >= iset.size()) {
				// valid block, so save off block to array.
				for (int j = start; j <= (start + blocksize - iset.size()); j++) {
					blocks.add(j);
				}
			}
		}
		
		// After block checking, do any blocks exist to place the program?
		if (blocks.size() == 0) {
			throw new OutOfMemoryError("Not enough memory for program.");
		}
		
		// Find a random index into blocks array and return the
		// value which will be the starting PC for program.
		int idx = (int)(Math.random() * blocks.size());
		idx = blocks.get(idx);
		process.setPC(idx);
		
		// Load the program into memory
		for (Integer instruction : iset) {
			memory[idx].setProcessGroup(pg);
			memory[idx].setPID(process.getPID());
			memory[idx].setInstruction(instruction);
			idx++;
		}
		
	}
	
	/**
	 * Return the memory Cell at specified location.
	 * 
	 * @param loc
	 * @return
	 */
	public Cell getMemory(int loc) {
		if (loc < 0 || loc > memory.length) {
			throw new IndexOutOfBoundsException("Invalid memory location: " + loc);
		}
		return memory[loc];
	}
	
	/**
	 * @return
	 */
	public List<ProcessGroup> getProcessGroups() {
		return groups;
	}
	
	/**
	 * @return
	 */
	public int memsize() {
		return memory.length;
	}
	
	/**
	 * @return
	 */
	public int getNext() {
		return next;
	}
	
	/**
	 * Returns the number of runnable process groups in the RVM.
	 * 
	 * @return the number of runnable process groups in the RVM.
	 */
	public int getNPG() {
		int count = 0;
		for (ProcessGroup pg : groups) {
			if (pg.isRunnable()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @return
	 */
	public int getNPlayers() {
		return groups.size();
	}
	
	/**
	 * @param npg
	 * @return
	 */
	public int getNProcs(int npg) {
		for (ProcessGroup pg : groups) {
			if (pg.getPlayer() == npg) {
				return pg.getNumberOfRunnableProcesses();
			}
		}
		return 0;
	}
	
	/**
	 * @param npg
	 * @return
	 */
	public int score(int npg) {
		for (ProcessGroup pg : groups) {
			if (pg.getPlayer() == npg) {
				return pg.score();
			}
		}
		return 0;
	}
	
	/**
	 * Start the battle. Use a round robin to conduct battle.
	 * Returns the player who won the battle.
	 */
	public int battle(int cycles) {
		// cycle through each process in each process
		// group and add to a round robin array
		// so that if you have process groups PG1 and PG2,
		// and PG1 has P11, P12, P13 processes 
		// and PG2 has P21, P22 processes, the round robin
		// order would be P11, P21, P12, P22, P13, P21, 
		// P11, P22, P12, P21, P13, ...
		// round robin 'cycles' times
		int cycle = 0;
		while (cycle < cycles) {
			
			// DEBUG:
			System.out.print("CYCLE " + cycle + "  G" + (next+1) + ":");
			
			// Run the ProcessGroup execute method.
			exec();
			
			// Check for end of cycle.
			if (next == 0) {
				
				// End of this cycle, check if any process groups
				// are still running. if only 1, game over (return). 
				// if 0, then no one wins (return). if >1, continue.
				int npg = getNPG();
				if (npg == 0) {
					// DEBUG:
					System.out.println("*** ALL GROUPS ARE NO LONGER RUNNABLE ***");
					return 0;
				} else if (npg == 1) {
					// DEBUG: 
					System.out.println("We have a winner");
					// TODO:
					return 0;
				}
				
				cycle++;
				System.out.println(toString());
				
			}
			
		}
		
		if (cycles != 1) {
			// TODO: end of all cycles, see who is winner
			//for (ProcessGroup pgx : groups) {
			//	System.out.println(pgx.score());
			//}
		}
		
		return 0;
		
	}
	
	/**
	 * The ProcessGroup execute method.
	 */
	public void exec() {
		ProcessGroup pg = groups.get(next);
		if (pg.isRunnable()) {
			pg.exec();
		} else {
			// DEBUG:
			System.out.println(" GROUP IS NO LONGER RUNNABLE!");
		}
		next = (next == groups.size() - 1 ? 0 : next + 1);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < memory.length; i++) {
			if ((i + 1) % 50 == 0) {
				sb.append(memory[i].toString() + "\n");
			} else {
				sb.append(memory[i].toString() + " ");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * test harness
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RVM rvm = new RVM(200);
		
		rvm.loadProgram(new File("test/warrior3.war"), 1);
		rvm.loadProgram(new File("test/pingpong.war"), 2);
		//rvm.loadProgram(new File("test/Chunky.war"), 1);
		//rvm.loadProgram(new File("test/splat.war"), 2);
		//rvm.loadProgram(new File("test/warrior2.war"), 2);
		//rvm.loadProgram(new File("test/Random.war"), 3);
		System.out.println(rvm);
				
		rvm.battle(15);
	}

}
