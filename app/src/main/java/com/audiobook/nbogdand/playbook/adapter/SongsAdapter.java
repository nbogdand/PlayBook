package com.audiobook.nbogdand.playbook.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.audiobook.nbogdand.playbook.SongsViewModel;
import com.audiobook.nbogdand.playbook.data.Song;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapterViewHolder>  {

    private int layoutId;
    private List<Song> songsList ;
    private SongsViewModel viewModel;

    public SongsAdapter(@LayoutRes  int layoutId, SongsViewModel viewModel) {
        this.viewModel = viewModel;
        this.layoutId = layoutId;
    }

    private int getLayoutIdForPosition(int position){
        return layoutId;
    }

    public void setSongsList(List<Song> songsList) {
        this.songsList = songsList;
    }

    @NonNull
    @Override
    public SongsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater,i,viewGroup,false);
        return new SongsAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapterViewHolder songsAdapterViewHolder, final int i) {
        songsAdapterViewHolder.bind(viewModel,i);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    @Override
    public int getItemCount() {
        return songsList == null ? 0 : songsList.size();
    }

}
