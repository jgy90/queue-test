package constants;

public enum OsType implements FilePathDiscriminate {
    WINDOWS {
        public boolean isValidFilePath(String path) {
            return CommonConstants.validFilePathRegexForWindows.matcher(path).matches();
        }
    },
    LINUX {
        public boolean isValidFilePath(String path) {
            return CommonConstants.validFilePathRegexForLinux.matcher(path).matches();
        }
    },
    MAC {
        public boolean isValidFilePath(String path) {
            return CommonConstants.validFilePathRegexForMac.matcher(path).matches();
        }
    }
}
