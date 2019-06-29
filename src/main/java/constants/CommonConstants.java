package constants;

import utils.CommonUtils;

import java.util.regex.Pattern;

public class CommonConstants {
    public final static Pattern validFilePathRegexForWindows = Pattern.compile("([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?");
    public final static Pattern validFilePathRegexForLinux = Pattern.compile("^/|(/[a-zA-Z0-9_-]+)+$");
    public final static Pattern validFilePathRegexForMac = Pattern.compile("^/|(/[a-zA-Z0-9_-]+)+$");
    public final static Pattern validWordRegex = Pattern.compile("[a-zA-Z][\\S]*");

    public static final int numOfArguments = 3;
    public static final int minPartitions = 2;
    public static final int maxPartitions = 26;
    public static final int memPageSize = CommonUtils.getOsType().equals(OsType.WINDOWS) ? 8192 : 4096;
    public static final String saveFileExtension = ".txt";

    public static final int numOfSavePartitions = 26;
}
