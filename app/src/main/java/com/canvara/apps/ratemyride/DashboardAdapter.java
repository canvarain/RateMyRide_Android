package com.canvara.apps.ratemyride;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canvara.apps.ratemyride.data.RateMyRideContract;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

public class DashboardAdapter extends CursorAdapter {


    public DashboardAdapter (Context context, Cursor c, int flags) {
        super (context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_dashboard, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView cabNameView = (TextView) view.findViewById(R.id.list_item_cab_name);

        cabNameView.setText(getCabNameFromCursor(cursor));
    }

    // TODO to implement view holder pattern, this is just for testing
    private String getCabNameFromCursor(Cursor cursor) {
        int idx_cab_name = cursor.getColumnIndex(RateMyRideContract.CabCompanyEntry.COLUMN_NAME);
        String cabName = cursor.getString(idx_cab_name);
        return cabName;
    }
}
