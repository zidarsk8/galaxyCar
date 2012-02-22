package org.psywerx.car;

import android.util.Log;

public class D { 
	private static final String TAG = "DEBUG ";
	protected static boolean showDBGV = true;
	protected static boolean showDBGI = true;
	protected static boolean showDBGD = true;
	protected static boolean showDBGW = true;
	protected static boolean showDBGE = true;
	protected static boolean showFullClassName = false;


	/** 
	 * print verbose
	 * @param m debug message
	 */
	public static void dbgv(String m){ 
		if (!showDBGV) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.v(TAG+"("+getName(e)+")", 
				e.getMethodName()+" : "+m);
	}   

	/** 
	 * print info
	 * @param m debug message
	 */
	public static void dbgi(String m){
		if (!showDBGI) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.i(TAG+"("+getName(e)+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print debug
	 * @param m error message
	 */
	public static void dbgd(String m){
		if (!showDBGE) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.d(TAG+"("+getName(e)+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print warning
	 * @param m debug message
	 */
	public static void dbgw(String m){
		if (!showDBGW) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.w(TAG+"("+getName(e)+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print error
	 * @param m error message
	 */
	public static void dbge(String m){
		if (!showDBGE) return;
		StackTraceElement e = new Exception().getStackTrace()[1];
		Log.e(TAG+"("+getName(e)+")", 
				e.getMethodName()+" : "+m);
	}

	/**
	 * print error
	 * @param m error message
	 */
	public static void dbge(String m, Exception e){
		if (!showDBGE) return;
		Log.e(TAG,e.toString());
		printStack(e);
	}

	/**
	 * print top stack element
	 */
	public static void printStack() {
		if (!showDBGE) return;
		StackTraceElement[] stack = new Exception().getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			Log.v(TAG,  stack[i].toString());
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
			Log.v(TAG,  stack[i].toString());
		}
	}
	
	private static String getName(StackTraceElement e){
		return showFullClassName ? e.getClassName() : e.getClassName().substring(e.getClassName().lastIndexOf("."));
	}
}
