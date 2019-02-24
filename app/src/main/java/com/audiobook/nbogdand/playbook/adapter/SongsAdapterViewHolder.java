package com.audiobook.nbogdand.playbook.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.audiobook.nbogdand.playbook.BR;
import com.audiobook.nbogdand.playbook.SongsViewModel;

public class SongsAdapterViewHolder extends RecyclerView.ViewHolder {

    final ViewDataBinding binding;

    public SongsAdapterViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(SongsViewModel viewModel, int position){

        binding.setVariable(BR.viewModel,viewModel);
        binding.setVariable(BR.position,position);
        binding.executePendingBindings();

    }

}
