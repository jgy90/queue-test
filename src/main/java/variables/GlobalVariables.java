package variables;

import domain.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class GlobalVariables {
    public static List<BlockingQueue<Word>> wordPartitions = new ArrayList<BlockingQueue<Word>>();
    public static List<BlockingQueue<Word>> savePartitions = new ArrayList<BlockingQueue<Word>>();

    public static String wordsFilePath;
    public static String resultFolderPath;
    public static int numOfWordPartitions;

    public static int numOfFinishedWordIntermediaryConsumer = 0;
}
