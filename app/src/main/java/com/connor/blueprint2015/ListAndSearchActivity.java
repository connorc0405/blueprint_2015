package com.connor.blueprint2015;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Connor on 2/22/15.
 */
public class ListAndSearchActivity extends Activity{
    File xmlSaveFile;
    XmlPullParser parser;
    ListView resterauntList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this.getApplicationContext();
        xmlSaveFile = new File(context.getFilesDir(), "resterauntMenus.xml");
        resterauntList = (ListView) findViewById(R.id.resterauntList);
        parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new FileInputStream(xmlSaveFile), null);
            parser.nextTag();
        } catch (XmlPullParserException e ) {
            e.printStackTrace();
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
        List<String> resteraunts = new ArrayList<>();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_and_search_activity);
        EditText searchBox = (EditText) findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.searchRadiusPicker);
        numberPicker.setValue(2);
        numberPicker.setMaxValue(25);
        numberPicker.setMinValue(0);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL("https://maps.googleapis.com/maps/api/place/queryautocomplete/json?").openConnection();
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    List<Place> resteraunts = PlacesServices.search("", longitude, latitude,  numberPicker.getValue()*1000 );
                    List<String> resterauntNames = new ArrayList<String>();
                    for(Place place : resteraunts){
                        resterauntNames.add(place.name);
                    }
                    XMLUtil xUtil = new XMLUtil();
                    xUtil.parse(new FileInputStream(xmlSaveFile), resterauntNames);
                    ArrayAdapter<String> adapt = new ArrayAdapter<String>(ListAndSearchActivity.this, android.R.layout.simple_list_item_1, resterauntNames);
                    resterauntList.setAdapter(adapt);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        });

    }




}
