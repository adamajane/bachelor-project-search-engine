package util;

public class Config {

    // Oliver's Windows PC
    public static final String OLIVER_WINDOWS = "C:\\Users\\olski\\Desktop\\";

    // Oliver's MacBook Pro
    public static final String OLIVER_MAC = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/";

    // Adam's MacBook Pro
    public static final String ADAM_MAC = "/Users/Adam/IdeaProjects/bachelor-project-search-engine/data-files/";

    // Change the variables below to test different indexes and files

    // Specify which index to test, which file path to use, and which size to use
    public static final int INDEX_TO_TEST = 4;
    public static final String CURRENT_FILE_PATH = ADAM_MAC;
    public static final String FILE_SIZE = "100MB";

    // Construct the full file path
    public static final String FULL_FILE_PATH = CURRENT_FILE_PATH + "WestburyLab.wikicorp.201004_" + FILE_SIZE + ".txt";

}
