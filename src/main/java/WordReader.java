import constants.CommonConstants;
import interfaces.ResourceCleaner;
import variables.SettingVariables;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WordReader implements ResourceCleaner {
    private BufferedReader wordsFile;
    private FileReader fileReader;

    public WordReader(String wordsFilePath) throws FileNotFoundException {
        initialize(wordsFilePath);
    }

    private void initialize(String wordsFilePath) throws FileNotFoundException {
        fileReader = new FileReader(wordsFilePath);
        wordsFile = new BufferedReader(fileReader, CommonConstants.memPageSize * SettingVariables.inputBufferSizeMultiplier);
    }

    public String readWord() throws IOException {
        return wordsFile.readLine();
    }

    @Override
    public void close() throws IOException{
        wordsFile.close();
        fileReader.close();
    }
}
