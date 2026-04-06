package com.arracso.ElfneinBot.command.message;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.arracso.ElfneinBot.util.Global;
import com.arracso.ElfneinBot.util.Util;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class MathCommand extends MessageCommand {

	public MathCommand(){
		commandId = Global.cmdIdMath;
	}
	
	@Override
	public Boolean check(Message message){
		// Avoid bots (also not allow herself to keep repeating)
		if(message.getAuthor().isPresent() && message.getAuthor().get().isBot()) return false;
		
		return hasOperator(message.getContent()) && isMathExpression(message.getContent());
	}
	
	@Override
	public Mono<Void> execute(Message message) {
		Double solution = solveMathExpression(message.getContent());
		if(solution == null) {
			return Util.replyToMessage(message, "Sorry. I cannot solve this.\n# " + Global.elf_cry).then();
		}
		
		String sol = formatDecimal(solution);
		sol = sol.replaceAll("\\.0$", "");
		
		return Util.replyToMessage(message, sol).then();
	}
	
    
	///////////////////
	
	
	 public static String formatDecimal(double value) {
		 if (Double.isNaN(value)) return "Sorry. I cannot solve this.\n# " + Global.elf_cry;
		 if (Double.isInfinite(value)) return value > 0 ? "Aproach infinity!" : "Aproach hell!";
		 
		 BigDecimal bd = BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP); // Round to 4 decimal places
		 return bd.stripTrailingZeros().toPlainString(); 
	 }

    /**
     * Checks if the provided expression is a valid JavaScript expression.
     *
     * @param expr the expression string to check
     * @return true if valid; false otherwise.
     */
    private static boolean isMathExpression(String expr) {
    	// Trim whitespaces
    	expr = expr.trim();
    	if(expr.isEmpty()) return false;
    	
    	// Check first part of expression
    	int pos = 0;
    	if(expr.charAt(0) == '(') {
    		pos = getEnclosingParenthesis(expr,0);
    		if(pos == -1) return false;
    		if(!isMathExpression(expr.substring(1, pos))) return false;
    		pos++;
    	} else if(isDigit(expr.charAt(0))) {
    		pos = getEndNumber(expr,0);
    		if(pos == -1) return false;
    		pos++;
    	}
    	
    	// Jump spaces
    	while(pos < expr.length() && expr.charAt(pos)==' ') pos++;
    	
    	// Check if expression still continues
    	if(pos < expr.length()) {
    		if(!isOperator(expr.charAt(pos))) return false;
    		return isMathExpression(expr.substring(pos+1,expr.length()));
    	}
    	
    	return true;
    }
    
    
    /**
     * 
     * @param c
     * @return
     */
	private static boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
	}
	
	private boolean hasOperator(String expr) {
		int i = 1;
		boolean trobat = false;
		while(!trobat && i<expr.length()) {
			trobat = isOperator(expr.charAt(i));
			i++;
		}
		return trobat;
	}

	/**
     * 
     * @param expr the expression string to check
     * @param i the position of the first digit
     * @return
     */
    private static int getEndNumber(String expr, int i) {
    	if(i<expr.length() && isDigit(expr.charAt(i))) {
    		while(i<expr.length() && isDigit(expr.charAt(i))) i++;
    		if(i<expr.length() && expr.charAt(i) == '.') {
    			i++;
    			while(i<expr.length() && isDigit(expr.charAt(i))) i++;
    		}	
    		return i-1;
    	}
		return -1;
	}

	/**
     * Checks if a character is a digit number from 0 to 9
     * 
     * @param c the character to check
     * @return true if char is a digit; false otherwise
     */
    private static boolean isDigit(char c) {
		return '0' <= c && c <= '9' ;
	}

	/**
     * Checks for structure () validity and gets position of enclosing parenthesis.
     * 
     * @param expr the expression string to check
     * @param i the position of the first parenthesis
     * @return return the position of the enclosing ')' if its within a valid structure.
     */
    private static int getEnclosingParenthesis(String expr, int i) {
    	if(expr.charAt(i) == '(') {
    		int count = 1;
    		while(count>0 && i<expr.length()) {
    			i++;
    			if(expr.charAt(i) == '(') count++;
    			else if(expr.charAt(i) == ')') count--;
    		}
    		if(count == 0) return i;
    	}
		return -1;
	}

	/**
     * Evaluates the given JavaScript expression.
     *
     * If the expression contains multiple statements (e.g. separated by semicolons),
     * the engine executes them sequentially and returns the value of the last statement.
     *
     * @param expr the expression (or block of expressions) to evaluate.
     * @return the result of the evaluation, or null if an error occurs.
     */
    private static Double solveMathExpression(String expr) {
    	try {
        	// Trim whitespaces
        	expr = expr.trim();
        	
	    	// Search for last outer + or -
	    	int parLv = 0;
	    	int pos = expr.length();
	    	boolean trobat = false;
	    	while(!trobat && pos>0) {
	    		pos--;
	    		if(expr.charAt(pos)=='(') parLv--;
	    		else if(expr.charAt(pos)==')') parLv++;
	    		else if(parLv == 0 && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')) trobat = true;
	    	}
	    	if(pos!=0) {
	    		if(expr.charAt(pos) == '+') return solveMathExpression(expr.substring(0, pos)) + solveMathExpression(expr.substring(pos+1, expr.length()));
	    		else return solveMathExpression(expr.substring(0, pos)) - solveMathExpression(expr.substring(pos+1, expr.length()));
	    	}
	    	
	    	// Search for last outer * or /
	    	parLv = 0;
	    	pos = expr.length();
	    	trobat = false;
	    	while(!trobat && pos>0) {
	    		pos--;
	    		if(expr.charAt(pos)=='(') parLv--;
	    		else if(expr.charAt(pos)==')') parLv++;
	    		else if(parLv == 0 && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/')) trobat = true;
	    	}
	    	if(pos!=0) {
	    		if(expr.charAt(pos) == '*') return solveMathExpression(expr.substring(0, pos)) * solveMathExpression(expr.substring(pos+1, expr.length()));
	    		else return solveMathExpression(expr.substring(0, pos)) / solveMathExpression(expr.substring(pos+1, expr.length()));
	    	}
	    	
	    	// Search for last outer ^
	    	parLv = 0;
	    	pos = expr.length();
	    	trobat = false;
	    	while(!trobat && pos>0) {
	    		pos--;
	    		if(expr.charAt(pos)=='(') parLv--;
	    		else if(expr.charAt(pos)==')') parLv++;
	    		else if(parLv == 0 && (expr.charAt(pos) == '^')) trobat = true;
	    	}
	    	if(pos!=0) {
	    		if(expr.charAt(pos) == '^') return Math.pow(solveMathExpression(expr.substring(0, pos)), solveMathExpression(expr.substring(pos+1, expr.length())));
	    		else return solveMathExpression(expr.substring(0, pos)) / solveMathExpression(expr.substring(pos+1, expr.length()));
	    	}

	    	// Check parenthesis, - or + at begining
	    	if(expr.charAt(0) == '(') return solveMathExpression(expr.substring(1, expr.length()-1));
	    	if(expr.charAt(0) == '+') return solveMathExpression(expr.substring(1, expr.length()));
	    	if(expr.charAt(0) == '-') return -solveMathExpression(expr.substring(1, expr.length()));
	    	
	    	// Get number literal
    		return Double.parseDouble(expr);
    		
    	} catch(Exception e) {
    		return null;
    	}
        
    }


}
