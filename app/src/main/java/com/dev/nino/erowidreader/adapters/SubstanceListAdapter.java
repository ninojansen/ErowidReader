package com.dev.nino.erowidreader.adapters;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.data.Substance;
import com.dev.nino.erowidreader.fragments.ExperienceTypesListFragment;
import com.dev.nino.erowidreader.fragments.SubstanceListFragment;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 */
public class SubstanceListAdapter extends RecyclerView.Adapter<SubstanceListAdapter.TileViewHolder> implements Filterable {

    private SubstanceListFragment fragment;
    private ArrayList<Substance> substances;
    private static final String SUBSTANCE_NAME_KEY = "SUBSTANCE_NAME";
    private static final String TAG_EXPERIENCE_TYPES_FRAGMENT = "EXPERIENCE_TYPES_LIST_FRAGMENT";
    private int lastPosition = -1;
    // Amount of tiles before animations stops
    private int animationThreshold = 2;
    private boolean stopAnimation;
    private boolean sortedAlphabetically = false;

    public SubstanceListAdapter(ArrayList<Substance> substances, SubstanceListFragment fragment) {
        this.substances = new ArrayList<>(substances);
        this.fragment = fragment;
    }

    @Override
    public TileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TileViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.substance_item, viewGroup, false), fragment);
    }

    // TODO Fix animations
    @Override
    public void onBindViewHolder(TileViewHolder holder, int position) {
        if (!stopAnimation || lastPosition < holder.getAdapterPosition()) {
            if (lastPosition < (substances.size() - animationThreshold)) {
                Animation animation = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.up_from_bottom);
                holder.itemView.startAnimation(animation);
                lastPosition = position;
            } else {
                stopAnimation = true;
            }
        }
        Substance substance = substances.get(position);
        holder.name.setText(substance.getName());
        if (substances.get(position).getSubText().trim().length() > 0) {
            holder.subText.setVisibility(View.VISIBLE);
            holder.subText.setText(substance.getSubText());
        }
        else {
            holder.subText.setVisibility(View.GONE);
        }
        holder.reportCount.setText(Integer.toString(substance.getReportCount()));
        holder.currentSubstance = substance;
    }

    @Override
    public void onViewDetachedFromWindow(TileViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return substances.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = substances;
                    results.count = substances.size();
                }
                else {
                    ArrayList<Substance> filteredSubstances = new ArrayList<>();

                    for (Substance substance: substances) {
                        if (clean(substance.getName()).contains(clean(constraint.toString()))
                                || clean(substance.getSubText()).contains(clean(constraint.toString()))) {
                            filteredSubstances.add(substance);
                        }
                    }
                    results.values = filteredSubstances;
                    results.count = filteredSubstances.size();
                }
                return results;
            }

            private String clean(String s) {
                s = s.toLowerCase();
                s = s.replaceAll("[-+.^:,]", "");
                return s;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                substances.clear();
                if (results.values != null) {

                substances.addAll((ArrayList<Substance>)results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    public void sortAlphabetically() {
        if (sortedAlphabetically) {
            if (substances.size() > 0) {
                Collections.sort(substances, new Comparator<Substance>() {
                    @Override
                    public int compare(final Substance object1, final Substance object2) {
                        return  -object1.getName().compareToIgnoreCase(object2.getName());
                    }
                });
            }
            sortedAlphabetically = false;
        }
        else {
            if (substances.size() > 0) {
                Collections.sort(substances, new Comparator<Substance>() {
                    @Override
                    public int compare(final Substance object1, final Substance object2) {
                        return object1.getName().compareTo(object2.getName());
                    }
                });
            }
            sortedAlphabetically = true;
        }
        notifyDataSetChanged();
    }

    public void setSubstances(ArrayList<Substance> substances) {
        this.substances.clear();
        this.substances.addAll(substances);
        notifyDataSetChanged();
    }

    public void setWithoutNotifySubstanceS(ArrayList<Substance> substances) {
        this.substances.clear();
        this.substances.addAll(substances);
    }

    public static class TileViewHolder extends RecyclerView.ViewHolder{
        public TextView name, subText, reportCount;
        public Substance currentSubstance;

        public TileViewHolder(View view, final SubstanceListFragment fragment) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    FragmentManager fm = fragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    // Start a new fragment and commit it
                    ExperienceTypesListFragment f = (ExperienceTypesListFragment) fm.findFragmentByTag(TAG_EXPERIENCE_TYPES_FRAGMENT);
                    // Fragment doesn't exist yet
                    if (f == null) {
                        f = new ExperienceTypesListFragment();
                        Bundle args = new Bundle();
                        args.putString(SUBSTANCE_NAME_KEY, name.getText().toString());
                        f.setArguments(args);
                    }
                    transaction.replace(R.id.frame, f);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            name = (TextView) view.findViewById(R.id.substance_item_name);
            subText = (TextView) view.findViewById(R.id.substance_item_subtext);
            reportCount = (TextView) view.findViewById(R.id.report_count_text);
        }
    }
}
