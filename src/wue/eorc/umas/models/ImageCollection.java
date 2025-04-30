package wue.eorc.umas.models;

import wue.eorc.umas.enums.ImageType;

import java.io.File;

public class ImageCollection {

    private File path;
    private ImageType type;
    private int numberOfImages;

    public ImageCollection(File path, ImageType type) {
        this.path = path;
        this.type = type;
        this.numberOfImages = countImages();
    }

    private int countImages(){
        return 0;
    }

}
