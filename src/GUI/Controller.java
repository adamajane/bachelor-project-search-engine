package gui;

import core.Index6;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Controller {
    private Index6 model;
    private View view;

    public Controller(Index6 model, View view) {
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
    }

    public void performSearch() {
        String searchString = view.getSearchQuery();
        List<String> results = model.search(searchString);
        view.displayResults(results);
    }
}