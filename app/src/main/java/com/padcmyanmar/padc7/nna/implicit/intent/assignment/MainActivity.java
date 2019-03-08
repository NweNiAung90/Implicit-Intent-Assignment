package com.padcmyanmar.padc7.nna.implicit.intent.assignment;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SELECT_CONTACT = 1;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private long startMillis = 0;
    private long endMillis = 0;
    private VideoView videoView;
    private boolean videoFlag = false;
    private boolean contactFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Create A Timer */
        Button alarm = findViewById(R.id.btn_alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm("Go to sleep", 1, 0);
            }
        });

        /* Create A Calender Event */
        Button calendarEvent = findViewById(R.id.btn_calendar_event);
        calendarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2019, 2, 23, 18, 00);
                startMillis = beginTime.getTimeInMillis();
                Calendar endTime = Calendar.getInstance();
                endTime.set(2019, 2, 23, 21, 00);
                endMillis = beginTime.getTimeInMillis();
                addEvent("Staff Party", "IBC", startMillis, endMillis);
            }
        });

        /* Capture a video with implicit intent and show it back in App with auto-play (need to use VideoView). */
        Button video = findViewById(R.id.btn_video);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                videoView = findViewById(R.id.vv_video);
                captureVideo(videoView);
            }
        });

        /* Select a contact and display its info back in the App.*/
        Button contact = findViewById(R.id.btn_contact);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });
        /* Perform a web search with query string from the App.*/
        Button btnSearch = findViewById(R.id.btn_search);
        final EditText etSearch = findViewById(R.id.et_search);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = etSearch.getText().toString();
                searchWeb(search);

            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void captureVideo(VideoView v) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
        videoFlag = true;
    }

    public void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
        contactFlag = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (videoFlag == true) {
            if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
                Uri videoUri = intent.getData();
                videoView.setVideoURI(videoUri);
                videoView.start();
            }
        }

        if (contactFlag == true) {
            if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
                Uri contactUri = intent.getData();
                Intent data = new Intent(Intent.ACTION_VIEW, contactUri);
                if (data.resolveActivity(getPackageManager()) != null) {
                    startActivity(data);
                    TextView tvName = findViewById(R.id.tv_name);
                    TextView tvPhone = findViewById(R.id.tv_phone);

                    Cursor cursor = getContentResolver().query(contactUri,
                            null, null, null, null);

                    try {
                        if (cursor.moveToFirst()) {
                            String name = cursor.getString(0);
                            if (name != null) {
                                tvName.setText(name);
                            }
                            String phone = cursor.getString(1);
                            if (phone != null) {
                                tvPhone.setText(phone);
                            }
                        }
                    } finally {
                        cursor.close();
                    }

                }
            }
        }
    }

    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
