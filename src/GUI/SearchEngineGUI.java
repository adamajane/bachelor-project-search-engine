package GUI;

import javax.swing.*;

public class SearchEngineGUI extends JFrame {
    private JTextField searchField;
    private JTextArea resultsArea;
    public SearchEngineGUI() {
        super("Search Engine"); // Set window title

        // GUI components will go here later...

        setSize(400, 300); // Set initial window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        resultsArea = new JTextArea(10, 30);

        // Add layout (we'll customize this later)
        JPanel panel = new JPanel();
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(resultsArea);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchEngineGUI());
    }
}