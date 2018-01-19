package jlog.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author prez
 */
public class LogEntry implements Serializable{
    private Date fecha;
    private Object mensaje;

    public LogEntry(Date fecha, Object mensaje) {
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    public Date getFecha() {
        return fecha;
    }

    public Object getMensaje() {
        return mensaje;
    }

    @Override
    public String toString() {
        return "LogEntry{" + "fecha=" + fecha + ", mensaje=" + mensaje + '}';
    }
    
    
}
