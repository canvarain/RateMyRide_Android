package com.canvara.apps.ratemyride.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // We want to start each test with clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(RateMyRideDBHelper.DATABASE_NAME);
    }

    /**
     * This method is called before each test and deletes the database. This ensures that
     * we always have a clean test.
     */
    public void setup() {
        deleteTheDatabase();
    }

    /**
     * Tests the creation of the database for our app
     */
    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(RateMyRideContract.ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(RateMyRideContract.ReviewAttachmentEntry.TABLE_NAME);
        tableNameHashSet.add(RateMyRideContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(RateMyRideContract.CabCompanyEntry.TABLE_NAME);
        tableNameHashSet.add(RateMyRideContract.ReportEntry.TABLE_NAME);

        mContext.deleteDatabase(RateMyRideDBHelper.DATABASE_NAME);

        SQLiteDatabase db = new RateMyRideDBHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Check if all tables have been created
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("This means that the database has not been created properly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while(c.moveToNext());

        assertTrue("Error: Database was created without some of the tables. hint: must contain 5 tables", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + RateMyRideContract.ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: we are not able to query the database for table information", c.moveToFirst());

        // Build the hashset of column names we are looking for
        final HashSet<String> reviewColumnHashSet = new HashSet<>();
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry._ID);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_SERVER_ID);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_START_DATE);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_END_DATE);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_CAB_COMPANY_KEY);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_DRIVER_NAME);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_LOCATION_KEY);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_RATING);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_REMARKS);
        reviewColumnHashSet.add(RateMyRideContract.ReviewEntry.COLUMN_REGISTRATION_NUMBER);

        int colIndex = c.getColumnIndex("name");

        do {
            String columnName = c.getString(colIndex);
            reviewColumnHashSet.remove(columnName);
        } while (c.moveToNext());



        c.close();
        db.close();
    }

    public void testLocationTable() {
        insertLocation();
    }

    public long insertLocation() {

        // Get an instance to a writable database
        RateMyRideDBHelper dbHelper = new RateMyRideDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create Content Values for what needs to be inserted
        // Thanks to Google Android Tutorial for the implementation idea
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationId;
        locationId = db.insert(RateMyRideContract.LocationEntry.TABLE_NAME, null, testValues);

        // Assert that we got a row back
        assertTrue(locationId != -1);

        // Let us pull the data and verify the inserted record
        Cursor cursor = db.query(
                RateMyRideContract.LocationEntry.TABLE_NAME,
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Validate the record against the actual data inserted
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Close Cursor and Database
        cursor.close();
        db.close();
        return locationId;
    }

}
