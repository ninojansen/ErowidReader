package com.dev.nino.erowidreader.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.*;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.adapters.SubstanceListAdapter;
import com.dev.nino.erowidreader.data.Substance;
import com.dev.nino.erowidreader.database.DatabaseAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Fragment for the substance list; has a special toolbar with a search button
 */
public class SubstanceListFragment extends Fragment {

    private static final String SUBSTANCES_KEY = "SUBSTANCES_KEY";

    private ArrayList<Substance> substances;

    private RecyclerView mRecyclerView;
    private SubstanceListAdapter mAdapter;

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

        // If it's a saved instance use the saved reports for a new adapter
        if (savedInstanceState != null) {
            substances = savedInstanceState.getParcelableArrayList(SUBSTANCES_KEY);
        }
        return view;
    }
    /**
     * Only start the generation of the list in onActivityCreated to assure fragment has an activity for the scraper to use
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (substances == null) {
            DatabaseAccess db = DatabaseAccess.getInstance(getContext());
            db.open();
            substances = db.getSubstances();
            db.close();
            setAdapter(substances);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Substances");
    }

    /**
     * Cancel the scraper so the app doesn't crash trying to update the fragment in the background
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (substances != null) {
            mAdapter = new SubstanceListAdapter(this.substances, this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
    private void setAdapter(ArrayList<Substance> substances) {
        this.substances = substances;
        mAdapter = new SubstanceListAdapter(this.substances, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SUBSTANCES_KEY, substances);
    }

    /**
     * Sets up the menu and the search functionality
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        menu.clear(); //Empty the old menu
        inflater.inflate(R.menu.substance_list_toolbar_buttons, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.substance_list_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();


        //Filters based on the query, only when adapter is created
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(s);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (s.trim().length() > 0) {
                    if (mAdapter != null) {
                        mAdapter.setWithoutNotifySubstanceS(substances);
                        mAdapter.getFilter().filter(s);
                    }
                }
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View arg0) {
                System.out.println(substances.size());
                mAdapter.setSubstances(substances);
                // search was detached/closed
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.substance_list_sort:
                if (mAdapter != null) {
                    mAdapter.sortAlphabetically();
                }
                return true;
            case R.id.substance_list_report_count_order:
                if (mAdapter != null) {
                    mAdapter.sortReportCount();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
