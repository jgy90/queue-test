import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.util.Queue;

public class WordIntermediaryConsumer implements Runnable {
    private static boolean isFinished = false;
    private int partition;

    public WordIntermediaryConsumer(int partition) {
        this.partition = partition;
    }

    public static boolean isFinished() {
        return isFinished;
    }

    public static void setFinished(boolean finished) {
        isFinished = finished;
    }

    private static synchronized void close() {
        for (Queue<Word> wordBlockingQueue : GlobalVariables.wordPartitions) {
            wordBlockingQueue.clear();
        }
        GlobalVariables.numOfFinishedWordIntermediaryConsumer++;
    }

    @Override
    public void run() {
        Word word = new Word();
        while (!isFinished || word != null) {

            word = GlobalVariables.wordPartitions.get(partition).poll();

            if (word == null) {
                try {
                    Thread.sleep(SettingVariables.wordPartitionsTimeout);
                } catch (InterruptedException e) {
                    throw new CommonException(CommonErrorCode.INTER_CONSUMER_THREAD_INTERRUPTED, e);
                }
                continue;
            }
            GlobalVariables.savePartitions.get(word.getSavePartition()).offer(word);

        }

        close();
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }
}
