package domain;

import constants.DistributionType;

public class Word {
    private String word;
    private int partition;
    private char alphabet;

    public Word(String word, int numOfPartitions) {
        this.word = word;
        String wordLowerCase = word.toLowerCase();
        DistributionType distributionType = DistributionType.getDistributionType();
        this.partition = distributionType.getPartition(wordLowerCase);
        alphabet = wordLowerCase.charAt(0);
    }

    public Word() {

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public char getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(char alphabet) {
        this.alphabet = alphabet;
    }

    public int getSavePartition() {
        return alphabet - 'a';
    }
}
