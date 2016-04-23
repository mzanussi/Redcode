package com.michaelzanussi.redcode;

import java.io.Reader;
import java.io.IOException;

/**
 * A table-driven lexer for parsing the Redcode assembly language. <p>
 * 
 * Input consists of 7-bit ASCII characters, valid input characters being
 * digits, letters or the punctuation '$', '-', '(' and ')'. Output is any 
 * sequence of characters delimited by whitespace or other non-valid 
 * punctuation. 
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (10 May 2004) 
 */
public final class RedcodeLexer extends AbstractLexer {

	/**
	 * Parsing state - currently not parsing anything.
	 */
	private static final int STATE_NONE = 0;
	
	/**
	 * Parsing state - currently parsing an opcode.
	 */
	private static final int STATE_OPCODE = 1;
	
	/**
	 * Parsing state - currently parsing an argument.
	 */
	private static final int STATE_ARG = 2;

	/**
	 * Parsing state - currently parsing a symbol.
	 */
	private static final int STATE_SYMBOL = 3;
	
	/**
	 * Standard constructor.
	 * 
	 * @param in the input stream.
	 */
	public RedcodeLexer(Reader in) {

		// Call the superclass's constructor.
		super();
		
		// Did the user specify a valid reader?
		if (in == null) {
			throw new NullPointerException("RedcodeLexer requires a reader.");
		}
		
		this.in = in;
		state = STATE_NONE;
		
	}

	/**
	 * Returns the next token in the token stream. <p>
	 * 
	 * The building of a token is accomplished by extracting single characters 
	 * from the input stream and building the tokens based on a finite state 
	 * machine (FSM) state table. Whereas a two-dimensional array works wonderful 
	 * for representing the state table, this implementation uses a series of
	 * if-else statements wrapped around a switch statement. Each if-else 
	 * statement corresponds to the current input character being parsed and the
	 * switch statement the current state the machine is in. Once reached, a
	 * corresponding "action" is performed and a new state is selected.<p>
	 * 
	 * The if-else/switch construct is especially useful when debugging in an
	 * IDE such as Eclipse or JBuilder because the debugger steps directly into
	 * the construct. Since the state tables are generally not very large, 
	 * performance doesn't take that much of a hit when compared to an array 
	 * representation, for example. 
	 * 
	 * @return the next token in the token stream.
	 */
	public Token nextToken() {
		
		// If there's a token already on the pushback buffer, 
		// pop and return that token first.
		if (!pushBackBuffer.isEmpty()) {
			return pushBackBuffer.pop();
		}
		
		// We'll never miss an exit point from this "endless" loop, 
		// so it's okay to block here.
		while (true) {
			
			// Read in the next character and test for EOF. If not EOF,
			// convert the integer to a character. If EOF, set availability 
			// and then return the token (as it currently exists).
			
			int i = 0;
			
			try {
				i = in.read();
			} catch (IOException e) {
				System.err.println("ERROR: " + e.getMessage());
			}

			if (i < 0) {
				avail = false;
				return new RedcodeToken(token.toString());
			}
			char ch = (char)i;
			
			// Input character is a dollar sign, signifying a register follows.
			// The output WILL contain the '$' so as to allow the individual
			// parsers to deal with it as they see fit. 
			
			if (ch == '$') {
				// Switch on the current state...
				switch (state) {
					case STATE_NONE:
						// Append
						actionAppend(ch, STATE_ARG);
						break;
					case STATE_OPCODE:
						// Save, Return
						return actionSaveReturn(ch, STATE_OPCODE);
					case STATE_ARG:
						// Append
						actionAppend(ch, STATE_ARG);
						break;
					case STATE_SYMBOL:
						// Save, Return
						return actionSaveReturn(ch, STATE_ARG);
				}
			}
			
			// Input character is either the open or closing parentheses,
			// which encompass an address register. The output only contains 
			// the symbol, and not the offset, which is returned later as
			// an integral value.
			
			else if (ch == '(' || ch == ')') {
				// Switch on the current state...
				switch (state) {
					case STATE_NONE:
						// Append
						actionAppend(ch, STATE_SYMBOL);
						break;
					case STATE_OPCODE:
						// Save, Return
						return actionSaveReturn(ch, STATE_SYMBOL);
					case STATE_ARG:
						// Save, Return
						return actionSaveReturn(ch, STATE_SYMBOL);
					case STATE_SYMBOL:
						// Save, Return
						return actionSaveReturn(ch, STATE_SYMBOL);
				}
			}
			
			// Input character is a digit (0-9) or the minus sign. The 
			// digit and minus sign only occurs within an offset argument.
			
			else if (Character.isDigit(ch) || ch == '-') {
				// Switch on the current state...
				switch (state) {
					case STATE_NONE:
						// Append
						actionAppend(ch, STATE_ARG);
						break;
					case STATE_OPCODE:
						// Save, Return
						return actionSaveReturn(ch, STATE_OPCODE);
					case STATE_ARG:
						// Append
						actionAppend(ch, STATE_ARG);
						break;
					case STATE_SYMBOL:
						// Save, Return
						return actionSaveReturn(ch, STATE_ARG);
				}
			}
			
			// Input character is an alphabetic character (A-Z, a-z).
			// An alphabetic character can be located in either an
			// opcode or an argument.
			
			else if (Character.isLetter(ch)) {
				// Switch on the current state...
				switch (state) {
					case STATE_NONE:
						// Append
						actionAppend(ch, STATE_OPCODE);
						break;
					case STATE_OPCODE:
						// Append
						actionAppend(ch, STATE_OPCODE);
						break;
					case STATE_ARG:
						// Append
						actionAppend(ch, STATE_ARG);
						break;
					case STATE_SYMBOL:
						// Save, Return
						actionSaveReturn(ch, STATE_OPCODE);
				}
			}
			
			// Whatever else that has not been handled will be treated 
			// as a terminating character.
			
			else {
				// Switch on the current state...
				switch (state) {
					case STATE_NONE:
						// Ignore
						break;
					case STATE_OPCODE:
						// Ignore, Return
						return actionIgnoreReturn(STATE_NONE);
					case STATE_ARG:
						// Ignore, Return
						return actionIgnoreReturn(STATE_NONE);
					case STATE_SYMBOL:
						// Ignore, Return
						return actionIgnoreReturn(STATE_NONE);
						
				}			
				
			}
		
		}
		
	}
	
	/**
	 * Helper function. Appends the current character to the token, then
	 * continues parsing at the next state.
	 * 
	 * @param ch the current character.
	 * @param state the next state.
	 * @return the current token.
	 */
	private void actionAppend(char ch, int state) {
		
		// Is this a valid state?
		if (state < 0) {
			throw new IllegalArgumentException("Invalid state specified for the Append action: " + state);
		}
		
		// Append current character to token.
		token.append(ch);
		
		// Set the next state.
		this.state = state;
		
	}
	
	/**
	 * Helper function. Ignores (drops) the current character, then
	 * returns the current token.
	 * 
	 * @param state the next state.
	 * @return the current token.
	 */
	private Token actionIgnoreReturn(int state) {

		// Is this a valid state?
		if (state < 0) {
			throw new IllegalArgumentException("Invalid state specified for the IgnoreReturn action: " + state);
		}
		
		// Save the token.
		Token newToken = new RedcodeToken(token.toString());
		
		// Empty the contents of the current token.
		token.delete(0, token.length());
		
		// Set the next state.
		this.state = state;
		
		return newToken;
		
	}

	/**
	 * Helper function. Appends the current character to a new token, then
	 * returns the previous token.
	 * 
	 * @param ch the current character.
	 * @param state the next state.
	 * @return the current token.
	 */
	private Token actionSaveReturn(char ch, int state) {

		// Is this a valid state?
		if (state < 0) {
			throw new IllegalArgumentException("Invalid state specified for the SaveReturn action: " + state);
		}
		
		// Save the token.
		Token newToken = new RedcodeToken(token.toString());
		
		// Empty the contents of the current token.
		token.delete(0, token.length());
		
		// Append the current character to token.
		token.append(ch);
		
		// Set the next state.
		this.state = state;
		
		return newToken;
		
	}
	
}
