package com.canvara.apps.ratemyride.data;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines the tables and columns for the RateMyRide database
 */
public class RateMyRideContract {
    // The "Content Authority" is the global name for the entire content provier.
    public static final String CONTENT_AUTHORITY = "com.canvara.apps.ratemyride";

    // Use "Content Authority" to create the base for all URI's with which the apps will contact the
    // content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    // Possible paths to access the content
    // Example content://com.canvara.apps.ratemyride/review is a valid path to look at review data
    public static final String PATH_REVIEW = "review";
    public static final String PATH_REVIEW_ATTACHMENT = "review_attachment";
    public static final String PATH_CAB_COMPANY = "cab_company";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_REPORT = "report";

    // To make it easy to query exact date, let us normalize the dates to the start of
    // Julian day at UTC
    public static long normalizeDate (long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDate = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDate);
    }

    /**
    * Inner class that represents the review table
    */
    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REVIEW;

        // Table Name
        public static final String TABLE_NAME = "review";

        // The location of the reviewer and the review itself
        public static final String COLUMN_LOCATION_KEY = "location_id";
        // Start date of the ride (trip) along with time
        public static final String COLUMN_START_DATE = "start_date";
        // End date of the ride (trip) along with the time
        public static final String COLUMN_END_DATE = "end_date";
        // Rating of the trip (positive or negative)
        public static final String COLUMN_RATING = "rating";
        // Remarks entered by the reviewer
        public static final String COLUMN_REMARKS = "remarks";
        // Cab company ID as returned by the API, will be used to look up image for the cab company
        public static final String COLUMN_CAB_COMPANY_KEY = "cab_company_id";
        // Name of the driver
        public static final String COLUMN_DRIVER_NAME = "driver_name";
        // Vehicle registration number
        public static final String COLUMN_REGISTRATION_NUMBER = "registration_number";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Inner class representing the review attachments
     */
    public static final class ReviewAttachmentEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW_ATTACHMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REVIEW_ATTACHMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REVIEW_ATTACHMENT;

        // Table Name
        public static final String TABLE_NAME = "review_attachment";

        // The review to which the attachment belongs to
        public static final String COLUMN_REVIEW_KEY = "review_id";
        // Mime type of the attachment
        public static final String COLUMN_MIME_TYPE = "mime_type";
        // Url for the attachment
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewAttachmentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /**
     * Inner class representing the location table
     */
    public static final class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        // The location is what will be sent during a review creation.
        // It can be a post code or a more elaborate location string
        // Example '600020' or 'Adyar, Chennai, India'
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // Human readable location string returned by the API
        public static final String COLUMN_LOCATION = "location";
        // Latitude
        public static final String COLUMN_COORD_LAT = "coord_lat";
        // Longitude
        public static final String COLUMN_COORD_LNG = "coord_lng";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Inner class representing the cab company
     */
    public static final class CabCompanyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAB_COMPANY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_CAB_COMPANY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_CAB_COMPANY;

        public static final String TABLE_NAME = "cab_company";

        // Name of the cab company
        public static final String COLUMN_NAME = "name";
        // Url of the company website
        public static final String COLUMN_WEBSITE_URL = "website_url";
        // Url for the company thumbnail
        public static final String COLUMN_thumbnail_url = "thumbnail_url";

        public static Uri buildCabCompanyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Inner class representing a report, This table can be further split into
     * query and results, it is deferred for now to maintain the simplicity
     * of the application
     */
    public static final class ReportEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPORT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REPORT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REPORT;

        public static final String TABLE_NAME = "report";

        // The cab company to which the review belongs to
        public static final String COLUMN_CAB_COMPANY_KEY = "cab_company_id";
        // Total number of reviews
        public static final String COLUMN_TOTAL_REVIEWS = "total_reviews";
        // Total positive ratings for the cab company
        public static final String COLUMN_POSITIVE_RATINGS = "positive_ratings";
        // Total negative ratings for the cab company
        public static final String COLUMN_NEGATIVE_RATINGS = "negative_ratings";
        // Start date for the report
        public static final String COLUMN_START_DATE = "start_date";
        // End date for the report
        public static final String COLUMN_END_DATE = "end_date";

        public static Uri buildCabCompanyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
