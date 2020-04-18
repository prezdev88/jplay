package cl.prezdev.jplay;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class MusicPlayer {

    private final BasicPlayer basicPlayer;
    private final BasicController basicController;
    private final Song currentSong;

    public MusicPlayer(
        Song currentSong, 
        BasicPlayerListener... basicPlayerListeners
    ) throws BasicPlayerException {
        this.currentSong = currentSong;

        basicPlayer = new BasicPlayer();
        basicController = (BasicController) basicPlayer;
        
        for (BasicPlayerListener basicPlayerListener : basicPlayerListeners) {
            basicPlayer.addBasicPlayerListener(basicPlayerListener);
        }

        basicController.open(currentSong);
    }

    public void play() throws BasicPlayerException {
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

}
