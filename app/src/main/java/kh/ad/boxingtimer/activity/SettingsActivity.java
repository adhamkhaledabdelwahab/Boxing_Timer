package kh.ad.boxingtimer.activity;

import static kh.ad.boxingtimer.tools.HelperMethods.getStorage;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import kh.ad.boxingtimer.R;
import kh.ad.boxingtimer.databinding.ActivitySettingsBinding;
import kh.ad.boxingtimer.model.Settings;
import kh.ad.boxingtimer.tools.TimerAdjustCustomDialog;
import kh.ad.boxingtimer.tools.TimerAdjustCustomDialogInterface;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    TextView Rounds;
    TextView TrainTime;
    TextView BreakTime;
    TextView DelayTime;
    CheckBox SoundOn;
    SeekBar Volume;
    CheckBox VibrationOn;
    CheckBox PreliminarySoundOn;
    TextView PreliminaryTime;
    CheckBox ProximityOn;
    CheckBox ShakeOn;
    MaterialRippleLayout saveButton;

    SharedPreferences STORAGE;
    SharedPreferences.Editor STORAGE_EDIT;
    EditText dialogText;
    int ROUNDS;
    String TRAINING_TIME;
    String BREAK_TIME;
    int DELAY_TIME;
    boolean SOUND_ON;
    int SOUND_VOLUME;
    boolean VIBRATION_ON;
    boolean PRELIMINARY_SOUND_ON;
    int PRELIMINARY_TIME;
    boolean PROXIMITY_ON;
    boolean SHAKE_ON;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    ArrayList<Settings> FAVOURITES;

    private String WhoIsThere = "Setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(1);
            finish();
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Rounds = binding.RoundsEditor;
        TrainTime = binding.TrainTimeEditor;
        BreakTime = binding.BreakTimeEditor;
        DelayTime = binding.DelayTimeEditor;
        SoundOn = binding.SoundSettingEditor;
        Volume = binding.VolumeSettingEditor;
        VibrationOn = binding.VibrationSettingEditor;
        PreliminarySoundOn = binding.PreliminarySoundSettingEditor;
        PreliminaryTime = binding.PreliminaryTimeEditor;
        ProximityOn = binding.SensorSetting1Editor;
        ShakeOn = binding.SensorSetting2Editor;
        saveButton = binding.saveButton;
        binding.RoundsIncreaseButton.setOnClickListener(this::RoundsIncrease);
        binding.RoundsDecreaseButton.setOnClickListener(this::RoundsDecrease);
        binding.TrainTimeIncreaseButton.setOnClickListener(this::TrainTimeIncrease);
        binding.TrainTimDecreaseButton.setOnClickListener(this::TrainTimeDecrease);
        binding.TrainTimeAdjustment.setOnClickListener(this::TrainTimerAdjust);
        binding.BreakTimeIncreaseButton.setOnClickListener(this::BreakTimeIncrease);
        binding.BreakTimeDecreaseButton.setOnClickListener(this::BreakTimeDecrease);
        binding.BreakTimeAdjustment.setOnClickListener(this::BreakTimerAdjust);
        binding.DelayTimeIncreaseButton.setOnClickListener(this::DelayTimeIncrease);
        binding.DelayTimeDecreaseButton.setOnClickListener(this::DelayTimeDecrease);
        binding.VolumeSettingEditorListen.setOnClickListener(this::VolumeListen);
        binding.PreliminaryTimeIncreaseButton.setOnClickListener(this::PreliminaryTimeIncrease);
        binding.PreliminaryTimeDecreaseButton.setOnClickListener(this::PreliminaryTimeDecrease);
        binding.aboutDelayTime.setOnClickListener(this::aboutDelayTime);
        binding.aboutVolumeSetting.setOnClickListener(this::aboutVolumeSetting);
        binding.aboutPreliminaryTime.setOnClickListener(this::aboutPreliminaryTime);
        binding.aboutProximitySensor.setOnClickListener(this::aboutProximitySensor);
        binding.aboutShakeSensor.setOnClickListener(this::aboutShakeSensor);
        binding.saveButton.setOnClickListener(this::pressSave);
        binding.cancelButton.setOnClickListener(this::pressCancel);

        WhoIsThere = getIntent().getStringExtra("WhoIsThere");
        String s;
        switch (WhoIsThere != null ? WhoIsThere : "Setting") {
            case "Setting":
                s = "Save";
                ((Button) saveButton.getChildAt(0)).setText(s);
                setTitle(getString(R.string.settingsText));
                break;
            case "Favourite":
                s = "Add";
                ((Button) saveButton.getChildAt(0)).setText(s);
                setTitle("Add New Favourite");
                break;
            default:
                break;
        }

        setSavedSettings();

        setCheckBoxListeners();

        setSoundVolume();

        if (savedInstanceState != null) {
            ROUNDS = savedInstanceState.getInt("rounds");
            TRAINING_TIME = savedInstanceState.getString("training_time");
            BREAK_TIME = savedInstanceState.getString("break_timer");
            DELAY_TIME = savedInstanceState.getInt("delay_time");
            SOUND_ON = savedInstanceState.getBoolean("sound_on");
            SOUND_VOLUME = savedInstanceState.getInt("sound_volume");
            VIBRATION_ON = savedInstanceState.getBoolean("vibration_on");
            PRELIMINARY_SOUND_ON = savedInstanceState.getBoolean("preliminary_sound_on");
            PRELIMINARY_TIME = savedInstanceState.getInt("preliminary_time");
            PROXIMITY_ON = savedInstanceState.getBoolean("proximity_on");
            SHAKE_ON = savedInstanceState.getBoolean("shake_on");

            Rounds.setText(String.valueOf(ROUNDS));

            TrainTime.setText(TRAINING_TIME);

            BreakTime.setText(BREAK_TIME);

            DelayTime.setText(String.valueOf(DELAY_TIME));

            SoundOn.setChecked(SOUND_ON);

            Volume.setProgress(SOUND_VOLUME);

            VibrationOn.setChecked(VIBRATION_ON);

            PreliminarySoundOn.setChecked(PRELIMINARY_SOUND_ON);

            PreliminaryTime.setText(String.valueOf(PRELIMINARY_TIME));

            ProximityOn.setChecked(PROXIMITY_ON);

            ShakeOn.setChecked(SHAKE_ON);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("rounds", ROUNDS);
        outState.putString("training_time", TRAINING_TIME);
        outState.putString("break_timer", BREAK_TIME);
        outState.putInt("delay_time", DELAY_TIME);
        outState.putBoolean("sound_on", SOUND_ON);
        outState.putInt("sound_volume", SOUND_VOLUME);
        outState.putBoolean("vibration_on", VIBRATION_ON);
        outState.putBoolean("preliminary_sound_on", PRELIMINARY_SOUND_ON);
        outState.putInt("preliminary_time", PRELIMINARY_TIME);
        outState.putBoolean("proximity_on", PROXIMITY_ON);
        outState.putBoolean("shake_on", SOUND_ON);
    }

    public void setCheckBoxListeners() {

        ProximityOn.setOnCheckedChangeListener((compoundButton, b) -> PROXIMITY_ON = b);

        ShakeOn.setOnCheckedChangeListener((compoundButton, b) -> SHAKE_ON = b);

        PreliminarySoundOn.setOnCheckedChangeListener((compoundButton, b) -> PRELIMINARY_SOUND_ON = b);

        VibrationOn.setOnCheckedChangeListener((compoundButton, b) -> VIBRATION_ON = b);

        SoundOn.setOnCheckedChangeListener((compoundButton, b) -> SOUND_ON = b);
    }

    public void setSoundVolume() {
        Volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SOUND_VOLUME = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });
    }

    private void RoundsIncrease(View view) {
        int curRounds = Integer.parseInt(String.valueOf(Rounds.getText()));
        if (curRounds < 99) {
            curRounds += 1;
            Rounds.setText(String.valueOf(curRounds));
        }
        ROUNDS = curRounds;
    }

    private void RoundsDecrease(View view) {
        int curRounds = Integer.parseInt(String.valueOf(Rounds.getText()));
        if (curRounds > 0) {
            curRounds -= 1;
            Rounds.setText(String.valueOf(curRounds));
        }
        ROUNDS = curRounds;
    }

    private void TrainTimeIncrease(View view) {
        int min, sec;
        String[] time = TrainTime.getText().toString().split(":");
        min = Integer.parseInt(time[0]);
        sec = Integer.parseInt(time[1]);
        if (min < 99 || (min == 99 && sec < 50)) {
            sec += 10;
            if (sec >= 60) {
                min += 1;
                sec -= 60;
            }
        }
        TrainTime.setText(String.format(Locale.ENGLISH, "%02d:%02d", min, sec));
        TRAINING_TIME = TrainTime.getText().toString();
    }

    private void TrainTimeDecrease(View view) {
        int min, sec;
        String[] time = TrainTime.getText().toString().split(":");
        min = Integer.parseInt(time[0]);
        sec = Integer.parseInt(time[1]);
        if (!(min == 0 && sec < 10))
            sec -= 10;
        if (sec >= -10 && sec < 0) {
            min -= 1;
            sec += 60;
        }
        TrainTime.setText(String.format(Locale.ENGLISH, "%02d:%02d", min, sec));
        TRAINING_TIME = TrainTime.getText().toString();
    }

    private void TrainTimerAdjust(View view) {
        TimerAdjustCustomDialogInterface listener = time -> {
            TrainTime.setText(time);
            TRAINING_TIME = time;
        };
        TimerAdjustCustomDialog customDialog = new TimerAdjustCustomDialog(TRAINING_TIME, this, listener);
        customDialog.showDialog();
    }

    private void BreakTimeIncrease(View view) {
        int min, sec;
        String[] time = BreakTime.getText().toString().split(":");
        min = Integer.parseInt(time[0]);
        sec = Integer.parseInt(time[1]);
        if (min < 99 || (min == 99 && sec < 50)) {
            sec += 10;
            if (sec >= 60) {
                min += 1;
                sec -= 60;
            }
        }
        BreakTime.setText(String.format(Locale.ENGLISH, "%02d:%02d", min, sec));
        BREAK_TIME = BreakTime.getText().toString();
    }

    private void BreakTimeDecrease(View view) {
        int min, sec;
        String[] time = BreakTime.getText().toString().split(":");
        min = Integer.parseInt(time[0]);
        sec = Integer.parseInt(time[1]);
        if (!(min == 0 && sec < 10))
            sec -= 10;
        if (sec >= -10 && sec < 0) {
            min -= 1;
            sec += 60;
        }
        BreakTime.setText(String.format(Locale.ENGLISH, "%02d:%02d", min, sec));
        BREAK_TIME = BreakTime.getText().toString();
    }

    private void BreakTimerAdjust(View view) {
        TimerAdjustCustomDialogInterface listener = time -> {
            BreakTime.setText(time);
            BREAK_TIME = time;
        };
        TimerAdjustCustomDialog customDialog = new TimerAdjustCustomDialog(BREAK_TIME, this, listener);
        customDialog.showDialog();
    }

    private void DelayTimeIncrease(View view) {
        int sec = Integer.parseInt(DelayTime.getText().toString());
        if (sec < 59)
            sec += 1;
        DelayTime.setText(String.valueOf(sec));
        DELAY_TIME = sec;
    }

    private void DelayTimeDecrease(View view) {
        int sec = Integer.parseInt(DelayTime.getText().toString());
        if (sec > 0)
            sec -= 1;
        DelayTime.setText(String.valueOf(sec));
        DELAY_TIME = sec;
    }

    private void VolumeListen(View view) {
        MediaPlayer mp = MediaPlayer.create(SettingsActivity.this, R.raw.roundring);
        float level = (float) SOUND_VOLUME / 100;
        mp.setVolume(level, level);
        mp.start();
    }

    private void PreliminaryTimeIncrease(View view) {
        int sec = Integer.parseInt(PreliminaryTime.getText().toString());
        if (sec < 59)
            sec += 1;
        PreliminaryTime.setText(String.valueOf(sec));
        PRELIMINARY_TIME = sec;
    }

    private void PreliminaryTimeDecrease(View view) {
        int sec = Integer.parseInt(PreliminaryTime.getText().toString());
        if (sec > 0)
            sec -= 1;
        PreliminaryTime.setText(String.valueOf(sec));
        PRELIMINARY_TIME = sec;
    }

    private void aboutDelayTime(View view) {
        builder = new AlertDialog.Builder(SettingsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Start delay time");
        builder.setMessage("It is time to prepare for the start\n" +
                "of the first round after pressing\n" +
                "the start button on the timer");
        builder.setPositiveButton("OK",
                (dialog, which) -> dialog.cancel());
        dialog = builder.create();
        dialog.show();
    }

    private void aboutVolumeSetting(View view) {
        builder = new AlertDialog.Builder(SettingsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Volume Setting");
        builder.setMessage("Preview volume is the same\n" +
                "media volume such as music,\n" +
                "videos and games. Volume is\n" +
                "reflected without having to press\n" +
                "the save button");
        builder.setPositiveButton("OK",
                (dialog, which) -> dialog.cancel());
        dialog = builder.create();
        dialog.show();
    }

    private void aboutPreliminaryTime(View view) {
        builder = new AlertDialog.Builder(SettingsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Preliminary Sound Setting");
        builder.setMessage("It tells you when the exercise\n" +
                "time reaches the set preliminary\n" +
                "time. If the pre-sound time is\n" +
                "greater than the exercise time, it\n" +
                "will not be applied");
        builder.setPositiveButton("OK",
                (dialog, which) -> dialog.cancel());
        dialog = builder.create();
        dialog.show();
    }

    private void aboutProximitySensor(View view) {
        builder = new AlertDialog.Builder(SettingsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Proximity Sensor Setting");
        builder.setMessage("By using the proximity sensor, you can start\n" +
                "(or stop) the timer without having\n" +
                "to touch the screen. For example,\n" +
                "when you were wearing gloves,\n" +
                "you can control the Timer without\n" +
                "taking off it.\n\n" +
                "* Proximity sensors are usually\n" +
                "located and the top of the phone");
        builder.setPositiveButton("OK",
                (dialog, which) -> dialog.cancel());
        dialog = builder.create();
        dialog.show();
    }

    private void aboutShakeSensor(View view) {
        builder = new AlertDialog.Builder(SettingsActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Shake On");
        builder.setMessage("Start and stop the timer by\n" +
                "shaking the phone back and forth\n" +
                "without touching the screen.");
        builder.setPositiveButton("OK",
                (dialog, which) -> dialog.cancel());
        dialog = builder.create();
        dialog.show();
    }

    private void pressSave(View view) {
        if (WhoIsThere.equals("Setting")) {
            STORAGE = getStorage(this);
            STORAGE_EDIT = STORAGE.edit();

            STORAGE_EDIT.remove(getString(R.string.CURRENT_SETTINGS));

            Settings curset = new Settings(ROUNDS, TRAINING_TIME, BREAK_TIME, DELAY_TIME, SOUND_ON,
                    SOUND_VOLUME, VIBRATION_ON, PRELIMINARY_SOUND_ON,
                    PRELIMINARY_TIME, PROXIMITY_ON, SHAKE_ON);
            Gson gson = new Gson();
            String json = gson.toJson(curset);
            STORAGE_EDIT.putString(getString(R.string.CURRENT_SETTINGS), json);
            STORAGE_EDIT.apply();

            setResult(3);
            finish();

            Toast.makeText(SettingsActivity.this, "Setting Saved Successfully", Toast.LENGTH_LONG).show();
        } else if (WhoIsThere.equals("Favourite")) {
            builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
            dialogText = new EditText(this);
            dialogText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            dialogText.setTextColor(getResources().getColor(R.color.white));
            dialogText.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(dialogText);
            builder.setCancelable(true);
            builder.setTitle("Favourites");
            builder.setPositiveButton("OK", (dialog, which) -> {
                if (dialogText.getText().toString().isEmpty()) {
                    dialog.cancel();
                    Toast.makeText(SettingsActivity.this, "Please enter the title", Toast.LENGTH_SHORT).show();
                } else {
                    STORAGE = getStorage(this);
                    STORAGE_EDIT = STORAGE.edit();
                    Settings curset = new Settings(dialogText.getText().toString(), ROUNDS, TRAINING_TIME, BREAK_TIME, DELAY_TIME, SOUND_ON,
                            SOUND_VOLUME, VIBRATION_ON, PRELIMINARY_SOUND_ON,
                            PRELIMINARY_TIME, PROXIMITY_ON, SHAKE_ON);
                    FAVOURITES.add(curset);
                    Gson gson = new Gson();
                    String json = gson.toJson(FAVOURITES);
                    STORAGE_EDIT.putString(getString(R.string.FAVOURITES_DATA), json);
                    STORAGE_EDIT.apply();
                    Toast.makeText(SettingsActivity.this, "Add Completed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.cancel());
            dialog = builder.create();
            dialog.show();
        }
    }

    public void setSavedSettings() {
        STORAGE = getStorage(this);

        Gson gson = new Gson();
        Settings curset;
        String data = STORAGE.getString(getString(R.string.CURRENT_SETTINGS), "");
        curset = gson.fromJson(data, Settings.class);

        if (STORAGE.contains(getString(R.string.FAVOURITES_DATA))) {
            String favourite = STORAGE.getString(getString(R.string.FAVOURITES_DATA), null);
            Type type = new TypeToken<ArrayList<Settings>>() {
            }.getType();
            FAVOURITES = gson.fromJson(favourite, type);
        } else {
            FAVOURITES = new ArrayList<>();
        }

        ROUNDS = curset.getROUNDS();
        TRAINING_TIME = curset.getTRAINING_TIME();
        BREAK_TIME = curset.getBREAK_TIME();
        DELAY_TIME = curset.getDELAY_TIME();
        SOUND_ON = curset.isSOUND_ON();
        SOUND_VOLUME = curset.getSOUND_VOLUME();
        VIBRATION_ON = curset.isVIBRATION_ON();
        PRELIMINARY_SOUND_ON = curset.isPRELIMINARY_SOUND_ON();
        PRELIMINARY_TIME = curset.getPRELIMINARY_TIME();
        PROXIMITY_ON = curset.isPROXIMITY_ON();
        SHAKE_ON = curset.isSHAKE_ON();

        Rounds.setText(String.valueOf(curset.getROUNDS()));

        TrainTime.setText(curset.getTRAINING_TIME());

        BreakTime.setText(curset.getBREAK_TIME());

        DelayTime.setText(String.valueOf(curset.getDELAY_TIME()));

        SoundOn.setChecked(curset.isSOUND_ON());

        Volume.setProgress(curset.getSOUND_VOLUME());

        VibrationOn.setChecked(curset.isVIBRATION_ON());

        PreliminarySoundOn.setChecked(curset.isPRELIMINARY_SOUND_ON());

        PreliminaryTime.setText(String.valueOf(curset.getPRELIMINARY_TIME()));

        ProximityOn.setChecked(curset.isPROXIMITY_ON());

        ShakeOn.setChecked(curset.isSHAKE_ON());
    }

    private void pressCancel(View view) {
        if (WhoIsThere.equals("Setting")) {
            setResult(1);
        }
        finish();
    }
}