package cl.prezdev.xjplay.model.search;

import javax.swing.JComboBox;

public interface SearchListener {
    void search(String searchText);
    void focusOn(String searchText);
    void loadSearchComboBox(JComboBox searchCombobox);
}
