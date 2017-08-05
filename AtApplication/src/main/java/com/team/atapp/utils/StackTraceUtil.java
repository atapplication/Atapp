
package com.team.atapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StackTraceUtil {

	private StackTraceUtil(){}
	
	public static String constructStackTrace(Throwable t) {		
		ByteArrayOutputStream stack = new ByteArrayOutputStream();
		PrintStream eStream = new PrintStream(stack);
		t.printStackTrace(eStream);
		eStream.close(); 
		return stack.toString();		
	}
}
