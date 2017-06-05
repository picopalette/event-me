package io.picopalette.apps.event_me.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.picopalette.apps.event_me.Adapters.EventAdapter;
import io.picopalette.apps.event_me.Datas.HomeEvent;
import io.picopalette.apps.event_me.EventCreation;
import io.picopalette.apps.event_me.R;


public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    List<HomeEvent> homeEvents;
    private EventAdapter adapter;




    public static EventsFragment newInstance() {
        return new EventsFragment();
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
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        getDataTask();
        recyclerView.setHasFixedSize(true);
        adapter = new EventAdapter(getContext(), homeEvents);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    private void getDataTask()  {

        homeEvents = new ArrayList<>();
        HomeEvent events = new HomeEvent();
        events.setEve_home_date_time("fgfdgsdf");
        events.setEve_home_img_url("thgfhfghfg");
        events.setEve_home_name("sdfgdfgdfg");
        events.setEve_home_place("sddgffdgfdg");
        events.setEve_home_type("dfgfdgdfg");
        homeEvents.add(events);



    }
}
