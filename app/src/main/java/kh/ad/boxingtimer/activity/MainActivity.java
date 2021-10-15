package kh.ad.boxingtimer.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.util.Locale;

import kh.ad.boxingtimer.R;
import kh.ad.boxingtimer.databinding.ActivityMainBinding;
import kh.ad.boxingtimer.model.Settings;
import kh.ad.boxingtimer.tools.HelperMethods;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    /**
     * Text displays animated dalay time
     */
    TextView timerDelay;

    /**
     * Parent that holds delay time animated text
     */
    FrameLayout timerDelayParent;

    /**
     * Text displays current timer
     */
    TextView round_timer;

    /**
     * Text displays current round
     */
    TextView round_number;

    // SharedPreferences storage variable to store data
    SharedPreferences STORAGE;
    // AlertDialog and its builder to display information or cautions to user
    AlertDialog dialog;
    AlertDialog.Builder builder;
    // Sensors vaiables for proximity and shake sensor settings
    SensorManager ProximityManager;
    Sensor Proximity;
    SensorManager ShakeManager;
    Sensor Shake;
    SensorEventListener ProximityListener;
    SensorEventListener ShakeListener;
    // Vibration variable to set vibration settings
    Vibrator V;
    // shake sensors var to controll shake settings
    float acelVal, acelLast, shake;
    //CountDownTimer
    CountDownTimer Timer;
    //boolean variable to track timer running state
    boolean runningState;
    //train and break timers trackers variables
    long TIMER;
    long BREAK_TIMER;
    long time = TIMER;
    long break_time = BREAK_TIMER;
    // round tracker variable
    int Current_Round = 1;
    // timer type tracker variable state whether train or break
    String Run = "Timer";
    // current setting object contains all current timer info
    Settings CurrentSetting;
    // resume after pause activity
    boolean resume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        timerDelay = binding.timerDelay;
        timerDelayParent = binding.timerDelayParent;
        round_timer = binding.roundTimer;
        round_number = binding.roundNumber;
        binding.settingButton.setOnClickListener(this::openSettings);
        binding.favouritesButton.setOnClickListener(this::openFavourites);
        binding.resetButton.setOnClickListener(this::Reset);
        binding.playButton.setOnClickListener(this::playTimer);
        binding.pauseButton.setOnClickListener(this::pauseTimer);

        setStartUpData();
        setProximitySensors();
        setShakeSensor();

        if (savedInstanceState != null) {
            String rnd_num = "Round " + savedInstanceState.getInt("round_number");
            round_timer.setText(savedInstanceState.getString("train_timer"));
            round_number.setText(rnd_num);
            time = savedInstanceState.getLong("timer");
            break_time = savedInstanceState.getLong("break_timer");
            resume = savedInstanceState.getBoolean("resume");
            if (resume) {
                TimerStart();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("train_timer", round_timer.getText().toString());
        outState.putInt("round_number", Current_Round);
        outState.putLong("timer", time);
        outState.putLong("break_timer", BREAK_TIMER);
        outState.putBoolean("resume", resume);
    }

    public void setShakeSensor() {
        ShakeManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Shake = ShakeManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;
        ShakeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                acelLast = acelVal;
                acelVal = (float) Math.sqrt(x * x + y * y + z * z);
                float delta = acelVal - acelLast;
                shake = shake * 0.9f + delta;

                if (shake > 12) {
                    if (CurrentSetting.isSHAKE_ON()) {
                        if (!runningState) {
                            Toast.makeText(MainActivity.this, "Timer On", Toast.LENGTH_SHORT).show();
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            if (time == TIMER && Current_Round == 1) {
                                startDelayTime();
                            } else {
                                if (!runningState && Run.equals("Timer"))
                                    TimerStart();
                                if (!runningState && Run.equals("Break"))
                                    BTimerStart();
                            }
                        } else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            Toast.makeText(MainActivity.this, "Timer Off", Toast.LENGTH_SHORT).show();
                            TimerStop();
                            resume = false;
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        ShakeManager.registerListener(ShakeListener, Shake, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void setProximitySensors() {
        ProximityManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Proximity = ProximityManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        ProximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[0] == Proximity.getMaximumRange()) {
                    if (CurrentSetting.isPROXIMITY_ON()) {
                        if (!runningState) {
                            Toast.makeText(MainActivity.this, "Timer On", Toast.LENGTH_SHORT).show();
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            if (time == TIMER && Current_Round == 1) {
                                startDelayTime();
                            } else {
                                if (!runningState && Run.equals("Timer"))
                                    TimerStart();
                                if (!runningState && Run.equals("Break"))
                                    BTimerStart();
                            }
                        } else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            Toast.makeText(MainActivity.this, "Timer Off", Toast.LENGTH_SHORT).show();
                            TimerStop();
                            resume = false;
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        ProximityManager.registerListener(ProximityListener, Proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            TimerStart();
        }
        if (requestCode == 3 && resultCode == 3) {
            if (Shake != null && ShakeManager != null && ShakeListener != null &&
                    Proximity != null && ProximityManager != null && ProximityListener != null) {
                ShakeManager.unregisterListener(ShakeListener, Shake);
                ProximityManager.unregisterListener(ProximityListener, Proximity);
            }
            finish();
            startActivity(getIntent());
        }
        if (requestCode == 6 && resultCode == 5) {
            if (Shake != null && ShakeManager != null && ShakeListener != null &&
                    Proximity != null && ProximityManager != null && ProximityListener != null) {
                ShakeManager.unregisterListener(ShakeListener, Shake);
                ProximityManager.unregisterListener(ProximityListener, Proximity);
            }
            finish();
            startActivity(getIntent());
        }
    }

    public void setStartUpData() {
        STORAGE = getApplicationContext().getSharedPreferences(
                getString(R.string.SHARED_PREFRENCES_NAME),
                MODE_PRIVATE
        );

        if (!STORAGE.contains(getString(R.string.CURRENT_SETTINGS))) {
            CurrentSetting = new Settings(3, "03:00", "01:00",
                    3, false,
                    100, false, false,
                    30, false, false);
            Gson gson = new Gson();
            String json = gson.toJson(CurrentSetting);
            SharedPreferences.Editor editt = STORAGE.edit();
            editt.putString(getString(R.string.CURRENT_SETTINGS), json);
            editt.apply();
        }


        Gson gson = new Gson();
        String data = STORAGE.getString(getString(R.string.CURRENT_SETTINGS), "");
        CurrentSetting = gson.fromJson(data, Settings.class);

        round_timer.setText(CurrentSetting.getTRAINING_TIME());
        String rnd = "Round " + Current_Round;
        round_number.setText(rnd);

        TIMER = HelperMethods.stringToMilliSeconds(CurrentSetting.getTRAINING_TIME());

        BREAK_TIMER = HelperMethods.stringToMilliSeconds(CurrentSetting.getBREAK_TIME());

        time = TIMER;
        break_time = BREAK_TIMER;

    }

    private void openSettings(View view) {
        if (runningState) {
            TimerStop();
        }
        startActivityForResult(
                new Intent(MainActivity.this, SettingsActivity.class).
                        putExtra("WhoIsThere", "Setting"), 3);
    }

    private void openFavourites(View view) {
        if (runningState) {
            TimerStop();
        }
        startActivityForResult(new Intent(MainActivity.this, FavouritesActivity.class), 6);
    }

    private void Reset(View view) {
        if (runningState) {
            TimerStop();
            resume = false;
        }
        builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Reset");
        builder.setMessage("Do you want to reset the timer?");
        builder.setPositiveButton("OK",
                (dialog, which) -> Reset());
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
        });

        dialog = builder.create();
        dialog.show();
    }

    private void Reset() {
        Current_Round = 1;
        String rnd = "Round " + Current_Round;
        round_number.setText(rnd);
        round_timer.setTextColor(Color.RED);
        round_timer.setText(CurrentSetting.getTRAINING_TIME());
        time = TIMER;
        break_time = BREAK_TIMER;
        runningState = false;
        Run = "Timer";
        resume = false;
    }

    private void playTimer(View view) {
        if (!runningState) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (time == TIMER && Current_Round == 1) {
                startDelayTime();
            } else {
                Toast.makeText(MainActivity.this, "Timer On", Toast.LENGTH_SHORT).show();
                if (!runningState && Run.equals("Timer"))
                    TimerStart();
                if (!runningState && Run.equals("Break"))
                    BTimerStart();
            }
        }
    }

    private void pauseTimer(View view) {
        if (runningState) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Toast.makeText(MainActivity.this, "Timer Off", Toast.LENGTH_SHORT).show();
            TimerStop();
            resume = false;
        }
    }

    private void startDelayTime() {
        if (CurrentSetting.getDELAY_TIME() > 0) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.animation_timer_delay);
            timerDelayParent.setVisibility(View.VISIBLE);
            timerDelay.setText(String.valueOf(CurrentSetting.getDELAY_TIME()));
            timerDelay.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animation.setAnimationListener(null);
                    int delay = Integer.parseInt(timerDelay.getText().toString());
                    delay--;
                    if (delay == 0) {
                        timerDelayParent.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Timer On", Toast.LENGTH_SHORT).show();
                        TimerStart();
                    } else {
                        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation_timer_delay);
                        animation.setAnimationListener(this);
                        timerDelay.setText(String.valueOf(delay));
                        timerDelay.startAnimation(animation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            TimerStart();
        }
    }

    public void TimerStart() {
        resume = true;
        Run = "Timer";
        if (time == TIMER) {
            round_timer.setTextColor(Color.RED);
            round_timer.setText(CurrentSetting.getTRAINING_TIME());
        }
        if (time == TIMER && CurrentSetting.isSOUND_ON()) {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.roundring);
            float lvl = (float) CurrentSetting.getSOUND_VOLUME() / 100;
            mp.setVolume(lvl, lvl);
            mp.start();
        }
        if (time == TIMER && CurrentSetting.isVIBRATION_ON()) {
            V = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            V.vibrate(1000);
        }
        Timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {
                time = l;
                int min = (int) (time / 1000) / 60;
                int sec = (int) (time / 1000) % 60;
                round_timer.setText(String.format(Locale.ENGLISH, "%02d:%02d", min, sec));
                if (CurrentSetting.isPRELIMINARY_SOUND_ON() &&
                        CurrentSetting.getPRELIMINARY_TIME() == sec &&
                        min == 0 &&
                        CurrentSetting.getPRELIMINARY_TIME() * 1000L < TIMER &&
                        CurrentSetting.isSOUND_ON()) {
                    MediaPlayer PrelMP = MediaPlayer.create(MainActivity.this, R.raw.preliminary);
                    float level = (float) CurrentSetting.getSOUND_VOLUME() / 100;
                    PrelMP.setVolume(level, level);
                    PrelMP.start();
                }
            }

            @Override
            public void onFinish() {
                if (Current_Round < CurrentSetting.getROUNDS()) {
                    if (CurrentSetting.getBREAK_TIME().equals("00:00")) {
                        time = TIMER;
                        Current_Round++;
                        String rnd = "Round " + Current_Round;
                        round_number.setText(rnd);
                        round_timer.setText(CurrentSetting.getTRAINING_TIME());
                        TimerStart();
                    } else {
                        BTimerStart();
                    }
                } else {
                    Reset();
                }
            }
        }.start();
        runningState = true;
    }

    public void BTimerStart() {
        resume = true;
        Run = "Break";
        if (break_time == BREAK_TIMER) {
            round_timer.setTextColor(Color.GREEN);
            round_timer.setText(CurrentSetting.getBREAK_TIME());
        }
        if (break_time == BREAK_TIMER && CurrentSetting.isSOUND_ON()) {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.roundring);
            float lvl = (float) CurrentSetting.getSOUND_VOLUME() / 100;
            mp.setVolume(lvl, lvl);
            mp.start();
        }
        if (time == TIMER && CurrentSetting.isVIBRATION_ON()) {
            V = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            V.vibrate(1000);
        }
        Timer = new CountDownTimer(break_time, 1000) {
            @Override
            public void onTick(long l) {
                break_time = l;
                int min = (int) (break_time / 1000) / 60;
                int sec = (int) (break_time / 1000) % 60;
                round_timer.setText(String.format(Locale.ENGLISH, "%02d:%02d", min, sec));
                if (CurrentSetting.isPRELIMINARY_SOUND_ON() &&
                        CurrentSetting.getPRELIMINARY_TIME() == sec &&
                        min == 0 &&
                        CurrentSetting.getPRELIMINARY_TIME() * 1000L < BREAK_TIMER &&
                        CurrentSetting.isSOUND_ON()) {
                    MediaPlayer PrelMP = MediaPlayer.create(MainActivity.this, R.raw.preliminary);
                    float level = (float) CurrentSetting.getSOUND_VOLUME() / 100;
                    PrelMP.setVolume(level, level);
                    PrelMP.start();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                break_time = BREAK_TIMER;
                if (Current_Round < CurrentSetting.getROUNDS()) {
                    time = TIMER;
                    Current_Round++;
                    round_number.setText("Round " + Current_Round);
                    round_timer.setText(CurrentSetting.getTRAINING_TIME());
                    TimerStart();
                }
            }
        }.start();
        runningState = true;
    }

    public void TimerStop() {
        Timer.cancel();
        runningState = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerDelay.clearAnimation();
        if (resume) {
            TimerStop();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (resume) {
            TimerStart();
        }
    }
}