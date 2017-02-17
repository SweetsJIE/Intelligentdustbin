package com.maker.intelligentdustbin;

/**
 * Created by sweets on 17/2/17.
 */

public class Dustbin_icon {

    private String name;
    private int imageId;

    public Dustbin_icon(String name , int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public String getName() {
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
