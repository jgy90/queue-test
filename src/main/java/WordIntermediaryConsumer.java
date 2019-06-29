import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.util.concurrent.BlockingQueue;

public class WordIntermediaryConsumer implements Runnable {
    private int partition;
    private static boolean isFinished = false;

    public WordIntermediaryConsumer(int partition) {
        this.partition = partition;
    }

    @Override
    public void run() {
        Word word = new Word();
        while (!isFinished || word != null) {

            try {
                word = GlobalVariables.wordPartitions.get(partition).poll(SettingVariables.wordPartitionsTimeout, SettingVariables.wordPartitionsTimeoutUnit);

            } catch (InterruptedException e) {
                close();
                throw new CommonException(CommonErrorCode.GET_WORD_QUEUE_INTERRUPTED, e);
            }

            if (word == null) continue;
            try {
                GlobalVariables.savePartitions.get(word.getSavePartition()).put(word);
            } catch (InterruptedException e) {
                close();
                throw new CommonException(CommonErrorCode.PUT_WORD_SAVE_QUEUE_INTERRUPTED, e);
            }

        }

        close();
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public static boolean isFinished() {
        return isFinished;
    }

    public static void setFinished(boolean finished) {
        isFinished = finished;
    }

    private static synchronized void close() {
        for (BlockingQueue<Word> wordBlockingQueue: GlobalVariables.wordPartitions) {
            wordBlockingQueue.clear();
        }
        GlobalVariables.numOfFinishedWordIntermediaryConsumer++;
    }
}
