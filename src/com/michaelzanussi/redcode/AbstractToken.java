package com.michaelzanussi.redcode;

/**
 * The <tt>AbstractToken</tt> class provides a minimal implementation of the 
 * <tt>Token</tt> interface.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public abstract class AbstractToken implements Token {

	/**
	 * The token.
	 */
	protected String token;
	
	/**
	 * Standard constructor.
	 * 
	 * @param token the contents of the token.
	 */
	public AbstractToken(String token) {
		
		// Do not allow null tokens.
		if (token == null) {
			throw new NullPointerException("Null tokens are not allowed.");
		}
		
		this.token = token;
		
	}
	
	/* (non-Javadoc)
	 * @see com.michaelzanussi.redcode.Token#getToken()
	 */
	public String getToken() { 
		
		return token; 
	
	}
	
}
