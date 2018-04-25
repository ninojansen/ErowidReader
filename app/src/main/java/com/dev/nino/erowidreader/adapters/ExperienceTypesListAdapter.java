package com.dev.nino.erowidreader.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.activities.ReaderActivity;
import com.dev.nino.erowidreader.data.ExperienceType;
import com.dev.nino.erowidreader.data.Report;
import com.dev.nino.erowidreader.fragments.ExperienceTypesListFragment;
import com.dev.nino.erowidreader.fragments.ReportTileRecyclerViewFragment;

import java.util.ArrayList;

/**
 *
 */
public class ExperienceTypesListAdapter extends RecyclerView.Adapter<ExperienceTypesListAdapter.TileViewHolder> {

    private static final String URL_KEY = "URL_KEY";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String TAG_EXPERIENCES_FRAGMENT = "EXPERIENCES_LIST_FRAGMENT";

    private ExperienceTypesListFragment fragment;
    private ArrayList<ExperienceType> experienceTypes;

    public ExperienceTypesListAdapter(ArrayList<ExperienceType> substances, ExperienceTypesListFragment fragment) {
        this.experienceTypes = new ArrayList<>(substances);
        this.fragment = fragment;
    }

    @Override
    public TileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TileViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.experience_type_item, parent, false), fragment);
    }

    @Override
    public void onBindViewHolder(TileViewHolder holder, int position) {
        ExperienceType experienceType = experienceTypes.get(position);
        holder.currentExperienceType = experienceType;
        holder.name.setText(experienceType.getName());
        holder.reportCount.setText(Integer.toString(experienceType.getReportCount()));
    }

    @Override
    public int getItemCount() {
        return experienceTypes.size();
    }

    public static class TileViewHolder extends RecyclerView.ViewHolder{
        public TextView name, reportCount;
        public ExperienceType currentExperienceType;


        public TileViewHolder(View view, final ExperienceTypesListFragment fragment) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    FragmentManager fm = fragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();

                    // Start a new fragment and commit it
                    ReportTileRecyclerViewFragment f = (ReportTileRecyclerViewFragment) fm.findFragmentByTag(TAG_EXPERIENCES_FRAGMENT);
                    // Fragment doesn't exist
                    if (f == null) {
                        f = new ReportTileRecyclerViewFragment();
                        Bundle args = new Bundle();
                        args.putString(URL_KEY, currentExperienceType.getUrl());
                        args.putString(TYPE_NAME, currentExperienceType.getName());
                        f.setArguments(args);
                    }
                    transaction.replace(R.id.frame, f, TAG_EXPERIENCES_FRAGMENT);
                    transaction.addToBackStack(TAG_EXPERIENCES_FRAGMENT);
                    transaction.commit();
                }
            });
            name = (TextView) view.findViewById(R.id.experience_type_name);
            reportCount = (TextView) view.findViewById(R.id.experience_type_report_count);
        }
    }
}
