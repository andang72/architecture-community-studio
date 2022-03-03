package tests;

public class PolymorphismTest {
	public static void main(String[] args) {
	      SuperClass var = new SubClass(); 
	      var.methodA(); 
	      var.staticMethodA(); 
	   }

}

class SuperClass {
    
	public void methodA(){};
    
    public static void staticMethodA(){};
}

class SubClass extends SuperClass {
	
    @Override
    public void methodA() { 
      System.out.println("SubClass");
    }
  
}
