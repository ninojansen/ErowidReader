package com.dev.nino.erowidreader.scrapers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Pair;
import android.view.View;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.data.Report;
import com.dev.nino.erowidreader.fragments.ReportTileRecyclerViewFragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 */
public class ReportListScraper extends AsyncTask<Void, Void, Pair<ArrayList<Report>, ArrayList<String>>  > {

    private ReportTileRecyclerViewFragment f;
    private String url;

    public ReportListScraper(ReportTileRecyclerViewFragment f, String url){
        this.f = f;
        this.url = url;
    }

    /**
     * Start a loading bar
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ContentLoadingProgressBar bar = f.getActivity().findViewById(R.id.contentLoadingBar);
        bar.show();
    }


    /**
     * Scrapes the first page of the given experiences url
     */
    @Override
    protected Pair doInBackground(Void... voids) {

        Pair<ArrayList<Report>, ArrayList<String>> res = null;

        try {
            String baseUrl = "https://erowid.org/experiences/exp.php?";
            Document doc = Jsoup.connect(url).get();

            ArrayList<Report> reports = new ArrayList<>();
            ArrayList<String> pageLinks = new ArrayList<>();

            Elements experiences = doc.select("tr[class=\"\"]");
            Elements links = experiences.select("a[href]");

            Elements pages = doc.select("td[align=center]").select("a[href^=/experiences/exp.cgi?]");
            for (Element page: pages) {
                pageLinks.add("https://erowid.org" + page.attr("href"));
            }

            for (int i = 0; i < experiences.size(); i++) {
                Report report = new Report();
                Element exp = experiences.get(i);
                Elements info = exp.select("td");
                Elements img = exp.select("img[alt]");
                report.setStars(0);
                report.setNew(false);
                // See if it's new and get the amount of stars
                if (img.size() > 0) {
                    if (Objects.equals(img.get(0).attr("alt"), "New")) {
                        report.setNew(true);
                        if (img.size() > 1) {
                            switch (img.get(1).attr("alt")) {
                                case "Very Highly Recommended":
                                    report.setStars(3);
                                    break;
                                case "Highly Recommended":
                                    report.setStars(2);
                                    break;
                                case "Recommended":
                                    report.setStars(1);
                                    break;
                                default:
                                    report.setStars(0);
                            }
                        }
                    } else {
                        report.setNew(false);
                        switch (img.get(0).attr("alt")) {
                            case "Very Highly Recommended":
                                report.setStars(3)
                                ;
                                break;
                            case "Highly Recommended":
                                report.setStars(2);
                                break;
                            case "Recommended":
                                report.setStars(1);
                                break;
                            default:
                                report.setStars(0);
                        }
                    }
                }
                // Set info based on element
                report.setTitle(cleanString(info.get(1).text()));
                report.setAuthor(info.get(2).text());
                report.setSubstances(info.get(3).text());
                report.setPubDate(info.get(4).text());
                report.setId(getIDfromString(links.get(i).attr("href")));
                report.setUrl(baseUrl + report.getId());
                reports.add(report);
            }
            res = new Pair<>(reports, pageLinks);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Fills in list view and sets the links array corresponding to elements in the view
     */
    @Override
    protected void onPostExecute(Pair result) {
        super.onPostExecute(result);
        ContentLoadingProgressBar bar =  f.getActivity().findViewById(R.id.contentLoadingBar);
        bar.hide();
        if (result == null) {
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ReportListScraper(f, url).execute();
                }
            };
            // Show snack bar when app cant connect to erowid and give retry option
            Snackbar.make(f.getActivity().findViewById(android.R.id.content), "Can't connect to Erowid", Snackbar.LENGTH_LONG)
                    .setAction("Retry", mOnClickListener)
                    .setActionTextColor(Color.YELLOW)
                    .show();
            //TODO maybe return this to previous fragment?
        } else {
            // Sets the fields and updates the adapter
            f.addReports((ArrayList<Report>)result.first);
            f.setPageLinks((ArrayList<String>)result.second);
            f.updateAdapter();
        }
    }

    private String cleanString(String s) {
        s = s.replaceAll("\u0092", "/");
        s = s.replaceAll("\u0096", "-");
        return s;
    }

    private String getIDfromString(String s) {
        return s.substring(s.indexOf("ID="));
    }
}