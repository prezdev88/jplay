// https://www.last.fm/api
package cl.prezdev.xjplay.model.lastFM;

import cl.prezdev.jlog.Log;
import cl.prezdev.xjplay.recursos.Recurso;
import cl.prezdev.xjplay.rules.Rule;
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

// @TODO: Pensar en externalizar esto en un proyecto
public class LastFM {

    public static Image getCoverArt(String artist, String album) throws Exception {
        final String API_URL = 
            "http://ws.audioscrobbler.com/2.0/?" + 
            "method=album.getinfo&" + 
            "api_key=" + Rule.API_KEY + "&" + 
            "artist=" + artist + "&" + 
            "album=" + album + "&" + 
            "format=json";

        System.out.println(API_URL);

        List<CoverArt> coversArt = LastFM.getCoversArt(API_URL, "album");

        for (CoverArt coverArt : coversArt) {
            Log.add(coverArt);
        }

        int lastIndex = coversArt.size() - 1;
        return getCoverArt(coversArt.get(lastIndex));
    }

    public static Image getCoverArt(String artist) throws Exception {
        String url = "http://ws.audioscrobbler.com/2.0/?"
                + "method=artist.getinfo&"
                + "api_key=" + Rule.API_KEY + "&"
                + "artist=" + artist + "&"
                + "format=json";

        System.out.println(url);

        List<CoverArt> covers = LastFM.getCoversArt(url, "artist");

//        for (CoverArt cover : covers) {
//            System.out.println(cover.toString());
//        }
        int ultimoIndice = covers.size() - 1;
        return getCoverArt(covers.get(ultimoIndice));
    }

    // @TODO: Consumir API con una librería apta para ello
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
     * @param apiUrl
     * @param parameter puede ser album o artist
     * @return Una lista de CoverArt
     * @throws Exception
     */
    private static List<CoverArt> getCoversArt(String apiUrl, String parameter) throws Exception {
        String jsonText = LastFM.readUrl(apiUrl);

        JSONObject jsonObject = new JSONObject(jsonText);

        JSONArray jsonArray = jsonObject.getJSONObject(parameter).getJSONArray("image");

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

    private static Image getCoverArt(CoverArt coverArt) throws MalformedURLException, IOException {
        try {
            URL url = new URL(coverArt.getUrl());
            Image image = ImageIO.read(url);

            return image;
        } catch (Exception e) {
            return Recurso.ICONO_JPLAY;
        }
    }
}
