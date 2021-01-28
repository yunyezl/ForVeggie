package com.forveggie.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.forveggie.R;
import com.forveggie.databinding.FragmentSearchBinding;
import com.forveggie.model.Product;
import com.forveggie.ui.activity.DetailActivity;
import com.forveggie.ui.adapter.ProductListAdapter;
import com.forveggie.ui.listener.ProductListener;
import com.forveggie.viewmodel.SearchViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements ProductListener {

    private FragmentSearchBinding fragmentSearchBinding;
    private SearchViewModel viewModel;
    private List<Product> productList = new ArrayList<>();
    private ProductListAdapter productListAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View root = fragmentSearchBinding.getRoot();
        initUI();
        return root;
    }

    private void initUI() {
        fragmentSearchBinding.productRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        productListAdapter = new ProductListAdapter(productList, this);
        fragmentSearchBinding.productRecyclerView.setAdapter(productListAdapter);
        fragmentSearchBinding.editTextSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalAvailablePages = 1;
                                searchProduct(s.toString());
                            });
                        }
                    }, 800);
                } else {
                    productList.clear();
                    productListAdapter.notifyDataSetChanged();
                }
            }
        });
        fragmentSearchBinding.productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int last = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (!fragmentSearchBinding.productRecyclerView.canScrollVertically(1)) {
                    final String inputStr = fragmentSearchBinding.editTextSearchBar.getText().toString().trim();
                    if (!inputStr.isEmpty()) {
                        if (currentPage < totalAvailablePages) {
                            currentPage += 1;
                            searchProduct(inputStr);
                            productListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        fragmentSearchBinding.editTextSearchBar.requestFocus();

    }

    private void searchProduct(String query) {
        toggleLoading();
        viewModel.searchProductList(query,"json", currentPage).observe(this, productResponse -> {
            toggleLoading();
            if (productResponse != null) {
                totalAvailablePages = productResponse.getTotalPages();
                if (productResponse.getProductList() != null) {
                    int oldCount = productList.size();
                    productList.addAll(productResponse.getProductList());
                    productListAdapter.notifyItemRangeChanged(oldCount, productList.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            fragmentSearchBinding.setIsLoading(
                    !(fragmentSearchBinding.getIsLoading() != null && fragmentSearchBinding.getIsLoading()));
        } else {
            fragmentSearchBinding.setIsLoadingMore(
                    !(fragmentSearchBinding.getIsLoadingMore() != null && fragmentSearchBinding.getIsLoadingMore()));
        }
    }

    @Override
    public void onProductClicked(Product product) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }
}