package com.canvara.apps.ratemyride;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MainActivityFragment launches the dashboard for the Rate My Ride Application.
 * When this activity starts, it will look up the current location of the user and
 * fetch the dashboard details for the user.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mDashboardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        // TODO Fetch this details from API and bind though sync adapter or volley
        String[] dashboardArray = {"Ola Cabs", "Meru Cabs", "Fast Track", "Friends Call Taxi", "Bharati Call Taxi"};
        List<String> dashboardList = new ArrayList<>(Arrays.asList(dashboardArray));

        // Setup the dashboard adapter by binding the data and the template
        mDashboardAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_dashboard,
                R.id.list_item_cab_name,
                dashboardList);

        ListView listView = (ListView) view.findViewById(R.id.listViewDashboard);
        listView.setAdapter(mDashboardAdapter);

        // TODO Set up an item click listener to view the details

        return view;
    }
}
