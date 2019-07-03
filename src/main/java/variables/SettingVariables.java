package variables;

import constants.CommonConstants;

import java.util.concurrent.TimeUnit;

public class SettingVariables {

    /**
     * words 파일 경로
     */
    public static String wordsFilePath;
    /**
     * 결과 파일 올더 경로
     */
    public static String resultFolderPath;
    /**
     * Partition 개수
     */
    public static int numOfWordPartitions;

    /**
     * word partition 접근 시 timeout
     */
    public static long wordPartitionsTimeout = 50;

    /**
     * word partition timeout 단위
     */
    @Deprecated
    public static TimeUnit wordPartitionsTimeoutUnit = TimeUnit.MILLISECONDS;

    /**
     * word partition 별 queue 크기
     */
    @Deprecated
    public static int wordPartitionsSize = 1000;

    /**
     * save partition 접근 시 timeout
     */
    public static long savePartitionsTimeout = 50;

    /**
     * save partition timeout 단위
     */
    @Deprecated
    public static TimeUnit savePartitionsTimeoutUnit = TimeUnit.MILLISECONDS;
    /**
     * save partition 별 queue 크기
     */
    @Deprecated
    public static int savePartitionsSize = 1000;

    /**
     * 중계 Consumer 수
     */
    public static int numberOfIntermediaryConsumer = 26;

    /**
     * words 입력 buffer 크기 승수
     */
    public static int inputBufferSizeMultiplier = 10;

    /**
     * save buffer 크기 승수
     */
    public static int outputBufferSizeMultiplier = 10;


    /**
     * soft flush 기준 %
     */
    public static int outputBufferLowerLimitPercent = 100;

    /**
     * soft flush 기준 byte
     */
    public static int outputBufferLowerLimit = CommonConstants.memPageSize * outputBufferSizeMultiplier * (outputBufferLowerLimitPercent / 100);

    /**
     * hard flush 기준 byte
     */
    public static int outputBufferUpperLimit = CommonConstants.memPageSize * outputBufferSizeMultiplier;

    /**
     * soft flush 동시 수
     */
    public static int flushIOCount = 2;

    /**
     * partition 분배 방식
     * HASH, ALPHABET, FNV1A
     */
    public static String distributionType = "FNV1A";

    /**
     * FNV1a Hash 대상 최대 String 개수
     * 0 은 전체
     */
    public static int fnv1aMaxLength = 0;
}
