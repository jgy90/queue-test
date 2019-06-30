package constants;

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
    RR {
        @Override
        public int getPartition(String word) {
            return DistributionType.getIncCounter();
        }
    },
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
