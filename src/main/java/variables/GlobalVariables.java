package variables;

import domain.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class GlobalVariables {
    public static List<Queue<Word>> wordPartitions = new ArrayList<Queue<Word>>();
    public static List<Queue<Word>> savePartitions = new ArrayList<Queue<Word>>();

    public static String wordsFilePath;
    public static String resultFolderPath;
    public static int numOfWordPartitions;

    public static int numOfFinishedWordIntermediaryConsumer = 0;
}
