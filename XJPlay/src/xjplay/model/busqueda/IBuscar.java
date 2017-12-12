package xjplay.model.busqueda;

import javax.swing.JComboBox;

/**
 *
 * @author prez
 */
public interface IBuscar {
    void search(String filtro);
    void focusOn();
    void cargarComboDeBusqueda(JComboBox cbo);
}
