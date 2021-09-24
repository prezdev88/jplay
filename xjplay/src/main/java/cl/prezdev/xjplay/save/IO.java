package cl.prezdev.xjplay.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// @TODO: Eliminar esta clase, ya que
// se supone que voy a almacenar todo en xml o json
public class IO {
    /**
     * Si la ruta o archivo no existe, se creará
     *
     * @param object El objeto que Quieres guardar
     * @param path   Usa el caracter "/" como separador
     */
    public static void writeObject(Object object, String path) throws IOException {
        /*si la ruta no existe, la creo*/
        if (!new File(path).exists()) {
            String[] folders = path.split("/");
            int index = folders.length - 1;
            String file = folders[index];

            if (folders.length != 1) {// si es != 1, quiere decir que quiere crear carpetas tambien,
                String foldersPath = "";
                for (String folder : folders) {
                    /*si la carpeta es distinta al archivo, lo agrego a la ruta*/
                    if (!folder.equalsIgnoreCase(file)) {
                        foldersPath += folder + "/";
                    }
                }
                //Creo los directorios necesarios
                new File(foldersPath).mkdirs();
            }
            //creo el archivo en la ruta especificada
            new File(path).createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);

        objectOutputStream.close();
        fileOutputStream.close();
    }

    public static Object readObject(String ruta) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(ruta);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return objectInputStream.readObject();
    }
}
