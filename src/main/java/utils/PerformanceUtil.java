package utils;

public class PerformanceUtil {

    private long stdTime;

    public void runningTime(String s) {
        if (stdTime == 0 ) {
            stdTime = System.currentTimeMillis();
            return;
        }
        long currentTime = System.currentTimeMillis();
        System.out.println(s + ": " + (currentTime - stdTime));

        stdTime = currentTime;
    }
}
