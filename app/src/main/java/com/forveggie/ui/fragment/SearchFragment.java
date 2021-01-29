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


import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

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
    private String[][] data;
    private int rowCount, colCount;
    private String userType = "vegan"; // 타입 생성 추가 전까지 임시 하드코딩

    private List<String> eggList = new ArrayList<>();
    private List<String> milkList = new ArrayList<>();
    private List<String> fishList = new ArrayList<>();
    private List<String> poultryList = new ArrayList<>();
    ;
    private List<String> meatList = new ArrayList<>();
    ;

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
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
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
        viewModel.searchProductList(query, "json", currentPage).observe(this, productResponse -> {
            toggleLoading();
            if (productResponse != null) {
                totalAvailablePages = productResponse.getTotalPages();
                if (productResponse.getProductList() != null) {
                    int oldCount = productList.size();
                    productList.addAll(productResponse.getProductList());
                    filtering(productList, oldCount, productList.size());
                    productListAdapter.notifyItemRangeChanged(oldCount, productList.size());
                }
            }
        });
    }

    /* 모든 제품의 허용/비허용 여부를 검사 */
    private void filtering(List<Product> productList, int oldCount, int size) {

        if (productList.size() > 0) {
            for (int i = oldCount; i < size; i++) { // 각 아이템마다 원재료를 가져온 후 필터링
                List<String> materialList = splitMaterialList(productList.get(i).getProductMaterial() + ", " + productList.get(i).getAllergy());
                setExcelData();

                Boolean egg = false;
                Boolean milk = false;
                Boolean fish = false;
                Boolean chicken = false;
                Boolean meat = false;

                for (int a = 1; a < rowCount; a++) {
                    for (int b = 0; b < colCount; b++) {
                        // 원재료 데이터의 길이가 2 이하인 경우 "동일한 경우" 만 검사 ("게" <-> "게" 매칭)
                        if (data[b][a].length() <= 1) {
                            if (materialList.contains(data[b][a])) {
                                if (data[b][a].length() < 1) continue;
                                if (b == 0) egg = true;
                                else if (b == 1) milk = true;
                                else if (b == 2) fish = true;
                                else if (b == 3) chicken = true;
                                else if (b == 4) meat = true;
                            }
                        } else {
                            // 2 이상이면 포함하는 원재료를 찾는다. (국내산닭가슴살 -> 닭가슴살 포함)
                            for (int c = 0; c < materialList.size(); c++) {
                                if (materialList.get(c).contains(data[b][a])) {
                                    if (b == 0) egg = true;
                                    else if (b == 1) milk = true;
                                    else if (b == 2) fish = true;
                                    else if (b == 3) chicken = true;
                                    else if (b == 4) meat = true;
                                }
                            }
                        }
                    }
                }


                switch (userType) {
                    case "vegan":
                        if (egg || milk || fish || chicken || meat)
                            productList.get(i).setFilterResult(false);
                        break;
                    case "ovo":
                        if (milk || fish || chicken || meat)
                            productList.get(i).setFilterResult(false);
                        break;
                    case "lacto":
                        if (egg || fish || chicken || meat)
                            productList.get(i).setFilterResult(false);
                        break;
                    case "lacto-ovo":
                        if (fish || chicken || meat)
                            productList.get(i).setFilterResult(false);
                        break;
                    case "pesco":
                        if (chicken || meat)
                            productList.get(i).setFilterResult(false);
                        break;
                    case "pollo":
                        if (meat)
                            productList.get(i).setFilterResult(false);
                        break;
                }
            }
        }
    }

    /* 개별 프로덕트의 원재료 배열을 ',' 기준으로 쪼개하는 함수 */
    private List<String> splitMaterialList(String material) {
        ArrayList<String> materialList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(material, ",() {}[]1234567890%:\\.+");

        while (st.hasMoreTokens()) {
            materialList.add(st.nextToken());
        }
        return materialList;
    }


    private void setExcelData() {
        Workbook workbook = null;
        data = null;

        try {
            InputStream file = getActivity().getResources().getAssets().open("search.xls");
            workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            rowCount = sheet.getRows();
            colCount = sheet.getColumns();

            data = new String[colCount][rowCount];
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    Cell cell = sheet.getCell(j, i);
                    if (cell == null) continue;
                    data[j][i] = cell.getContents();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }


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