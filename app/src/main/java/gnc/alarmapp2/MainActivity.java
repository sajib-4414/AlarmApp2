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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    CheckBox repeatbox;
    private List<Boolean> savedListStatus;
    protected String[] weekdaysArray = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    TextView tvRepeatDays;
    private AtomicInteger incrementalAlarmID;


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
        incrementalAlarmID = new AtomicInteger();

        savedListStatus= new ArrayList<>();
        for(int i = 0;i<7;i++) {
            savedListStatus.add(false);
        }


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
                showRIngtoneChOserDilog();
            }
        });
        whichRingTone = findViewById(R.id.textChosenRingTone);
        repeatbox = findViewById(R.id.repeat_checkbox);
        repeatbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatbox.setChecked(!repeatbox.isChecked());
                showDayChooserDialog();
            }
        });
        tvRepeatDays = findViewById(R.id.tvdays);
    }

    public void onToggleClicked(View view) {
        if(repeatbox.isChecked())
        {
            //set repeat alarm

            //turning on
            if (((SwitchCompat) view).isChecked()) {
                Log.d("MyActivity", "Alarm On");
                setAlarmForDay(alarmTimePicker.getCurrentHour(), alarmTimePicker.getCurrentMinute());
                Toast.makeText(getApplicationContext(),"Alarm is set at " + alarmTimePicker.getCurrentHour() + ":" +
                        alarmTimePicker.getCurrentMinute() + "on" + tvRepeatDays.getText().toString(), Toast.LENGTH_SHORT).show();
                //Calendar calendar = Calendar.getInstance();
                //calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
               // calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
               // Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                //pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
               // alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            }

            //turning off
            else {
                alarmManager.cancel(pendingIntent);
                TriggerAlarm("");
                if(mPlayer !=null && mPlayer.isPlaying()){
                    mPlayer.stop();
                }

                Log.d("MyActivity", "Alarm Off");
            }
        }
        else
        {
            //set unset single alarm
            if (((SwitchCompat) view).isChecked()) {
                Log.d("MyActivity", "Alarm On");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, incrementalAlarmID.incrementAndGet(), myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(getApplicationContext(),"Alarm is set at " + alarmTimePicker.getCurrentHour() + ":" +
                        alarmTimePicker.getCurrentMinute() , Toast.LENGTH_SHORT).show();
            } else {
                alarmManager.cancel(pendingIntent);
                TriggerAlarm("");
                if(mPlayer !=null && mPlayer.isPlaying()){
                    mPlayer.stop();
                }

                Log.d("MyActivity", "Alarm Off");
            }
        }

    }
    public void setAlarmForDay(int hours, int minutes)
    {
        for(int i=0; i<7; i++)
        {
            if(savedListStatus.get(i))
            {
                forday(i, hours, minutes);
            }
        }
    }
    public void forday(int dayNo, int hours, int minutes) {
        if(dayNo == 0)
            dayNo = 7;

        //saturday = 7;
        Calendar calSet = Calendar.getInstance();
        calSet.set(Calendar.DAY_OF_WEEK, dayNo);
        calSet.set(Calendar.HOUR_OF_DAY, hours);
        calSet.set(Calendar.MINUTE, minutes);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, incrementalAlarmID.incrementAndGet(), myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calSet.getTimeInMillis(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
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

    private void showRIngtoneChOserDilog()
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




    private void showDayChooserDialog()
    {


        final HashMap<String, Boolean> temporaryDaysValueMap = new HashMap<>();



        //restoring the saved values in temporaryHashmap
        for(int ix=0; ix<7;ix++)
        {
            temporaryDaysValueMap.put(weekdaysArray[ix], savedListStatus.get(ix));
        }





        boolean[] booleanStatusArrayForDialog = new boolean[7];
        //getting saved values in this array

        //String[] singleChoiceItems = {"1","2"};
        //getResources().getStringArray(R.array.dialog_single_choice_array);
//        int itemSelected = 0;


        for(int i=0; i< 7; i++){
            booleanStatusArrayForDialog[i] = temporaryDaysValueMap.get(weekdaysArray[i]);
        }



        AlertDialog mAlert = new AlertDialog.Builder(this)
                .setTitle("Select days to repeat")
                .setCancelable(false)
                 .setMultiChoiceItems(weekdaysArray, booleanStatusArrayForDialog, new DialogInterface.OnMultiChoiceClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int selectedIndex, boolean isChecked) {
                         if(isChecked)
                            temporaryDaysValueMap.put(weekdaysArray[selectedIndex],true);
                         else
                             temporaryDaysValueMap.put(weekdaysArray[selectedIndex],false);
                     }

                 })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        boolean isFoundChecked = checkifAnyTrueinHashmap(temporaryDaysValueMap);
                        repeatbox.setChecked(isFoundChecked);
                        saveMapToList(temporaryDaysValueMap);

                        dialogInterface.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        mAlert.show();

    }

    private void saveMapToList(HashMap<String, Boolean> map) {
        tvRepeatDays.setText("");
        for(int ij=0 ; ij<7; ij++)
        {
            savedListStatus.set(ij,map.get(weekdaysArray[ij]));
            if(map.get(weekdaysArray[ij])){
                if(tvRepeatDays.getText().toString().length() ==0){
                    tvRepeatDays.setText( weekdaysArray[ij]);
                }
                else
                {
                    tvRepeatDays.setText(tvRepeatDays.getText().toString() + "," + weekdaysArray[ij]);
                }

            }
        }
        if(tvRepeatDays.getText().toString().length() == 0)
            tvRepeatDays.setText("None");
    }


    private boolean checkifAnyTrueinHashmap(HashMap<String, Boolean> map)
    {
        for (Object value : map.values()) {
            if((Boolean)value)
                return true;
        }
        return false;
    }
    private void cancelAlarm(int alarmId){
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntentToCancel = PendingIntent.getActivity(MainActivity.this,
                alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntentToCancel);
    }
}