package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Administrator
 *
 * 获取文件路径
 * http://blog.csdn.net/xuweilinjijis/article/details/8691442
 */
public class Test {

	public static void main(String[] args) {
		String [] arg = {"/wordcount/wordcount.txt",""};
		try {
			
			String relativelyPath=System.getProperty("user.dir");
			File file = new File(relativelyPath+arg[0]);
			System.out.println(file.getName());
			FileInputStream fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
