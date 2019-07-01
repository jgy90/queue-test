import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import variables.GlobalVariables;
import variables.SettingVariables;

public class WordSaveConsumer extends InterruptedException implements Runnable {
    private int partition;

    private WordWriter wordWriter;

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

            wordWriter.saveWordToFile(word);
            if (Thread.interrupted()) {
                break;
            }
        }
        wordWriter.close();
    }

}
