package com.michaelzanussi.redcode;

/**
 * A bad instruction exception indicating problems were encountered during
 * instruction object instantiation. Encompasses the following exceptions:
 * <code>ClassNotFoundException</code>, <code>IllegalAccessException</code>,
 * and <code>IntantiationException</code>.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public class BadInstructionException extends Exception {

	/**
	 * Because: It is strongly recommended that all serializable
	 * classes explicitly declare serialVersionUID values.
	 */
	private static final long serialVersionUID = 5785823256442211560L;

	/**
	 * Standard constructor.
	 * 
	 * @param message the exception message.
	 */
	public BadInstructionException(String message) {
		
		super(message);
		
	}
	
}
