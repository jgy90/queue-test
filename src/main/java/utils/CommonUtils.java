package utils;

import constants.CommonConstants;
import constants.OsType;
import exceptions.CommonErrorCode;
import exceptions.CommonException;

public class CommonUtils {
    public static OsType getOsType() {
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();
        if (osNameMatch.contains("linux")) {
            return OsType.LINUX;
        } else if (osNameMatch.contains("windows")) {
            return OsType.WINDOWS;
        } else if (osNameMatch.contains("mac os") || osNameMatch.contains("macos") || osNameMatch.contains("darwin")) {
            return OsType.MAC;
        } else {
            throw new CommonException(CommonErrorCode.UNKNOWN_OS_TYPE);
        }
    }

    public static boolean isValidWord(String word) {
        return CommonConstants.validWordRegex.matcher(word).matches();
    }
}
