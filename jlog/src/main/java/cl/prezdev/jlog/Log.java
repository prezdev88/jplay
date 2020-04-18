package cl.prezdev.jlog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log implements Serializable{
    
    private static List<LogEntry> logEntries;
    private static UpdateLogUI update;
    
    static{ 
        logEntries = new ArrayList<>();
    }
    
    public static void setUpdateLogUI(UpdateLogUI update){
        Log.update = update;
    }
    
    public static void add(String message){
        LogEntry logEntry = new LogEntry(new Date(), message);
        Log.logEntries.add(logEntry);
        Log.update.updateLogUI(logEntry);
    }
    
    public static void add(Object message) {
        Log.add(message.toString());
    }
    
    public static List<LogEntry> getEntries(){
        return Log.logEntries;
    }

    public static void setLogEntries(List<LogEntry> le) {
        Log.logEntries = le;
    }
}
