package com.forveggie.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.forveggie.R;
import com.forveggie.databinding.ItemContainerProductBinding;
import com.forveggie.model.Product;
import com.forveggie.ui.listener.ProductListener;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {

    private final List<Product> productList;
    private LayoutInflater layoutInflater;
    private ProductListener productListener;

    public ProductListAdapter(List<Product> productList, ProductListener productListener) {
        this.productList = productList;
        this.productListener = productListener;
    }

    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerProductBinding productBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_product, parent, false
        );

        return new ProductListViewHolder(productBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListViewHolder holder, int position) {
        holder.bindProduct(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductListViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerProductBinding itemContainerProductBinding;

        public ProductListViewHolder(@NonNull ItemContainerProductBinding itemContainerProductBinding) {
            super(itemContainerProductBinding.getRoot());
            this.itemContainerProductBinding = itemContainerProductBinding;
        }

        public void bindProduct(Product product) {
            itemContainerProductBinding.setProduct(product);
            itemContainerProductBinding.executePendingBindings();
            itemContainerProductBinding.getRoot().setOnClickListener(v ->
                    productListener.onProductClicked(product));
            if (product.getFilterResult()) {
                itemContainerProductBinding.badgeAllow.setVisibility(View.VISIBLE);
                itemContainerProductBinding.badgeNotAllow.setVisibility(View.GONE);
            } else {
                itemContainerProductBinding.badgeAllow.setVisibility(View.GONE);
                itemContainerProductBinding.badgeNotAllow.setVisibility(View.VISIBLE);
            }
        }
    }
}
