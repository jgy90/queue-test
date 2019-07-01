import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.Writable;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.IOException;

public class WordSaveConsumer extends InterruptedException implements Runnable {
    private int partition;

    private Writable wordWriter;

    public WordSaveConsumer(int partition) {
        super("WordSaveConsumer is interrupted");
        this.partition = partition;
        wordWriter = new WordWriter(partition);
    }

    @Override
    public void run() {
        Word word = new Word();
        while (GlobalVariables.numOfFinishedWordIntermediaryConsumer < SettingVariables.numberOfIntermediaryConsumer || word != null) {
            word = GlobalVariables.savePartitions.get(partition).poll();
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

}
