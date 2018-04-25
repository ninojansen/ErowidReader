package com.dev.nino.erowidreader.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.*;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.dev.nino.erowidreader.*;
import com.dev.nino.erowidreader.data.Report;
import com.dev.nino.erowidreader.scrapers.ReportScraper;

import java.util.Objects;


/**
 * Activity that is used to read the experience
 */
public class ReaderActivity extends AppCompatActivity {

    private Report report;
    /**
     * sets up a special toolbar with a close button and scrapes the html for the experience text and displays
     * this using a web view (in the ReportScraper)
     * @param savedInstanceState .
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        setupToolBar();
        // Setup a ReportScraper to get the report to display
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("REPORT") == null) {
                Intent intent = getIntent();
                new ReportScraper(this, intent.getExtras().getString("url")).execute();
            } else {
                report = savedInstanceState.getParcelable("REPORT");
                updateView(report);
            }
        }
        else {
            Intent intent = getIntent();
            new ReportScraper(this, intent.getExtras().getString("url")).execute();
        }
    }

    /**
     * Special toolbar has close button and settings button. Title starts emtpy gets changed in ReportScraper
     * to experience title
     */
    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.reader_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_toolbar_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open_in_browser) {
            if (report != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(report.getUrl())));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("REPORT", report);
    }

    public void updateView(Report report) {
        if (report != null) {
            // Do this first as it can be a big view
            TextView body = (TextView) findViewById(R.id.reader_body);

            body.setText(fromHtml(report.getReport()));

            updateTable();

            TextView title = (TextView) findViewById(R.id.reader_title);
            TextView substance = (TextView) findViewById(R.id.reader_substance);
            TextView author = (TextView) findViewById(R.id.reader_author);
            TextView ageGender = (TextView) findViewById(R.id.reader_age_gender);
            TextView pubDate = (TextView) findViewById(R.id.reader_pubDate);
            TextView expYear = (TextView) findViewById(R.id.reader_expYear);
            TextView views = (TextView) findViewById(R.id.reader_views);
            TextView weight = (TextView) findViewById(R.id.reader_weight);

            if (Objects.equals(report.getAge(), "Not Given")) {
                ageGender.setText(report.getGender());

            }
            else {
                ageGender.setText(report.getGender() +", "+ report.getAge() + " yrs old at time of exp.");
            }
            pubDate.setText("published on " + report.getPubDate());
            expYear.setText("Experience Year: " + report.getExpYear());
            views.setText("Views: " + report.getViews());
            title.setText(report.getTitle());
            substance.setText(report.getSubstances());
            author.setText("by " + report.getAuthor());
            weight.setText("Weight: " + report.getWeight());
        }
    }

    /**
     *  Method used to set the table view
     */
    private void updateTable() {
        int rows = report.getTable().length;
        int cols =  report.getTable()[0].length;
        int textSize = 12;
        if (cols < 5) {
            textSize = 13;
        }
        TableLayout tableLayout = (TableLayout) findViewById(R.id.dose_chart_table);
        tableLayout.setBackgroundColor(Color.WHITE);

        // Have equal width of cells
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int cellWidth = size.x / cols;

        tableLayout.setWeightSum((float)report.getTable().length);

        //Setup initial border
        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 7));
        v.setBackgroundColor(Color.GRAY);

        tableLayout.addView(v);

        for (int i = 0; i < rows ; i++) {
            // Create a new table row
            TableRow row= new TableRow(this);
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            row.setWeightSum(report.getTable()[0].length);

            for (int j = 0; j < cols; j++) {
                // Create the text views to fill the rows with
                TextView tv = new TextView(this);
                tv.setText(report.getTable()[i][j]);
                tv.setTextColor(Color.BLACK);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setBackgroundColor(Color.WHITE);
                tv.setTextSize(textSize);
                tv.setWidth(cellWidth);
                tv.setLongClickable(true);
                tv.setTextIsSelectable(true);
                if (i == 0) {
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextSize(textSize+2);
                }
                // View used to have columnwise borders
                v = new View(this);
                v.setBackgroundColor(Color.GRAY);
                v.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));

                row.addView(v);
                row.addView(tv);
            }

            TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1f);
            params.setMargins(2, 2, 2, 2);
            row.setLayoutParams(params);

            // View used to have row wise borders
            v = new View(this);
            v.setBackgroundColor(Color.GRAY);
            if (i == 0) {
                v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 3));
            }
            else {
                v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            }

            tableLayout.addView(row);
            tableLayout.addView(v);
        }
    }


    /**
     * Used to work with decrapted
     * @param html
     * @return
     */
    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


    public void setReport(Report report) {
        this.report = report;
    }
}
