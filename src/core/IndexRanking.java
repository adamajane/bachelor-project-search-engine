package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static util.TestConfig.*;

class IndexRanking {

    private WikiItem[] hashTable;
    private int tableSize = 49999;
    private int numItems = 0; // Track the number of items
    private double loadFactor = 0.75;

    private class WikiItem {
        String searchString;
        DocumentList documents;
        WikiItem next;

        WikiItem(String s, DocumentList d, WikiItem n) {
            searchString = s;
            documents = d;
            next = n;
        }
    }

    private class DocumentList {
        String documentName;

        int count;
        DocumentList next;

        DocumentList(String documentName, int count, DocumentList next) {
            this.documentName = documentName;
            this.count = count;
            this.next = next;
        }
    }

    public IndexRanking(String filename) {
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

    // using modulus instead of logical AND, reduced the running time by half!!
    // using java inbuilt hash function on strings now further increased runtime by 20-25%
    private int hash(String word) {
        // Use the built-in hashCode() method
        int hashValue = word.hashCode();

        // Ensure the hash value is non-negative
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
            DocumentList newDocList = new DocumentList(docTitle, 1, null);
            WikiItem newItem = new WikiItem(word, newDocList, hashTable[hashIndex]);
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

        for (int i = 0; i < tableSize; i++) {
            WikiItem item = hashTable[i];
            while (item != null) {
                System.out.println("Rehashing item: " + item.searchString); // Log item
                int newIndex = rehash(item.searchString, newTableSize);

                WikiItem nextItem = item.next; // Save the next item

                // Insert at the head of the list in the new table
                item.next = tempTable[newIndex];
                tempTable[newIndex] = item;

                item = nextItem; // Move to the next item in the old list
            }
        }

        hashTable = tempTable;
        tableSize = newTableSize;

        System.out.println("Resize complete. New size: " + tableSize); // Log end
    }

    private int rehash(String word, int newSize) {
        int hashValue = word.hashCode();
        hashValue = hashValue & 0x7fffffff;
        hashValue = hashValue % newSize;
        return hashValue;
    }


    public void search(String searchString) {
        int hashIndex = hash(searchString);
        WikiItem foundItem = findWikiItem(searchString);

        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");
            DocumentList currentDoc = foundItem.documents;

            if (currentDoc == null) {
                System.out.println("  No documents found.");
            } else {
                // Create a list from the linked list of documents
                List<DocumentList> docs = new ArrayList<>();
                while (currentDoc != null) {
                    docs.add(currentDoc);
                    currentDoc = currentDoc.next;
                }

                // Sort the list in descending order of count
                Collections.sort(docs, (doc1, doc2) -> Integer.compare(doc2.count, doc1.count));

                // Print the sorted list of documents
                for (DocumentList doc : docs) {
                    System.out.println("  - " + doc.documentName + " (count: " + doc.count + ")");
                }
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
                //System.out.println("Found WikiItem for: " + searchString);
                return current;
            }
            current = current.next;
        }

        return null; // Item not found
    }


    private void addDocumentToWikiItem(WikiItem item, String documentName) {
        DocumentList docList = item.documents;
        while (docList != null) {
            if (docList.documentName.equals(documentName)) {
                docList.count++; // Increment the count if the document is already in the list
                return;
            }
            docList = docList.next;
        }
        // If the document is not in the list, add it with a count of 1
        item.documents = new DocumentList(documentName, 1, item.documents);

        //System.out.println("Adding document '" + documentName + "' to WikiItem: " + item.searchString);
    }

    public static void main(String[] args) {
        // String filePath = "...";

        System.out.println("Preprocessing " + FULL_FILE_PATH);
        IndexRanking index = new IndexRanking(FULL_FILE_PATH);

        Scanner console = new Scanner(System.in);
        while (true) {
            System.out.println("Input search string or type 'exit' to stop");
            String searchString = console.nextLine();
            if (searchString.equals("exit")) {
                break;
            }
            index.search(searchString);
        }
        console.close();
    }
}
