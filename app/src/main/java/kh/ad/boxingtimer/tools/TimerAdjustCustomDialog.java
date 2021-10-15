package kh.ad.boxingtimer.tools;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import kh.ad.boxingtimer.R;
import kh.ad.boxingtimer.databinding.LayoutTimerAdjustPopupBinding;

public class TimerAdjustCustomDialog {
    LayoutTimerAdjustPopupBinding binding;
    AlertDialog dialog;
    MaterialAlertDialogBuilder builder;
    TextView min;
    TextView sec;

    TimerAdjustCustomDialogInterface listener;
    Activity activity;
    String Timer;

    public TimerAdjustCustomDialog(String Timer, Activity activity, TimerAdjustCustomDialogInterface listener) {
        this.Timer = Timer;
        this.activity = activity;
        this.listener = listener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        binding = LayoutTimerAdjustPopupBinding.inflate(inflater);
        min = binding.min;
        sec = binding.sec;
    }

    public void showDialog() {
        builder = new MaterialAlertDialogBuilder(activity, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        builder.setCancelable(true);
        builder.setView(binding.getRoot());
        dialog = builder.create();
        setUpViewData();
        dialog.show();
    }

    private void setUpViewData() {
        String[] set = Timer.split(":");
        min.setText(set[0]);
        sec.setText(set[1]);
        binding.timeAdjustCancel.setOnClickListener(this::setCANCELButton);
        binding.timeAdjustSave.setOnClickListener(this::setOKButton);
        binding.minInc.setOnClickListener(this::minInc);
        binding.minDec.setOnClickListener(this::minDec);
        binding.secInc.setOnClickListener(this::secInc);
        binding.secDec.setOnClickListener(this::secDec);
    }

    private void minInc(View view) {
        int m = Integer.parseInt(min.getText().toString());
        String z = String.valueOf(m);
        if (m < 99) {
            m += 1;
            if (m >= 0 && m < 10) {
                z = "0" + m;
            } else {
                z = String.valueOf(m);
            }
        }
        min.setText(z);
    }

    private void secInc(View view) {
        int s = Integer.parseInt(sec.getText().toString());
        int m = Integer.parseInt(min.getText().toString());
        String z, zz;
        z = String.valueOf(s);
        if (m < 99 || (m == 99 && s < 59)) {
            s += 1;
            if (s == 60) {
                s = 0;
                m += 1;
                if (m >= 0 && m < 10) {
                    zz = "0" + m;
                } else {
                    zz = String.valueOf(m);
                }
                min.setText(zz);
            }
            if (s >= 0 && s < 10) {
                z = "0" + s;
            } else {
                z = String.valueOf(s);
            }
        }
        sec.setText(z);
    }


    private void minDec(View view) {
        int m = Integer.parseInt(min.getText().toString());
        String z;
        if (m != 0) {
            m -= 1;
        }
        if (m >= 0 && m < 10) {
            z = "0" + m;
        } else {
            z = String.valueOf(m);
        }
        min.setText(z);
    }

    private void secDec(View view) {
        int s = Integer.parseInt(sec.getText().toString());
        int m = Integer.parseInt(min.getText().toString());
        String z, zz;
        s -= 1;
        if (s == -1 && m > 0) {
            s = 59;
            m -= 1;
            if (m < 10) {
                zz = "0" + m;
            } else {
                zz = String.valueOf(m);
            }
            min.setText(zz);
        }
        if (s == -1 && m == 0) {
            s = 0;
        }
        if (s >= 0 && s < 10) {
            z = "0" + s;
        } else {
            z = String.valueOf(s);
        }
        sec.setText(z);
    }

    private void setOKButton(View view) {
        String time = min.getText().toString() + ":" + sec.getText().toString();
        listener.onOKClickListener(time);
        dialog.cancel();
    }

    private void setCANCELButton(View view) {
        dialog.cancel();
    }
}
