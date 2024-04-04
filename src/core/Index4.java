package core;

import java.io.*;
import java.util.Scanner;

class Index4 {

    private WikiItem[] hashTable;
    private int tableSize = 50007;
    private int numItems = 0; // Track number of items
    private double loadFactor = 0.75;
    public Index4() {
        hashTable = new WikiItem[tableSize];
    }

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
        DocumentList next;

        DocumentList(String documentName, DocumentList next) {
            this.documentName = documentName;
            this.next = next;
        }
    }

    public Index4(String filename) {
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
        hashValue = Math.abs(hashValue);

        // Reduce the hash value to fit within your table size
        hashValue = Math.abs((hashValue * 31) % tableSize);

        return hashValue;
    }



    private void addWordToIndex(String word, String docTitle) {
        int hashIndex = hash(word);
        WikiItem existingItem = findWikiItem(word);

        if (existingItem == null) {
            WikiItem newItem = new WikiItem(word, new DocumentList(docTitle, null), hashTable[hashIndex]);
            hashTable[hashIndex] = newItem;
        } else {
            addDocumentToWikiItem(existingItem, docTitle);
        }

        //System.out.println("Added word: " + word + " for document: " + docTitle);

        numItems++; // Increment the item count
        double currentLoadFactor = (double) numItems / tableSize;

        if (currentLoadFactor > loadFactor) {
            resizeHashTable();
        }
    }

    private void resizeHashTable() {
        System.out.println("Starting resize..."); // Log start

        int newTableSize = tableSize * 2;
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


    public void search(String searchString) {
        int hashIndex = hash(searchString);
        WikiItem foundItem = findWikiItem(searchString);

        if (foundItem != null) {
            System.out.println("Documents associated with '" + searchString + "':");
            DocumentList currentDoc = foundItem.documents;

            if (currentDoc == null) {
                System.out.println("  No documents found.");
            } else {
                while (currentDoc != null) {
                    System.out.println("  - " + currentDoc.documentName);
                    currentDoc = currentDoc.next;
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

        //System.out.println("Adding document '" + documentName + "' to WikiItem: " + item.searchString);
    }

    public static void main(String[] args) {
        String filePath = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/WestburyLab.wikicorp.201004_400MB.txt";
        //String filePath = "C:\\Users\\olski\\Desktop\\WestburyLab.wikicorp.201004_400MB.txt";

        System.out.println("Preprocessing " + filePath);
        Index4 index = new Index4(filePath);

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
