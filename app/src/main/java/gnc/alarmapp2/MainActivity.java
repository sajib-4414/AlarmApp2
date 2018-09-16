package gnc.alarmapp2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
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
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;
    private TextView alarmTextView, whichRingTone;
    private Switch soundSwitch;
    private Ringtone ringtone;
    private Button ringchoser;

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
                    if(ringtone !=null)
                    {
                        if(ringtone.isPlaying())
                        {
                            ringtone.stop();
                        }
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
            setAlarmText("", null);
            if(ringtone !=null)
            {
                if(ringtone.isPlaying())
                {
                    ringtone.stop();
                }
            }

            Log.d("MyActivity", "Alarm Off");
        }
    }

    public void setAlarmText(String alarmText, Ringtone ringtone) {
        alarmTextView.setText(alarmText);
        if(alarmText.length()==0)
        {
            soundSwitch.setVisibility(View.GONE);
        }
        else
        {
            soundSwitch.setVisibility(View.VISIBLE);
        }
        if(ringtone !=null){
            this.ringtone = ringtone;
            if(alarmText !=null && alarmText.length()>0)
            {
                soundSwitch.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDialog()
    {
        Map<String, String> map = RingtoneHelper.getRingToneList(this);
        List<String> ringtonesList = new ArrayList<String>(map.keySet());
        String[] ringtonesArray = new String[ringtonesList.size()];
        ringtonesArray = ringtonesList.toArray(ringtonesArray);

        //String[] singleChoiceItems = {"1","2"};
                //getResources().getStringArray(R.array.dialog_single_choice_array);
        int itemSelected = 0;
        final String[] finalRingtonesArray=ringtonesArray;
        new AlertDialog.Builder(this)
                .setTitle("Select a ringtone")
                .setSingleChoiceItems(ringtonesArray, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        ListView lv = ((AlertDialog)dialogInterface).getListView();
                        lv.setTag(new Integer(selectedIndex));
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
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        whichRingTone.setText("No ringtone chosen");
                    }
                })
                .show();
    }
}