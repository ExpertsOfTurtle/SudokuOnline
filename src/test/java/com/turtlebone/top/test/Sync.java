package com.turtlebone.top.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class Sync {
	public static Map<Integer, Integer> map = new HashMap<>();
	public static Integer[] arr;
	static {
		arr = new Integer[100];
		for (int i = 0; i < 100; i++) {
			arr[i] = new Integer(0);
		}
	}
	@Test
	public void t1() {
		Integer x = new Integer(1);
		Integer y = new Integer(1);
		String s1 = String.format("%d", 1);
		String s2 = String.format("%d", 1);
		System.out.println(s1 == s2);
		StringBuffer sb1 = new StringBuffer(s1);
		StringBuffer sb2 = new StringBuffer(s2);
		System.out.println(sb1 == sb2);
	}
	@Test
	public void test () throws Exception {
		Thread t1 = new Thread(new Runnable(){
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					doSomething(3);
					
				}
			}});
		Thread t2 = new Thread(new Runnable(){
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					doSomething(5);
					
				}
			}});
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println("DONE");
	}
	
	public void doSomething(Integer id) {
		synchronized(arr[id]) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Integer x = map.get(id);
			if (x == null) {
				x = new Integer(1);
			} else {
				x++;
			}
			map.put(id, x);
			System.out.println(id + "," + x);
		}
	}
}
