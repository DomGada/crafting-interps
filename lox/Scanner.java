package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
//Questions for this.

//What is a token within the scope of a Scanner?
//What is a Scanner?
//What is an operator?
//What is a lexeme?




class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	// Information available for the line numbers and array index.
	private int start = 0;
	private int current = 0;
	private int line = 1;


	Scanner(String source){
		this.source = source;
	}

	List<Token> scanTokens(){
		while(!isAtEnd()){
			start = current;
			scanToken();
		}

		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}
	
	private void scanTokens() {
		char c = advance();
		switch(c) {
			default: 
				if (isDigit(c)){
					number();
				} else if (isAlpha(c)){
					identifier();
				}
				else {
					Lox.error(line, "Ay thats not a character we know lil bro");
					break;
				}
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '{': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': addToken(MINUS); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case '*': addToken(STAR); break;
			case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
			case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
			case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
			case '>': addToken(match('=') ? GREATER_EQUAL: GREATER); break;
			case '/': 
				if (match('/')){
					while(peek() != '\n' && !isAtEnd()) advance();

				}
				else if (match('*')){
					c_comment();
				}
				else {
				addToken(SLASH);
				}
				break;
			case ' ':
			case '\r':
			case '\t':
				break;
			
			case '\n':
				line++;
				break;
			case '"':
				string(); break;

					}

	}
	
	private void identifier(){
		while(isAlphaNumeric(peek())) advance();

		addToken(IDENTIFIER);
	}

	private void c_comment(){
		while(peek() =/= '*' && peekNext() =/= '/' && !isAtEnd()){
			if(peek() == '\n') line++;
			advance();
		}
	}

	private void number(){
		while(isDigit(peak())) advance();
		
		if(peek() == '.' && isDigit(peekNext())){
			advance();
			while(isDigit(peek())) advance();
		}
		addToken(NUMBER, Double.parseDouble(source.substring(start,current)));
	}

	private void string(){
		while(peek() != '"' && !isAtEnd()){
			if(peek() == '\n') line++;
			advance();
		}
		
		if(isAtEnd()){
			Lox.error(line, "Unterminated string.");
			return;
		}
		advance();
		// Start is always equivalent to where current also started
		// current is itterating, and thus when it reachs the end it will be at the quote
		// we want everything between the two quotes to produce the acutal string value
		String value = source.substring(start+1, current-1);
		addToken(STRING, value);
	}
	private boolean match(char expected){
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}

	private char peek(){
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}
	private char peekNext(){
		if(current + 1 >= source.length()) return '\0';
		return source.charAt(current+1);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c<= 'z') || (c <= 'A' && c >= 'Z') || c == '_';
	}
	
	private boolen isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private boolean isDigit(char c){
		return c >= '0' && c <= '9';
	}
	
	private boolean isAtEnd(){
		return current >= source.length();
	}

	private char advance(){
		return source.charAt(current++);
	}

	private void addToken(TokenType type){
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal){
		String text = source.substring(start,current);
		tokens.add(new Token(type, text, literal, line));
	}
	

}

