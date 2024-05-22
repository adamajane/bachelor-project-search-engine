package test;

import core.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestDriver {

    public static final String OLIVER_WINDOWS = "C:\\Users\\olski\\Desktop\\bachelor-project-search-engine\\data-files\\";
    public static final String OLIVER_MAC = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/";
    public static final String ADAM_MAC = "/Users/Adam/IdeaProjects/bachelor-project-search-engine/data-files/";

    public static final String CURRENT_FILE_PATH = OLIVER_MAC;
    public static void main(String[] args) {

        // List of all data files
        List<String> dataFiles = Arrays.asList(
                CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100KB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_1MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_2MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_5MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_10MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_20MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_50MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_200MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_400MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_800MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004.txt"
        );

        try (PrintWriter writer = new PrintWriter("test-output.txt", "UTF-8")) {
            for (String dataFile : dataFiles) {
                String fileSize = extractFileSize(dataFile);
                boolean skipIndex3 = shouldSkipIndex3(fileSize);

                for (int index = 1; index <= 6; index++) {
                    if (index == 3 && skipIndex3) {
                        String output = "Skipping index " + index + " for file of size " + fileSize + " (greater than 5MB)";
                        System.out.println(output);
                        writer.println(output);
                        continue;
                    }

                    String output = "Testing index " + index + " with file of size " + fileSize;
                    System.out.println(output);
                    writer.println(output);
                    long startTime = System.currentTimeMillis();
                    long endTime = 0;
                    switch (index) {
                        case 1:
                            Index1 i1 = new Index1(dataFile);
                            break;
                        case 2:
                            Index2 i2 = new Index2(dataFile);
                            break;
                        case 3:
                            Index3 i3 = new Index3(dataFile);
                            break;
                        case 4:
                            Index4 i4 = new Index4(dataFile);
                            break;
                        case 5:
                            output = "Testing index 5a with file of size " + fileSize;
                            System.out.println(output);
                            writer.println(output);
                            startTime = System.currentTimeMillis();
                            Index5a i5a = new Index5a(dataFile);
                            endTime = System.currentTimeMillis();
                            double minutes = (double) (endTime - startTime) / (1000 * 60);
                            output = String.format("Preprocessing for index 5a completed in %.5f minutes.", minutes);
                            System.out.println(output);
                            writer.println(output);

                            output = "Testing index 5b with file of size " + fileSize;
                            System.out.println(output);
                            writer.println(output);
                            startTime = System.currentTimeMillis();
                            Index5b i5b = new Index5b(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            output = String.format("Preprocessing for index 5b completed in %.5f minutes.", minutes);
                            System.out.println(output);
                            writer.println(output);

                            output = "Testing index 5c with file of size " + fileSize;
                            System.out.println(output);
                            writer.println(output);
                            startTime = System.currentTimeMillis();
                            Index5c i5c = new Index5c(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            output = String.format("Preprocessing for index 5c completed in %.5f minutes.", minutes);
                            System.out.println(output);
                            writer.println(output);
                            continue;
                        case 6:
                            output = "Testing index 6a with file of size " + fileSize;
                            System.out.println(output);
                            writer.println(output);
                            startTime = System.currentTimeMillis();
                            Index6a i6a = new Index6a(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            output = String.format("Preprocessing for index 6a completed in %.5f minutes.", minutes);
                            System.out.println(output);
                            writer.println(output);

                            output = "Testing index 6b with file of size " + fileSize;
                            System.out.println(output);
                            writer.println(output);
                            startTime = System.currentTimeMillis();
                            Index6b i6b = new Index6b(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            output = String.format("Preprocessing for index 6b completed in %.5f minutes.", minutes);
                            System.out.println(output);
                            writer.println(output);
                            continue;
                    }
                    endTime = System.currentTimeMillis();
                    double minutes = (double) (endTime - startTime) / (1000 * 60);
                    output = String.format("Preprocessing completed in %.5f minutes.", minutes);
                    System.out.println(output);
                    writer.println(output);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    private static String extractFileSize(String filePath) {
        String fileName = new File(filePath).getName();
        String[] parts = fileName.split("_");
        return parts[parts.length - 1].replace(".txt", "");
    }

    private static boolean shouldSkipIndex3(String fileSize) {
        String unit = fileSize.replaceAll("[^a-zA-Z]", "");
        long size = Long.parseLong(fileSize.replaceAll("[^0-9]", ""));
        if (unit.equalsIgnoreCase("MB") && size > 5) {
            return true;
        }
        return unit.equalsIgnoreCase("GB") || unit.equalsIgnoreCase("TB");
    }
}
