import constants.CommonConstants;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WordsParserProducer implements Runnable {
    private BufferedReader wordsFile;
    private FileReader fileReader;

    public WordsParserProducer(String wordsFilePath) {
        try {
            fileReader = new FileReader(wordsFilePath);
            wordsFile = new BufferedReader(fileReader, CommonConstants.memPageSize * SettingVariables.inputBufferSizeMultiplier);
        } catch (FileNotFoundException e) {
            close();
            throw new CommonException(CommonErrorCode.WORDS_FILE_NOT_EXISTS, e);
        }
    }

    public void run() {
        String word;
        try {
            word = wordsFile.readLine();
            while (word != null) {
                if (!isValidWord(word)) {
                    word = wordsFile.readLine();
                    continue;
                }
                // 분리된 단어를 공통 Queue 에 저장
                putWordIntoQueue(new Word(word, SettingVariables.numOfWordPartitions));

                word = wordsFile.readLine();
            }
            wordsFile.close();
            fileReader.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.WORDS_FILE_IO_ERROR, e);
        } finally {
            close();
        }
    }

    private void putWordIntoQueue(Word word) {
        GlobalVariables.wordPartitions.get(word.getPartition()).offer(word);
    }

    private boolean isValidWord(String word) {
        return CommonConstants.validWordRegex.matcher(String.valueOf(word.charAt(0))).matches();
    }

    private void close() {
        WordIntermediaryConsumer.setFinished(true);
    }
}
