package jplay.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Validar {
    public static boolean isCancion(File f) throws IOException{
        String mime = Files.probeContentType(f.toPath());
                    
        if(mime != null){
            if(mime.equalsIgnoreCase("audio/mpeg")){
                return true;
            }
        }
        
        return false;
    }

    /**
     * Esta funcion la hice para que no aparecieran las carpetas ocultas en linux
     * @param a
     * @return 
     */
    public static boolean isArchivoCorrecto(File a) {
        String nombre = a.getName();
        
        return nombre.charAt(0) != '.';
    }
}
