package cz.fel.cvut.pjv.dgnmstr.global;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImgLoader {
    private static final Map<ImgId, Image> images = new HashMap<>();
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
}
