package com.canvara.apps.ratemyride.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.canvara.apps.ratemyride.Review;
import  com.canvara.apps.ratemyride.data.RateMyRideContract.LocationEntry;
import  com.canvara.apps.ratemyride.data.RateMyRideContract.ReviewEntry;
import  com.canvara.apps.ratemyride.data.RateMyRideContract.ReviewAttachmentEntry;
import  com.canvara.apps.ratemyride.data.RateMyRideContract.CabCompanyEntry;
import  com.canvara.apps.ratemyride.data.RateMyRideContract.ReportEntry;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */
public class RateMyRideDBHelper extends SQLiteOpenHelper {

    // If the schema changes the version has to be incremented
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "rate_my_ride.db";

    public RateMyRideDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID +                           " INTEGER PRIMARY KEY," +
                ReviewEntry.COLUMN_SERVER_ID +              " TEXT NOT NULL," +
                ReviewEntry.COLUMN_LOCATION_KEY +           " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_START_DATE +             " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_END_DATE +               " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_RATING +                 " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_REMARKS +                " TEXT," +
                ReviewEntry.COLUMN_CAB_COMPANY_KEY +        " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_DRIVER_NAME +            " TEXT NOT NULL," +
                ReviewEntry.COLUMN_REGISTRATION_NUMBER +    " TEXT" +
                " );" ;

        final String SQL_CREATE_REVIEW_ATTACHMENT_TABLE = "CREATE TABLE " + ReviewAttachmentEntry.TABLE_NAME + " (" +
                ReviewAttachmentEntry._ID +                 " INTEGER PRIMARY KEY," +
                ReviewAttachmentEntry.COLUMN_SERVER_ID +    " TEXT NOT NULL," +
                ReviewAttachmentEntry.COLUMN_REVIEW_KEY +   " INTEGER NOT NULL," +
                ReviewAttachmentEntry.COLUMN_MIME_TYPE +    " TEXT NOT NULL," +
                ReviewAttachmentEntry.COLUMN_URL +          " TEXT NOT NULL" +
                " );" ;

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID +                         " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_SERVER_ID +            " TEXT NOT NULL," +
                LocationEntry.COLUMN_LOCATION_SETTING +     " TEXT NOT NULL," +
                LocationEntry.COLUMN_LOCATION +             " TEXT NOT NULL," +
                LocationEntry.COLUMN_COORD_LAT +            " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LNG +            " REAL NOT NULL" +
                ");" ;

        final String SQL_CREATE_CAB_COMPANY_TABLE = "CREATE TABLE " + CabCompanyEntry.TABLE_NAME + " (" +
                CabCompanyEntry._ID +                       " INTEGER PRIMARY KEY," +
                CabCompanyEntry.COLUMN_SERVER_ID +          " TEXT NOT NULL," +
                CabCompanyEntry.COLUMN_NAME +               " TEXT NOT NULL," +
                CabCompanyEntry.COLUMN_WEBSITE_URL +        " TEXT," +
                CabCompanyEntry.COLUMN_THUMBNAIL_URL +      " TEXT" +
                " );" ;

        final String SQL_CREATE_REPORT_TABLE = "CREATE TABLE " + ReportEntry.TABLE_NAME + " (" +
                ReportEntry._ID +                           " INTEGER NOT NULL," +
                ReportEntry.COLUMN_CAB_COMPANY_ID +         " TEXT NOT NULL," +
                ReportEntry.COLUMN_POSITIVE_RATINGS +       " INTEGER NOT NULL," +
                ReportEntry.COLUMN_NEGATIVE_RATINGS +       " INTEGER NOT NULL," +
                ReportEntry.COLUMN_TOTAL_REVIEWS +          " INTEGER NOT NULL," +
                ReportEntry.COLUMN_START_DATE +             " INTEGER NOT NULL," +
                ReportEntry.COLUMN_END_DATE +               " INTEGER NOT NULL" +
                " );" ;

        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_ATTACHMENT_TABLE);
        db.execSQL(SQL_CREATE_CAB_COMPANY_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_REPORT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop the existing tables and recreate the newer version
        // PLEASE NOTE THAT THIS WILL WIPE EXISTING DATA

        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewAttachmentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CabCompanyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReportEntry.TABLE_NAME);

        onCreate(db);
    }
}
