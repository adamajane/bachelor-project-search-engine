import java.io.*;
import java.util.Scanner;

class Index3 {

    WikiItem start;

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

    public Index3(String filename) {
        try {
            Scanner input = new Scanner(new File(filename), "UTF-8");
            if (input.hasNext()) {
                String firstWord = input.next();
                start = new WikiItem(firstWord, null, null);
                WikiItem current = start;
                while (input.hasNext()) {
                    String word = input.next();
                    System.out.println(word);
                    DocumentList documentList = new DocumentList("default_document", null);
                    WikiItem tmp = new WikiItem(word, documentList, null);
                    current.next = tmp;
                    current = tmp;
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file " + filename);
        }
    }

    public boolean search(String searchstr) {
        WikiItem current = start;
        boolean found = false;

        while (current != null) {
            if (current.searchString.endsWith(".")) {
                // Potential document title
                String documentTitle = current.searchString;
                StringBuilder documentContent = new StringBuilder();

                // Read the content of the document until the end marker is found
                while (current != null && !current.searchString.equals("---END.OF.DOCUMENT---")) {
                    documentContent.append(current.searchString).append(" ");
                    current = current.next;
                }

                // Check if the search string is present in the document
                if (documentContent.toString().contains(searchstr)) {
                    System.out.println("Found in: Document Title: " + documentTitle);
                    found = true;
                }
            }
            if (current != null) {
                current = current.next;
            }
        }

        return found;
    }

    public static void main(String[] args) {
        // Specify the file path
        String filePath = "/Users/mr.brandt/Desktop/bachelor-project-search-engine/data-files/WestburyLab.wikicorp.201004_100KB.txt";

        System.out.println("Preprocessing " + filePath);
        Index3 i = new Index3(filePath);
        Scanner console = new Scanner(System.in);
        for (; ; ) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
            if (searchstr.equals("exit")) {
                break;
            }
            if (i.search(searchstr)) {
                System.out.println(searchstr + " exists");
            } else {
                System.out.println(searchstr + " does not exist");
            }
        }
        console.close();
    }
}
