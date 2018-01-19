package jlog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author prez
 */
public class Log {
    //
    private static List<LogEntry> log;
    private static UpdateLogUI update;
    
    static{ 
        log = new ArrayList<>();
    }
    
    public static void setUpdateLogUI(UpdateLogUI update){
        Log.update = update;
    }
    
    public static void add(Object mensaje){
        LogEntry nle = new LogEntry(new Date(), mensaje);
        Log.log.add(nle);
        Log.update.updateLogUI(nle);
    }
    
    public static List<LogEntry> getEntrys(){
        return Log.log;
    }
}
