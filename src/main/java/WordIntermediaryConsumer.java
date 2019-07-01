import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.Receivable;
import interfaces.ResourceClean;
import interfaces.Sendable;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.util.ArrayList;
import java.util.List;

public class WordIntermediaryConsumer extends InterruptedException implements Runnable, ResourceClean {
    private static boolean isFinished = false;
    private static Object clearLock = new Object();
    private List<Integer> partitions;
    private Sendable queueSender;
    private Receivable queueReceiver;

    public WordIntermediaryConsumer(int std) {
        super("WordIntermediaryConsumer is interrupted");
        partitions = getPartitions(std);
        queueSender = new QueueSender();
        queueReceiver = new QueueReceiver();
    }

    public static boolean isFinished() {
        return isFinished;
    }

    public static void setFinished(boolean finished) {
        isFinished = finished;
    }

    @Override
    public void close() {
        synchronized (clearLock) {
            GlobalVariables.numOfFinishedWordIntermediaryConsumer++;
        }
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
                word = (Word) queueReceiver.receive(GlobalVariables.wordPartitions.get(partition));

                if (word == null) {
                    try {
                        Thread.sleep(SettingVariables.wordPartitionsTimeout);
                    } catch (InterruptedException e) {
                        throw new CommonException(CommonErrorCode.INTER_CONSUMER_THREAD_INTERRUPTED, e);
                    }
                    continue;
                }
                isContinue = true;
                queueSender.send(word, GlobalVariables.savePartitions.get(word.getSavePartition()));
                if (Thread.interrupted()) {
                    break;
                }
            }
        }

        close();
    }
}
