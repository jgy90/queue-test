package variables;

import java.util.concurrent.TimeUnit;

public class SettingVariables {
    public static long wordPartitionsTimeout = 50;
    @Deprecated
    public static TimeUnit wordPartitionsTimeoutUnit = TimeUnit.MILLISECONDS;
    @Deprecated
    public static int wordPartitionsSize = 1000000;

    public static long savePartitionsTimeout = 50;
    @Deprecated
    public static TimeUnit savePartitionsTimeoutUnit = TimeUnit.MILLISECONDS;
    @Deprecated
    public static int savePartitionsSize = 1000000;

    public static int numberOfIntermediaryConsumer = 10;

    public static int inputBufferSizeMultiplier = 10;
    public static int outputBufferSizeMultiplier = 10;

}
