package io.picopalette.apps.event_me.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.picopalette.apps.event_me.EventCreation;
import io.picopalette.apps.event_me.R;

/**
 * Created by holmesvinn on 3/6/17.
 */

public class EventsFragment extends Fragment {




    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"fab clicked",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), EventCreation.class);
                getActivity().startActivity(intent);
            }
        });
        return v;
    }
}
