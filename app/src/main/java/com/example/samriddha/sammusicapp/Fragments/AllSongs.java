package com.example.samriddha.sammusicapp.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.samriddha.sammusicapp.R;

/**
 * Created by Samriddha on 4/22/2019.
 */

public class AllSongs extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allsongs, container, false);
        return v;
    }
}
