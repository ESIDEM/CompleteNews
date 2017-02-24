package ng.com.techdepo.completenews.net;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;

import java.util.List;




/**
 * Created by ESIDEM jnr on 2/15/2017.
 */

public class RSSParser {

   public static String link ;


    public List<RSSItem> parse() {
        List<RSSItem> itemsList = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            // auto-detect the encoding from the stream
            parser.setInput(getInputStream(), null);
            int eventType = parser.getEventType();
            RSSItem rssItem = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                String attributeValue = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        itemsList = new ArrayList<RSSItem>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();

                        if (name.equalsIgnoreCase("item")) {
                            rssItem = new RSSItem();
                        } else if (rssItem != null) {
                            if (name.equalsIgnoreCase("description")) {
                            rssItem.setDescription(parser.nextText());

                            } else if (name.equalsIgnoreCase("pubDate")) {
                                rssItem.setPubDate(parser.nextText());
                            } else if (name.equalsIgnoreCase("link")) {
                                rssItem.setLink(parser.nextText());
                            } else if (name.equalsIgnoreCase("guid")) {
                                rssItem.setLink(parser.nextText());
                            } else if (name.equalsIgnoreCase("title")) {
                                rssItem.setTitle(parser.nextText());
                            } else if (name.equalsIgnoreCase("content:encoded")) {
                                rssItem.setImageUrl2(parser.getAttributeValue(null, "src"));
                            }else if (name.equalsIgnoreCase("enclosure")) {
                                rssItem.setImageUrl(parser.getAttributeValue(null, "url"));
                            }

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && rssItem != null) {
                            assert itemsList != null;
                            itemsList.add(rssItem);
                        } else if (name.equalsIgnoreCase("channel")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("PullFeedParser", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return itemsList;
    }

    private InputStream getInputStream() {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            Log.w("TAG", "Exception while retrieving the input stream", e);
            return null;
        }
    }









}
