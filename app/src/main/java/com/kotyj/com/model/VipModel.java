package com.kotyj.com.model;

import java.io.Serializable;


public class VipModel implements Serializable {


    /**
     * level : 1
     * price : 0
     * image : http://47.115.157.28:80/image/app_image/D2C254DAF761492E9D6DC729013FA0CD.png?0.06094404580009627
     */

    private String level;
    private String price;
    private String image;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
