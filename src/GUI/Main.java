package gui;

import core.Index6;

import javax.swing.*;

import static util.Config.FILE_PATH2;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Index6 model = new Index6(FILE_PATH2);
            View view = new View();
            Controller controller = new Controller(model, view);
        });
    }
}