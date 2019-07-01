import constants.CommonConstants;
import interfaces.ResourceClean;
import domain.Word;
import exceptions.CommonErrorCode;
import exceptions.CommonException;
import utils.PerformanceUtil;
import variables.GlobalVariables;
import variables.SettingVariables;

import java.io.File;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WordSeparatorStarter implements ResourceClean {

    public void run(String[] args) {
        PerformanceUtil pu = new PerformanceUtil();
        pu.runningTime("init");
        // arguments 체크
        checkValidArguments(args);

        // partitions 생성
        createPartitions(SettingVariables.numOfWordPartitions);

        Stack<Thread> threadList = new Stack<>();

        // 파일 Parser Producer thread 생성
        WordsParserProducer wordsParserProducer = new WordsParserProducer(args[0]);
        Thread wordsParserProducerThread = new Thread(wordsParserProducer);
        threadList.push(wordsParserProducerThread);

        // 단어 중계자 Consumer Thread 생성
        for (int i = 0; i < SettingVariables.numberOfIntermediaryConsumer; i++) {
            Thread wordIntermediaryConsumerThread = new Thread(new WordIntermediaryConsumer(i));
            threadList.push(wordIntermediaryConsumerThread);
        }

        // 단어 저장 Consumer Thread 생성
        for (int i = 0; i < CommonConstants.numOfSavePartitions; i++) {
            Thread wordSaveConsumerThread = new Thread(new WordSaveConsumer(i));
            threadList.push(wordSaveConsumerThread);
        }

        Stack<Thread> threadChecker = new Stack<>();
        while (!threadList.empty()) {
            Thread thread = threadList.pop();
            thread.start();
            threadChecker.push(thread);
        }

        for (Thread thread : threadChecker) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new CommonException(e);
            }
        }

        close();
        pu.runningTime("Done!\nElapsed Time(ms)");
    }

    @Override
    public void close() {
        if (GlobalVariables.wordPartitions != null) {
            for (Queue<Word> wordQueue : GlobalVariables.wordPartitions) {
                wordQueue.clear();
            }
            GlobalVariables.wordPartitions = null;
        }
        if (GlobalVariables.savePartitions != null) {
            for (Queue<Word> saveQueue : GlobalVariables.savePartitions) {
                saveQueue.clear();
            }
            GlobalVariables.savePartitions = null;
        }
    }

    private void checkValidArguments(String[] args) {
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

    private void createPartitions(int numOfWordPartitions) {
        for (int i = 0; i < numOfWordPartitions; i++) {
            GlobalVariables.wordPartitions.add(new ConcurrentLinkedDeque<Word>());
        }

        for (int i = 0; i < CommonConstants.numOfSavePartitions; i++) {
            GlobalVariables.savePartitions.add(new ConcurrentLinkedDeque<Word>());
        }
    }
}
