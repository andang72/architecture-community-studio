package tests;

import java.lang.reflect.Constructor;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonTest {  

	public static void main(String[] args) { 
		new SingletonTest().test();
	}
	
	private static Logger log = LoggerFactory.getLogger(SingletonTest.class);
	
	@Test
	public void testSingleton()  {
		Object instance = Singleton.getInstance();
		log.debug("sigleton instance : " + instance.toString());  
	}

	public void test() {
		System.out.println("start sigleton test.");
		Singleton instance = Singleton.getInstance();
		log.debug("sigleton instance : " + instance.hashCode()); 
		Singleton instance2 = null;
		try {
            Constructor[] cstr = Singleton.class.getDeclaredConstructors();
            for (Constructor constructor: cstr) { 
                constructor.setAccessible(true);
                instance2 = (Singleton) constructor.newInstance();
                break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
		log.debug("sigleton instance : " + instance2.hashCode()); 
		
		
	};
	
//	public void test() {
//		System.out.println("start sigleton test.");
//		new MultiThread("thread01").start();
//		new MultiThread("thread02").start();
//		new MultiThread("thread03").start();
//	};
	
	public class MultiThread extends Thread { 
		
		private String name;  
		
		public MultiThread(String name) {
		   this.name = name;
		} 
		
		public void run() { 
	    	int count = 0; 
	    	for(int i=0; i<5; i++) {
	    		Singleton singleton = Singleton.getInstance(); 
	    		System.out.println( name + " : " + singleton.toString());	 
	    		try {	
	    			Thread.sleep(400);
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	    }
	}
}

