package test;

import core.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestDriverSearch {

    public static final String OLIVER_WINDOWS = "C:\\Users\\olski\\Desktop\\bachelor\\data-files\\";
    public static final String OLIVER_MAC = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/";
    public static final String ADAM_MAC = "/Users/Adam/IdeaProjects/bachelor-project-search-engine/data-files/";

    public static final String CURRENT_FILE_PATH = OLIVER_WINDOWS;
    public static final String SEARCH_WORD = "specificWordThatIsNotExisting";  // Replace with the word you want to search

    public static void main(String[] args) {

        // List of all data files
        List<String> dataFiles = Arrays.asList(
                CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_100KB.txt",
                CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_1MB.txt"
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
        for (int index = 1; index <= 6; index++) {
            if (index == 5) {
                outputBuilder.append(String.format("%-15s%-15s%-15s", "Index 5a", "Index 5b", "Index 5c"));
            } else if (index == 6) {
                outputBuilder.append(String.format("%-15s%-15s", "Index 6a", "Index 6b"));
            } else {
                outputBuilder.append(String.format("%-15s", "Index " + index));
            }
        }
        outputBuilder.append("\n");

        try (PrintWriter writer = new PrintWriter("test-output.txt", "UTF-8")) {
            for (String dataFile : dataFiles) {
                String fileSize = extractFileSize(dataFile);
                boolean skipIndex3 = shouldSkipIndex3(fileSize);
                boolean skipIndex1 = shouldSkipIndex1(fileSize);
                boolean skipIndex2 = shouldSkipIndex2(fileSize);

                outputBuilder.append(String.format("%-10s", fileSize));

                for (int index = 1; index <= 6; index++) {
                    if (index == 3 && skipIndex3) {
                        outputBuilder.append(String.format("%-15s", "Skipped"));
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
                            Index1 index1 = new Index1(dataFile);
                            logSearchTimeMillis(index1, SEARCH_WORD, outputBuilder);
                            break;
                        case 2:
                            Index2 index2 = new Index2(dataFile);
                            logSearchTimeMillis(index2, SEARCH_WORD, outputBuilder);
                            break;
                        case 3:
                            Index3 index3 = new Index3(dataFile);
                            logSearchTimeNanos(index3, SEARCH_WORD, outputBuilder);
                            break;
                        case 4:
                            Index4 index4 = new Index4(dataFile);
                            logSearchTimeNanos(index4, SEARCH_WORD, outputBuilder);
                            break;
                        case 5:
                            Index5a index5a = new Index5a(dataFile);
                            logSearchTimeNanos(index5a, SEARCH_WORD, outputBuilder);

                            Index5b index5b = new Index5b(dataFile);
                            logSearchTimeNanos(index5b, SEARCH_WORD, outputBuilder);

                            Index5c index5c = new Index5c(dataFile);
                            logSearchTimeNanos(index5c, SEARCH_WORD, outputBuilder);

                            Index5d index5d = new Index5d(dataFile);
                            logSearchTimeNanos(index5d, SEARCH_WORD, outputBuilder);
                            break;
                        case 6:
                            Index6a index6a = new Index6a(dataFile);
                            logSearchTimeNanos(index6a, SEARCH_WORD, outputBuilder);

                            Index6b index6b = new Index6b(dataFile);
                            logSearchTimeNanos(index6b, SEARCH_WORD, outputBuilder);
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

    private static void logSearchTimeMillis(Index1 index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.currentTimeMillis();
        boolean searchResult = index.search(searchWord);
        long searchEndTime = System.currentTimeMillis();
        long searchMillis = searchEndTime - searchStartTime;
        appendSearchTimeMillis(outputBuilder, searchMillis);
        logSearchResult(searchResult);
    }

    private static void logSearchTimeMillis(Index2 index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.currentTimeMillis();
        boolean searchResult = index.search(searchWord);
        long searchEndTime = System.currentTimeMillis();
        long searchMillis = searchEndTime - searchStartTime;
        appendSearchTimeMillis(outputBuilder, searchMillis);
        logSearchResult(searchResult);
    }

    private static void logSearchTimeNanos(Index3 index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index4 index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index5a index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index5b index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index5c index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index5d index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index6a index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void logSearchTimeNanos(Index6b index, String searchWord, StringBuilder outputBuilder) {
        long searchStartTime = System.nanoTime();
        index.search(searchWord);
        long searchEndTime = System.nanoTime();
        long searchNanos = searchEndTime - searchStartTime;
        appendSearchTimeNanos(outputBuilder, searchNanos);
        logSearchResult();
    }

    private static void appendSearchTimeMillis(StringBuilder outputBuilder, long searchMillis) {
        if (searchMillis > 1000) {
            double searchSeconds = (double) searchMillis / 1000;
            outputBuilder.append(String.format("%-15s", String.format("%.0f s", searchSeconds)));
        } else {
            outputBuilder.append(String.format("%-15s", String.format("%d ms", searchMillis)));
        }
    }

    private static void appendSearchTimeNanos(StringBuilder outputBuilder, long searchNanos) {
        if (searchNanos > 1_000_000) {
            double searchMillis = (double) searchNanos / 1_000_000;
            outputBuilder.append(String.format("%-15s", String.format("%.0f ms", searchMillis)));
        } else {
            outputBuilder.append(String.format("%-15s", String.format("%d ns", searchNanos)));
        }
    }

    private static void logSearchResult() {
        System.out.println("The search operation was completed.");
    }

    private static void logSearchResult(boolean searchResult) {
        if (searchResult) {
            System.out.println("The word '" + SEARCH_WORD + "' was found in the index.");
        } else {
            System.out.println("The word '" + SEARCH_WORD + "' was not found in the index.");
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
        return unit.equalsIgnoreCase("MB") && size > 2 || unit.equalsIgnoreCase("GB") || unit.equalsIgnoreCase("TB");
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
