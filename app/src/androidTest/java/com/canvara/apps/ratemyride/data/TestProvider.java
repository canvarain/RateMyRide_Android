package com.canvara.apps.ratemyride.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.canvara.apps.ratemyride.Review;
import com.canvara.apps.ratemyride.data.RateMyRideContract.CabCompanyEntry;
import com.canvara.apps.ratemyride.data.RateMyRideContract.LocationEntry;
import com.canvara.apps.ratemyride.data.RateMyRideContract.ReportEntry;
import com.canvara.apps.ratemyride.data.RateMyRideContract.ReviewAttachmentEntry;
import com.canvara.apps.ratemyride.data.RateMyRideContract.ReviewEntry;

import junit.framework.Test;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /**
     * Removes all records from the Database and the ContentProvider
     * After deleting it also queries the content provider to ensure that it is empty
     */
    public void deleteAllRecordsFromProvider() {
        // Review
        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );
        // Review Attachment
        mContext.getContentResolver().delete(
                ReviewAttachmentEntry.CONTENT_URI,
                null,
                null
        );
        // Location
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );
        // Cab Company
        mContext.getContentResolver().delete(
                CabCompanyEntry.CONTENT_URI,
                null,
                null
        );
        // Report
        mContext.getContentResolver().delete(
                ReportEntry.CONTENT_URI,
                null,
                null
        );

        // Now let us check the database

        // Review
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records have not been deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

        // Review Attachment
        cursor = mContext.getContentResolver().query(
                ReviewAttachmentEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records have not been deleted from Review Attachment table during delete", 0, cursor.getCount());
        cursor.close();

        // Location
        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records have not been deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();

        // Cab Company
        cursor = mContext.getContentResolver().query(
                CabCompanyEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records have not been deleted from Cab Company table during delete", 0, cursor.getCount());
        cursor.close();

        // Report
        cursor = mContext.getContentResolver().query(
                ReportEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records have not been deleted from Report table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /**
     * A helper to delete all records, in turn calls deleteAllRecordsFromProvider
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    /**
     * Sets up the test by deleting all records, this enables us to run the test in a clean manner
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /**
     * This test ensures that the content provider has been registered correctly
     */
    public void testProviderRegistration() {
        PackageManager pm = mContext.getPackageManager();


        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                RateMyRideProvider.class.getName());

        try {
            // Fetch the provider from the component name from Package Manager
            // Throws exception if the provider is not registered
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from Contract
            assertEquals("Error: WeatherProvider registered with authority: "
                            + providerInfo.authority
                            + " instead of authority: "
                            + RateMyRideContract.CONTENT_AUTHORITY,
                    providerInfo.authority,
                    RateMyRideContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException ex) {
            // The provider was not registered properly
            assertTrue("Error: RateMyRideProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    /**
     * Verifies that the content provider returns correct type for each type of URI it
     * can handle
     */
    public void testGetType() {

        // content://com.canvara.apps.ratemyride/review/
        String type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        //vnd.android.cursor.dir/com.canvara.apps.ratemyride/review
        assertEquals("Error: The ReviewEntry CONTENT_URI must return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);

        String testLocation="Chennai,TN";
        // content://com.canvara.apps.ratemyride/review/Chennai,TN
        type = mContext.getContentResolver().getType(
                ReviewEntry.buildReviewLocation(testLocation)
        );
        assertEquals("Error: The ReviewEntry CONTENT_URI must return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);

        long testDate = 1442275200L; // September 15, 2015
        type = mContext.getContentResolver().getType(
          ReviewEntry.buildReviewLocationAndDate(testLocation, testDate)
        );
        assertEquals("Error: The ReviewEntry CONTENT_URI must return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_ITEM_TYPE, type);

        // content://com.canvara.apps.ratemyride/review_attachment/
        type = mContext.getContentResolver().getType(ReviewAttachmentEntry.CONTENT_URI);
        //vnd.android.cursor.dir/com.canvara.apps.ratemyride/review_attachment
        assertEquals("Error: The ReviewAttachmentEntry CONTENT_URI must return ReviewAttachmentEntry.CONTENT_TYPE",
                ReviewAttachmentEntry.CONTENT_TYPE, type);

        // content://com.canvara.apps.ratemyride/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        //vnd.android.cursor.dir/com.canvara.apps.ratemyride/location
        assertEquals("Error: The LocationEntry CONTENT_URI must return LocationEntry.CONTENT_TYPE",
                LocationEntry.CONTENT_TYPE, type);

        // content://com.canvara.apps.ratemyride/cab_company/
        type = mContext.getContentResolver().getType(CabCompanyEntry.CONTENT_URI);
        //vnd.android.cursor.dir/com.canvara.apps.ratemyride/cab_company
        assertEquals("Error: The LocationEntry CONTENT_URI must return LocationEntry.CONTENT_TYPE",
                CabCompanyEntry.CONTENT_TYPE, type);

        // content://com.canvara.apps.ratemyride/report/
        type = mContext.getContentResolver().getType(ReportEntry.CONTENT_URI);
        //vnd.android.cursor.dir/com.canvara.apps.ratemyride/report
        assertEquals("Error: The ReportEntry CONTENT_URI must return ReportEntry.CONTENT_TYPE",
                ReportEntry.CONTENT_TYPE, type);
        
        // content://com.canvara.apps.ratemyride/report/Chennai,TN
        type = mContext.getContentResolver().getType(
                ReportEntry.buildReportLocation(testLocation)
        );
        assertEquals("Error: The ReportEntry CONTENT_URI must return ReportEntry.CONTENT_TYPE",
                ReportEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
                ReportEntry.buildReportLocationAndDate(testLocation, testDate)
        );
        assertEquals("Error: The ReportEntry CONTENT_URI must return ReportEntry.CONTENT_TYPE",
                ReportEntry.CONTENT_ITEM_TYPE, type);
    }

    /**
     * Tests a basic review creation by inserting data into the database and
     * then uses ContentProvider to read the data
     */
    public void testBasicReviewQuery() {

        RateMyRideDBHelper dbHelper = new RateMyRideDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        long locationId     = TestUtilities.insertNorthPoleLocationValues(mContext);
        long cabCompanyId   = TestUtilities.insertCabCompanyValues(mContext);
        ContentValues reviewValues = TestUtilities.createReviewValues(locationId, cabCompanyId);

        // we have the location, let us add a review now
        long reviewId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue("Unable to insert review entry into the database", reviewId != -1);

        db.close();

        Cursor reviewCursor = mContext.getContentResolver().query(
          ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicReviewQuery", reviewCursor, reviewValues);
    }

    /**
     * Tests the insert functionality, by first inserting the record
     * and reading the inserted record
     */
    public void testInsertReadProvider() {

        ContentValues locationValues = TestUtilities.createNorthPoleLocationValues();
        long locationId = TestUtilities.insertValueAndTest(
                "Error: TestInsertReadProvider failed, could not validate Location insert",
                mContext,
                LocationEntry.CONTENT_URI,
                locationValues);

        // Let us test the cab company insert
        ContentValues cabCompanyValues = TestUtilities.createCabCompanyValues();
        long cabCompanyId = TestUtilities.insertValueAndTest(
                "Error: TestInsertReadProvider failed, could not validate Cab Company insert",
                mContext,
                CabCompanyEntry.CONTENT_URI,
                cabCompanyValues);

        // We have a location and cab company let us insert some reviews
        ContentValues reviewValues = TestUtilities.createReviewValues(locationId, cabCompanyId);
        long reviewId = TestUtilities.insertValueAndTest(
                "Error: TestInsertReadProvider failed, could not validate Review insert",
                mContext,
                ReviewEntry.CONTENT_URI,
                reviewValues);

        // Now let us test the joins
        reviewValues.putAll(locationValues);
        reviewValues.putAll(cabCompanyValues);

        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildReviewLocationAndDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_REVIEW_DATE),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Could not fetch review with date", reviewCursor, reviewValues);

        reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildReviewLocationWithStartDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_REVIEW_DATE),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Could not fetch review with startdate", reviewCursor, reviewValues);
    }

    /**
     * Tests the update of the location
     */
    public void testUpdateLocation() {

        //  TODO the original example had tco registered on a cursor, however it is not working, need to fix this

        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, locationRowId);
        updatedValues.put(LocationEntry.COLUMN_LOCATION, "Global Village");

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);
        //locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, updatedValues, LocationEntry._ID + "= ?",
                new String[]{Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        tco.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(tco);
        //locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,   // projection
                LocationEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, locationObserver);

        // Register a content observer for our cab company delete.
        TestUtilities.TestContentObserver cabCompanyObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(CabCompanyEntry.CONTENT_URI, true, cabCompanyObserver);

        // Register a content observer for our review delete.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();

        locationObserver.waitForNotificationOrFail();
        cabCompanyObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(locationObserver);
        mContext.getContentResolver().unregisterContentObserver(cabCompanyObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
    }

    public void testBulkInsert() {
        ContentValues locationValues = TestUtilities.createNorthPoleLocationValues();
        long locationId = TestUtilities.insertValueAndTest(
                "Error: testBulkInsert failed, could not validate Location insert",
                mContext,
                LocationEntry.CONTENT_URI,
                locationValues);

        // Let us test the cab company insert
        ContentValues cabCompanyValues = TestUtilities.createCabCompanyValues();
        long cabCompanyId = TestUtilities.insertValueAndTest(
                "Error: testBulkInsert failed, could not validate Cab Company insert",
                mContext,
                CabCompanyEntry.CONTENT_URI,
                cabCompanyValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for review
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertReviewValues(locationId, cabCompanyId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);

        assertEquals(insertCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ReviewEntry.COLUMN_START_DATE + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < TestUtilities.BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating ReviewEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
 }
