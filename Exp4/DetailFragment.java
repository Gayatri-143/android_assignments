package com.example.newsflow;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";

    public static DetailFragment newInstance(String title, String content) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        if (getArguments() != null) {
            ((TextView) view.findViewById(R.id.detail_title)).setText(getArguments().getString(ARG_TITLE));
            ((TextView) view.findViewById(R.id.detail_content)).setText(getArguments().getString(ARG_CONTENT));
        }
        return view;
    }
}
