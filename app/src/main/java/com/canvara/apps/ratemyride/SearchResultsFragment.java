package com.canvara.apps.ratemyride;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment for diaplaying search results
 */
public class SearchResultsFragment extends Fragment {

    private ArrayAdapter<String> mSearchResultsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_results, container, false);

        // TODO Fetch this details from API and bind though sync adapter or volley
        String[] searchResultArray = {
                "Great Service, took me on time to airport",
                "Very impolite driver, appeared drunk too. I wish that OLA Cab will investigate this issue",
                "Thanks for wonderful service",
                "The driver took care of my parents as I could not accompany them to the station",
                "Excellent, I am a very happy customer at the end of the day"};

        List<String> searchResultList = new ArrayList<>(Arrays.asList(searchResultArray));

        // Setup the dashboard adapter by binding the data and the template
        mSearchResultsAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_search_result,
                R.id.search_result,
                searchResultArray);

        ListView listView = (ListView) view.findViewById(R.id.listViewSearchResult);
        listView.setAdapter(mSearchResultsAdapter);

        // Event handler to handle clicks on the search results.
        // Clicking the search result will fire an intent to open the
        // Review screen
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result = mSearchResultsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), Review.class)
                        .putExtra(Intent.EXTRA_TEXT, result);
                startActivity(intent);
            }
        });

        return view;
    }
}
