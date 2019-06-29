import constants.CommonConstants;
import constants.OsType;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import utils.CommonUtils;
import utils.PerformanceUtil;
import variables.GlobalVariables;

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
        createPartitions(GlobalVariables.numOfWordPartitions);

        // 파일 Parser Producer thread 실행
        WordsParserProducer wordsParserProducer = new WordsParserProducer(args[0]);
        Thread wordsParserProducerThread = new Thread(wordsParserProducer);

        // 단어 중계자 Consumer Thread 실행
        List<Thread> wordIntermediaryConsumerThreadList = new ArrayList<Thread>();
        for (int i = 0; i < GlobalVariables.numOfWordPartitions; i++) {
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

        GlobalVariables.wordsFilePath = args[0];
        GlobalVariables.resultFolderPath = args[1];

        OsType osType = CommonUtils.getOsType();

        // 정상적인 파일 경로인지 체크
        if (!osType.isValidFilePath(GlobalVariables.wordsFilePath)) {
            throw new CommonException(CommonErrorCode.INVALID_FILE_PATH);
        } else if (!osType.isValidFilePath(GlobalVariables.resultFolderPath)) {
            throw new CommonException(CommonErrorCode.INVALID_FILE_PATH);
        }

        File wordsFile = new File(GlobalVariables.wordsFilePath);
        File resultFolder = new File(GlobalVariables.resultFolderPath);

        // 파일 및 폴더 존재 확인
        if (!wordsFile.exists()) {
            throw new CommonException(CommonErrorCode.WORDS_FILE_NOT_EXISTS);
        } else if (!resultFolder.exists()) {
            throw new CommonException(CommonErrorCode.RESULT_FOLDER_NOT_EXISTS);
        }

        // 파티션 수 확인
        GlobalVariables.numOfWordPartitions = Integer.parseInt(args[2]);

        if (GlobalVariables.numOfWordPartitions < CommonConstants.minPartitions || GlobalVariables.numOfWordPartitions > CommonConstants.maxPartitions) {
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
