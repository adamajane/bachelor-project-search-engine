package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Index6a {

    /*
    This index implements compression techniques to further reduce memory usage.

    It builds up Index5a, which uses an ArrayList for the index array.

    In addition, it uses a difference array and variable byte encoding for storing document IDs to make the
    index more compact.
    */
    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private ArrayList<String> documentNames;
    private int numItems = 0;
    private double loadFactor = 0.75;
    public long totalBytesUsed = 0;

    private class WikiItem {
        String searchString;
        ByteArrayOutputStream documentDiffs;
        WikiItem next;
        int lastDocId;// Cache the last document ID

        WikiItem(String s, int firstDocId, WikiItem n) {
            this.searchString = s;
            this.documentDiffs = new ByteArrayOutputStream();
            this.lastDocId = firstDocId;
            try {
                writeVByte(firstDocId, this.documentDiffs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.next = n;
            totalBytesUsed += estimateMemoryUsage(s);
            totalBytesUsed += estimateMemoryUsage(this);
        }
    }

    private void writeVByte(int value, ByteArrayOutputStream output) throws IOException {
        ArrayList<Integer> bytes = new ArrayList<>();
        while (true) {
            bytes.add(0, value % 128); // PREPEND(bytes, n mod 128)
            if (value < 128) {
                break;
            }
            value = value / 128; // n div 128
        }
        bytes.set(bytes.size() - 1, bytes.get(bytes.size() - 1) + 128); // Modify the last element
        for (int b : bytes) {
            output.write(b);
        }
    }


    private int readVByte(ByteArrayInputStream input) {
        int n = 0;
        while (true) {
            int b = input.read();
            if (b >= 128) {
                n = 128 * n + (b - 128);
                break;
            } else {
                n = 128 * n + b;
            }
        }
        return n;
    }

    public Index6a(String filename) {
        long startTime = System.currentTimeMillis();
        hashTable = new WikiItem[tableSize];
        totalBytesUsed += estimateMemoryUsage(hashTable);
        documentNames = new ArrayList<>();

        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");

            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder();
            boolean readingTitle = true;

            while (input.hasNext()) {
                String word = input.next();

                if (readingTitle) {
                    if (currentTitle == null) {
                        currentTitle = word;
                    } else {
                        currentTitle = currentTitle + " " + word;
                    }

                    if (word.endsWith(".")) {
                        readingTitle = false;
                        documentNames.add(currentTitle);
                        totalBytesUsed += estimateMemoryUsage(currentTitle);
                    }
                } else {
                    if (word.equals("---END.OF.DOCUMENT---")) {
                        Scanner contentScanner = new Scanner(documentContent.toString());
                        while (contentScanner.hasNext()) {
                            addWordToIndex(contentScanner.next(), documentNames.size() - 1);
                        }
                        readingTitle = true;
                        currentTitle = null;
                        documentContent.setLength(0);
                        contentScanner.close();
                    } else {
                        documentContent.append(word).append(" ");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }

        totalBytesUsed += estimateMemoryUsage(documentNames);

        long endTime = System.currentTimeMillis();
        double minutes = (double) (endTime - startTime) / (1000 * 60);
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
        System.out.println("Total memory used: " + totalBytesUsed + " bytes (" + totalBytesUsed / (1024 * 1024) + " MB).");
    }

    private int hash(String word) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % tableSize;
        return hashValue;
    }

    private void addWordToIndex(String word, int docId) {
        double currentLoadFactor = (double) (numItems + 1) / tableSize;

        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }

        int hashIndex = hash(word);
        WikiItem existingItem = findWikiItem(word);

        if (existingItem == null) {
            WikiItem newItem = new WikiItem(word, docId, hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
            numItems++;
        } else {
            if (existingItem.lastDocId != docId) {
                try {
                    writeVByte(docId - existingItem.lastDocId, existingItem.documentDiffs);
                    existingItem.lastDocId = docId;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int nextPrime(int input) {
        int counter;
        boolean prime = false;
        int num = input;

        while (!prime) {
            num++;
            prime = true;
            int sqrt = (int) Math.sqrt(num);

            for (counter = 2; counter <= sqrt; counter++) {
                if (num % counter == 0) {
                    prime = false;
                    break;
                }
            }
        }

        return num;
    }

    private void resizeHashTable() {
        System.out.println("Starting resize...");

        int newTableSize = nextPrime(tableSize * 2);
        WikiItem[] tempTable = new WikiItem[newTableSize];

        for (int i = 0; i < tableSize; i++) {
            WikiItem item = hashTable[i];
            while (item != null) {
                System.out.println("Rehashing item: " + item.searchString);
                int newIndex = rehash(item.searchString, newTableSize);

                WikiItem nextItem = item.next;

                item.next = tempTable[newIndex];
                tempTable[newIndex] = item;

                item = nextItem;
            }
        }

        hashTable = tempTable;
        tableSize = newTableSize;
        totalBytesUsed += estimateMemoryUsage(tempTable);

        System.out.println("Resize complete. New size: " + tableSize);
    }

    private int rehash(String word, int newSize) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % newSize;
        return hashValue;
    }

    public void search(String searchString) {
        WikiItem foundItem = findWikiItem(searchString);

        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");
            byte[] encodedDiffs = foundItem.documentDiffs.toByteArray();
            ByteArrayInputStream input = new ByteArrayInputStream(encodedDiffs);

            int docId = readVByte(input);
            System.out.println(docId + "  - " + documentNames.get(docId));
            while (input.available() > 0) {
                docId += readVByte(input);
                System.out.println(docId + "  - " + documentNames.get(docId));
            }
        } else {
            System.out.println(searchString + " not found in the index.");
        }
    }

    private WikiItem findWikiItem(String searchString) {
        int hashIndex = hash(searchString);
        WikiItem current = hashTable[hashIndex];

        while (current != null) {
            if (current.searchString.equals(searchString)) {
                return current;
            }
            current = current.next;
        }

        return null;
    }

    private long estimateMemoryUsage(WikiItem item) {
        long memoryUsage = 12 + 4 + 4 + 4;
        memoryUsage += estimateMemoryUsage(item.documentDiffs);
        return memoryUsage;
    }

    private long estimateMemoryUsage(ByteArrayOutputStream outputStream) {
        int size = outputStream.size();
        long memoryUsage = 12 + 4 + 4 + 4;
        memoryUsage += 12 + size;
        return memoryUsage;
    }

    private long estimateMemoryUsage(WikiItem[] array) {
        return 12 + (array.length * 4);
    }

    private long estimateMemoryUsage(ArrayList<String> arrayList) {
        long arrayListMemory = 12 + 4 + 4 + 4;
        if (arrayList.size() > 0) {
            arrayListMemory += 12 + (arrayList.size() * 4);
            for (String s : arrayList) {
                arrayListMemory += estimateMemoryUsage(s);
            }
        }
        return arrayListMemory;
    }

    private long estimateMemoryUsage(String s) {
        int numChars = s.length();
        int memoryUsage = 8 * (int) Math.ceil(((numChars * 2) + 38) / 8.0);
        return memoryUsage;
    }
}
