package com.dev.nino.erowidreader.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.adapters.ExperienceTypesListAdapter;
import com.dev.nino.erowidreader.adapters.SubstanceListAdapter;
import com.dev.nino.erowidreader.data.ExperienceType;
import com.dev.nino.erowidreader.data.Substance;
import com.dev.nino.erowidreader.scrapers.ExperienceTypesListScraper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 *
 */
public class ExperienceTypesListFragment extends Fragment {


    private ExperienceTypesListScraper scraper;
    private boolean isTaskCancelled;
    private static final String SUBSTANCE_NAME_KEY = "SUBSTANCE_NAME";
    private static final String EXPERIENCE_TYPES_KEY = "EXPERIENCE_TYPES_KEY";


    private ArrayList<ExperienceType> experienceTypes;


    private RecyclerView mRecyclerView;
    private ExperienceTypesListAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view =  inflater.inflate(R.layout.recycler_view_frag, container, false);

        mRecyclerView =  view.findViewById(R.id.base_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Add a divider to the recyclerview
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if (savedInstanceState != null) {
            experienceTypes = savedInstanceState.getParcelableArrayList(EXPERIENCE_TYPES_KEY);
        }
        return view;
    }

    /**
     * Only start the generation of the list in onActivityCreated to assure fragment has an activity for the scraper to use
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Fixes bug where bar is displayed when orientation is switched
        ContentLoadingProgressBar bar =  getActivity().findViewById(R.id.contentLoadingBar);
        bar.hide();
        // Only create a new scraper if none exists
        if (scraper == null) {
            scraper = new ExperienceTypesListScraper(this, getArguments().getString(SUBSTANCE_NAME_KEY));
        }
        // If the scraper was cancelled rerun it when the app is resumed or simply run it when it's pending
        if (scraper.getStatus() == AsyncTask.Status.PENDING || isTaskCancelled) {
            scraper.execute();
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Experiences");
    }

    /**
     * Cancel the scraper so the app doesn't crash trying to update the fragment in the background
     */
    @Override
    public void onPause() {
        super.onPause();
        // Cancel the scraper if it's running when the fragment is paused
        if(scraper != null && scraper.getStatus() == AsyncTask.Status.RUNNING) {
            scraper.cancel(true);
            isTaskCancelled = true;
        }
    }


/*

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView name = (TextView) v;


    }
*/

    @Override
    public void onStart() {
        super.onStart();
        if (experienceTypes != null) {
            mAdapter = new ExperienceTypesListAdapter(this.experienceTypes, this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXPERIENCE_TYPES_KEY, experienceTypes);
    }

    public void setAdapter(ArrayList<ExperienceType> experienceTypes) {
        this.experienceTypes = experienceTypes;
        mAdapter = new ExperienceTypesListAdapter(this.experienceTypes, this);
        mRecyclerView.setAdapter(mAdapter);
    }

}
