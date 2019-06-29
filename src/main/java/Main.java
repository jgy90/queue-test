import constants.CommonConstants;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import utils.PerformanceUtil;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Main {

    public static void main(String[] args) {
        run(args);
    }

    private static void run(String[] args) {
        PerformanceUtil pu = new PerformanceUtil();
        pu.runningTime("init");
        // arguments 체크
        checkValidArguments(args);

        // partitions 생성
        createPartitions(SettingVariables.numOfWordPartitions);

        // 파일 Parser Producer thread 실행
        WordsParserProducer wordsParserProducer = new WordsParserProducer(args[0]);
        Thread wordsParserProducerThread = new Thread(wordsParserProducer);

        // 단어 중계자 Consumer Thread 실행
        List<Thread> wordIntermediaryConsumerThreadList = new ArrayList<Thread>();
        for (int i = 0; i < SettingVariables.numOfWordPartitions; i++) {
            Thread wordIntermediaryConsumerThread = new Thread(new WordIntermediaryConsumer(i));
            wordIntermediaryConsumerThreadList.add(wordIntermediaryConsumerThread);
        }

        // 단어 저장 Consumer Thread 실행
        List<Thread> wordSaveConsumerThreadList = new ArrayList<Thread>();
        for (int i = 0; i < CommonConstants.numOfSavePartitions; i++) {
            Thread wordSaveConsumerThread = new Thread(new WordSaveConsumer(i));
            wordSaveConsumerThreadList.add(wordSaveConsumerThread);
            try {
                wordSaveConsumerThread.join();
            } catch (InterruptedException e) {
                throw new CommonException(CommonErrorCode.SAVE_CONSUMER_THREAD_INTERRUPTED, e);
            }
        }

        for (Thread wordSaveConsumerThread : wordSaveConsumerThreadList) {
            wordSaveConsumerThread.start();
        }

        for (Thread wordIntermediaryConsumerThread : wordIntermediaryConsumerThreadList) {
            wordIntermediaryConsumerThread.start();
        }

        wordsParserProducerThread.start();

        try {
            wordsParserProducerThread.join();
        } catch (InterruptedException e) {
            throw new CommonException(CommonErrorCode.PRODUCER_THREAD_INTERRUPTED, e);
        }

        for (Thread wordIntermediaryConsumerThread : wordIntermediaryConsumerThreadList) {
            try {
                wordIntermediaryConsumerThread.join();
            } catch (InterruptedException e) {
                throw new CommonException(CommonErrorCode.INTER_CONSUMER_THREAD_INTERRUPTED, e);
            }
        }

        for (Thread wordSaveConsumerThread : wordSaveConsumerThreadList) {
            try {
                wordSaveConsumerThread.join();
            } catch (InterruptedException e) {
                throw new CommonException(CommonErrorCode.INTER_CONSUMER_THREAD_INTERRUPTED, e);
            }
        }
        pu.runningTime("end");
    }

    private static void checkValidArguments(String[] args) {
        // arguments 개수 체크
        if (args.length > CommonConstants.numOfArguments) {
            throw new CommonException(CommonErrorCode.TOO_MANY_ARGUMENTS);
        } else if (args.length < CommonConstants.numOfArguments) {
            throw new CommonException(CommonErrorCode.NOT_ENOUGH_ARGUMENTS);
        }

        SettingVariables.wordsFilePath = args[0];
        SettingVariables.resultFolderPath = args[1];

        File wordsFile = new File(SettingVariables.wordsFilePath);
        File resultFolder = new File(SettingVariables.resultFolderPath);

        // 파일 및 폴더 존재 확인
        if (!wordsFile.exists()) {
            throw new CommonException(CommonErrorCode.WORDS_FILE_NOT_EXISTS);
        } else if (!resultFolder.exists()) {
            throw new CommonException(CommonErrorCode.RESULT_FOLDER_NOT_EXISTS);
        }

        // 파티션 수 확인
        SettingVariables.numOfWordPartitions = Integer.parseInt(args[2]);

        if (SettingVariables.numOfWordPartitions < CommonConstants.minPartitions || SettingVariables.numOfWordPartitions > CommonConstants.maxPartitions) {
            throw new CommonException(CommonErrorCode.INVALID_NUMBER_OF_PARTITIONS);
        }
    }

    private static void createPartitions(int numOfWordPartitions) {
        for (int i = 0; i < numOfWordPartitions; i++) {
            GlobalVariables.wordPartitions.add(new ConcurrentLinkedDeque<Word>());
        }

        for (int i = 0; i < CommonConstants.numOfSavePartitions; i++) {
            GlobalVariables.savePartitions.add(new ConcurrentLinkedDeque<Word>());
        }
    }

}
