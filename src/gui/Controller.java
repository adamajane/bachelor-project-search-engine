package gui;

import core.Index7;
import core.IndexGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class Controller {
    private Index7 model;
    private View view;

    public Controller(IndexGUI model, View view) {
        this.model = model;
        this.view = view;
        initView();
    }

    public void initView() {
        view.addSearchButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // Add key listener to the search field in the view
        view.getSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }

    public void performSearch() {
        String searchString = view.getSearchQuery();
        List<String> results = model.search(searchString);
        view.displayResults(results);
    }
}