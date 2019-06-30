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

    public static int fnv1aHash32(String data, int length) {
        int hash = CommonConstants.FNV1_32_INIT;
        for (int i = 0; i < length; i++) {
            hash ^= (data.charAt(i) & 0xff);
            hash *= CommonConstants.FNV1_PRIME_32;
        }

        return hash;
    }
}
