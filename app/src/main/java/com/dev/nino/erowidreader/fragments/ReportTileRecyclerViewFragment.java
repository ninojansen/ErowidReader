package com.dev.nino.erowidreader.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.adapters.ReportTileListAdapter;
import com.dev.nino.erowidreader.data.Report;
import com.dev.nino.erowidreader.scrapers.ReportListScraper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * ListFragment that represents a list of experiences urls
 */
public class ReportTileRecyclerViewFragment extends Fragment {



    private ArrayList<Report> reports  = new ArrayList<>();


    // Links to the further pages if any
    private ArrayList<String> pageLinks;

    // The current page that is being looked at
    private int currentPage = 0;

    private ReportListScraper scraper;
    private boolean isTaskCancelled;

    private RecyclerView mRecyclerView;
    private ReportTileListAdapter mAdapter;

    private static final String URL_KEY = "URL_KEY";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String REPORT_KEY = "REPORT_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment for orientation switch
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            reports = savedInstanceState.getParcelableArrayList(REPORT_KEY);
            if (reports != null) {
                mAdapter = new ReportTileListAdapter(reports, mRecyclerView);
                mRecyclerView.setAdapter(mAdapter);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(savedInstanceState.getString(TYPE_NAME));
            }
        }
        return view;
    }
    /**
     * Only start the generation of the list in onActivityCreated to assure fragment has an activity for the scraper to use
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Only do scraper behavior if it's a fresh instance
        if (savedInstanceState == null || mAdapter == null) {
            // Only create a new scraper if none exists
            if (scraper == null) {
                scraper = new ReportListScraper(this, getArguments().getString(URL_KEY));
            }
            // If the scraper was cancelled rerun it when the app is resumed or simply run it when it's pending
            if (scraper.getStatus() == AsyncTask.Status.PENDING || isTaskCancelled) {
                scraper.execute();
            }
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getArguments().getString(TYPE_NAME));
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(REPORT_KEY, reports);
        outState.putString(TYPE_NAME, getArguments().getString(TYPE_NAME));
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Creates an adapter when data has been created and adds a listener to loadmore
     */
    public void updateAdapter() {
        if (mAdapter == null) {
            mAdapter = new ReportTileListAdapter(reports, mRecyclerView);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnLoadMoreListener(new ReportTileListAdapter.OnLoadMoreListener() {
                  @Override
                  public void onLoadMore() {
                      if (scraper.getStatus() == AsyncTask.Status.FINISHED) {
                          if (currentPage < pageLinks.size()) {
                              scraper = new ReportListScraper(ReportTileRecyclerViewFragment.this, pageLinks.get(currentPage));
                              scraper.execute();
                          }
                          else {
                              //Nothing else too load
                              mAdapter.removeOnLoadMoreListener();
                          }
                      }
                  }
              });
        }
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    public void addReports(final ArrayList<Report> reports) {
        this.reports.addAll(reports);
        // update page and update adapter !! has to be done here or it fucks up for some reason
        currentPage++;
        if (mAdapter != null) {
            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    mAdapter.addReports(reports);
                }
            };
            handler.post(r);
        }
    }

    public void setPageLinks(ArrayList<String> pageLinks) {
        this.pageLinks = pageLinks;
    }

}
