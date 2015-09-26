package com.canvara.apps.ratemyride;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.canvara.apps.ratemyride.data.RateMyRideContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MainActivityFragment launches the dashboard for the Rate My Ride Application.
 * When this activity starts, it will look up the current location of the user and
 * fetch the dashboard details for the user.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private DashboardAdapter mDashboardAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = RateMyRideContract.ReportEntry.COLUMN_TOTAL_REVIEWS + " ASC";
        Uri reportForLocationUri = RateMyRideContract.ReportEntry.CONTENT_URI;

        Cursor cursor = getActivity().getContentResolver()
                .query(reportForLocationUri, null, null, null, sortOrder);

        Log.d(LOG_TAG, "Cursor returned " + cursor.getCount() + " rows");
        mDashboardAdapter = new DashboardAdapter(getActivity(), cursor, 0);

        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listViewDashboard);
        listView.setAdapter(mDashboardAdapter);

        // TODO Set up an item click listener to view the details

        return view;
    }

    private void updateDashboard () {
        FetchReportTask reportTask = new FetchReportTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        reportTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDashboard();
    }
}
