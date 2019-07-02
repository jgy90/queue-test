import constants.CommonConstants;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import interfaces.Readable;
import interfaces.ResourceClean;
import interfaces.Sendable;
import interfaces.ValidationCheck;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WordsParserProducer extends InterruptedException implements Runnable, ValidationCheck<String>, ResourceClean {

    private Readable wordReader;
    private Sendable queueSender;

    public WordsParserProducer(String wordsFilePath) {
        super("WordsParserProducer is interrupted");
        try {
            wordReader = new WordReader(wordsFilePath);
        } catch (FileNotFoundException e) {
            close();
            throw new CommonException(CommonErrorCode.WORDS_FILE_NOT_EXISTS, e);
        }
        queueSender = new QueueSender();
    }

    @Override
    public void run() {
        String word;
        try {
            word = readFromFile();
            while (word != null) {
                if (!isValid(word)) {
                    word = readFromFile();
                    continue;
                }
                // 분리된 단어를 공통 Queue 에 저장
                Word wordObj = new Word(word, SettingVariables.numOfWordPartitions);
                queueSender.send(wordObj, GlobalVariables.wordPartitions.get(wordObj.getPartition()));

                word = readFromFile();
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

    private String readFromFile() throws IOException {
        return (String) wordReader.read();
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
