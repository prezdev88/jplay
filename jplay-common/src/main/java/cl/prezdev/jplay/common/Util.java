package cl.prezdev.jplay.common;

public class Util {
    /**
     * Este método le pasamos milisegundos y lo entrega como dias, horas,
     * minutos, segundos como String
     * @param milliseconds
     * @return
     */
    public static String getFormattedDuration(long milliseconds) {
        long remainder = milliseconds;

        long days, hours, minutes, seconds;

        days = milliseconds / 86400000;

        if(days != 0){
            remainder = milliseconds % 86400000;
        }

        hours = remainder / 3600000;

        if(hours != 0){
            remainder = remainder % 3600000;
        }

        minutes = remainder / 60000;

        if(minutes != 0){
            remainder = remainder % 60000;
        }

        seconds = remainder / 1000;

        if(seconds != 0){
            milliseconds = remainder % 1000;
        }

        return "["+days +"d. "+hours+"h. "+minutes+"m. "+seconds+"s. "+milliseconds+" ms.] ";
    }

    public static String getFormattedYear(String year) {
        final String NO_YEAR = "[----] ";
        if (year != null) {
            try {
                int an = Integer.parseInt(year.trim());

                return "[" + an + "] ";
            } catch (NumberFormatException e) {
                return NO_YEAR;
            }
        }else{
            return NO_YEAR;
        }
    }

    public static String getDurationAsString(long microseconds) {
        int milliseconds = (int) (microseconds / 1000);
        return getDurationAsString(milliseconds);
    }

    public static String getDurationAsString(int millisTime) {
        int seconds = (millisTime / 1000) % 60;
        int minutes = (millisTime / 1000) / 60;
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}
