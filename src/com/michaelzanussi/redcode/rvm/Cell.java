package com.michaelzanussi.redcode.rvm;

/**
 * Represents each cell of the RVM's RAM. Each cell contains
 * an instruction, the owner, and a process id.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (8 April 2016)
 */
public class Cell {

	private ProcessGroup pg;
	private int pid;
	private Integer instruction;
	
	/**
	 * 
	 */
	public Cell() {
		this.pg = null;
		this.pid = 0;
		this.instruction = 13;	// hlt
	}
	
	/**
	 * @return
	 */
	public ProcessGroup getProcessGroup() {
		return this.pg;
	}
	
	/**
	 * @param pg
	 */
	public void setProcessGroup(ProcessGroup pg) {
		this.pg = pg;
	}
	
	/**
	 * @return
	 */
	public int getPID() {
		return this.pid;
	}
	
	/**
	 * @param pid
	 */
	public void setPID(int pid) {
		this.pid = pid;
	}
	
	/**
	 * @return
	 */
	public Integer getInstruction() {
		return this.instruction;
	}
	
	/**
	 * @param instruction
	 */
	public void setInstruction(Integer instruction) {
		this.instruction = instruction;
	}
	
	/**
	 * @return
	 */
	public Integer getWord() {
		return getInstruction();
	}
	
	/**
	 * @param word
	 */
	public void setWord(Integer word) {
		setInstruction(word);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (pg == null ? "0" : pg.getPlayer().toString());
	}
	
}
