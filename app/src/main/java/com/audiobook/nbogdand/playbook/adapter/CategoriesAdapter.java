package com.audiobook.nbogdand.playbook.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.audiobook.nbogdand.playbook.CategoriesViewModel;
import com.audiobook.nbogdand.playbook.data.Category;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {

    private ArrayList<Category> categories;
    private Integer resourceLayout;
    private CategoriesViewModel viewModel;

    public CategoriesAdapter(@LayoutRes Integer resourceId,CategoriesViewModel viewModel){
        this.resourceLayout = resourceId;
        this.viewModel = viewModel;
    }

    public void setCategoriesList(ArrayList<Category> categoriesList){
        this.categories = categoriesList;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(inflater,i,viewGroup,false);
        return new CategoriesViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder categoriesViewHolder, int i) {
        categoriesViewHolder.bind(viewModel,i);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    private int getLayoutIdForPosition(int position){ return resourceLayout;}
}
