package com.michaelzanussi.redcode;

/**
 * An interface for a standard token extracted by a lexer. The single
 * method contained in the interface, <code>getToken()</code> returns
 * the string representation of the token.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public interface Token {

	/**
	 * Returns the string representation of the token.
	 * 
	 * @return the token.
	 */
	public String getToken();
	
}
