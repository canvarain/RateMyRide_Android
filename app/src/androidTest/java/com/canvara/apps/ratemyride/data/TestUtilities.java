package com.canvara.apps.ratemyride.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.canvara.apps.ratemyride.data.RateMyRideContract;
import com.canvara.apps.ratemyride.utils.PollingCheck;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_LOCATION       = "99705";
    static final String TEST_CAB_NAME       = "Fast Cabs";
    static final long TEST_REVIEW_DATE      = 1442275200L; // September 15, 2015


    /**
     * Creates test values for the location table
     * @returns testValues which is an instance of ContentValues
     */
    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();

        testValues.put(RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(RateMyRideContract.LocationEntry.COLUMN_SERVER_ID, UUID.randomUUID().toString());
        testValues.put(RateMyRideContract.LocationEntry.COLUMN_LOCATION, "North Pole");
        testValues.put(RateMyRideContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(RateMyRideContract.LocationEntry.COLUMN_COORD_LNG, -147.353);

        return testValues;
    }

    /**
     * Inserts the north pole location values into the location table
     * @param context   database context
     * @return          id of the row inserted, -1 in case of a failure
     */
    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        RateMyRideDBHelper dbHelper = new RateMyRideDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(RateMyRideContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /**
     * Creates test values for cab company table
     * @return ContentValues
     */
    static ContentValues createCabCompanyValues() {
        ContentValues cabValues = new ContentValues();

        cabValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_NAME, TEST_CAB_NAME);
        cabValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_SERVER_ID, UUID.randomUUID().toString());
        cabValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_WEBSITE_URL, "");
        cabValues.put(RateMyRideContract.CabCompanyEntry.COLUMN_THUMBNAIL_URL, "");

        return cabValues;
    }


    /**
     * Inserts cab company values into the location table
     * @param context   database context
     * @return          id of the row inserted, -1 in case of a failure
     */
    static long insertCabCompanyValues(Context context) {
        // insert our test records into the database
        RateMyRideDBHelper dbHelper = new RateMyRideDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createCabCompanyValues();

        long cabCompanyId;
        cabCompanyId = db.insert(RateMyRideContract.CabCompanyEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Cab Company Values", cabCompanyId != -1);

        return cabCompanyId;
    }

    /**
     * Vallidates a cursor
     * @param error             Error message to throw
     * @param valueCursor       Cursor containing record fetched
     * @param expectedValues    Expected record
     */
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * Validates if the current record contains the expected values
     * @param error             Error message to throw
     * @param valueCursor       Cursor containing record fetched
     * @param expectedValues    Expected record
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createReviewValues (long locationId, long cabCompanyId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_START_DATE, TEST_REVIEW_DATE);
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_END_DATE, TEST_REVIEW_DATE);
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_LOCATION_KEY, locationId);
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_CAB_COMPANY_KEY, cabCompanyId);
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_DRIVER_NAME, "Ramesh");
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_RATING, "5");
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_REGISTRATION_NUMBER, "TN01AH0011");
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_REMARKS, "Thanks, it was a great service");
        reviewValues.put(RateMyRideContract.ReviewEntry.COLUMN_SERVER_ID, UUID.randomUUID().toString());

        return reviewValues;
    }

    // NOTE: Adapted from Sunshine Android Tutorial / App
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
