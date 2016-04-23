package com.michaelzanussi.redcode.rvm;

import java.util.ArrayList;
import java.util.List;

import com.michaelzanussi.redcode.RedcodeInstruction;

/**
 * The ProcessGroup, one for each player. The ProcessGroup contains
 * one or more Processes, one for each Redcode warrior loaded.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (8 April 2016)
 */
public class ProcessGroup {

	private Integer player;
	private List<WarriorProcess> processes;
	private int next;
	private int lastProcNo;
	private RVM rvm;
	
	/**
	 * @param player
	 * @param rvm
	 */
	public ProcessGroup(Integer player, RVM rvm) {
		
		if (player != null && (player < 1 || player > 9)) {
			throw new IllegalArgumentException("Illegal player number: " + player);
		}
		
		if (rvm == null) {
			throw new NullPointerException("No RVM specified.");
		}
		
		this.player = player;
		this.rvm = rvm;
		processes = new ArrayList<WarriorProcess>();
		next = 0;
		lastProcNo = 0;
	}
	
	/**
	 * @return
	 */
	public Integer getPlayer() {
		return player;
	}
	
	/**
	 * @param process
	 */
	public void addProcess(WarriorProcess process) {
		processes.add(process);
	}
	
	/**
	 * @return
	 */
	public int getNext() {
		return next;
	}
	
	/**
	 * @return
	 */
	public int getLastProcNo() {
		return lastProcNo;
	}

	/**
	 * @return
	 */
	public int getNumberOfProcesses() {
		return processes.size();
	}
	
	/**
	 * @return
	 */
	public int getNumberOfRunnableProcesses() {
		int count = 0;
		for (WarriorProcess process : processes) {
			if (process.isRunnable()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @return
	 */
	public List<WarriorProcess> getProcesses() {
		return processes;
	}
	
	/**
	 * @return
	 */
	public int score() {
		int score = 0;
		for (int i = 0; i < rvm.memsize(); i++) {
			if (rvm.getMemory(i).getProcessGroup() != null) {
				if (rvm.getMemory(i).getProcessGroup().getPlayer() == player) {
					score++;
				}
			}
		}
		return score;
	}
	
	/**
	 * A process group is considered runnable if any of its
	 * processes is still runnable.
	 * 
	 * @return
	 */
	public boolean isRunnable() {
		for (WarriorProcess process : processes) {
			if (process.isRunnable()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public boolean exec() {
		
		int count = 0;
		
		while (count < processes.size()) {
			
			WarriorProcess process = processes.get(next);
			
			if (process.isRunnable()) {
				
				// DEBUG:
				System.out.println("p" + (next+1));
				
				Integer instruction = null;
				
				try {
					// get pc for next instruction to execute
					int pc = process.getPC();
					
					// DEBUG:
					System.out.print("\t\tpc:" + pc + " ");
					
					// retrieve cell at memory location point to by pc
					Cell cell = rvm.getMemory(pc);
					
					// DEBUG:
					System.out.print("owner:" + (cell.getProcessGroup() == null ? 0 : cell.getProcessGroup().getPlayer()) + " ");
					
					// extract the instruction to execute
					instruction = cell.getInstruction();
					
					// Check if cell is owned by another player.
					// If not, then halt the process.
					if (cell.getProcessGroup() != null) {
						Integer cp = cell.getProcessGroup().getPlayer();
						if (cp != player) {
							instruction = 13;	// hlt
						}
					}
					
				} catch (IndexOutOfBoundsException e) {
					// An attempt to access invalid memory has
					// occurred, halt the process.
					instruction = 13;	// hlt
				}
				
				// DEBUG:
				System.out.print("inst:" + instruction + " ");
				
				// return the Redcode instruction
				RedcodeInstruction rci = process.getInstruction(instruction);
				
				// DEBUG:
				System.out.print(rci);
				
				lastProcNo = next;		// for test app
				
				// now execute the instruction!
				rci.exec(process, rvm);
				next = (next == processes.size() - 1 ? 0 : next + 1);
				
				// DEBUG:
				System.out.println();
				
				process.dump(); System.out.println();
				
				return true;
				
			} else {
				
				// DEBUG:
				System.out.print("p" + (next+1));
				
				// DEBUG:
				System.out.println("\t\tNO LONGER RUNNABLE!");
				
				count++;
				next = (next == processes.size() - 1 ? 0 : next + 1);
				
			}
			
		}
		
		// Cycled through all processes and none were runnable.
		return false;
		
	}
	
}
