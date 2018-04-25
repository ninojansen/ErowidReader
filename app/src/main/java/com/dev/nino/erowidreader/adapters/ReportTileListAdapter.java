package com.dev.nino.erowidreader.adapters;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.activities.ReaderActivity;
import com.dev.nino.erowidreader.data.Report;

import java.util.ArrayList;

/**
 *
 */
public class ReportTileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

// TODO make this simpler? compare to substancelistscraper

    private ArrayList<Report> reports;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;


    private int lastPosition = -1;

    private OnLoadMoreListener onLoadMoreListener;

    public ReportTileListAdapter(ArrayList<Report> reports, RecyclerView recyclerView){
        this.reports = new ArrayList<>(reports);

        //Scrollistener to load more when reaching the bottom
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }

    public void addReports(ArrayList<Report> reports) {
            this.reports.addAll(reports);
        this.notifyDataSetChanged();
    }

    public void setReports(ArrayList<Report> reports) {
        this.reports.clear();
        this.reports.addAll(reports);
        this.notifyDataSetChanged();
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        vh = new TileViewHolder(itemView);
        return vh;
    }

    /**
     * Binds the view setting up all the variables
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        lastPosition = position;
        if (viewHolder instanceof TileViewHolder) {
            TileViewHolder holder = (TileViewHolder) viewHolder;
            Report report = reports.get(position);
            holder.title.setText(report.getTitle());
            holder.date.setText(report.getPubDate());
            holder.author.setText("by " + report.getAuthor());
            holder.substances.setText(report.getSubstances());
            holder.currentReport = reports.get(position);
            if (report.isNew()) {
                holder.newText.setVisibility(View.VISIBLE);
            } else {
                holder.newText.setVisibility(View.INVISIBLE);
            }
            switch (report.getStars()) {
                case 3:
                    holder.star1.setVisibility(View.VISIBLE);
                    holder.star2.setVisibility(View.VISIBLE);
                    holder.star3.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    holder.star1.setVisibility(View.VISIBLE);
                    holder.star2.setVisibility(View.VISIBLE);
                    holder.star3.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    holder.star1.setVisibility(View.VISIBLE);
                    holder.star2.setVisibility(View.INVISIBLE);
                    holder.star3.setVisibility(View.INVISIBLE);
                    break;
                default:
                    holder.star1.setVisibility(View.INVISIBLE);
                    holder.star2.setVisibility(View.INVISIBLE);
                    holder.star3.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    @Override
    public int getItemCount() {
        return (null != reports ? reports.size() : 0);
    }
    /**
     * Viewholder that holds the tile view for the reports
     */
    public static class TileViewHolder extends RecyclerView.ViewHolder{
        public TextView title, substances, date, newText, author;
        public ImageView star1, star2, star3;
        public View v;
        public Report currentReport;

        public TileViewHolder(View view) {
            super(view);
            v = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent reader = new Intent(v.getContext(), ReaderActivity.class);
                    reader.putExtra("url", currentReport.getUrl());
                    v.getContext().startActivity(reader);
                }
            });
            title = (TextView) view.findViewById(R.id.report_card_title);
            substances = (TextView) view.findViewById(R.id.report_card_substances);
            date = (TextView) view.findViewById(R.id.report_card_date);
            newText = (TextView) view.findViewById(R.id.report_card_new);
            author = (TextView) view.findViewById(R.id.report_card_author);
            star1 = (ImageView) view.findViewById(R.id.report_card_star1);
            star2 = (ImageView) view.findViewById(R.id.report_card_star2);
            star3 = (ImageView) view.findViewById(R.id.report_card_star3);
        }
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void removeOnLoadMoreListener() {
        this.onLoadMoreListener = null;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}



