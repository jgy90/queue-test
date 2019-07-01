import constants.CommonConstants;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.ResourceCleaner;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WordWriter implements ResourceCleaner {

    private StringBuilder memstore = new StringBuilder();

    private BufferedWriter saveFile;
    private FileWriter fileWriter;

    public WordWriter(int partition) {
        initialize(partition);
    }

    private void initialize(int partition) {
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
        saveFile = new BufferedWriter(fileWriter, CommonConstants.memPageSize * SettingVariables.outputBufferSizeMultiplier);
    }

    public void saveWordToFile(Word word) {
        memstore.append(word.getWord());
        memstore.append(CommonConstants.lineSeparator);
        if (memstore.length() > SettingVariables.outputBufferUpperLimit) {
            forceFlushSaveFile();
        } else if (memstore.length() > SettingVariables.outputBufferLowerLimit) {
            flushSaveFile();
        }
    }

    private void forceFlushSaveFile() {
        try {
            saveFile.write(memstore.toString());
            clearMemstore();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_WRITE_ERROR, e);
        }
    }

    private void flushSaveFile() {
        boolean lockAcquire = GlobalVariables.saveFileFlushLock.tryAcquire();
        if (!lockAcquire) return;

        try {
            saveFile.write(memstore.toString());
            clearMemstore();
        } catch (IOException e) {
            GlobalVariables.saveFileFlushLock.release();
            throw new CommonException(CommonErrorCode.SAVE_FILE_WRITE_ERROR, e);
        }

        try {
            saveFile.flush();
        } catch (IOException e) {
            GlobalVariables.saveFileFlushLock.release();
            throw new CommonException(CommonErrorCode.SAVE_FILE_FLUSH_ERROR, e);
        }

        GlobalVariables.saveFileFlushLock.release();

    }

    private void clearMemstore() {
        memstore.delete(0, memstore.length());
    }

    @Override
    public void close() {

        try {
            saveFile.flush();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_FLUSH_ERROR, e);
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_CLOSE_ERROR, e);
        }

        try {
            saveFile.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.SAVE_FILE_CLOSE_ERROR, e);
        }

        memstore = null;

    }
}
