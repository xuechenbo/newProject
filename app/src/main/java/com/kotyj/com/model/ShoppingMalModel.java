package com.kotyj.com.model;

import java.io.Serializable;

/**
 * @author: lilingfei
 * @description:
 * @date: 2019/8/20
 */
public class ShoppingMalModel implements Serializable {


    /**
     * detailImage : http://47.99.143.160:80/image/goods_image/3CA2AF92C4AD4128836DEB5F6F21CE92.jpg?0.5191796915258255?1590081904207?1590989678564?1590989698315?1590989717568?1596421584713?1596421615745?1596421673548?1596426997436
     * id : A0E266B63E2042478966C025BF882850
     * image : http://47.99.143.160:80/image/goods_image/501FDB70B6F840709F8945751279ACF9.jpg?0.09162686063687042?1590081904201?1590989678560?1590989698311?1590989717563?1596421584711?1596421615743?1596421673546?1596426997432
     * name : 正宗东海优质黄鱼酥
     * point : 0
     * price : 128.00
     */

    private String detailImage;
    private String id;
    private String image;
    private String name;
    private String point;
    private String price;

    public String getDetailImage() {
        return detailImage;
    }

    public void setDetailImage(String detailImage) {
        this.detailImage = detailImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
