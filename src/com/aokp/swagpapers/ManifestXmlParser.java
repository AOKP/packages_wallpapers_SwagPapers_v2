
package com.aokp.swagpapers;

import android.content.Context;
import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ManifestXmlParser extends DefaultHandler {

    private static final String TAG = "ManifestXMLParser";

    ArrayList<WallpaperCategory> wallpaperCategories = new ArrayList<WallpaperCategory>();
    WallpaperCategory currentCategory;
    String value = null;
    Context mContext;

    public ArrayList<WallpaperCategory> parse(File xmlFile, Context c) throws IOException {
        mContext = c;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlFile, this);
            return wallpaperCategories;
        } catch (ParserConfigurationException ex) {
            Log.e(TAG, "", ex);
        } catch (SAXException ex) {
            Log.e(TAG, "", ex);
        }
        return null;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            org.xml.sax.Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("category")) {
            // create a new instance of employee
            currentCategory = new WallpaperCategory(attributes.getValue("id"),
                    attributes.getValue("name"));
        } else if (qName.equalsIgnoreCase("wallpaper")) {

            Wallpaper wp = new Wallpaper();

            String url = attributes.getValue("url");
            String xmlThumbUrl = attributes.getValue("thumbUrl");
            String author = attributes.getValue("author");
            String date = attributes.getValue("date");
            String name = attributes.getValue("name");

            wp.setName(name);
            wp.setAuthor(author != null ? author : "");
            wp.setDate(date != null ? date : "");
            wp.setThumbUrl(xmlThumbUrl != null ? xmlThumbUrl : generateThumbUrl(url));
            wp.setUrl(url);

            currentCategory.addWallpaper(wp);

        }
        super.startElement(uri, localName, qName, attributes);
    }

    private String generateThumbUrl(String url) {
        String prefix = url.substring(0, url.lastIndexOf("."));
        String extention = url.substring(url.lastIndexOf("."));

        String generatedUrl = prefix + "_small" + extention;
        Log.i(TAG, "thumb url generated: " + generatedUrl);
        return generatedUrl;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        value = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("category")) {
            wallpaperCategories.add(currentCategory);
        } else if (qName.equalsIgnoreCase("wallpaper")) {

        }

    }
}
