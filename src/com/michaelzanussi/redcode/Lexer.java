package com.michaelzanussi.redcode;

/**
 * An interface for a generic lexer which loosely mimics the interface to
 * <code>StringTokenizer</code>. Unlike <code>StringTokenizer</code>,
 * this interface also defines a <code>pushBack()</code> method which pushes a 
 * single token back onto the token stream.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public interface Lexer {

	/**
	 * Determines if there are any more tokens available in this lexer.
	 * 
	 * @return <code>true</code> if there are more tokens, otherwise
	 * <code>false</code>.
	 */
	public boolean hasMoreTokens();
	
	/**
	 * Returns the next token in the token stream.
	 * 
	 * @return the next token in the token stream.
	 */
	public Token nextToken();
	
	/**
	 * Pushes back a single token onto the token stream.
	 * 
	 * @param t the token to push back.
	 */
	public void pushBack(Token t);
	
}
