package variables;

import domain.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GlobalVariables {
    public static List<Queue<Word>> wordPartitions = new ArrayList<>();
    public static List<Queue<Word>> savePartitions = new ArrayList<>();

    public static String wordsFilePath;
    public static String resultFolderPath;
    public static int numOfWordPartitions;

    public static int numOfFinishedWordIntermediaryConsumer = 0;

    public static Semaphore saveFileFlushLock = new Semaphore(SettingVariables.flushIOCount);
}
