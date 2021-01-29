package com.forveggie.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {

    @SerializedName("prdlstNm")
    private String productName;

    @SerializedName("rawmtrl")
    private String productMaterial;

    @SerializedName("allergy")
    private String allergy;

    @SerializedName("imgurl1")
    private String imgurl1;

    @SerializedName("imgurl2")
    private String imgurl2;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("manufacture")
    private String manufacture;

    private Boolean filterResult = true;

    public String getProductName() {
        return productName;
    }

    public String getProductMaterial() {
        return productMaterial;
    }

    public String getAllergy() {
        return allergy;
    }

    public String getImgurl1() {
        return imgurl1;
    }

    public String getImgurl2() {
        return imgurl2;
    }

    public String getBarcode() {
        return barcode;
    }

    public Boolean getFilterResult() { return filterResult; }

    public String getManufacture() { return manufacture; }

    public void setFilterResult(Boolean filterResult) {
        this.filterResult = filterResult;
    }
}
