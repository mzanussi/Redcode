package com.michaelzanussi.redcode;

import java.util.Stack;
import java.io.Reader;

/**
 * The <tt>AbstractLexer</tt> class provides a minimal implementation of the 
 * <tt>Lexer</tt> interface.
 *
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public abstract class AbstractLexer implements Lexer {

	/**
	 * 	Are there any more tokens available?
	 */
	protected boolean avail;
	
	/**
	 * 	The input stream.
	 */
	protected Reader in;
	
	/**
	 * The pushback buffer.
	 */
	protected Stack<Token> pushBackBuffer;

	/**
	 * The current state of the token.
	 */
	protected int state;
	
	/**
	 * The current token being constructed.
	 */
	protected StringBuilder token;
	
	/**
	 * No-arg constructor.
	 */
	public AbstractLexer() {
		
		avail = true;
		in = null;
		pushBackBuffer = new Stack<Token>();
		state = -1;
		token = new StringBuilder();
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.Lexer#hasMoreTokens()
	 */
	public boolean hasMoreTokens() {
		
		return avail;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.Lexer#nextToken()
	 */
	public abstract Token nextToken();
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.Lexer#pushBack(com.michaelzanussi.redcode.Token)
	 */
	public void pushBack(Token t) {
		
		// Verify the token isn't null.
		if (t == null) {
			throw new NullPointerException("Token to push back cannot be null.");
		}
		
		pushBackBuffer.push(t);
		
	}
	
}
