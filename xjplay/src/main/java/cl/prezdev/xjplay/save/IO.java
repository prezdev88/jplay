package cl.prezdev.xjplay.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// @TODO: Eliminar esta clase, ya que
// se supone que voy a almacenar todo en xml o json
public class IO {
    private static FileInputStream fis;
    private static FileOutputStream fos;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;
    
    /**
     * Si la ruta o archivo no existe, se creará
     * @param objeto El objeto que Quieres guardar
     * @param ruta Usa el caracter "/" como separador
     */
    public static void escribirObjetoEn(Object objeto, String ruta) throws IOException{
        /*si la ruta no existe, la creo*/
        if (!new File(ruta).exists()) {
            String[] carpetas = ruta.split("/");
            String archivo = carpetas[carpetas.length-1];

            if(carpetas.length != 1){// si es != 1, quiere decir que quiere crear carpetas tambien, 
                String rutaCarpetas = "";
                for(String carpeta : carpetas){
                    /*si la carpeta es distinta al archivo, lo agrego a la ruta*/
                    if(!carpeta.equalsIgnoreCase(archivo)){
                        rutaCarpetas += carpeta + "/";
                    }
                }
                //Creo los directorios necesarios
                new File(rutaCarpetas).mkdirs();
            }
            //creo el archivo en la ruta especificada
            new File(ruta).createNewFile();
        }
        fos = new FileOutputStream(ruta);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(objeto);
        
        oos.close();
        fos.close();
    }
    
    public static Object readObject(String ruta) throws FileNotFoundException, IOException, ClassNotFoundException{
        fis = new FileInputStream(ruta);
        ois = new ObjectInputStream(fis);
        return ois.readObject();
    }
}
