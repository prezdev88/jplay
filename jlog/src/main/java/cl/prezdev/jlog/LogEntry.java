package cl.prezdev.jlog;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class LogEntry implements Serializable{
    private Date date;
    private Object message;

    public LogEntry(Date date, Object message) {
        this.date = date;
        this.message = message;
    }
}
