package com.forveggie.model.response;

import com.forveggie.model.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {

    @SerializedName("totalCount")
    private int totalPages;

    @SerializedName("pageNo")
    private int page;

    @SerializedName("list")
    private List<Product> ProductList;

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }

    public List<Product> getProductList() {
        return ProductList;
    }
}
