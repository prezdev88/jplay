package cl.prezdev.jplay;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class Reproductor {

    private final BasicPlayer player;
    private final BasicController control;
    private final Song actual;

    public Reproductor(Song c, BasicPlayerListener... listeners) throws BasicPlayerException {
        actual = c;
        String s = "";

        player = new BasicPlayer();
        control = (BasicController) player;
        
        for (BasicPlayerListener lis : listeners) {
            player.addBasicPlayerListener(lis);
        }

        control.open(actual);
    }

    public void play() throws BasicPlayerException {
        control.play();
    }

    public void pause() throws BasicPlayerException {
        control.pause();
    }

    public void resume() throws BasicPlayerException {
        control.resume();
    }

    public void stop() throws BasicPlayerException {
        control.stop();
    }

    public void seek(long bytes) throws BasicPlayerException {
        /*INVESTIGAR*/
        control.seek(bytes);
    }

    public void setVol(int vol) throws BasicPlayerException {
        control.setGain((double) vol / 100);
    }

    public Song getCancionActual() {
        return actual;
    }

}
