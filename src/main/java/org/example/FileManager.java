package org.example;

import java.io.FileReader;
import java.io.IOException;

public class FileManager {
    public static String fileReader(String path) throws IOException {
        FileReader fileReader = new FileReader(path);
        StringBuilder line = new StringBuilder("");
        int c;
        while ((c = fileReader.read()) != -1) {
            line.append((char) c);
        }
        fileReader.close();
        return line.toString();
    }
}
