package cl.prezdev.xjplay.model.progress;

import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

// @TODO: Pensar en cambiar el nombre (SongProgressBar quizás)
public class WorkerStringProgress extends SwingWorker<Void, String> {

    private final JLabel durationLabel;
    private final String songDuration;
    private final int totalSeconds;

    private int initialMinute;
    private int initialSecond;
    private boolean paused;

    public WorkerStringProgress(JLabel durationLabel, String songDuration) {
        this.durationLabel = durationLabel;
        this.songDuration = songDuration;
        this.totalSeconds = getTotalSeconds();

        initialMinute = 0;
        initialSecond = 0;

        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    /**
     * este método se llama cuando libero el click sobre el progress bar
     * de la duracion de la canción
     *
     * @param percentage
     */
    public void changeProgressBarValue(int percentage) {
        if (percentage < 0) { // si el porcentaje es negativo, dejo los minutos y segundos en 0
            initialMinute = 0;
            initialSecond = 0;
        } else if (percentage > 100) { // si es mayor a 100, lo dejo en el total
            initialMinute = totalSeconds / 60;
            initialSecond = totalSeconds - (initialMinute * 60);
        } else { // si no, se calcula el porcentaje del total.
            initialSecond = (percentage * totalSeconds) / 100;
            initialMinute = initialSecond / 60;
            initialSecond = initialSecond - (initialMinute * 60);

        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (initialMinute = 0; initialMinute < 60; initialMinute++) {
            for (initialSecond = 0; initialSecond < 60; initialSecond++) {
                while (paused) {
                    Thread.sleep(500);
                }

                publish(initialMinute + ":" + (initialSecond < 10 ? "0" + initialSecond : initialSecond));
                Thread.sleep(1000);
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        durationLabel.setText(chunks.get(0) + " - " + songDuration);
    }

    private int getTotalSeconds() {
        String[] time = songDuration.split(":");

        int minutos, segundos;

        minutos = Integer.parseInt(time[0]);
        minutos = minutos * 60;
        segundos = Integer.parseInt(time[1]);

        return (minutos + segundos);
    }

}
