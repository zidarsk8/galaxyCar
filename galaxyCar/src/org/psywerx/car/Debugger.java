package org.psywerx.car;

import android.util.Log;

public class Debugger {
	private static final String DBGATAG = "cVERBOSE ";
	private static final String DBGITAG = "cINFO    ";
	private static final String DBGETAG = "cERROR   ";
	private static final String DBGSTAG = "cSTACK   ";
	protected static boolean showDBGV = true;
	protected static boolean showDBGI = true;
	protected static boolean showDBGE = true;
	protected static boolean showFullClassName = false;


	/**
	 * print verbose debug info
	 * @param m debug message
	 */
	public static void dbgv(String m){
		if (!showDBGV) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		String className = showFullClassName ? e.getClassName() : e.getClassName().substring(e.getClassName().lastIndexOf("."));
		Log.v(DBGATAG+"("+className+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print info
	 * @param m error message
	 */
	public static void dbge(String m){
		if (!showDBGE) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.e(DBGETAG+"("+e.getClassName()+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print error
	 * @param m error message
	 */
	public static void dbgi(String m){
		if (!showDBGI) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.i(DBGITAG+"("+e.getClassName()+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print top stack element
	 */
	public static void printStack() {
		if (!showDBGE) return;
		StackTraceElement[] stack = new Exception().getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			Log.v(DBGSTAG,  stack[i].toString());
		}
	}
	/**
	 * print whole stack
	 * @param m error message
	 */
	public static void printStack(Exception e) {
		if (!showDBGE) return;
		StackTraceElement[] stack = e.getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			Log.v(DBGSTAG,  stack[i].toString());
		}
	}
}
