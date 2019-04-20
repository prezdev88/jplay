package jplay.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import jlog.model.Log;
import jlog.model.UpdateLogUI;

/**
 *
 * @author pperezp
 */
public class Biblioteca implements Serializable{

    private List<Cancion>   canciones;
    private List<Cancion>   favoritos;
    private List<Album>     albums;
    private List<File>      rutas; // rutas para analizar
    
    private long            msBiblioteca;
    private long            msMasEscuchadas;
    private long            msFavoritos;

    public Biblioteca() {
        albums              = new ArrayList<>();
        canciones           = new ArrayList<>();
        favoritos           = new ArrayList<>();
        rutas               = new ArrayList<>();
        
        msBiblioteca     = 0;
        msMasEscuchadas  = 0;
        msFavoritos      = 0;
    }
    
    public String getDuracionBiblioteca(){
        return "Biblioteca --> "+getDuracion(msBiblioteca);
    }
    
    public String getDuracionFavoritos(){
        return "Favoritos --> "+getDuracion(msFavoritos);
    }
    
    public String getDuracionMasEscuchadas(){
        return "Más escuchadas --> "+getDuracion(msMasEscuchadas);
    }
    
    public void addFavorita(Cancion c){
        favoritos.add(c);
        Log.add("Añadida a favoritos: "+c);
        msFavoritos += c.getDuracionEnMilis();
    }
    
    public void removeFavorita(Cancion c){
        favoritos.remove(c);
        Log.add("Removido de favoritos: "+c);
        msFavoritos -= c.getDuracionEnMilis();
    }
    
    public boolean isFavorita(Cancion c){
        return favoritos.contains(c);
    }

    public List<Cancion> getFavoritos() {
        return favoritos;
    }
    
    public void addRuta(File ruta){
        if(!rutas.contains(ruta)){
            rutas.add(ruta);
        }
    }

    public List<File> getRutas() {
        return rutas;
    }
    
    public boolean removerRuta(File ruta){
        return rutas.remove(ruta);
    }
    
    public void add(Cancion c) {
        if (!estaCancion(c)) {
            this.canciones.add(c);
            msBiblioteca += c.getDuracionEnMilis();
        }
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void remover(Cancion c) {
        canciones.remove(c);
    }

    public int removerNoExistentes() {
        Iterator<Cancion> iterator = canciones.iterator();
        Cancion c;
        int cont = 0;
        while (iterator.hasNext()) {
            c = iterator.next();

            if (!c.exists()) {
                iterator.remove();
                cont++;
            }
        }

        return cont;
    }

    public boolean estaCancion(Cancion c) {
        return canciones.contains(c);
    }

    public List<Cancion> getCancionesMasReproducidas() {
        List<Cancion> topCanciones = new ArrayList<>();
        msMasEscuchadas = 0;

        for (Cancion c : canciones) {
            if (c.getCantidadReproducciones() != 0) {
                topCanciones.add(c);
                msMasEscuchadas += c.getDuracionEnMilis();
            }
        }

        /*acá tengo que ordenarlas (de la mas reproducida a la menos)*/
 /*------ Proceso de ordenado de lista ------*/
        Collections.sort(topCanciones, new Comparator<Cancion>() {
            @Override
            public int compare(Cancion o1, Cancion o2) {
                if (o1.getCantidadReproducciones() > o2.getCantidadReproducciones()) {
                    return -1;
                } else if (o1.getCantidadReproducciones() < o2.getCantidadReproducciones()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        /*------ Proceso de ordenado de lista ------*/
        
        return topCanciones;
    }
//    
//    public Cancion getCancion(Cancion c){
//        for(Cancion cancion : CANCIONES){
//            if(cancion.getNombre().equalsIgnoreCase(c.getNombre()) &&
//                    cancion.getAutor().equalsIgnoreCase(c.getAutor()) &&
//                    cancion.getAlbum().equalsIgnoreCase(c.getAlbum())){
//                return cancion;
//            }
//        }
//        
//        return null;
//    }

    public void procesarAlbums() {
        for (Cancion cancion : canciones) {
            addToAlbum(cancion);
        }

//        printAlbums();
    }

    private void addToAlbum(Cancion c) {
        boolean encontrado = false;
        for (Album a : albums) {
            // si el album es igual que el album de la cancion
            if (a.getName().trim().equalsIgnoreCase(c.getAlbum().trim())) {
                // y si la cancion no existe en el album
                if (!a.existCancion(c)) { // este if es por el issue #12
                    // la añado!
                    a.addCancion(c);
                }
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            Album a = new Album(c.getAutor(), c.getAlbum(), c.getAnio());
            a.addCancion(c);

            albums.add(a);
        }
    }

    public void printAlbums() {
        for (Album album : albums) {
            Log.add(album.toString());
            Log.add("Canciones: " + album.getCanciones().size());
        }
    }

    public Album getAlbum(Cancion c) {
        for (Album a : albums) {
            if (a.existCancion(c)) {
                Log.add("Album encontrado! : "+a);
                return a;
            }
        }
        return null;
    }
    
    public List<Album> getAlbumsByArtist(String nombreArtista) {
        List<Album> lista = new ArrayList<>();
        for (Album a : albums) {
            if (a.getArtist().equalsIgnoreCase(nombreArtista)) {
                Log.add("Album encontrado! : "+a);
                lista.add(a);
            }
        }
        return lista;
    }

    public List<String> getArtistas() {
        List<String> lista = new ArrayList<>();
        String artista;
        for (Album album : albums) {
            artista = album.getArtist().trim().toLowerCase();

            if (!artista.equals("")) {
                if (!lista.contains(artista)) {
                    lista.add(artista);
                }
            }
        }

        return lista;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setUpdateLogUI(UpdateLogUI update){
        Log.setUpdateLogUI(update);
    }

    public void setMilisBiblioteca(long milisBiblioteca) {
        this.msBiblioteca = milisBiblioteca;
    }

    public void setMilisMasEscuchadas(long milisMasEscuchadas) {
        this.msMasEscuchadas = milisMasEscuchadas;
    }

    public void setMilisFavoritos(long milisFavoritos) {
        this.msFavoritos = milisFavoritos;
    }

    /**
     * Este método le pasamos milisegundos y lo entrega como dias, horas, 
     * minutos, segundos como String
     * @param ms
     * @return 
     */
    public String getDuracion(long ms) {
        long resto = ms;
        
        long dias, horas, minutos, segundos;
        
        dias = ms / 86400000;
        
        if(dias != 0){
            resto = ms % 86400000;
        }
        
        horas = resto / 3600000;
        
        if(horas != 0){
            resto = resto % 3600000;
        }
        
        minutos = resto / 60000;
        
        if(minutos != 0){
            resto = resto % 60000;
        }
        
        segundos = resto / 1000;
        
        if(segundos != 0){
            ms = resto % 1000;
        }
        
        return "["+dias +"d. "+horas+"h. "+minutos+"m. "+segundos+"s. "+ms+" ms.] ";
    }
}
