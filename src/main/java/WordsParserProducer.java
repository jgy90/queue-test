import constants.CommonConstants;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.ResourceCleaner;
import interfaces.ValidationChecker;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WordsParserProducer extends InterruptedException implements Runnable, ResourceCleaner, ValidationChecker {

    private WordReader wordReader;

    public WordsParserProducer(String wordsFilePath) {
        super("WordsParserProducer is interrupted");
        try {
            wordReader = new WordReader(wordsFilePath);
        } catch (FileNotFoundException e) {
            close();
            throw new CommonException(CommonErrorCode.WORDS_FILE_NOT_EXISTS, e);
        }
    }

    @Override
    public void run() {
        String word;
        try {
            word = wordReader.readWord();
            while (word != null) {
                if (!isValid(word)) {
                    word = wordReader.readWord();
                    continue;
                }
                // 분리된 단어를 공통 Queue 에 저장
                putWordIntoQueue(new Word(word, SettingVariables.numOfWordPartitions));

                word = wordReader.readWord();
                if (Thread.interrupted()) {
                    break;
                }
            }
            wordReader.close();
        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.WORDS_FILE_IO_ERROR, e);
        } finally {
            close();
        }
    }

    private void putWordIntoQueue(Word word) {
        GlobalVariables.wordPartitions.get(word.getPartition()).offer(word);
    }

    @Override
    public boolean isValid(String word) {
        return CommonConstants.validWordRegex.matcher(String.valueOf(word.charAt(0))).matches();
    }

    @Override
    public void close() {
        WordIntermediaryConsumer.setFinished(true);
    }
}
