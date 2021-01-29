package com.forveggie.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.forveggie.R;
import com.forveggie.databinding.ActivityDetailBinding;
import com.forveggie.model.Product;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding activityDetailBinding;
    private Product product;

    private String[][] data;
    private int rowCount, colCount;
    private String userType = "vegan"; // 타입 생성 추가 전까지 임시 하드코딩

    private List<String> eggList = new ArrayList<>();
    private List<String> milkList = new ArrayList<>();
    private List<String> fishList = new ArrayList<>();
    private List<String> poultryList = new ArrayList<>();
    private List<String> meatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        initUI();
    }

    private void initUI() {
        product = (Product) getIntent().getSerializableExtra("product");
        filtering(product);

        String eggs = "";
        String milks = "";
        String fishes = "";
        String poultry = "";
        String meats = "";

        /* 원재료명에 "돼지고기" 등이 다수 포함되는 경우 존재 ex. 돼지고기(국내산), 조미돈지[돈지(돼지고기:국산)] */
        Set<String> setEggList = new HashSet<>(eggList);
        Set<String> setMilkList = new HashSet<>(milkList);
        Set<String> setFishList = new HashSet<>(fishList);
        Set<String> setPoultryList = new HashSet<>(poultryList);
        Set<String> setMeatList = new HashSet<>(meatList);

        for (String s : setEggList) eggs += ", " + s;
        for (String s : setMilkList) milks += ", " + s;
        for (String s : setFishList) fishes += ", " + s;
        for (String s : setPoultryList) poultry += ", " + s;
        for (String s : setMeatList) meats += ", " + s;

        if (eggList.size() > 0) {
            activityDetailBinding.eggList.setText(eggs.substring(1));
            activityDetailBinding.egg.setVisibility(View.VISIBLE);
        }
        if (milkList.size() > 0) {
            activityDetailBinding.milkList.setText(milks.substring(1));
            activityDetailBinding.milk.setVisibility(View.VISIBLE);
        }
        if (fishList.size() > 0) {
            activityDetailBinding.fishList.setText(fishes.substring(1));
            activityDetailBinding.fish.setVisibility(View.VISIBLE);
        }
        if (poultryList.size() > 0) {
            activityDetailBinding.poultryList.setText(poultry.substring(1));
            activityDetailBinding.poultry.setVisibility(View.VISIBLE);
        }
        if (meatList.size() > 0) {
            activityDetailBinding.meatList.setText(meats.substring(1));
            activityDetailBinding.meat.setVisibility(View.VISIBLE);
        }

        if (!product.getManufacture().equals("알 수 없음")) activityDetailBinding.manufacturer.setText(product.getManufacture());
        else activityDetailBinding.manufacturer.setText("미표기");

        activityDetailBinding.setProductName(product.getProductName());
        activityDetailBinding.setProductMaterial(product.getProductMaterial());
        activityDetailBinding.setProductAllergy(product.getAllergy());
        activityDetailBinding.setProductImage(product.getImgurl1());
        if (product.getFilterResult()) activityDetailBinding.badgeAllow.setVisibility(View.VISIBLE);
        else activityDetailBinding.badgeNotAllow.setVisibility(View.VISIBLE);
    }


    /* 제품의 원재료들을 필터링하는 함수 */
    private void filtering(Product product) {

        Boolean egg = false;
        Boolean milk = false;
        Boolean fish = false;
        Boolean chicken = false;
        Boolean meat = false;

        List<String> materialList = splitMaterialList(product.getProductMaterial() + "," + product.getAllergy());
        setExcelData();
        for (int a = 1; a < rowCount; a++) {
            for (int b = 0; b < colCount; b++) {
                // 만약에 엑셀데이터의 길이가 2이하 이면
                if (data[b][a].length() <= 1) {
                    // 일치하는 검사를 하고
                    if (materialList.contains(data[b][a])) {
                        if (data[b][a].length() < 1) continue;
                        if (b == 0) {
                            egg = true;
                            eggList.add(data[b][a]);
                        }
                        if (b == 1) {
                            milk = true;
                            milkList.add(data[b][a]);
                        }
                        if (b == 2) {
                            fish = true;
                            fishList.add(data[b][a]);
                        }
                        if (b == 3) {
                            chicken = true;
                            poultryList.add(data[b][a]);
                        }
                        if (b == 4) {
                            meat = true;
                            meatList.add(data[b][a]);
                        }
                    }
                }
                else {
                    // 2 이상이면 포함하는 원재료를 찾는다. (국내산닭가슴살 -> 닭가슴살 포함)
                    for (int c = 0; c < materialList.size(); c++) {
                        if (materialList.get(c).contains(data[b][a])) {
                            if (b == 0) {
                                egg = true;
                                eggList.add(materialList.get(c));
                            }
                            if (b == 1) {
                                milk = true;
                                milkList.add(materialList.get(c));
                            }
                            if (b == 2) {
                                fish = true;
                                fishList.add(materialList.get(c));
                            }
                            if (b == 3) {
                                chicken = true;
                                poultryList.add(materialList.get(c));
                            }
                            if (b == 4) {
                                meat = true;
                                System.out.println(data[b][a] + " " + materialList.get(c));
                                meatList.add(materialList.get(c));
                            }
                        }
                    }
                }
            }
        }
        switch (userType) {
            case "vegan":
                if (egg || milk || fish || chicken || meat)
                    product.setFilterResult(false);
                break;
            case "ovo":
                if (milk || fish || chicken || meat)
                    product.setFilterResult(false);
                break;
            case "lacto":
                if (egg || fish || chicken || meat)
                    product.setFilterResult(false);
                break;
            case "lacto-ovo":
                if (fish || chicken || meat)
                    product.setFilterResult(false);
                break;
            case "pesco":
                if (chicken || meat)
                    product.setFilterResult(false);
                break;
            case "pollo":
                if (meat)
                    product.setFilterResult(false);
                break;
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
            InputStream file = this.getResources().getAssets().open("search.xls");
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

}