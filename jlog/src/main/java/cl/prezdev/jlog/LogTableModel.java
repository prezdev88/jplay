package cl.prezdev.jlog;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class LogTableModel implements TableModel{

    @Override
    public int getRowCount() {
        return Log.getEntries().size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex){
            case 0: return "Fecha";
            case 1: return "Mensaje";
            default:return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogEntry le = Log.getEntries().get(rowIndex);
        
        switch(columnIndex){
            case 0: return le.getDate();
            case 1: return le.getMessage();
            default:return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {}

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {}

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {}
    
}
