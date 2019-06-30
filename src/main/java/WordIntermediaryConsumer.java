import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class WordIntermediaryConsumer implements Runnable {
    private static boolean isFinished = false;
    private List<Integer> partitions;

    public WordIntermediaryConsumer(int std) {
        partitions = getPartitions(std);
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

    private List<Integer> getPartitions(int std) {
        List<Integer> partitions = new ArrayList<>();

        for (int i = 0; i * SettingVariables.numberOfIntermediaryConsumer + std < SettingVariables.numOfWordPartitions; i++) {
            partitions.add(i * SettingVariables.numberOfIntermediaryConsumer + std);
        }

        return partitions;
    }

    @Override
    public void run() {
        Word word;
        boolean isContinue = true;
        while (!isFinished || isContinue) {

            isContinue = false;
            for (Integer partition : partitions) {
                word = GlobalVariables.wordPartitions.get(partition).poll();

                if (word == null) {
                    try {
                        Thread.sleep(SettingVariables.wordPartitionsTimeout);
                    } catch (InterruptedException e) {
                        throw new CommonException(CommonErrorCode.INTER_CONSUMER_THREAD_INTERRUPTED, e);
                    }
                    continue;
                }
                isContinue = true;
                GlobalVariables.savePartitions.get(word.getSavePartition()).offer(word);
            }
        }

        close();
    }

}
