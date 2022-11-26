package org.jason.bmeg3130;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WebScrape extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... voids) {
        try {
            Document doc = Jsoup.connect("http://mindjority.com/index.php/74-2/").get();
            return doc.select("h3").first().text();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}