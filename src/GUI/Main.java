package GUI;

import core.Index6;
import GUI.SearchEngineGUI;
import GUI.Controller;
import util.Config;

import javax.swing.*;

import static util.Config.FILE_PATH2;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Index6 model = new Index6(FILE_PATH2);
            SearchEngineGUI view = new SearchEngineGUI();
            Controller controller = new Controller(model, view);
        });
    }
}