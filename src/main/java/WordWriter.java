import constants.CommonConstants;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.Writable;
import utils.BufferedWriterUD;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WordWriter implements Writable<String> {

    private BufferedWriterUD saveFile;
    private FileWriter fileWriter;
    private int partition;

    public WordWriter(int partition) {
        initialize(partition);
    }

    private void initialize(int partition) {
        this.partition = partition;
        String saveFilePathName;
        StringBuilder sb = new StringBuilder();
        sb.append(SettingVariables.resultFolderPath);
        sb.append(File.separator);
        sb.append((char) (partition + 'a'));
        sb.append(CommonConstants.saveFileExtension);
        saveFilePathName = sb.toString();


        try {
            fileWriter = new FileWriter(saveFilePathName, true);
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_OPEN_ERROR, e);
        }
        saveFile = new BufferedWriterUD(fileWriter, SettingVariables.outputBufferUpperLimit);
    }

    @Override
    public void write(String word) {

        int size;
        try {
            saveFile.write(word + CommonConstants.lineSeparator);
            size = saveFile.size();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_WRITE_ERROR, e);
        }

        if (size > SettingVariables.outputBufferLowerLimit) {
            flushSaveFile();
        }
    }

    private void flushSaveFile() {
        boolean lockAcquire = GlobalVariables.saveFileFlushLock.tryAcquire();
        if (!lockAcquire) return;

        try {
            saveFile.flush();
        } catch (IOException e) {
            GlobalVariables.saveFileFlushLock.release();
            throw new CommonException(CommonErrorCode.SAVE_FILE_FLUSH_ERROR, e);
        }

        GlobalVariables.saveFileFlushLock.release();

    }

    @Override
    public void close() {
        try {
            saveFile.flush();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_FLUSH_ERROR, e);
        }

        try {
            saveFile.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_CLOSE_ERROR, e);
        }

        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_CLOSE_ERROR, e);
        }
    }
}
