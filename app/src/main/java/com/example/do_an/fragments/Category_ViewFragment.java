package com.example.do_an.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.do_an.adapter.FoodAdapter;
import com.example.do_an.FoodItem;
import com.example.do_an.R;

import java.util.ArrayList;


public class Category_ViewFragment extends Fragment {
    RecyclerView rvsCategory;
    FoodAdapter foodAdapter;
    ArrayList<FoodItem> ListFood;


    public Category_ViewFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category__view, container, false);

        rvsCategory = view.findViewById(R.id.rcv_Category);


//        LoadData();

        rvsCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter= new FoodAdapter(ListFood);
        rvsCategory.setAdapter(foodAdapter);
        return view;
    }
//    void LoadData()
//    {
//        ListFood = new ArrayList<>();
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-5.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//
//    }
}