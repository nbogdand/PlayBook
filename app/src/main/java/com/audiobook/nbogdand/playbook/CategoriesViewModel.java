package com.audiobook.nbogdand.playbook;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.LayoutRes;

import com.audiobook.nbogdand.playbook.R;
import com.audiobook.nbogdand.playbook.adapter.CategoriesAdapter;
import com.audiobook.nbogdand.playbook.data.Categories;
import com.audiobook.nbogdand.playbook.data.Category;
import com.audiobook.nbogdand.playbook.data.Songs;

import java.util.ArrayList;

public class CategoriesViewModel extends ViewModel {

    //adapter for RecycleView
    private CategoriesAdapter adapter = null;

    // data needed
    private Categories categories = null;

    // selected category from click
    private MutableLiveData<Category> selectedMutableLiveData = null;
    private int position;

    public void init(){
        categories = Categories.getInstance();
        adapter = new CategoriesAdapter(R.layout.category_element,this);
        selectedMutableLiveData = new MutableLiveData<>();
    }

    // provide adapter for recyclerView
    public CategoriesAdapter getAdapter(){return adapter;}

    public MutableLiveData<ArrayList<Category>> getCategories(){return categories.getCategories();}

    // clicked category ("selected")
    public MutableLiveData<Category> getSelectedMutableLiveData(){ return  selectedMutableLiveData;}

    // updates the selected category
    public void setSelected(int position){
        this.position = position;
        categories.setSelectedPosition(position);
        selectedMutableLiveData.setValue(categories.getCategoryAt(position));
    }

    // set categories list for RecycleView's adapter
    public void setCategoriesInAdapter(ArrayList<Category> categoriesArrayList){
        this.adapter.setCategoriesList(categoriesArrayList);
        this.adapter.notifyDataSetChanged();
    }

    // Add new category to the list, the update to LiveData
    // is done inside Categories.class
    public void addCategory(String name,Integer resourceId){
        categories.addCategory(name,resourceId);
    }

    public Category getCategoryAt(Integer position){
        if(categories.getCategoryAt(position) != null){
            return categories.getCategoryAt(position);
        }
        return null;
    }

    public void onClickedAddButton(){
        categories.addCategory("categ1",1);
    }

}
