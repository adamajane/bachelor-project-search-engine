import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

class Index3 {

    //WikiItem start;

    private WikiItem index; // Represents the head of our main index

    public Index3() {
        index = null;
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
    // add documentlist class, that can be used to create a linked list of documents,
// and attach it to each wikiitem object.
    private class DocumentList {
        String documentName;
        DocumentList next;

        DocumentList(String documentName, DocumentList next) {
            this.documentName = documentName;
            this.next = next;
        }
    }

    // changed the index from using scanner to buffered reader. It has a larger default buffer size and is typically faster for file reading.
    public Index3(String filename) {
        long startTime = System.currentTimeMillis(); // Start timing

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String currentTitle = null;
            StringBuilder documentContent = new StringBuilder();
            boolean readingTitle = true;
            String line;

            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");

                for (String word : words) {
                    if (readingTitle) {
                        if (word.endsWith(".")) {
                            currentTitle = documentContent.toString() + word;
                            readingTitle = false;
                        } else {
                            documentContent.append(word).append(" ");
                        }
                    } else {
                        if (word.equals("---END.OF.DOCUMENT---")) {
                            String content = documentContent.toString();
                            String[] contentWords = content.split("\\s+");
                            for (String w : contentWords) {
                                addWordToIndex(w, currentTitle);
                            }

                            readingTitle = true;
                            currentTitle = null;
                            documentContent.setLength(0);
                        } else {
                            documentContent.append(word).append(" ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis(); // End timing
        double minutes = (double) (endTime - startTime) / (1000 * 60); // Convert to minutes with decimals
        System.out.println("Preprocessing completed in " + minutes + " minutes.");
    }



    private void addWordToIndex(String word, String docTitle) {
        WikiItem existingItem = findWikiItem(word);

        // If the word doesn't exist in the index yet
        if (existingItem == null) {
            // Create a new WikiItem and start its document list
            WikiItem newItem = new WikiItem(word, new DocumentList(docTitle, null), null);
            newItem.next = index; // Add as new head of main index
            index = newItem;
        } else {
            // Word exists, need to add document to its list
            addDocumentToWikiItem(existingItem, docTitle);
        }

        // Logging for debugging (remove these lines later)
        //System.out.println("Added word: " + word + " for document: " + docTitle);
    }




    public void search(String searchString) {
        long startTime = System.currentTimeMillis(); // Start timing
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
        long endTime = System.currentTimeMillis(); // End timing
        long elapsedTime = endTime - startTime;
        System.out.println("Preprocessing completed in " + elapsedTime + " milliseconds.");
    }


    public static void main(String[] args) {
        // Specify the file path
        String filePath = "C:\\Users\\olski\\Desktop\\WestburyLab.wikicorp.201004_100KB.txt";
        //String filePath = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/WestburyLab.wikicorp.201004_100KB.txt";

        System.out.println("Preprocessing " + filePath);
        Index3 index = new Index3(filePath);

        Scanner console = new Scanner(System.in);
        while (true) { // Simple loop for multiple searches
            System.out.println("Input search string or type 'exit' to stop");
            String searchString = console.nextLine();
            if (searchString.equals("exit")) {
                break;
            }
            index.search(searchString);
        }
        console.close();
    }




    // Finds a WikiItem with the given searchString or returns null
    private WikiItem findWikiItem(String searchString) {
        WikiItem current = index;
        while (current != null) {
            if (current.searchString.equals(searchString)) {
                //System.out.println("Found WikiItem for: " + searchString); // Debugging log
                return current;
            }
            current = current.next;
        }
        return null; // Item not found
    }


    // Adds a document to a WikiItem's DocumentList
    private void addDocumentToWikiItem(WikiItem item, String documentName) {
        DocumentList currentDoc = item.documents;

        // Check for duplicates
        while (currentDoc != null) {
            if (currentDoc.documentName.equals(documentName)) {
                //System.out.println("Document '" + documentName + "' already exists in WikiItem: " + item.searchString);
                return; // Document already exists, no need to add again
            }
            currentDoc = currentDoc.next;
        }

        // If DocumentList is empty
        if (item.documents == null) {
            item.documents = new DocumentList(documentName, null);
        } else {
            // Adding at the end of the DocumentList
            DocumentList newDoc = new DocumentList(documentName, null);
            currentDoc = item.documents; // Resetting currentDoc to the beginning

            while (currentDoc.next != null) {
                currentDoc = currentDoc.next;
            }

            currentDoc.next = newDoc;
        }

        //System.out.println("Adding document '" + documentName + "' to WikiItem: " + item.searchString);
    }





}
