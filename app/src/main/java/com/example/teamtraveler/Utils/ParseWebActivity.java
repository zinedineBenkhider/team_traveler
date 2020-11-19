package com.example.teamtraveler.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * class to parse activity from website
 */
public class ParseWebActivity {
    private Document document;

    /**
     * method to get the web page to parse
     * @param urlPage the url of the web site
     * @throws IOException exception throw if the url is wrong
     */
    public ParseWebActivity(String urlPage) throws IOException {
        document = Jsoup.connect(urlPage).get();
    }

    /**
     * get the title of the activity propose by the web site
     * @return the title
     */
    public String getTitle(){
        String s =  document.getElementsByTag("title").text();
        if(s.length()>20){
            return s.substring(0,19);
        }
        else {
            return s;
        }
    }

}
