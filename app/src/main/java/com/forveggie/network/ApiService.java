package com.forveggie.network;

import com.forveggie.model.response.ProductResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("CertImgListService/getCertImgListService?serviceKey=KJeZEeFZbu%2BlQy3OMv3FpnbLWrqM7mj8orXbKZQVAz%2FQe0ELaEIQnM%2BxY3NQY6k8%2BMi52M8Si%2B3Ys4bYV84o4w%3D%3D")
    Call<ProductResponse> searchProducts(
                                      @Query("prdlstNm") String ProductName,
                                      @Query("returnType") String returnType,
                                      @Query("pageNo") int page);

}
