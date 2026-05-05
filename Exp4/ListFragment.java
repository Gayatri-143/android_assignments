package com.example.newsflow;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        
        view.findViewById(R.id.card1).setOnClickListener(v -> openDetail(getString(R.string.news_headline_1), getString(R.string.news_description_1)));
        view.findViewById(R.id.card2).setOnClickListener(v -> openDetail(getString(R.string.news_headline_2), getString(R.string.news_description_2)));
        view.findViewById(R.id.card3).setOnClickListener(v -> openDetail(getString(R.string.news_headline_3), getString(R.string.news_description_3)));
        
        return view;
    }

    private void openDetail(String title, String content) {
        DetailFragment fragment = DetailFragment.newInstance(title, content);
        getParentFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }
}
