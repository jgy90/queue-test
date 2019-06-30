package constants;

import utils.CommonUtils;

import java.util.regex.Pattern;

public class CommonConstants {
    //    public final static Pattern validWordRegex = Pattern.compile("[a-zA-Z][\\S]*");
    public final static Pattern validWordRegex = Pattern.compile("[a-zA-Z]");

    public static final int numOfArguments = 3;
    public static final int minPartitions = 2;
    public static final int maxPartitions = 26;
    public static final int memPageSize = CommonUtils.getOsType().equals(OsType.WINDOWS) ? 8192 : 4096;
    public static final String saveFileExtension = ".txt";

    public static final String lineSeparator = System.getProperty("line.separator");

    public static final int numOfSavePartitions = 26;

    public static final int FNV1_32_INIT = 0x811c9dc5;
    public static final int FNV1_PRIME_32 = 16777619;
}
