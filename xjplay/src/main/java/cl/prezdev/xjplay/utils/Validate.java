package cl.prezdev.xjplay.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Validate {
    public static boolean isSong(File file) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());

        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("audio/mpeg")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Esta funcion la hice para que no aparecieran las carpetas ocultas en linux
     *
     * @param file
     * @return
     */
    public static boolean isHiddenFile(File file) {
        String nombre = file.getName();

        return nombre.charAt(0) == '.';
    }
}
