package com.suheb.createcalanderevent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by suheb on 22/10/16.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Spinner calendarIdSpinner;
    private Hashtable<String, String> calendarIdTable;
    private Button newEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarIdSpinner = (Spinner) findViewById(R.id.calendarid_spinner);
        newEventButton = (Button) findViewById(R.id.newevent_button);
        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CalendarHelper.haveCalendarReadWritePermissions(MainActivity.this)) {
                    addNewEvent();
                } else {
                    CalendarHelper.requestCalendarReadWritePermission(MainActivity.this);
                }
            }
        });

        calendarIdSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        if (CalendarHelper.haveCalendarReadWritePermissions(this)) {
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);

            updateCalendarIdSpinner();

        }


    }

    private void updateCalendarIdSpinner() {
        if (calendarIdTable == null) {
            return;
        }

        List<String> list = new ArrayList<String>();

        Enumeration e = calendarIdTable.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            list.add(key);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarIdSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CalendarHelper.CALENDARHELPER_PERMISSION_REQUEST_CODE) {
            if (CalendarHelper.haveCalendarReadWritePermissions(this)) {
                Toast.makeText(this, (String) "Have Calendar Read/Write Permission.",
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addNewEvent() {
        if (calendarIdTable == null) {
            Toast.makeText(this, (String) "No calendars found. Please ensure at least one google account has been added.",
                    Toast.LENGTH_LONG).show();
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);
            updateCalendarIdSpinner();
            return;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss");
        Date date = null;
        try {
            /*pass your date here in required format*/
            date = formatter.parse("2016-22-12 15:56:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("converted_date", String.valueOf(date.getTime()));
        long time = date.getTime();
//        final long oneHour = 1000 * 60 * 60;
        final long tenMinutes = 1000 * 60 * 10;
        /* time + tenMinutes means event time + ten minutes */
        String calendarString = calendarIdSpinner.getSelectedItem().toString();
        int calendar_id = Integer.parseInt(calendarIdTable.get(calendarString));
        CalendarHelper.MakeNewCalendarEntry(this, "EventTitle", "Description", "Somewhere", time, time + tenMinutes, false, true, calendar_id, 3);
    }
}
