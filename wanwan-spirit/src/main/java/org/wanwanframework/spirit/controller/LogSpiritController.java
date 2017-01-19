package org.wanwanframework.spirit.controller;

import java.util.logging.Logger;

public class LogSpiritController {
	private static final Logger logger = Logger.getLogger("" + LogSpiritController.class);
	
	private String className;
	
	private String word = "private static final Logger logger = Logger.getLogger(\"\" + @className.class);";
	
	private String getContent(String className, String word) {
		return word.replaceAll(className, word);
	}
	
	private boolean isHave(String word, String content) {
		if(content.indexOf(word) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		
	}
}
