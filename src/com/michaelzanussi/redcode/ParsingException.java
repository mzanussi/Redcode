package com.michaelzanussi.redcode;

/**
 * A parsing exception indicating problems were encountered during
 * a parsing session, usually indicating something we weren't 
 * expecting was encountered.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class ParsingException extends Exception {

	/**
	 * Because: It is strongly recommended that all serializable
	 * classes explicitly declare serialVersionUID values.
	 */
	private static final long serialVersionUID = 1504232237399258570L;

	/**
	 * Standard constructor.
	 * 
	 * @param message the exception message.
	 */
	public ParsingException(String message) {
		
		super(message);
		
	}
	
}
