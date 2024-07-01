package org.example.javageneral;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BinarySearch {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("m2", "m3", "m1");
        System.out.println(Collections.binarySearch(list, "m3"));

        Collections.sort(list);
        System.out.println(Collections.binarySearch(list, "m3"));

        Collections.reverse(list);
        System.out.println(Collections.binarySearch(list, "m3"));

        char[] buffer = new char[8];

        try (FileReader reader = new FileReader("src/main/resources/general/file1.txt");
             FileWriter writer = new FileWriter("src/main/resources/general/file2.txt")) {

            var count = 0;

            while( (count = reader.read(buffer)) != -1) {
                writer.write(buffer);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
