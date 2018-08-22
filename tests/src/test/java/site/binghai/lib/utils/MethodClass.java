package site.binghai.lib.utils;

public class MethodClass {
    public MethodClass() {
        System.out.println("GGGGGG");
    }

    static {
        System.out.println("STATIC....");
    }

    public static boolean someMethod(){
        System.out.println("someMethod in MethodClass");
        return true;
    }
}
