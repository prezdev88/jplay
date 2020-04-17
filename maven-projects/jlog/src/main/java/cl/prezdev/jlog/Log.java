package cl.prezdev.jlog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log implements Serializable{
    
    private static List<LogEntry> log;
    private static UpdateLogUI update;
    
    static{ 
        log = new ArrayList<>();
    }
    
    public static void setUpdateLogUI(UpdateLogUI update){
        Log.update = update;
    }
    
    public static void add(String mensaje){
        LogEntry nle = new LogEntry(new Date(), mensaje);
        Log.log.add(nle);
        Log.update.updateLogUI(nle);
    }
    
    public static List<LogEntry> getEntrys(){
        return Log.log;
    }

    public static void setLogEntries(List<LogEntry> le) {
        Log.log = le;
    }
}
