package gui;

import core.IndexGUI;

import javax.swing.*;

import static util.Config.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IndexGUI model = new IndexGUI(FULL_FILE_PATH);
            View view = new View();
            Controller controller = new Controller(model, view);
        });
    }
}