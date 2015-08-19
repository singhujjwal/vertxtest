package com.mydomain;



interface intefaceddd{
	public int foo();
}

interface myinterface3{
	public void strFunc(String s);
}

interface MyInterface {
	public int add(int x, int y);
}

public class JavaTest {
	

	public static void main(String[] args) throws Exception {
		
		MyInterface m = (int x, int y) -> x + y;
		
		
		intefaceddd n = () -> 42;
		
		myinterface3 o = (String s) -> {
			System.out.println("Hello World!!");
		};
		
//		someFunction(4, new MyInterface() {
//			public int add(int x, int y) {
//				// TODO Auto-generated method stub
//				return x+y;
//			}
//		});
	
		
		Runnable r = () -> {
				System.out.println("Thread running");
		};
//		Thread t = new Thread(r);
//		t.start();
//		
//		
//		Thread.sleep(1000);
//		
//		t.join();
		r.run();
		
		r.run();
		
		Thread t2 = new Thread( () -> {
			
			System.out.println("Something");
			
		});
		
		t2.start();
		Thread.sleep(2000);
		t2.join();
		
	}
}
