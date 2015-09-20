package com.canvara.apps.ratemyride.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.canvara.apps.ratemyride.data.RateMyRideContract;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_LOCATION = "99705";

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
}
