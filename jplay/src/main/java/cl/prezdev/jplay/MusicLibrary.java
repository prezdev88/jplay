package cl.prezdev.jplay;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cl.prezdev.jplay.common.Util;

public class MusicLibrary implements Serializable{

    private final static Logger LOGGER = Logger.getLogger("cl.prezdev.jplay.MusicLibrary");

    private List<Song> songs;
    private List<Song> favoritesSongs;
    private List<Album> albums;
    private List<File> paths; // rutas para analizar
    
    private long msMusicLibrary;
    private long msMostPlayed;
    private long msFavorites;
    
    private static MusicLibrary musicLibrary;

    private MusicLibrary() {
        albums          = new ArrayList<>();
        songs           = new ArrayList<>();
        favoritesSongs  = new ArrayList<>();
        paths           = new ArrayList<>();
        
        msMusicLibrary  = 0;
        msMostPlayed    = 0;
        msFavorites     = 0;
    }
    
    public static MusicLibrary getInstance(){
        if(musicLibrary == null){
            musicLibrary = new MusicLibrary();
        }
        
        return musicLibrary;
    }
    
    public String getLibraryDuration(){
        return "Biblioteca --> " + Util.getFormattedDuration(msMusicLibrary);
    }
    
    public String getFavoritesDuration(){
        return "Favoritos --> " + Util.getFormattedDuration(msFavorites);
    }
    
    public String getMostPlayedDuration(){
        return "Más escuchadas --> " + Util.getFormattedDuration(msMostPlayed);
    }
    
    public void addFavoriteSong(Song song){
        favoritesSongs.add(song);
        LOGGER.log(Level.INFO, "Añadida a favoritos: "+song);
        msFavorites += song.getMilliSeconds();
    }
    
    public void removeFavoriteSong(Song song){
        favoritesSongs.remove(song);
        LOGGER.log(Level.INFO, "Removido de favoritos: "+song);
        msFavorites -= song.getMilliSeconds();
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
            msMusicLibrary += song.getMilliSeconds();
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
        List<Song> topSongs = new ArrayList<>();
        msMostPlayed = 0;

        for (Song song : songs) {
            if (song.hasPlays()) {
                topSongs.add(song);
                msMostPlayed += song.getMilliSeconds();
            }
        }

        orderSongs(topSongs);
        
        return topSongs;
    }

    private void orderSongs(List<Song> topSongs){
        Collections.sort(topSongs, (song1, song2) -> {
            if (song1.getPlayCount() > song2.getPlayCount()) {
                return -1;
            } else if (song1.getPlayCount() < song2.getPlayCount()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public void addSongsToAlbums() {
        for (Song song : songs) {
            if(song != null){
                addToAlbum(song);
            }
        }
    }

    private void addToAlbum(Song song) {
        boolean found = false;
        
        for (Album album : albums) {
            try{
                // si el album es igual que el album de la cancion
                if (album.getName().trim().equalsIgnoreCase(song.getAlbum().trim())) {
                    // y si la cancion no existe en el album
                    if (!album.songExist(song)) {
                        // la añado!
                        album.addSong(song);
                    }

                    found = true;

                    break;
                }
            }catch(NullPointerException ex){
                System.out.println(song);
                System.out.println(album);
            }
        }

        if (!found) {
            Album album = new Album(song);
            
            album.addSong(song);

            albums.add(album);
        }
    }

    public void printAlbumsToLog() {
        for (Album album : albums) {
            LOGGER.log(Level.INFO, album.toString());
            LOGGER.log(Level.INFO, "Canciones: " + album.getSongs().size());
        }
    }

    public Album getAlbum(Song song) {
        for (Album album : albums) {
            if (album.songExist(song)) {
                LOGGER.log(Level.INFO, "Album encontrado! : " + album);
                return album;
            }
        }
        
        return null;
    }
    
    public List<Album> getAlbumsByArtist(String artistName) {
        List<Album> albumsByArtist = new ArrayList<>();
        
        for (Album album : albums) {
            if (album.getArtistName().equalsIgnoreCase(artistName)) {
                LOGGER.log(Level.INFO, "Album encontrado! : " + album);
                albumsByArtist.add(album);
            }
        }
        
        return albumsByArtist;
    }

    public List<String> getArtistNames() {
        List<String> artistNames = new ArrayList<>();
        String artistName;
        
        for (Album album : albums) {
            artistName = album.getArtistName().trim().toLowerCase();

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
}
