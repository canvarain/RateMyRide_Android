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

        String labelReviews = context.getResources().getString(R.string.label_reviews);
        String labelPercentage = context.getResources().getString(R.string.label_percentage_positive);
        String thousands = context.getResources().getString(R.string.label_thousands);

        TextView cabNameView = (TextView) view.findViewById(R.id.list_item_cab_name);
        TextView totalReviewsView = (TextView) view.findViewById(R.id.list_item_review_count);
        TextView positiveReviewsView = (TextView) view.findViewById(R.id.list_item_positive_reviews);

        cabNameView.setText(getCabNameFromCursor(cursor));
        totalReviewsView.setText(getReviewCountFromCursor(cursor) + thousands + " " + labelReviews);
        positiveReviewsView.setText(getPostiveReviewsFromCursor(cursor) + labelPercentage);

    }

    // TODO to implement view holder pattern, this is just for testing
    private String getCabNameFromCursor(Cursor cursor) {
        return cursor.getString(MainActivityFragment.COL_COMPANY_NAME);
    }

    private String getReviewCountFromCursor(Cursor cursor) {
        return Utility.convertToK(
                cursor.getLong(MainActivityFragment.COL_TOTAL_REVIEWS)
        );
    }

    private String getPostiveReviewsFromCursor(Cursor cursor) {
        return Utility.convertToPercentage(
          cursor.getDouble(MainActivityFragment.COL_POSITIVE_RATINGS)
        );
    }
}
