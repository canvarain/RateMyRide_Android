package com.canvara.apps.ratemyride.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.canvara.apps.ratemyride.data.RateMyRideContract.ReviewEntry;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */
public class TestReviewContract extends AndroidTestCase {
    private static final String TEST_REVIEW_LOCATION    = "/Chennai,TN";
    private static final long TEST_REVIEW_DATE          = 1442275200L; // September 15, 2015
    private static final long NORMALIZED_TEST_REVIEW_DATE = RateMyRideContract.normalizeDate(TEST_REVIEW_DATE);
    public void testBuildReviewLocation() {
        Uri locationUri = RateMyRideContract.ReviewEntry.buildReviewLocation(TEST_REVIEW_LOCATION);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildReviewLocation in " +
                        "RateMyRideContract.",
                locationUri);
        assertEquals("Error: Review location not properly appended to the end of the Uri",
                TEST_REVIEW_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Review location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.canvara.apps.ratemyride/review/%2FChennai%2CTN");

        // Test the location and date combination
        Uri locationWithDateUri = ReviewEntry
                .buildReviewLocationAndDate(TEST_REVIEW_LOCATION, TEST_REVIEW_DATE);

        assertEquals("Error: Review date not properly appended to the end of the Uri",
                Long.toString(NORMALIZED_TEST_REVIEW_DATE),
                locationWithDateUri.getLastPathSegment());

        String expectedURI = "content://com.canvara.apps.ratemyride/review/%2FChennai%2CTN/" +
                Long.toString(NORMALIZED_TEST_REVIEW_DATE);
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationWithDateUri.toString(),expectedURI
                );
    }
}
