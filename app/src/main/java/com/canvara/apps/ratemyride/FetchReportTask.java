package com.canvara.apps.ratemyride;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.canvara.apps.ratemyride.data.RateMyRideContract;

import java.util.UUID;
import java.util.Vector;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

public class FetchReportTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReportTask.class.getSimpleName();

    private final Context mContext;

    public FetchReportTask (Context context) {
        mContext = context;
    }

    private long addLocation(String locationSetting, String location, double lat, double lng) {

        long locationId;

        // First check if the location with the name exists in the database
        Cursor locationCursor = mContext.getContentResolver().query(
                RateMyRideContract.LocationEntry.CONTENT_URI,
                new String[] {RateMyRideContract.LocationEntry._ID},
                RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING +  " = ?",
                new String[] {locationSetting},
                null);

        if(locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(RateMyRideContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(RateMyRideContract.LocationEntry.COLUMN_SERVER_ID, UUID.randomUUID().toString());
            locationValues.put(RateMyRideContract.LocationEntry.COLUMN_LOCATION, location);
            locationValues.put(RateMyRideContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(RateMyRideContract.LocationEntry.COLUMN_COORD_LNG, lng);

            Uri insertedUri  = mContext.getContentResolver()
                    .insert(RateMyRideContract.LocationEntry.CONTENT_URI,locationValues);

            locationId = ContentUris.parseId(insertedUri);
        }
        locationCursor.close();
        return locationId;
    }

    private long addCabCompany(String companyName) {

        long companyId;

        // First check if the location with the name exists in the database
        Cursor cabCompanyCursor = mContext.getContentResolver().query(
                RateMyRideContract.CabCompanyEntry.CONTENT_URI,
                new String[] {RateMyRideContract.CabCompanyEntry._ID},
                RateMyRideContract.CabCompanyEntry.COLUMN_NAME +  " = ?",
                new String[] {companyName},
                null);

        if(cabCompanyCursor.moveToFirst()) {
            int cabCompanyIdIndex = cabCompanyCursor.getColumnIndex(RateMyRideContract.CabCompanyEntry._ID);
            companyId = cabCompanyCursor.getLong(cabCompanyIdIndex);
        } else {
            ContentValues cabCompanyValues = new ContentValues();

            cabCompanyValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_NAME, companyName);
            cabCompanyValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_SERVER_ID, UUID.randomUUID().toString());
            cabCompanyValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_WEBSITE_URL, "");
            cabCompanyValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_THUMBNAIL_URL, "");

            Uri insertedUri  = mContext.getContentResolver()
                    .insert(RateMyRideContract.CabCompanyEntry.CONTENT_URI,cabCompanyValues);

            companyId = ContentUris.parseId(insertedUri);
        }
        cabCompanyCursor.close();
        return companyId;
    }
    /**
     * A helper to parse JSON response and insert data into the local database
     * @param jsonResponse
     * @param location
     */
    private void getReportDataFromJSON(String jsonResponse, String location) {

        // TODO this is dummy implementation, replace it with real implementation
        // once the service is completed

        int inserted = 0;

        long locationId = addLocation("Chennai", "Chennai", 13.0833, 80.2666);

        Vector<ContentValues> reportValues = new Vector<>(10);
        long date = getUTCDate();

        reportValues.add(generateReportValues(locationId, addCabCompany("OLA Cabs"),100000,0.97,0.03,date, date));
        reportValues.add(generateReportValues(locationId, addCabCompany("Fast Track"),88000,0.56,0.44,date, date));
        reportValues.add(generateReportValues(locationId, addCabCompany("Meru Cabs"), 93000, 0.91, 0.09, date, date));
        reportValues.add(generateReportValues(locationId, addCabCompany("Barathi Call Taxi"), 70000, 0.77, 0.23, date, date));
        reportValues.add(generateReportValues(locationId, addCabCompany("Friends Call Taxi"), 560000, 0.47, 0.53, date, date));

        ContentValues[] reportValuesArray = new ContentValues[reportValues.size()];
        reportValues.toArray(reportValuesArray);

        inserted = mContext.getContentResolver().bulkInsert(RateMyRideContract.ReportEntry.CONTENT_URI,reportValuesArray);

        Log.d(LOG_TAG, "Fetch Report Task Completed " + inserted + " rows inserted");
    }

    private ContentValues generateReportValues(long locationId,
                                               long cabCompanyId,
                                               long totalReviews,
                                               double positiveRatings,
                                               double negativeRatings,
                                               long startDate,
                                               long endDate) {
        ContentValues cv = new ContentValues();
        cv.put(RateMyRideContract.ReportEntry.COLUMN_LOCATION_KEY,locationId);
        cv.put(RateMyRideContract.ReportEntry.COLUMN_CAB_COMPANY_KEY,cabCompanyId);
        cv.put(RateMyRideContract.ReportEntry.COLUMN_TOTAL_REVIEWS,totalReviews);
        cv.put(RateMyRideContract.ReportEntry.COLUMN_POSITIVE_RATINGS,positiveRatings);
        cv.put(RateMyRideContract.ReportEntry.COLUMN_NEGATIVE_RATINGS,negativeRatings);
        cv.put(RateMyRideContract.ReportEntry.COLUMN_START_DATE,startDate);
        cv.put(RateMyRideContract.ReportEntry.COLUMN_END_DATE, endDate);

        return cv;
    }

    private long getUTCDate() {
        Time dayTime = new Time();
        dayTime.setToNow();
        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        // now we work exclusively in UTC
        dayTime = new Time();
        return dayTime.setJulianDay(julianStartDay);
    }
    @Override
    protected Void doInBackground(String... params) {

        // TODO,replace with actual fetch
        // This is the placeholder for implementing reports fetching
        // We will make a HTTP call and format the result
        getReportDataFromJSON("", "");
        return null;
    }
}
