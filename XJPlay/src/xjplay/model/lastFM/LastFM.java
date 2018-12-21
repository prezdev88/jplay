package xjplay.model.lastFM;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;
import jlog.model.Log;
import xjplay.rules.Rule;

public class LastFM {

    public static Image getImage(String artist, String album) throws Exception {
        String url = "http://ws.audioscrobbler.com/2.0/?"
                + "method=album.getinfo&"
                + "api_key=" + Rule.API_KEY + "&"
                + "artist=" + artist + "&"
                + "album=" + album + "&"
                + "format=json";

        System.out.println(url);

        List<CoverArt> covers = LastFM.getCovers(url, "album");

        for (CoverArt cover : covers) {
            Log.add(cover.toString());
        }
        
        int ultimoIndice = covers.size() - 1;
        return getImage(covers.get(ultimoIndice));
    }

    public static Image getImage(String artist) throws Exception {
        String url = "http://ws.audioscrobbler.com/2.0/?"
                + "method=artist.getinfo&"
                + "api_key=" + Rule.API_KEY + "&"
                + "artist=" + artist + "&"
                + "format=json";

        System.out.println(url);

        List<CoverArt> covers = LastFM.getCovers(url, "artist");

//        for (CoverArt cover : covers) {
//            System.out.println(cover.toString());
//        }
        int ultimoIndice = covers.size() - 1;
        return getImage(covers.get(ultimoIndice));
    }

    /**
     * Transforma una URL en un String JSON
     *
     * @param urlString
     * @return
     * @throws Exception
     */
    private static String readUrl(String urlString) throws Exception {
//        Log.add(urlString);
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Obtiene una lista de CoverArt a partir de una URL de LastFM API
     *
     * @param url
     * @param parametro puede ser album o artist
     * @return Una lista de CoverArt
     * @throws Exception
     */
    private static List<CoverArt> getCovers(String url, String parametro) throws Exception {
        String jsonText = LastFM.readUrl(url);

        JSONObject jo = new JSONObject(jsonText);

        JSONArray jsonArray = jo.getJSONObject(parametro).getJSONArray("image");

        List<CoverArt> covers = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            covers.add(
                    new CoverArt(
                            jsonArray.getJSONObject(i).get("#text").toString(),
                            jsonArray.getJSONObject(i).get("size").toString()
                    )
            );
        }
        return covers;

        //https://stackoverflow.com/questions/19966672/java-json-with-gson
    }

    private static List<CoverArt> getArtistCovers(String url) throws Exception {
        String jsonText = LastFM.readUrl(url);

        JSONObject jo = new JSONObject(jsonText);

        JSONArray jsonArray = jo.getJSONObject("artist").getJSONArray("image");

        List<CoverArt> covers = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            covers.add(
                    new CoverArt(
                            jsonArray.getJSONObject(i).get("#text").toString(),
                            jsonArray.getJSONObject(i).get("size").toString()
                    )
            );
        }
        return covers;

        //https://stackoverflow.com/questions/19966672/java-json-with-gson
    }

//    public static void main(String[] args) {
//        try {
//            LastFM.getImage("Cannibal Corpse", "Tomb of the mutilated");
//        } catch (Exception ex) {
//            System.out.println("EX: "+ex.getMessage());
//        }
//    }
    private static Image getImage(CoverArt ca) throws MalformedURLException, IOException {
        URL url = new URL(ca.getUrl());
        Image image = ImageIO.read(url);

        return image;
    }
}
