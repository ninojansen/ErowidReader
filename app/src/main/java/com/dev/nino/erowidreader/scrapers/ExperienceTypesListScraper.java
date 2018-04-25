package com.dev.nino.erowidreader.scrapers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import com.dev.nino.erowidreader.R;
import com.dev.nino.erowidreader.data.ExperienceType;
import com.dev.nino.erowidreader.database.DatabaseAccess;
import com.dev.nino.erowidreader.fragments.ExperienceTypesListFragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class ExperienceTypesListScraper extends AsyncTask<Void, Void, ArrayList<ExperienceType>> {

    private ExperienceTypesListFragment f;
    private String substanceName;


    public ExperienceTypesListScraper(ExperienceTypesListFragment f, String substanceName){
        this.f = f;
        this.substanceName = substanceName;

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
    protected ArrayList<ExperienceType> doInBackground(Void... voids) {
        //TODO make it a substance object not just a string
        DatabaseAccess db = DatabaseAccess.getInstance(f.getContext());
        db.open();
        ArrayList<ExperienceType> experienceTypes = db.getExperienceTypesForSubstance(substanceName);
        db.close();
        return experienceTypes;
        //TODO make it find the experience tpyes when db return null
        /*
        String url = "https://erowid.org/experiences/subs/exp_" + cleanName(substanceName);
        Pair res = null;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements names = doc.select("a[href^=#]");

            ArrayList<String> types = new ArrayList<>();
            ArrayList<String> links = new ArrayList<>();
            links.add(getAllExperiencesUrl(url));
            types.add("All Experiences");
            for (Element name : names) {
                links.add(url + "_" + cleanUrlType(name.text()));
                types.add(name.text());
            }
            res = new Pair<>(links.toArray(new String[0]), types.toArray(new String[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
        */
    }

    /**
     * Fills in list view and sets the links array corresponding to elements in the view
     */
    @Override
    protected void onPostExecute(ArrayList<ExperienceType> experienceTypes) {
        super.onPostExecute(experienceTypes);
        ContentLoadingProgressBar bar =  f.getActivity().findViewById(R.id.contentLoadingBar);
        bar.hide();
        if (experienceTypes == null) {
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ExperienceTypesListScraper(f, substanceName).execute();
                }
            };
            // Show snack bar when app cant connect to erowid and give retry option
            Snackbar.make(f.getActivity().findViewById(android.R.id.content), "Can't connect to Erowid", Snackbar.LENGTH_LONG)
                    .setAction("Retry", mOnClickListener)
                    .setActionTextColor(Color.YELLOW)
                    .show();
            //TODO maybe return this to previous fragment?
        } else {
            // TODO make custom adapter for experiencetypes
            f.setAdapter(experienceTypes);
        }
    }

    /**
     * Method to clean up a substance name removing special characters so it can be used for a url
     */
    private String cleanName(String name) {
        name = name.trim();
        name = name.replaceAll("[-]", "");
        name = name.replaceFirst("/", "I");
        name = name.replaceAll(" ", "_");
        return name;
    }

    /**
     * Method to clean up an url type so it can be used properly
     */
    private String cleanUrlType(String name) {
        name = name.replace("/", "I");
        if (name.contains("&")) {
            name  = name.substring(0, name.indexOf("&") -1) + "_" + name.substring(name.indexOf("&")+1);
        }
        return name;
    }

    /**
     * Method to get the all experiences url from the button
     */
    private String getAllExperiencesUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements names = doc.select("a[href]:has(img[alt = Show ALL Reports])");
        String res = "https://erowid.org";
        res += names.attr("href");
        return res;
    }
}
