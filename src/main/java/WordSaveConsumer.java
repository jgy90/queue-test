import constants.CommonConstants;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

public class WordSaveConsumer implements Runnable {
    private int partition;

    private BufferedWriter saveFile;
    private FileWriter fileWriter;

    public WordSaveConsumer(int partition) {
        this.partition = partition;

        String saveFilePathName;
        StringBuilder sb = new StringBuilder();
        sb.append(GlobalVariables.resultFolderPath);
        sb.append(File.separator);
        sb.append((char) (partition + 'a'));
        sb.append(CommonConstants.saveFileExtension);
        saveFilePathName = sb.toString();


        try {
            fileWriter = new FileWriter(saveFilePathName, true);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_OPEN_ERROR);
        }
        saveFile = new BufferedWriter(fileWriter, CommonConstants.memPageSize * SettingVariables.outputBufferSizeMultiplier);
    }

    private static synchronized void queuesClear() {
        for (Queue<Word> saveBlockingQueue : GlobalVariables.savePartitions) {
            saveBlockingQueue.clear();
        }
    }

    @Override
    public void run() {
        Word word = new Word();
        while (GlobalVariables.numOfFinishedWordIntermediaryConsumer < GlobalVariables.wordPartitions.size() || word != null) {
            word = GlobalVariables.savePartitions.get(partition).poll();
            if (word == null) {
                try {
                    Thread.sleep(SettingVariables.savePartitionsTimeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            saveWordToFile(word);
        }
        queuesClear();
        try {
            saveFile.flush();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_FLUSH_ERROR);
        }
        try {
            saveFile.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_CLOSE_ERROR);
        }
    }

    private void saveWordToFile(Word word) {
        try {
            saveFile.write(word.getWord() + System.getProperty("line.separator"));
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_WRITE_ERROR);
        }
    }
}
