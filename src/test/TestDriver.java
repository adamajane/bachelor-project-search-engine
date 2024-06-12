package test;

import core.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestDriver {

    public static final String OLIVER_WINDOWS = "C:\\Users\\olski\\Desktop\\bachelor\\data-files\\";
    public static final String OLIVER_MAC = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/";
    public static final String ADAM_MAC = "/Users/Adam/IdeaProjects/bachelor-project-search-engine/data-files/";

    public static final String CURRENT_FILE_PATH = OLIVER_MAC;

    public static void main(String[] args) {

        // List of all data files
        List<String> dataFiles = Arrays.asList(
                CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100KB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_1MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_2MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_5MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_10MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_20MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_50MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_200MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_400MB.txt"
                , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_800MB.txt"
                //, CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004.txt"
        );

        StringBuilder outputBuilder = new StringBuilder();

        // Header row for the output table
        outputBuilder.append(String.format("%-10s", "File Size"));
        for (int index = 5; index <= 5; index++) {
            if (index == 5) {
                outputBuilder.append(String.format("%-12s%-12s%-12s%-12s", "Index 5a", "Index 5b", "Index 5c","Index 5d" ));
            } else if (index == 6) {
                outputBuilder.append(String.format("%-12s%-12s", "Index 6a", "Index 6b"));
            } else {
                outputBuilder.append(String.format("%-12s", "Index " + index));
            }
        }
        outputBuilder.append("\n");

        try (PrintWriter writer = new PrintWriter("test-output-preprocessing2.txt", "UTF-8")) {
            for (String dataFile : dataFiles) {
                String fileSize = extractFileSize(dataFile);
                boolean skipIndex3 = shouldSkipIndex3(fileSize);
                boolean skipIndex1 = shouldSkipIndex1(fileSize);
                boolean skipIndex2 = shouldSkipIndex2(fileSize);

                outputBuilder.append(String.format("%-10s", fileSize));

                for (int index = 5; index <= 5; index++) {
                    long startTime;
                    long endTime;
                    double minutes;
                    if (index == 3 && skipIndex3) {
                        outputBuilder.append(String.format("%-12s", "Skipped"));
                        continue;
                    }
                    if (index == 1 && skipIndex1) {
                        outputBuilder.append(String.format("%-12s", "Skipped"));
                        continue;
                    }
                    if (index == 2 && skipIndex2) {
                        outputBuilder.append(String.format("%-12s", "Skipped"));
                        continue;
                    }

                    switch (index) {
                        case 1:
                            startTime = System.currentTimeMillis();
                            new Index1(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            break;
                        case 2:
                            startTime = System.currentTimeMillis();
                            new Index2(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            break;
                        case 3:
                            startTime = System.currentTimeMillis();
                            new Index3(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            break;
                        case 4:
                            startTime = System.currentTimeMillis();
                            new Index4(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            break;
                        case 5:
                            startTime = System.currentTimeMillis();
                            Index5a index5a = new Index5a(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            index5a = null;

                            startTime = System.currentTimeMillis();
                            Index5b index5b = new Index5b(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            index5b = null;

                            startTime = System.currentTimeMillis();
                            Index5c index5c = new Index5c(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            index5c = null;

                            startTime = System.currentTimeMillis();
                            Index5d index5d = new Index5d(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            break;
                        case 6:
                            startTime = System.currentTimeMillis();
                            new Index6a(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));

                            startTime = System.currentTimeMillis();
                            new Index6b(dataFile);
                            endTime = System.currentTimeMillis();
                            minutes = (double) (endTime - startTime) / (1000 * 60);
                            outputBuilder.append(String.format("%-12s", String.format("%.5f", minutes)));
                            break;
                    }
                }
                outputBuilder.append("\n");
            }
            writer.print(outputBuilder.toString());
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
        return unit.equalsIgnoreCase("MB") && size > 5 || unit.equalsIgnoreCase("GB") || unit.equalsIgnoreCase("TB");
    }

    private static boolean shouldSkipIndex1(String fileSize) {
        String unit = fileSize.replaceAll("[^a-zA-Z]", "");
        long size = Long.parseLong(fileSize.replaceAll("[^0-9]", ""));
        return unit.equalsIgnoreCase("MB") && size > 200 || unit.equalsIgnoreCase("GB") || unit.equalsIgnoreCase("TB");
    }

    private static boolean shouldSkipIndex2(String fileSize) {
        String unit = fileSize.replaceAll("[^a-zA-Z]", "");
        long size = Long.parseLong(fileSize.replaceAll("[^0-9]", ""));
        return unit.equalsIgnoreCase("MB") && size > 200 || unit.equalsIgnoreCase("GB") || unit.equalsIgnoreCase("TB");
    }
}
