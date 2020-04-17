package cl.prezdev.xjplay.model.busqueda;

import javax.swing.JComboBox;

public interface IBuscar {
    void search(String filtro);
    void focusOn(String filtro);
    void cargarComboDeBusqueda(JComboBox cbo);
}
