package cl.prezdev.jplay;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cl.prezdev.jlog.Log;
import cl.prezdev.jlog.UpdateLogUI;

public class MusicLibrary implements Serializable{

    private List<Song> songs;
    private List<Song> favoritesSongs;
    private List<Album> albums;
    private List<File> paths; // rutas para analizar
    
    private long msMusicLibrary;
    private long msMostPlayed;
    private long msFavorites;

    public MusicLibrary() {
        albums = new ArrayList<>();
        songs = new ArrayList<>();
        favoritesSongs = new ArrayList<>();
        paths = new ArrayList<>();
        
        msMusicLibrary = 0;
        msMostPlayed = 0;
        msFavorites = 0;
    }
    
    public String getLibraryDuration(){
        return "Biblioteca --> "+getFormattedDuration(msMusicLibrary);
    }
    
    public String getFavoritesDuration(){
        return "Favoritos --> "+getFormattedDuration(msFavorites);
    }
    
    public String getMostPlayedDuration(){
        return "Más escuchadas --> "+getFormattedDuration(msMostPlayed);
    }
    
    public void addFavoriteSong(Song song){
        favoritesSongs.add(song);
        Log.add("Añadida a favoritos: "+song);
        msFavorites += song.getMilisDuration();
    }
    
    public void removeFavoriteSong(Song song){
        favoritesSongs.remove(song);
        Log.add("Removido de favoritos: "+song);
        msFavorites -= song.getMilisDuration();
    }
    
    public boolean isFavoriteSong(Song song){
        return favoritesSongs.contains(song);
    }

    public List<Song> getFavoritesSongs() {
        return favoritesSongs;
    }
    
    public void addPath(File file){
        if(!paths.contains(file)){
            paths.add(file);
        }
    }

    public List<File> getPaths() {
        return paths;
    }
    
    public boolean removePath(File file){
        return paths.remove(file);
    }
    
    public void addSong(Song song) {
        if (!isSongInLibrary(song)) {
            this.songs.add(song);
            msMusicLibrary += song.getMilisDuration();
        }
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    /**
     * Método que elimina las canciones que no existen
     * @return 
     */
    public int cleanLibrary() {
        int count = 0;
        
        Iterator<Song> songsIterator = songs.iterator();
        Song song;
        
        while (songsIterator.hasNext()) {
            song = songsIterator.next();

            if (!song.exists()) {
                songsIterator.remove();
                count++;
            }
        }

        return count;
    }

    public boolean isSongInLibrary(Song c) {
        return songs.contains(c);
    }

    public List<Song> getMostPlayedSongs() {
        List<Song> topCanciones = new ArrayList<>();
        msMostPlayed = 0;

        for (Song song : songs) {
            if (song.hasPlays()) {
                topCanciones.add(song);
                msMostPlayed += song.getMilisDuration();
            }
        }

        // @TODO: Desacoplar este método
        /*acá tengo que ordenarlas (de la mas reproducida a la menos)*/
        /*------ Proceso de ordenado de lista ------*/
        Collections.sort(topCanciones, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                if (song1.getPlayCount() > song2.getPlayCount()) {
                    return -1;
                } else if (song1.getPlayCount() < song2.getPlayCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        /*------ Proceso de ordenado de lista ------*/
        
        return topCanciones;
    }
    
    // @TODO: renombrar método
    public void processAlbum() {
        for (Song cancion : songs) {
            addToAlbum(cancion);
        }
    }

    private void addToAlbum(Song song) {
        boolean finded = false;
        
        for (Album album : albums) {
            // si el album es igual que el album de la cancion
            if (album.getName().trim().equalsIgnoreCase(song.getAlbum().trim())) {
                // y si la cancion no existe en el album
                if (!album.songExist(song)) { // este if es por el issue #12
                    // la añado!
                    album.addSong(song);
                }
                finded = true;
                
                break;
            }
        }

        if (!finded) {
            Album album = new Album(song);
            
            album.addSong(song);

            albums.add(album);
        }
    }

    // @TODO: desacoplar este método
    public void printAlbums() {
        for (Album album : albums) {
            Log.add(album.toString());
            Log.add("Canciones: " + album.getSongs().size());
        }
    }

    // @TODO: Agregar una excepción
    public Album getAlbum(Song song) {
        for (Album album : albums) {
            if (album.songExist(song)) {
                Log.add("Album encontrado! : "+album);
                return album;
            }
        }
        
        return null;
    }
    
    public List<Album> getAlbumsByArtist(String artistName) {
        List<Album> albumsByArtist = new ArrayList<>();
        
        for (Album album : albumsByArtist) {
            if (album.getArtist().equalsIgnoreCase(artistName)) {
                Log.add("Album encontrado! : "+album);
                albumsByArtist.add(album);
            }
        }
        
        return albumsByArtist;
    }

    public List<String> getArtistNames() {
        List<String> artistNames = new ArrayList<>();
        String artistName;
        
        for (Album album : albums) {
            artistName = album.getArtist().trim().toLowerCase();

            if (!artistName.equals("")) {
                if (!artistNames.contains(artistName)) {
                    artistNames.add(artistName);
                }
            }
        }

        return artistNames;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setUpdateLogUI(UpdateLogUI update){
        Log.setUpdateLogUI(update);
    }

    /**
     * Este método le pasamos milisegundos y lo entrega como dias, horas, 
     * minutos, segundos como String
     * @param ms
     * @return 
     */
    // @TODO: desacoplar este método
    public String getFormattedDuration(long ms) {
        long remainder = ms;
        
        long days, hours, minutes, seconds;
        
        days = ms / 86400000;
        
        if(days != 0){
            remainder = ms % 86400000;
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
            ms = remainder % 1000;
        }
        
        return "["+days +"d. "+hours+"h. "+minutes+"m. "+seconds+"s. "+ms+" ms.] ";
    }
}
