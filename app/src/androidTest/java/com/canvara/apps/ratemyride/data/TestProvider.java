package com.canvara.apps.ratemyride.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

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

        // Register the content observer for our insert.
        // This time directly with the content resolver.

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationId = ContentUris.parseId(locationUri);

        // Verify that we have got a row back
        assertTrue(locationId != -1);

        // The data should have been inserted, let us query to verify the results

        Cursor cursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor(
                "Error: TestInsertReadProvider failed, could not validate the insert",
                cursor,
                locationValues);

        // Let us test the cab company insert
        ContentValues cabCompanyValues = TestUtilities.createCabCompanyValues();
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(CabCompanyEntry.CONTENT_URI, true, tco);
        Uri cabCompanyUri = mContext.getContentResolver().insert(CabCompanyEntry.CONTENT_URI, cabCompanyValues);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long cabCompanyId = ContentUris.parseId(cabCompanyUri);
        // Verify that we have got a row back
        assertTrue(cabCompanyId != -1);

        // The data should have been inserted, let us query to verify the results
        Cursor cabCompanycursor = mContext.getContentResolver().query(CabCompanyEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor(
                "Error: TestInsertReadProvider failed, could not validate the insert",
                cabCompanycursor,
                cabCompanyValues);

        // We have a location and cab company let us insert some reviews
        ContentValues reviewValues = TestUtilities.createReviewValues(locationId, cabCompanyId);

        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewUri = mContext.getContentResolver().insert(
                ReviewEntry.CONTENT_URI,
                reviewValues
        );

        assertTrue(reviewUri != null);

        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null, 
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                reviewCursor, reviewValues);

    }
}
