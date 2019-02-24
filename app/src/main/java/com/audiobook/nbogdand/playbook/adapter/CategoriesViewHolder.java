package com.audiobook.nbogdand.playbook.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.audiobook.nbogdand.playbook.BR;
import com.audiobook.nbogdand.playbook.CategoriesViewModel;

public class CategoriesViewHolder extends RecyclerView.ViewHolder {

    final private ViewDataBinding dataBinding;

    public CategoriesViewHolder(ViewDataBinding dataBinding){
        super(dataBinding.getRoot());
        this.dataBinding = dataBinding;
    }

    public void bind(CategoriesViewModel categoriesViewModel,int position){
        dataBinding.setVariable(BR.categoriesViewModel,categoriesViewModel);
        dataBinding.setVariable(BR.categoryPosition, position);
        dataBinding.executePendingBindings();
    }

}
