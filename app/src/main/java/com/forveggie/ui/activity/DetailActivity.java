package com.forveggie.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.forveggie.R;
import com.forveggie.databinding.ActivityDetailBinding;
import com.forveggie.model.Product;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding activityDetailBinding;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        initUI();
    }

    private void initUI() {
        product = (Product) getIntent().getSerializableExtra("product");
        activityDetailBinding.setProductName(product.getProductName());
        activityDetailBinding.setProductMaterial(product.getProductMaterial());
        activityDetailBinding.setProductAllergy(product.getAllergy());
        activityDetailBinding.setProductImage(product.getImgurl1());
    }

    private void getProductDetail() {

    }
}