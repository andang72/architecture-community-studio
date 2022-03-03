package tests;

public class Singleton {

    //private static Singleton instance = new Singleton() ; // ❸ 유일한 인스턴스 저장을 위한 변수 
	//private static volatile Singleton instance ;
	
    private Singleton (){ // ❷ 외부에서 인스턴스 생성이 불가하게 생성자를 private 로 선언
    
    }

    public static synchronized  Singleton getInstance (){ // ❶ Singleton 클래스 인스턴스를 생성하여 리턴.
        return Holder.instance ;
    }
    
    private static class Holder {
    	private static final Singleton instance = new Singleton();
    }

}
