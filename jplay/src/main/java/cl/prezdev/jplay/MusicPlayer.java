package cl.prezdev.jplay;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class MusicPlayer {

    private final BasicPlayer basicPlayer;
    private final BasicController basicController;
    private Song currentSong;
    
    private static MusicPlayer musicPlayer;
    
    public static MusicPlayer getInstance(){
        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer();
        }
        
        return musicPlayer;
    }
    
    private MusicPlayer() {
        basicPlayer = new BasicPlayer();
        basicController = (BasicController) basicPlayer;
    }
    
    public void addBasicPlayerListener(BasicPlayerListener basicPlayerListener){
        basicPlayer.addBasicPlayerListener(basicPlayerListener);
    }

    public void play(Song song) throws BasicPlayerException{
        if(hasCurrentSong()){
            stop();
        }
        
        this.currentSong = song;
        this.basicController.open(currentSong);
        
        basicController.play();
    }

    public void pause() throws BasicPlayerException {
        basicController.pause();
    }

    public void resume() throws BasicPlayerException {
        basicController.resume();
    }

    public void stop() throws BasicPlayerException {
        basicController.stop();
    }

    public void seek(long bytes) throws BasicPlayerException {
        /*INVESTIGAR*/
        basicController.seek(bytes);
    }

    public void setVolume(int volume) throws BasicPlayerException {
        basicController.setGain((double) volume / 100);
    }

    public Song getCurrentSong() {
        return currentSong;
    }
    
    public boolean hasCurrentSong(){
        return this.currentSong != null;
    }
}
