package GUI;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SearchEngineGUI extends JFrame {
    private JTextField searchField;
    private JTextArea resultsArea;
    private JButton searchButton = new JButton("Search");

    public SearchEngineGUI() {
        super("Search Engine"); // Set window title
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        searchField = new JTextField(20);
        resultsArea = new JTextArea(10, 30);
        resultsArea.setEditable(false); // Users should not edit this area directly
    }

    private void setupLayout() {
        setSize(700, 700); // Set initial window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add layout (we'll customize this later)
        JPanel panel = new JPanel();
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(new JScrollPane(resultsArea)); // Wrap resultsArea in a JScrollPane for scrolling
        add(panel);

        setVisible(true);
    }

    // Removed the instantiation of Index6 and the call to addSearchButtonListener

    // Method to attach the search button listener from the controller
    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public String getSearchQuery() {
        return searchField.getText();
    }

    public void displayResults(List<String> results) {
        resultsArea.setText(""); // Clear old results
        if (results.isEmpty()) {
            resultsArea.setText("No results found.");
        } else {
            for (String document : results) {
                resultsArea.append(document + "\n");
            }
        }
    }


}
