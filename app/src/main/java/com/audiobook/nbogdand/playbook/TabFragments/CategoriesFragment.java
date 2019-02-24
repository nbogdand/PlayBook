package com.audiobook.nbogdand.playbook.TabFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.audiobook.nbogdand.playbook.CategoriesViewModel;
import com.audiobook.nbogdand.playbook.R;
import com.audiobook.nbogdand.playbook.data.Category;
import com.audiobook.nbogdand.playbook.databinding.CategoriesFragmentBinding;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    RecyclerView recyclerView;
    CategoriesViewModel categoriesViewModel;
    LinearLayoutManager linearLayoutManager;

    CategoriesFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.categories_fragment, container, false);
        View view = binding.getRoot();

        // Initialize RecyclerView and sets Observers
        init(view);

        return view;
    }

    private void init(View view){
        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        categoriesViewModel.init();

        binding.setCategoryFragment(this);

        setObservers();

        recyclerView = view.findViewById(R.id.categories_recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(categoriesViewModel.getAdapter());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setObservers(){
        categoriesViewModel.getCategories().observe(this, new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Category> categories) {
                categoriesViewModel.setCategoriesInAdapter(categories);
            }
        });

        categoriesViewModel.getSelectedMutableLiveData().observe(this, new Observer<Category>() {
            @Override
            public void onChanged(@Nullable Category category) {
                Toast.makeText(getActivity(),"Ai dat click",Toast.LENGTH_SHORT).show();
            }
        });

    }

}

