package com.canvara.apps.ratemyride;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.canvara.apps.ratemyride.data.RateMyRideContract;
import com.canvara.apps.ratemyride.data.RateMyRideContract.ReportEntry;
import com.canvara.apps.ratemyride.data.RateMyRideContract.CabCompanyEntry;

/**
 * MainActivityFragment launches the dashboard for the Rate My Ride Application.
 * When this activity starts, it will look up the current location of the user and
 * fetch the dashboard details for the user.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int DASHBOARD_LOADER = 0;
    private DashboardAdapter mDashboardAdapter;

    private static final String[] DASHBOARD_COLUMNS = {
            ReportEntry.TABLE_NAME + "." + ReportEntry._ID,
            CabCompanyEntry.COLUMN_NAME,
            ReportEntry.COLUMN_TOTAL_REVIEWS,
            ReportEntry.COLUMN_POSITIVE_RATINGS,
            ReportEntry.COLUMN_NEGATIVE_RATINGS
    };

    static final int COL_REPORT_ENTRY_ID = 0;
    static final int COL_COMPANY_NAME = 1;
    static final int COL_TOTAL_REVIEWS = 2;
    static final int COL_POSITIVE_RATINGS = 3;
    static final int COL_NEGATIVE_RATINGS = 4;

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

        mDashboardAdapter = new DashboardAdapter(getActivity(), null, 0);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DASHBOARD_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = RateMyRideContract.ReportEntry.COLUMN_TOTAL_REVIEWS + " DESC";
        Uri reportForLocationUri = RateMyRideContract.ReportEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                reportForLocationUri,
                DASHBOARD_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDashboardAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDashboardAdapter.swapCursor(null);
    }
}
