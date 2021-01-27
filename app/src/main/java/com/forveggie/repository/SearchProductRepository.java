package com.forveggie.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.forveggie.model.response.ProductResponse;
import com.forveggie.network.ApiClient;
import com.forveggie.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchProductRepository {

    private ApiService apiService;

    public SearchProductRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<ProductResponse> searchProductList(String productName, String returnType, int page) {
        MutableLiveData<ProductResponse> data = new MutableLiveData<>();
        apiService.searchProducts(productName, returnType, page).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                Log.d("retrofit", "onResponse: 응답");
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("retrofit", "onFailure: 응답 실패");
                t.printStackTrace();
                data.setValue(null);
            }
        });
        return data;
    }
}
