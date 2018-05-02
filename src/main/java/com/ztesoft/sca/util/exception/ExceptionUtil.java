package com.ztesoft.sca.util.exception;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicLong;


public class ExceptionUtil {
	public static AtomicLong logTimes = new AtomicLong(0L);
	public static String getMessage(Throwable ex) {
		StringWriter sw = null;
		PrintWriter pw = null;
		String stacktrace = "";
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			stacktrace = sw.toString();
		} catch (Exception e1) {
			ex.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stacktrace;

	}
	/**
	 * 每隔100次打印错误描述信息及stacktrace。不足100次时，只打印错误描述信息
	 * @param log
	 * @param e
	 * @param errorMsg
	 */
	public static void printException(Logger log,Throwable e,String errorMsg){
		if(logTimes.incrementAndGet()%100==0){
			log.error(errorMsg,e);
		}else{
			log.error(errorMsg);
		}
	}
}
