package javafx_nio_async_chat_client;

public class Util {
    public static String getThreadName() {
		return Thread.currentThread().getName();
    }
    public static String getClassName() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
    }
    public static String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
    // public static void printDbgInfo(Thread t) {
    //     //final String className = o.getClass().getEnclosingClass().getName();
    //     //final String methodName = o.getClass().getEnclosingMethod().getName();
    //     final String className = t.getStackTrace()[1].getClassName();
    //     final String methodName = t.getStackTrace()[1].getMethodName();
    //     System.out.println(t.getName()+": "+className+": "+methodName);
    // }
}
