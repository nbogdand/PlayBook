package com.audiobook.nbogdand.playbook.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.audiobook.nbogdand.playbook.BR;
import com.audiobook.nbogdand.playbook.R;
import com.audiobook.nbogdand.playbook.SongsViewModel;
import com.audiobook.nbogdand.playbook.data.Song;

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
