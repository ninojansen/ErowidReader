package com.dev.nino.erowidreader.scrapers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.data.Report;
import com.dev.nino.erowidreader.activities.ReaderActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Asynctask to scrape a report object from a url
 */
public class ReportScraper extends AsyncTask<Void, Void, Report> {

    // Reference for activity to update it onPostExecute
    private ReaderActivity reader;
    private String url;

    public ReportScraper(ReaderActivity reader, String url) {
        this.reader = reader;
        this.url = url;
    }

    /**
     * Start a loading bar
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ContentLoadingProgressBar bar = (ContentLoadingProgressBar)reader.findViewById(R.id.contentLoadingBar);
        bar.show();
    }

    @Override
    protected Report doInBackground(Void... voids) {
        return scrapeExperienceReport(url);
    }

    @Override
    protected void onPostExecute(Report report) {
        super.onPostExecute(report);
        ContentLoadingProgressBar bar =  (ContentLoadingProgressBar)reader.findViewById(R.id.contentLoadingBar);
        bar.hide();
        if (report == null) {
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ReportScraper(reader, url).execute();
                }
            };
            Snackbar.make(reader.findViewById(android.R.id.content), "Can't connect to Erowid", Snackbar.LENGTH_LONG)
                    .setAction("Retry", mOnClickListener)
                    .setActionTextColor(Color.YELLOW)
                    .show();
        }
        else {
            // Set the report and update the view
            reader.setReport(report);
            reader.updateView(report);
        }
    }

    /**
     * Cleans chars that are buggy in the given string
     */
    private String cleanString(String s) {
        s = s.replaceAll("\u0092", "/");
        s = s.replaceAll("\u0096", "-");
        return s;
    }

    /**
     * Generates a report object from the given url
     * @param url used as page to scrape
     * @return report
     */
    private Report scrapeExperienceReport(String url) {
        Report report = null;
        try {

            Document doc = Jsoup.connect(url).get();

            // Get elements from the start of scraped html
            String title = cleanString(doc.getElementsByClass("title").text());
            String author = doc.getElementsByClass("author").text().substring(3);
            String substance = doc.getElementsByClass("substance").text();
            String weight = doc.getElementsByClass("bodyweight-amount").text();


            // Convert the html table to 2d string array
            Elements table_time = doc.select("td[width=90][align=right]");
            Elements table_amount = doc.getElementsByClass("dosechart-amount");
            Elements table_method = doc.getElementsByClass("dosechart-method");
            Elements table_substance = doc.getElementsByClass("dosechart-substance");
            Elements table_form = doc.getElementsByClass("dosechart-form");

            int cols = 0;
            int rows = 0;
            if (table_time.size() > 0) {
                cols++;
                rows = table_time.size();
            }
            if (table_amount.size() > 0) {
                cols++;
                rows = table_amount.size();
            }
            if (table_method.size() > 0) {
                cols++;
                rows = table_method.size();
            }
            if (table_substance.size() > 0) {
                cols++;
                rows = table_substance.size();
            }
            if (table_form.size() > 0) {
                cols++;
                rows = table_form.size();
            }
            rows++;

            String[][] table = new String[rows][cols];
            for (int i = 0; i < rows; i++) {
                int j = 0;
                if (table_time.size() > 0) {
                    if (i == 0) {
                        table[i][j] = "Dose T:";
                    }
                    else {
                        if (i == 1 && table_time.get(i-1).text().contains("DOSE:")) {
                            if (table_time.get(i-1).text().length() >= 6) {
                                table[i][j] = table_time.get(i - 1).text().substring(6);
                            }
                        }
                        else {
                            table[i][j] = table_time.get(i-1).text();
                        }

                    }
                    j++;
                }
                if (table_amount.size() > 0) {
                    if (i == 0) {
                        table[i][j] = "Amount";
                    }
                    else {
                        table[i][j] = table_amount.get(i-1).text();
                    }
                    j++;
                }
                if (table_method.size() > 0) {
                    if (i == 0) {
                        table[i][j] = "Method";
                    }
                    else {
                        table[i][j] = table_method.get(i-1).text();
                    }
                    j++;
                }
                if (table_substance.size() > 0) {
                    if (i == 0) {
                        table[i][j] = "Substance";
                    }
                    else {
                        table[i][j] = table_substance.get(i-1).text();
                    }
                    j++;
                }
                if (table_form.size() > 0) {
                    if (i == 0) {
                        table[i][j] = "Form";
                    }
                    else {
                        table[i][j] = table_form.get(i-1).text();
                    }
                }
            }
            // Grab the actual report text
            String body = doc.html();
            body = doc.html().substring(body.indexOf("<!-- Start Body -->"), body.indexOf("<!-- End Body -->"));

            Document bodyDoc = Jsoup.parse(body);

            //Improves display of erowid notes
            Elements erowidNotes = bodyDoc.getElementsByClass("erowid-caution");
            erowidNotes.tagName("b");
            erowidNotes.prepend("<br>");
            bodyDoc.select("[href]").remove();
            bodyDoc.getElementsByClass("pullquote-text").remove();


            body = bodyDoc.html();
            // Fixes display error with '
            body = body.replace("\u0092", "'");

            // Grab the final elements from the footerData

            String footerData = doc.getElementsByClass("footdata").eachText().get(0);
            int start = "Exp Year: ".length();
            int end = footerData.indexOf("ExpID: ");

            String expYear = footerData.substring(start, end-1);

            start = end + "ExpID: ".length();
            end = footerData.indexOf("Gender: ");

            String expID = footerData.substring(start, end-1);

            start = end + "Gender: ".length();
            end = footerData.indexOf("Age at time of experience: ");

            String gender = footerData.substring(start, end-1);

            start = end + "Age at time of experience: ".length();
            end = footerData.indexOf("Published: ");

            String age = footerData.substring(start, end-1);

            start = end + "Published: ".length();
            end = footerData.indexOf("Views: ");

            String pubData = footerData.substring(start, end-1);

            start = end + "Views: ".length();
            end = footerData.indexOf("[");

            String views = footerData.substring(start, end-1);

            report = new Report(title, url, author, body, table, substance, age, pubData, views, expID, weight, gender, expYear, 0 , false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return report;
    }
}
