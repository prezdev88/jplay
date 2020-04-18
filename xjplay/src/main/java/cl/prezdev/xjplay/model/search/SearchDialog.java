package cl.prezdev.xjplay.model.search;

import cl.prezdev.jlog.Log;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class SearchDialog extends javax.swing.JDialog {

    private SearchListener searchListener;
    private InputMap inputMap;
    private final Component editorComponent;
    private final ActionMap actionMap;
    
    public SearchDialog(Frame frame, boolean modal) {
        super(frame, modal);
        this.setUndecorated(true);
        
        initComponents();
        
        inputMap = getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW);
        actionMap = getRootPane().getActionMap();
        editorComponent = searchCombobox.getEditor().getEditorComponent();
        
        init();
    }
    
    private void searchComboboxKeyReleased() {
        try {
            Object editorItem = searchCombobox.getEditor().getItem();
            String searchText = editorItem.toString().toLowerCase().trim();
            
            searchListener.search(searchText);
        } catch (Exception ex) {
            Log.add(ex.getMessage());
        }
    }

    public void setSearchable(SearchListener searchable) {
        this.searchListener = searchable;
        searchable.loadSearchCombobox(searchCombobox);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchCombobox = new javax.swing.JComboBox<>();
        cancelSearchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        searchCombobox.setEditable(true);
        searchCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "asd", "canni", "creed" }));
        searchCombobox.setOpaque(false);
        searchCombobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                searchComboboxItemStateChanged(evt);
            }
        });

        cancelSearchButton.setText("Cancelar");
        cancelSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelSearchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(searchCombobox, 0, 338, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(cancelSearchButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(searchCombobox)
                .addComponent(cancelSearchButton))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelSearchButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelSearchButtonActionPerformed

    private void searchComboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchComboboxItemStateChanged
        try {
            Object editorItem = searchCombobox.getEditor().getItem();
            String searchText = editorItem.toString().toLowerCase().trim();
            
            searchListener.search(searchText);
        } catch (Exception e) {
            Log.add(e.getMessage());
        }
    }//GEN-LAST:event_searchComboboxItemStateChanged

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SearchDialog searchDialog = new SearchDialog(new JFrame(), true);
                
                searchDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                
                searchDialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelSearchButton;
    private javax.swing.JComboBox<String> searchCombobox;
    // End of variables declaration//GEN-END:variables

    private void init() {
        // @TODO: Arreglar "buscar" hardcode
        /*Con escape, f3 y control f se desaparece el dialogo*/
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "buscar");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "buscar");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), "buscar");
        /*Con escape, f3 y control f se desaparece el dialogo*/

        actionMap.put("buscar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchDialog.this.setVisible(false);
            }
        });
        
        editorComponent.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {}

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() != KeyEvent.VK_ENTER) {
                    searchComboboxKeyReleased();
                } else {
                    SearchDialog.this.setVisible(false);

                    Object editorItem = searchCombobox.getEditor().getItem();
                    String searchText = editorItem.toString().toLowerCase().trim();

                    searchListener.focusOn(searchText);
                }
            }
        });
        
        AutoCompleteDecorator.decorate(searchCombobox);
    }
}
