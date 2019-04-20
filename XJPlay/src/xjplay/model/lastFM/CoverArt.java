package xjplay.model.lastFM;

public class CoverArt {
    private String url;
    private String size;

    public CoverArt(String url, String size) {
        this.url = url;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CoverArt{" + "url=" + url + ", size=" + size + '}';
    }
    
    
}
