package gnc.alarmapp2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;
    private TextView alarmTextView, whichRingTone;
    private Switch soundSwitch;
    //private Ringtone ringtone;
    private int selectedRingtoneResousrceId= -1;
    private Button ringchoser;
    MediaPlayer mPlayer;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        SwitchCompat alarmToggle = (SwitchCompat) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        soundSwitch = findViewById(R.id.soundSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true)
                {
                    if(mPlayer !=null && mPlayer.isPlaying()){
                        mPlayer.stop();
                    }

                }
            }
        });
        soundSwitch.setVisibility(View.GONE);
        ringchoser = findViewById(R.id.btnShowRIngtones);
        ringchoser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        whichRingTone = findViewById(R.id.textChosenRingTone);
    }

    public void onToggleClicked(View view) {
        if (((SwitchCompat) view).isChecked()) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            TriggerAlarm("");
            if(mPlayer !=null && mPlayer.isPlaying()){
                mPlayer.stop();
            }

            Log.d("MyActivity", "Alarm Off");
        }
    }

    public void TriggerAlarm(String alarmText) {
        alarmTextView.setText(alarmText);
        if(alarmText.length()==0)
        {
            soundSwitch.setVisibility(View.GONE);
        }
        else
        {
            soundSwitch.setVisibility(View.VISIBLE);

        }
        if(selectedRingtoneResousrceId != -1){
            if(alarmText !=null && alarmText.length()>0)
            {
                soundSwitch.setVisibility(View.VISIBLE);
                //play it

                mPlayer = MediaPlayer.create(MainActivity.this, selectedRingtoneResousrceId);
                mPlayer.start();
            }
        }
    }

    private void showDialog()
    {

        final Field[] rawFiles = RingtoneHelper.getListOfRawFiles();
        String[] ringtonesArray = new String[rawFiles.length];
        for(int count=0; count < rawFiles.length; count++){
            ringtonesArray[count] = rawFiles[count].getName();
            //Log.i("Raw Asset: ", rawFiles[count].getName());
        }



        //String[] singleChoiceItems = {"1","2"};
                //getResources().getStringArray(R.array.dialog_single_choice_array);
        int itemSelected = 0;
        final String[] finalRingtonesArray=ringtonesArray;
        new AlertDialog.Builder(this)
                .setTitle("Select a ringtone")
                .setSingleChoiceItems(ringtonesArray, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        if(mPlayer !=null && mPlayer.isPlaying()){
                            mPlayer.stop();
                        }

                        ListView lv = ((AlertDialog)dialogInterface).getListView();
                        lv.setTag(new Integer(selectedIndex));
                        //play it
                        int resourceID =0;
                        try {
                            resourceID=rawFiles[selectedIndex].getInt(rawFiles[selectedIndex]);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        mPlayer = MediaPlayer.create(MainActivity.this, resourceID);
                        mPlayer.start();

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        ListView lv = ((AlertDialog)dialogInterface).getListView();
                        Integer selected = (Integer)lv.getTag();
                        if(selected != null) {
                            // do something interesting
                            whichRingTone.setText("Chosen: " + finalRingtonesArray[selected]);
                            if(mPlayer !=null && mPlayer.isPlaying()){
                                mPlayer.stop();
                            }
                            int resourceID =0;
                            try {
                                resourceID=rawFiles[selected].getInt(rawFiles[selected]);
                                selectedRingtoneResousrceId= resourceID;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        whichRingTone.setText("No ringtone chosen");
                        if(mPlayer !=null && mPlayer.isPlaying()){
                            mPlayer.stop();
                        }
                    }
                })
                .show();
    }

    public void onDestroy() {

        mPlayer.stop();
        super.onDestroy();

    }
}