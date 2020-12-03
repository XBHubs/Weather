package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseAreaFragment extends AppCompatActivity {
    final int LEVEL_PROVINCE = 0;
    final int LEVEL_CITY = 1;
    //final int LEVEL_COUNTY = 2;

    private TextView txtTitle;
    private Button btnBack;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    public static boolean isOk=false;
    private TextView txt;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);

        txtTitle = findViewById(R.id.txt_title);
        btnBack = findViewById(R.id.btn_back);
        listView = findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(ChooseAreaFragment.this, R.layout.simple_list_item, dataList);
        listView.setAdapter(adapter);
        isOk=false;
        Log.d("Choose","~~~~~~~~~~~~~~~~~~~start queryProvince~~~~~~~~~~~~~~~~~");
        queryProvinces();
        Log.d("Choose","~~~~~~~~~~~~~~~~~~~end queryProvince~~~~~~~~~~~~~~~~~");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentLevel) {
                    case LEVEL_PROVINCE:
                        selectedProvince = provinceList.get(position);
                        Toast.makeText(ChooseAreaFragment.this, "你选择了城市", Toast.LENGTH_SHORT).show();
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        selectedCity = cityList.get(position);
                        String city_code=selectedCity.getCityCode();
                        Toast.makeText(ChooseAreaFragment.this, "你选择"+selectedCity.getCityName(), Toast.LENGTH_SHORT).show();
                        isOk=true;
                        Intent intent =new Intent(ChooseAreaFragment.this,MainActivity.class);
                        intent.putExtra("id",city_code);
                        startActivity(intent);
                        break;

                    default:
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    queryProvinces();
            }
        });
    }

    private void queryProvinces() {
        txtTitle.setText("中国");
        btnBack.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        Log.d("choosefragment:","provincelist_size:"+provinceList.size());
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            //
        }
    }

    private void queryCities() {
        txtTitle.setText(selectedProvince.getProvinceName());
        btnBack.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {

                dataList.clear();
                for (City c : cityList) {
                    dataList.add(c.getCityName());
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_CITY;

        } else {
            //
        }
    }
/*
    private void queryCounties() {
        txtTitle.setText(selectedCity.getCityName());
        btnBack.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            try {
                dataList.clear();
                for (County c : countyList) {
                    dataList.add(c.getCountyName());
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel = LEVEL_COUNTY;
            } catch (NullPointerException e) {
                int provinceCode = selectedProvince.getProvinceCode();
                String url = getResources().getString(R.string.url_query_province) + provinceCode;
                queryFromServer(url, "city");
                url = getResources().getString(R.string.url_query_province)
                        + selectedProvince.getProvinceCode() + "/"
                        + selectedCity.getCityCode();
                queryFromServer(url, "county");
            }
        } else {
            String url = getResources().getString(R.string.url_query_province)
                    + selectedProvince.getProvinceCode() + "/"
                    + selectedCity.getCityCode();
            queryFromServer(url, "county");
        }
    }*/

}
