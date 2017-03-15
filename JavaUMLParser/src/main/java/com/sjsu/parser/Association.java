/**
 * 
 */
package com.sjsu.parser;

/**
 * To track the type of class associations
 * 
 * @author Meenakshi
 *
 */
public enum Association {
	
	ONE_TO_ONE(" \"1\" -- \"1\" "), 
	ONE_TO_MANY(" \"1\" -- \"*\" ");
	//MANY_TO_ONE(" \"*\" -- \"1\" "), 
	//MANY_TO_MANY(" \"*\" -- \"*\" ");
	
	private String symbol;
	
	Association(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	
}
