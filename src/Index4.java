import java.io.*;
import java.util.Scanner;

class Index4 {

    private WikiItem[] hashTable;
    private int tableSize = 15000;

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
        hashTable = new WikiItem[tableSize];

        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");

            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder();
            boolean readingTitle = true;

            while (input.hasNext()) {
                String word = input.next();

                if (readingTitle) {
                    if (word.endsWith(".")) {
                        currentTitle = word;
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
    }

    private int hash(String word) {
        int hashValue = 0;
        for (char c : word.toCharArray()) {
            hashValue += c;
        }
        System.out.println(hashValue & (tableSize - 1));
        return hashValue & (tableSize - 1);
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

        System.out.println("Added word: " + word + " for document: " + docTitle);
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
                System.out.println("Found WikiItem for: " + searchString);
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
                System.out.println("Document '" + documentName + "' already exists in WikiItem: " + item.searchString);
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

        System.out.println("Adding document '" + documentName + "' to WikiItem: " + item.searchString);
    }

    public static void main(String[] args) {
        String filePath = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/WestburyLab.wikicorp.201004_100KB.txt";

        System.out.println("Preprocessing " + filePath);
        Index3 index = new Index3(filePath);

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
