package io.picopalette.apps.event_me.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.picopalette.apps.event_me.R;

/**
 * Created by Aswin Sundar on 25-06-2017.
 */

public class PersonalListsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_lists, container, false);
        return v;
    }
}
