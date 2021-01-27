package com.forveggie.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.forveggie.model.response.ProductResponse;
import com.forveggie.repository.SearchProductRepository;


public class SearchViewModel extends ViewModel {

    private SearchProductRepository searchProductRepository;

    public SearchViewModel() { searchProductRepository = new SearchProductRepository(); }

    public LiveData<ProductResponse> searchProductList(String productName, String returnType, int page) {
        return searchProductRepository.searchProductList(productName, returnType, page);
    }

}
