package com.example.teamtraveler.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class to parse AirBnb web site
 */
public class ParseWebPageAirBnb{
    private Document document;

    /**
     * parser of a housing of AirBnb
     * @param urlPage the url of the web page
     * @throws IOException exception throw if the url is invalid
     */
    public ParseWebPageAirBnb(String urlPage) throws IOException {
        URL url = new URL(urlPage);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        String line;
        StringBuilder tmp = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        while ((line = in.readLine()) != null) {
            tmp.append(line);
        }
        document = Jsoup.parse(tmp.toString());
    }

    /**
     * map the infos inside the web site
     * @return a map
     */
    public Map<String,String> getInfos(){
        Map<String,String> infos=new HashMap<>();
        infos.put("Voyageurs",document.selectFirst("div:containsOwn(voyageur)").text().split(" ")[0]);
        infos.put("Chambres",document.selectFirst("div:containsOwn(chambre)").text().split(" ")[0]);
        infos.put("Lits",document.selectFirst("div:containsOwn(lit)").text().split(" ")[0]);
        infos.put("SallesDeBain",document.selectFirst("div:containsOwn(de bain)").text().split(" ")[0]);
        return infos;
    }

    /**
     * get title of the housing AirBnb
     * @return the title
     */
    public String getTitle(){
        return document.getElementsByTag("title").text().split(" - ")[0];
    }

    /**
     * get the equipment ofthe housing
     * @return a list of equipment
     */
    public List<String> getEquipements(){
        List<String> list=new ArrayList<>();
        Elements elements= document.select("ul>li>span");
        for (int i=0;i<elements.size();i=i+1){
            if(!elements.get(i).text().contains("Indisponible") && !elements.get(i).text().equals(""))
                list.add(elements.get(i).text());
        }
        return list;
    }

}

