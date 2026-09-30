package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Img loader.
 */
public class ImgLoader {
    private static final Logger log = Logger.getLogger(ImgLoader.class.getName());
    private static final Map<ImgId, Image> images = new HashMap<>();
    private static final Map<String, Image> sheets = new ConcurrentHashMap<>();

    public static void loadAll(){
       for(ImgId i : ImgId.values()){
            Image img = new Image(ImgId.class.getResourceAsStream(i.getImgPath()));
            images.put(i,img);
            i.setHeight(img.getHeight());
            i.setWidth(img.getWidth());


       }
    }
    public static Image get(ImgId id){
        return images.get(id);
    }

    /**
     * Returns a sprite sheet by its resource path, loading and caching it on first use.
     * @param path absolute resource path of the image
     * @return the image, or null when the resource does not exist
     */
    public static Image sheet(String path) {
        Image cached = sheets.get(path);
        if (cached != null) {
            return cached;
        }
        InputStream stream = ImgId.class.getResourceAsStream(path);
        if (stream == null) {
            log.severe("Missing image resource " + path);
            return null;
        }
        Image img = new Image(stream);
        sheets.put(path, img);
        return img;
    }
}
