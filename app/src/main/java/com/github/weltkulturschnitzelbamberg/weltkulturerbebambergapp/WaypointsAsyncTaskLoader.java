package com.github.weltkulturschnitzelbamberg.weltkulturerbebambergapp;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;

import com.github.weltkulturschnitzelbamberg.weltkulturerbebambergapp.contentprovider.WeltkulturerbeContentProvider;
import com.github.weltkulturschnitzelbamberg.weltkulturerbebambergapp.database.WaypointsTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This AsyncTaskLoader loads the waypoints into the Weltkulturschnitzel Database
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-06-04
 */
public class WaypointsAsyncTaskLoader extends AsyncTaskLoader{

    public static final int LOADER_ID = 0;
    private static final String PACKAGE = WaypointsAsyncTaskLoader.class.getPackage().getName();
    private static final String INT_NOT_DEFINED = "" + -1;

    public WaypointsAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Object loadInBackground() {
        try {
            writeWaypointsToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeWaypointsToDatabase() throws IOException, ParserConfigurationException, SAXException{
        Document doc = loadFile();

        NodeList nodeList = doc.getElementsByTagName(Waypoints.TAG_WAYPOINT);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                ContentValues values = new ContentValues();

                String waypointID = INT_NOT_DEFINED;
                String name = null;
                String latitude = null;
                String longitude = null;
                String quizID = null;

                if (element.getElementsByTagName(Waypoints.TAG_ID).getLength() > 0) waypointID = element.getElementsByTagName(Waypoints.TAG_ID).item(0).getTextContent();
                if (element.getElementsByTagName(Waypoints.TAG_NAME).getLength() > 0) name = element.getElementsByTagName(Waypoints.TAG_NAME).item(0).getTextContent();
                if (element.getElementsByTagName(Waypoints.TAG_LATITUDE).getLength() > 0) latitude = element.getElementsByTagName(Waypoints.TAG_LATITUDE).item(0).getTextContent();
                if (element.getElementsByTagName(Waypoints.TAG_LONGITUDE).getLength() > 0) longitude = element.getElementsByTagName(Waypoints.TAG_LONGITUDE).item(0).getTextContent();
                if (element.getElementsByTagName(Waypoints.TAG_QUIZ_ID).getLength() > 0) quizID = element.getElementsByTagName(Waypoints.TAG_QUIZ_ID).item(0).getTextContent();

                values.put(WaypointsTable.COLUMN_WAYPOINT_ID, waypointID);
                values.put(WaypointsTable.COLUMN_NAME, name);
                values.put(WaypointsTable.COLUMN_LATITUDE, latitude);
                values.put(WaypointsTable.COLUMN_LONGITUDE, longitude);
                values.put(WaypointsTable.COLUMN_QUIZ_ID, quizID);

                getContext().getContentResolver().insert(WeltkulturerbeContentProvider.URI_TABLE_WAYPOINTS, values);
            }
        }
    }

    private Document loadFile() throws IOException, ParserConfigurationException, SAXException{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        InputSource inputSource = new InputSource(getContext().getAssets().open(Waypoints.FILENAME));
        Document doc = docBuilder.parse(inputSource);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private static class Waypoints {

        private static final String FILENAME = "waypoints.xml";
        private static final String TAG_WAYPOINT = "waypoint";
        private static final String TAG_ID = "id";
        private static final String TAG_NAME = "name";
        private static final String TAG_LATITUDE = "latitude";
        private static final String TAG_LONGITUDE = "longitude";
        private static final String TAG_QUIZ_ID = "quiz-id";
    }
}