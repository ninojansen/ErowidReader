package com.dev.nino.erowidreader.scrapers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.dev.nino.erowidreader.data.Substance;
import com.dev.nino.erowidreader.database.DatabaseAccess;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;

/**
 * Scrapes the erowid substance list using JSoup
 */
public class SubstanceListScraper  extends AsyncTask<Void, Void, ArrayList<Substance>> {

    private Context context;


    public SubstanceListScraper(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Scrapes substances from erowid page
     */
    @Override
    protected ArrayList<Substance> doInBackground(Void... voids) {
        ArrayList<Substance> res = null;

        try {
            String url = "https://erowid.org/experiences/exp_list.shtml";
            Document doc = Jsoup.connect(url).get();
            res = new ArrayList<>();
            Elements subst = doc.select("td[colspan=2]");

            for (Element element : subst) {
                // Get the name
                Elements name = element.select("a[name]");
                String substName = name.attr("name");


                // Get the base subtext
                List<TextNode> list = element.select("b").get(0).textNodes();
                TextNode longestString = list.get(0);
                for (TextNode s : list) {
                    if (s.toString().length() > longestString.toString().length()) {
                        longestString = s;
                    }
                }

                // Builds the extra subtext if necessary
                StringBuilder subText = new StringBuilder(cleanSubText(element.select("b").get(0).textNodes().get(list.indexOf(longestString)).toString()));
                if (subText.toString().contains("see also")) {
                    List<String> seeAlsoElements = element.select(":not(a[href^=subs])").eachAttr("href");
                    subText.append(":");
                    for (int j = 0; j < seeAlsoElements.size(); j++) {
                        String seeAlso = seeAlsoElements.get(j);
                        subText.append(" ").append(seeAlso.replaceFirst("#", ""));
                        if (j != seeAlsoElements.size() - 1) {
                            subText.append(",");
                        }
                    }
                }
                // Add the substance to the list
                res.add(new Substance(substName, subText.toString(), false, 0, 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Refresh the substances in the db with the found ones
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();
        db.refreshSubstances(res);
        db.close();
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Substance> substances) {
        super.onPostExecute(substances);
        Log.v("SubstanceScraper", "Succesfully scraped substances and refreshed DB On: " + Calendar.getInstance().getTime());
    }

    private static String cleanSubText(String s) {
        // Hardcoded cause erowid fucked up the text with 2 commas in a row
        if (Objects.equals(s, "- (Bad Pills, Rolls, E, X, , see also MDMA)")) {
            return "Bad Pills, Rolls, E, X";
        }

        // Get rid of trailing - and trim whitespace
        s = s.replaceFirst("- ", "");
        s = s.trim();

        // Return if nothing needs to be done
        if (s.length() == 0) {
            return s;
        }

        // Next 2 lines replace the ( ) the text is wrapped in
        s = s.replaceFirst("\\(", "");

        if (s.charAt(s.length() - 1) == ')') {
            s = s.substring(0, s.length() - 1);
        }

        // Odd case where this can appear in the string
        if (s.contains("&#x200b;")) {
            s = s.replaceAll("&#x200b;", "");
        }

        return s;
    }

}
