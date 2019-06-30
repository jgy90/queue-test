package constants;

import utils.CommonUtils;
import variables.SettingVariables;

public enum DistributionType implements PartitionDistribute {
    HASH {
        @Override
        public int getPartition(String word) {
            return (word.hashCode() & 0xfffffff) % SettingVariables.numOfWordPartitions;
        }
    },
    ALPHABET {
        @Override
        public int getPartition(String word) {
            return (word.charAt(0) - 'a') % SettingVariables.numOfWordPartitions;
        }
    },
    @Deprecated
    RR {
        @Override
        public int getPartition(String word) {
            return DistributionType.getIncCounter();
        }
    },
    FNV1A {
        @Override
        public int getPartition(String word) {
            if (SettingVariables.fnv1aMaxLength == 0) {
                return (CommonUtils.fnv1aHash32(word, word.length()) & 0xfffffff) % SettingVariables.numOfWordPartitions;
            } else {
                return (CommonUtils.fnv1aHash32(word, word.length() < SettingVariables.fnv1aMaxLength ? word.length() : SettingVariables.fnv1aMaxLength) & 0xfffffff) % SettingVariables.numOfWordPartitions;
            }
        }
    }
    ;

    private static int counter = 0;

    private synchronized static int getIncCounter() {
        counter++;
        if (counter >= SettingVariables.numOfWordPartitions) {
            counter = 0;
        }
        return counter;
    }

    public static DistributionType getDistributionType() {
        return DistributionType.valueOf(SettingVariables.distributionType);
    }
}
