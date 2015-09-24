package com.canvara.apps.ratemyride.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

public class RateMyRideProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RateMyRideDBHelper mDBHelper;

    static final int REVIEW = 100;
    static final int REVIEW_WITH_LOCATION = 101;
    static final int REVIEW_WITH_LOCATION_AND_DATE = 102;
    static final int REVIEW_ATTACHMENT = 200;
    static final int LOCATION = 300;
    static final int CAB_COMPANY = 400;
    static final int REPORT = 500;
    static final int REPORT_WITH_LOCATION = 501;
    static final int REPORT_WITH_LOCATION_AND_DATE = 502;

    private static final SQLiteQueryBuilder sReviewQueryBuilder;
    private static final SQLiteQueryBuilder sReportQueryBuilder;

    static {
        sReviewQueryBuilder = new SQLiteQueryBuilder();
        sReviewQueryBuilder.setTables(
                RateMyRideContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        RateMyRideContract.LocationEntry.TABLE_NAME +
                        " ON " + RateMyRideContract.ReviewEntry.TABLE_NAME +
                        "." + RateMyRideContract.ReviewEntry.COLUMN_LOCATION_KEY +
                        " = " + RateMyRideContract.LocationEntry.TABLE_NAME +
                        "." + RateMyRideContract.LocationEntry._ID + " INNER JOIN " +
                        RateMyRideContract.CabCompanyEntry.TABLE_NAME +
                        " ON " + RateMyRideContract.ReviewEntry.TABLE_NAME +
                        "." + RateMyRideContract.ReviewEntry.COLUMN_CAB_COMPANY_KEY +
                        " = " + RateMyRideContract.CabCompanyEntry.TABLE_NAME +
                        "." + RateMyRideContract.CabCompanyEntry._ID);

        sReportQueryBuilder = new SQLiteQueryBuilder();
        sReportQueryBuilder.setTables(
                RateMyRideContract.ReportEntry.TABLE_NAME + " INNER JOIN " +
                        RateMyRideContract.LocationEntry.TABLE_NAME +
                        " ON " + RateMyRideContract.ReportEntry.TABLE_NAME +
                        "." + RateMyRideContract.ReportEntry.COLUMN_LOCATION_KEY +
                        " = " + RateMyRideContract.LocationEntry.TABLE_NAME +
                        "." + RateMyRideContract.LocationEntry._ID);
    }

    private static final String sLocationSelection =
            RateMyRideContract.LocationEntry.TABLE_NAME +
                    "." + RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    private static final String sLocationWithStartDateSelectionReview =
            RateMyRideContract.LocationEntry.TABLE_NAME +
                    "." + RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    RateMyRideContract.ReviewEntry.COLUMN_START_DATE + " >= ? " ;

    private static final String sLocationAndStartDateSelectionReview =
            RateMyRideContract.LocationEntry.TABLE_NAME +
                    "." + RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    RateMyRideContract.ReviewEntry.COLUMN_START_DATE + " = ? " ;

    private static final String sLocationWithStartDateSelectionReport =
            RateMyRideContract.LocationEntry.TABLE_NAME +
                    "." + RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    RateMyRideContract.ReportEntry.COLUMN_START_DATE + " >= ? " ;

    private static final String sLocationAndStartDateSelectionReport =
            RateMyRideContract.LocationEntry.TABLE_NAME +
                    "." + RateMyRideContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    RateMyRideContract.ReportEntry.COLUMN_START_DATE + " = ? " ;

    private Cursor getReviewByLocation(Uri uri, String[] projection, String sortOrder) {

        String location = RateMyRideContract.ReviewEntry.getLocationFromUri(uri);
        long startDate = RateMyRideContract.ReviewEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if(startDate == 0) {
            selection = sLocationSelection;
            selectionArgs = new String[] {location};
        } else {
            selection = sLocationWithStartDateSelectionReview;
            selectionArgs = new String[] {location, Long.toString(startDate)};
        }
        return sReviewQueryBuilder.query(mDBHelper.getReadableDatabase(),
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
    }

    private Cursor getReviewByLocationAndDate(Uri uri, String[] projection, String sortOrder) {
        String location = RateMyRideContract.ReviewEntry.getLocationFromUri(uri);
        long date = RateMyRideContract.ReviewEntry.getDateFromUri(uri);

        return sReviewQueryBuilder.query(mDBHelper.getReadableDatabase(),
            projection,
                sLocationAndStartDateSelectionReview,
            new String[]{location, Long.toString(date)},
            null,
            null,
            sortOrder
        );
    }

    private Cursor getReportByLocation(Uri uri, String[] projection, String sortOrder) {

        String location = RateMyRideContract.ReportEntry.getLocationFromUri(uri);
        long startDate = RateMyRideContract.ReportEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if(startDate == 0) {
            selection = sLocationSelection;
            selectionArgs = new String[] {location};
        } else {
            selection = sLocationWithStartDateSelectionReport;
            selectionArgs = new String[] {location, Long.toString(startDate)};
        }
        return mDBHelper.getReadableDatabase().query(
                RateMyRideContract.ReportEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReportByLocationAndDate(Uri uri, String[] projection, String sortOrder) {
        String location = RateMyRideContract.ReportEntry.getLocationFromUri(uri);
        long date = RateMyRideContract.ReportEntry.getDateFromUri(uri);

        return sReviewQueryBuilder.query(mDBHelper.getReadableDatabase(),
                projection,
                sLocationAndStartDateSelectionReport,
                new String[]{location, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    private void normalizeDate(ContentValues values, String dateColumnName) {
        // normalize the date value
        if (values.containsKey(dateColumnName)) {
            long dateValue = values.getAsLong(dateColumnName);
            values.put(dateColumnName, RateMyRideContract.normalizeDate(dateValue));
        }
    }

    /**
     * Matches the application's URI to the constants defined in the class
     * @return Instance of UriMatcher constructed for our application
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RateMyRideContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RateMyRideContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, RateMyRideContract.PATH_REVIEW + "/*", REVIEW_WITH_LOCATION);
        matcher.addURI(authority, RateMyRideContract.PATH_REVIEW + "/*/#", REVIEW_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, RateMyRideContract.PATH_REVIEW_ATTACHMENT, REVIEW_ATTACHMENT);

        matcher.addURI(authority, RateMyRideContract.PATH_LOCATION, LOCATION);

        matcher.addURI(authority, RateMyRideContract.PATH_CAB_COMPANY, CAB_COMPANY);

        matcher.addURI(authority, RateMyRideContract.PATH_REPORT, REPORT);
        matcher.addURI(authority, RateMyRideContract.PATH_REPORT + "/*", REPORT_WITH_LOCATION);
        matcher.addURI(authority, RateMyRideContract.PATH_REPORT + "/*/#", REPORT_WITH_LOCATION_AND_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new RateMyRideDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REVIEW_WITH_LOCATION_AND_DATE:
                return RateMyRideContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case REVIEW_WITH_LOCATION:
                return RateMyRideContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW:
                return RateMyRideContract.ReviewEntry.CONTENT_TYPE;
            case LOCATION:
                return RateMyRideContract.LocationEntry.CONTENT_TYPE;
            case REVIEW_ATTACHMENT:
                return RateMyRideContract.ReviewAttachmentEntry.CONTENT_TYPE;
            case CAB_COMPANY:
                return RateMyRideContract.CabCompanyEntry.CONTENT_TYPE;
            case REPORT_WITH_LOCATION_AND_DATE:
                return RateMyRideContract.ReportEntry.CONTENT_ITEM_TYPE;
            case REPORT_WITH_LOCATION:
                return RateMyRideContract.ReportEntry.CONTENT_TYPE;
            case REPORT:
                return RateMyRideContract.ReportEntry.CONTENT_TYPE;
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case REVIEW_WITH_LOCATION_AND_DATE:
            {
                retCursor = getReviewByLocationAndDate(uri, projection, sortOrder);
                break;
            }
            case REVIEW_WITH_LOCATION:
            {
                retCursor = getReviewByLocation(uri, projection, sortOrder);
                break;
            }
            case REVIEW:
            {
                retCursor = mDBHelper.getReadableDatabase().query(
                        RateMyRideContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOCATION:
            {
                retCursor = mDBHelper.getReadableDatabase().query(
                        RateMyRideContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CAB_COMPANY:
            {
                retCursor = mDBHelper.getReadableDatabase().query(
                        RateMyRideContract.CabCompanyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW_ATTACHMENT:
            {
                retCursor = mDBHelper.getReadableDatabase().query(
                        RateMyRideContract.ReviewAttachmentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REPORT_WITH_LOCATION_AND_DATE:
            {
                retCursor = getReportByLocationAndDate(uri, projection, sortOrder);
                break;
            }
            case REPORT_WITH_LOCATION:
            {
                retCursor = getReportByLocation(uri, projection, sortOrder);
                break;
            }
            case REPORT:
            {
                retCursor = mDBHelper.getReadableDatabase().query(
                        RateMyRideContract.ReportEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case REVIEW:
            {
                // Normalize the date columns
                // TODO this looks a bit ugly, should convert it into a factory someitme
                normalizeDate(values, RateMyRideContract.ReviewEntry.COLUMN_START_DATE);
                normalizeDate(values, RateMyRideContract.ReviewEntry.COLUMN_END_DATE);
                long _id = db.insert(RateMyRideContract.ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = RateMyRideContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            }
            case REVIEW_ATTACHMENT:
            {
                long _id = db.insert(RateMyRideContract.ReviewAttachmentEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = RateMyRideContract.ReviewAttachmentEntry.buildReviewAttachmentUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case LOCATION:
            {
                long _id = db.insert(RateMyRideContract.LocationEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = RateMyRideContract.LocationEntry.buildLocationUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case CAB_COMPANY:
            {
                long _id = db.insert(RateMyRideContract.CabCompanyEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = RateMyRideContract.CabCompanyEntry.buildCabCompanyUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REPORT:
            {
                // Normalize the date columns
                // TODO this looks a bit ugly, should convert it into a factory someitme
                normalizeDate(values, RateMyRideContract.ReportEntry.COLUMN_START_DATE);
                normalizeDate(values, RateMyRideContract.ReportEntry.COLUMN_END_DATE);

                long _id = db.insert(RateMyRideContract.ReportEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = RateMyRideContract.ReportEntry.buildReportUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // If selection is not provided, delete all rows and return the number of rows deleted
        if (null == selection) selection = "1";

        switch(match) {
            case REVIEW:
                rowsDeleted = db.delete(RateMyRideContract.ReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case REVIEW_ATTACHMENT:
                rowsDeleted = db.delete(RateMyRideContract.ReviewAttachmentEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(RateMyRideContract.LocationEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CAB_COMPANY:
                rowsDeleted = db.delete(RateMyRideContract.CabCompanyEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case REPORT:
                rowsDeleted = db.delete(RateMyRideContract.ReportEntry.TABLE_NAME,
                    selection,
                    selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch(match) {
            case REVIEW:
                rowsUpdated = db.update(RateMyRideContract.ReviewEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case REVIEW_ATTACHMENT:
                rowsUpdated = db.update(RateMyRideContract.ReviewAttachmentEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(RateMyRideContract.LocationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CAB_COMPANY:
                rowsUpdated = db.update(RateMyRideContract.CabCompanyEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case REPORT:
                rowsUpdated = db.update(RateMyRideContract.ReportEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case REVIEW:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value, RateMyRideContract.ReviewEntry.COLUMN_START_DATE);
                        normalizeDate(value, RateMyRideContract.ReviewEntry.COLUMN_END_DATE);
                        long _id = db.insert(RateMyRideContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REPORT:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value, RateMyRideContract.ReportEntry.COLUMN_START_DATE);
                        normalizeDate(value, RateMyRideContract.ReportEntry.COLUMN_END_DATE);
                        long _id = db.insert(RateMyRideContract.ReportEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
