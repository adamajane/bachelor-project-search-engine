package core;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static util.Config.*;

public class Index6 {

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;

    private class WikiItem {
        String searchString;
        DocumentList documents;
        WikiItem next;

        WikiItem(String s, DocumentList d, WikiItem n) {
            this.searchString = s;
            this.documents = d;
            this.next = n;
        }
    }

    private class DocumentList {
        String documentName;
        DocumentList next;

        DocumentList(String documentName, DocumentList next) {
            this.documentName = documentName;
            this.next = next;
        }
    }

    public Index6(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing
        hashTable = new WikiItem[tableSize];

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
                        currentTitle = currentTitle + " " + word; // Append words
                    }

                    if (word.endsWith(".")) {
                        readingTitle = false;
                    }
                } else {
                    if (word.equals("---END.OF.DOCUMENT---")) {
                        Scanner contentScanner = new Scanner(documentContent.toString());
                        while (contentScanner.hasNext()) {
                            addWordToIndex(contentScanner.next(), currentTitle);
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
        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
    }

    // Using modulus instead of logical AND, reduced the running time by half!!
    // Using java inbuilt hash function on strings now further increased runtime by 20-25%
    private int hash(String word) {
        // Use the built-in hashCode() method
        int hashValue = word.hashCode();

        // Ensures that the hash value is non-negative
        hashValue = hashValue & 0x7fffffff;

        // Reduce the hash value to fit within your table size
        hashValue = hashValue % tableSize;

        return hashValue;
    }


    private void addWordToIndex(String word, String docTitle) {

        double currentLoadFactor = (double) numItems / tableSize;

        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }

        int hashIndex = hash(word);
        WikiItem existingItem = findWikiItem(word);

        if (existingItem == null) {
            WikiItem newItem = new WikiItem(word, new DocumentList(docTitle, null), hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
            numItems++; // Increment the item count
        } else {
            addDocumentToWikiItem(existingItem, docTitle);
        }

        //System.out.println("Added word: " + word + " for document: " + docTitle);

    }

    private int nextPrime(int input) {
        int counter;
        boolean prime = false;

        // Start searching for next prime number
        int num = input;

        while (!prime) {
            num++;
            prime = true;
            int sqrt = (int) Math.sqrt(num);

            for (counter = 2; counter <= sqrt; counter++) {
                if (num % counter == 0) {
                    prime = false;
                    break; // exit the inner for loop
                }
            }
        }

        return num;
    }

    private void resizeHashTable() {
        System.out.println("Starting resize..."); // Log start

        int newTableSize = nextPrime(tableSize * 2);
        WikiItem[] tempTable = new WikiItem[newTableSize];

        for (WikiItem item : hashTable) {
            while (item != null) {
                System.out.println("Rehashing item: " + item.searchString); // Log item
                int newIndex = hash(item.searchString) % newTableSize;

                WikiItem nextItem = item.next; // Save the next item

                // Create a new item with the same data but without the old 'next' reference
                WikiItem newItem = new WikiItem(item.searchString, item.documents, tempTable[newIndex]);
                tempTable[newIndex] = newItem;

                item = nextItem; // Move to the next item in the old list
            }
        }

        hashTable = tempTable;
        tableSize = newTableSize;

        System.out.println("Resize complete. New size: " + tableSize);  // Log end
    }

    // changed search method to return list instead of being void
    public List<String> search(String searchString) {
        int hashIndex = hash(searchString);
        WikiItem foundItem = findWikiItem(searchString);

        List<String> results = new ArrayList<>(); // Create a list to hold the results

        if (foundItem != null) {
            DocumentList currentDoc = foundItem.documents;
            while (currentDoc != null) {
                results.add(currentDoc.documentName);
                currentDoc = currentDoc.next;
            }
        }
        return results; // Return the list of results
    }

    private WikiItem findWikiItem(String searchString) {
        int hashIndex = hash(searchString);
        WikiItem current = hashTable[hashIndex];

        while (current != null) {
            if (current.searchString.equals(searchString)) {
                //System.out.println("Found WikiItem for: " + searchString);
                return current;
            }
            current = current.next;
        }

        return null; // Item not found
    }


    private void addDocumentToWikiItem(WikiItem item, String documentName) {
        DocumentList currentDoc = item.documents;

        while (currentDoc != null) {
            if (currentDoc.documentName.equals(documentName)) {
                //System.out.println("Document '" + documentName + "' already exists in WikiItem: " + item.searchString);
                return;
            }
            currentDoc = currentDoc.next;
        }

        if (item.documents == null) {
            item.documents = new DocumentList(documentName, null);
        } else {
            DocumentList newDoc = new DocumentList(documentName, null);
            currentDoc = item.documents;

            while (currentDoc.next != null) {
                currentDoc = currentDoc.next;
            }

            currentDoc.next = newDoc;
        }

        // System.out.println("Adding document '" + documentName + "' to WikiItem: " + item.searchString);
    }

    public static void main(String[] args) {
        // String filePath = "...";

        System.out.println("Preprocessing " + FILE_PATH2);
        Index6 index = new Index6(FILE_PATH2);

        Scanner console = new Scanner(System.in);
        while (true) {
            System.out.println("Input search string or type 'exit' to stop");
            String searchString = console.nextLine();
            if (searchString.equals("exit")) {
                break;
            }
            System.out.println(index.search(searchString));
        }
        console.close();
    }
}
