package com.audiobook.nbogdand.playbook.data;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.BaseObservable;

import java.util.ArrayList;

public class Categories extends BaseObservable {

    // Make it singleton
    private static Categories mInstance = null;

    private int selectedPosition;
    private ArrayList<Category> categoriesList = new ArrayList<>();

    // LiveData
    private MutableLiveData<ArrayList<Category>> categories = new MutableLiveData<>();

    private Categories(){}

    public static Categories getInstance(){
        if(mInstance == null){
            mInstance = new Categories();
        }

        return mInstance;
    }

    public Category getCategoryAt(int position){
        return categoriesList.get(position);
    }

    public void addCategory(String categoryName, int resourceId){
        categoriesList.add(new Category(categoryName,resourceId));
        categories.setValue(categoriesList);
    }

    public ArrayList<Category> getCategoriesList(){
        return categoriesList;
    }

    public MutableLiveData<ArrayList<Category>> getCategories(){ return  categories;}

    public int getSelectedPosition(){return selectedPosition;}

    public void setSelectedPosition(int position){this.selectedPosition = position;}

}
