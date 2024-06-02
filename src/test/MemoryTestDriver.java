package test;

import core.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MemoryTestDriver {

    public static final String OLIVER_WINDOWS = "C:\\Users\\olski\\Desktop\\bachelor\\data-files\\";
    public static final String OLIVER_MAC = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/";
    public static final String ADAM_MAC = "/Users/Adam/IdeaProjects/bachelor-project-search-engine/data-files/";

    public static final String CURRENT_FILE_PATH = OLIVER_MAC;

    public static void main(String[] args) {

        // List of all data files
        List<String> dataFiles = Arrays.asList(
               // CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100KB.txt",
               // CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_1MB.txt"
               // , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_2MB.txt"
               //  , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_5MB.txt"
               //  , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_10MB.txt"
               //  , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_20MB.txt"
               //  , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_50MB.txt"
               //  , CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100MB.txt"
                 // CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_200MB.txt"
                  //CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_400MB.txt"
                  //CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_800MB.txt"
                 //CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004.txt"
        );

        StringBuilder outputBuilder = new StringBuilder();

        // Header row for the output table
        outputBuilder.append(String.format("%-10s", "File Size"));
        for (int index = 1; index <= 6; index++) {
            if (index == 5) {
                outputBuilder.append(String.format("%-15s%-15s%-15s", "Index 5a (MB)", "Index 5b (MB)", "Index 5c (MB)"));
            } else if (index == 6) {
                outputBuilder.append(String.format("%-15s%-15s", "Index 6a (MB)", "Index 6b (MB)"));
            } else {
                outputBuilder.append(String.format("%-15s", "Index " + index + " (MB)"));
            }
        }
        outputBuilder.append("\n");

        try (PrintWriter writer = new PrintWriter("memory-test-output.txt", "UTF-8")) {
            for (String dataFile : dataFiles) {
                String fileSize = extractFileSize(dataFile);
                boolean skipIndex3 = shouldSkipIndex3(fileSize);
                boolean skipIndex1 = shouldSkipIndex1(fileSize);
                boolean skipIndex2 = shouldSkipIndex2(fileSize);

                outputBuilder.append(String.format("%-10s", fileSize));

                for (int index = 1; index <= 6; index++) {
                    double totalMegabytesUsed;
                    if (index == 3 && skipIndex3) {
                        outputBuilder.append(String.format("%-15s", "Skipped"));
                        continue;
                    }
                    if (index == 1 && skipIndex1) {
                        outputBuilder.append(String.format("%-15s", "Skipped"));
                        continue;
                    }
                    if (index == 2 && skipIndex2) {
                        outputBuilder.append(String.format("%-15s", "Skipped"));
                        continue;
                    }

                    switch (index) {
                        case 1:
                            Index1 index1 = new Index1(dataFile);
                            totalMegabytesUsed = index1.totalBytesUsed / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index1 = null;  // Release the reference
                            break;
                        case 2:
                            Index2 index2 = new Index2(dataFile);
                            totalMegabytesUsed = index2.totalBytesUsed / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index2 = null;  // Release the reference
                            break;
                        case 3:
                            Index3 index3 = new Index3(dataFile);
                            totalMegabytesUsed = index3.totalBytesUsed / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index3 = null;  // Release the reference
                            break;
                        case 4:
                            Index4 index4 = new Index4(dataFile);
                            totalMegabytesUsed = index4.totalBytesUsed / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index4 = null;  // Release the reference
                            break;
                        case 5:
                            Index5a index5a = new Index5a(dataFile);
                            totalMegabytesUsed = index5a.getTotalBytesUsed() / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index5a = null;  // Release the reference

                            Index5b index5b = new Index5b(dataFile);
                            totalMegabytesUsed = index5b.getTotalBytesUsed() / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index5b = null;  // Release the reference

                            Index5c index5c = new Index5c(dataFile);
                            totalMegabytesUsed = index5c.getTotalBytesUsed() / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index5c = null;  // Release the reference
                            break;
                        case 6:
                            Index6a index6a = new Index6a(dataFile);
                            totalMegabytesUsed = index6a.totalBytesUsed / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index6a = null;  // Release the reference

                            Index6b index6b = new Index6b(dataFile);
                            totalMegabytesUsed = index6b.totalBytesUsed / (1024.0 * 1024.0);
                            outputBuilder.append(String.format("%-15s", String.format("%.2f", totalMegabytesUsed)));
                            index6b = null;  // Release the reference
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
