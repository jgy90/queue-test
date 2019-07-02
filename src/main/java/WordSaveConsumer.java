import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.Receivable;
import interfaces.Writable;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.IOException;

public class WordSaveConsumer extends InterruptedException implements Runnable {
    private int partition;

    private Writable wordWriter;
    private Receivable queueReceiver;

    public WordSaveConsumer(int partition) {
        super("WordSaveConsumer is interrupted");
        this.partition = partition;
        wordWriter = new WordWriter(partition);
        queueReceiver = new QueueReceiver();
    }

    @Override
    public void run() {
        Word word;
        while (GlobalVariables.numOfFinishedWordIntermediaryConsumer < SettingVariables.numberOfIntermediaryConsumer || isContinue()) {
            word = (Word) queueReceiver.receive(GlobalVariables.savePartitions.get(partition));
            if (word == null) {
                try {
                    Thread.sleep(SettingVariables.savePartitionsTimeout);
                } catch (InterruptedException e) {
                    throw new CommonException(CommonErrorCode.SAVE_CONSUMER_THREAD_INTERRUPTED, e);
                }
                continue;
            }

            wordWriter.write(word.getWord());
            if (Thread.interrupted()) {
                break;
            }
        }
        try {
            wordWriter.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_CLOSE_ERROR, e);
        }
    }

    private boolean isContinue() {
        return GlobalVariables.savePartitions.get(partition).size() > 0;
    }

}
