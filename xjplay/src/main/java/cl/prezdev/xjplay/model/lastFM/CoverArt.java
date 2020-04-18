package cl.prezdev.xjplay.model.lastFM;

// @TODO: Lombok
public class CoverArt {
    private String imageUrl;
    private String size;

    public CoverArt(String imageUrl, String size) {
        this.imageUrl = imageUrl;
        this.size = size;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CoverArt{" + "url=" + imageUrl + ", size=" + size + '}';
    }
    
    
}
