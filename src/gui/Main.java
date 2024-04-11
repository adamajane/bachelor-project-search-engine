package gui;

import core.Index7;

import javax.swing.*;

import static util.Config.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Index7 model = new Index7(FILE_PATH2);
            View view = new View();
            Controller controller = new Controller(model, view);
        });
    }
}