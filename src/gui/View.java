package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class View extends JFrame {
    private JTextField searchField;
    private JTextArea resultsArea;
    private JButton searchButton = new JButton("Search");

    public View() {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Set the JFrame's layout manager

        // Create constraints for components
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        // Panel for search components
        JPanel searchPanel = new JPanel(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        searchPanel.add(searchField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0.1; // Button should not take up much horizontal space
        searchPanel.add(searchButton, constraints);

        // Add searchPanel to the north of the BorderLayout
        add(searchPanel, BorderLayout.NORTH);

        // Results area in the center for maximum space utilization
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        add(scrollPane, BorderLayout.CENTER);

        pack(); // Adjusts the JFrame size to fit the components
        setLocationRelativeTo(null); // Center the window
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


    public JTextField getSearchField() {
        return searchField;
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
