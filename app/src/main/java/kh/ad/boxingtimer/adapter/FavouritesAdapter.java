package kh.ad.boxingtimer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import kh.ad.boxingtimer.R;
import kh.ad.boxingtimer.databinding.FavouriteListItemBinding;
import kh.ad.boxingtimer.model.Settings;

import static android.content.Context.MODE_PRIVATE;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyHolder> {

    ArrayList<Settings> data;
    Context context;
    Activity activity;
    SharedPreferences STORAGE;
    SharedPreferences.Editor STORAGE_EDIT;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    public FavouritesAdapter(Activity activity, Context context, ArrayList<Settings> set) {
        this.context = context;
        this.activity = activity;
        data = set;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavouriteListItemBinding binding = FavouriteListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Settings current = data.get(position);

        holder.name.setText(current.getFavourite_Name());
        holder.Timer.setText(current.getTRAINING_TIME());
        holder.Break.setText(current.getBREAK_TIME());
        holder.number.setText(String.valueOf(current.getROUNDS()));

        if (current.isSOUND_ON()) {
            holder.sound.setImageResource(R.drawable.ic_checked);
            holder.sound.setRotation(0);
        } else {
            holder.sound.setImageResource(R.drawable.ic_unchecked);
            holder.sound.setRotation(45);
        }

        if (current.isPRELIMINARY_SOUND_ON()) {
            holder.preSound.setImageResource(R.drawable.ic_checked);
            holder.preSound.setRotation(0);
        } else {
            holder.preSound.setImageResource(R.drawable.ic_unchecked);
            holder.preSound.setRotation(45);
        }

        if (current.isVIBRATION_ON()) {
            holder.vibration.setImageResource(R.drawable.ic_checked);
            holder.vibration.setRotation(0);
        } else {
            holder.vibration.setImageResource(R.drawable.ic_unchecked);
            holder.vibration.setRotation(45);
        }

        if (current.isPROXIMITY_ON() || current.isSHAKE_ON()) {
            holder.sensor.setImageResource(R.drawable.ic_checked);
            holder.sensor.setRotation(0);
        } else {
            holder.sensor.setImageResource(R.drawable.ic_unchecked);
            holder.sensor.setRotation(45);
        }

        holder.itemSelect.setOnClickListener(v -> FavouriteChoose(current));
        holder.itemRemove.setOnClickListener(v -> FavouriteRemove(current, position));
    }

    private void FavouriteChoose(Settings mySet) {
        STORAGE = context.getApplicationContext().getSharedPreferences(context.getString(R.string.SHARED_PREFRENCES_NAME), MODE_PRIVATE);
        STORAGE_EDIT = STORAGE.edit();
        STORAGE_EDIT.remove(context.getString(R.string.CURRENT_SETTINGS));
        Gson gson = new Gson();
        String json = gson.toJson(mySet);
        STORAGE_EDIT.putString(context.getString(R.string.CURRENT_SETTINGS), json);
        STORAGE_EDIT.apply();
        activity.setResult(5);
        activity.finish();
    }

    private void FavouriteRemove(Settings mySet, int index) {
        builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle(mySet.getFavourite_Name());
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("OK",
                (dialog, which) -> {
                    deleteItem(index);
                    STORAGE = context.getApplicationContext().getSharedPreferences(context.getString(R.string.SHARED_PREFRENCES_NAME), MODE_PRIVATE);
                    STORAGE_EDIT = STORAGE.edit();
                    STORAGE_EDIT.remove(context.getString(R.string.FAVOURITES_DATA));
                    Gson gson = new Gson();
                    String json = gson.toJson(data);
                    STORAGE_EDIT.putString(context.getString(R.string.FAVOURITES_DATA), json);
                    STORAGE_EDIT.apply();
                    Toast.makeText(context, "Favourite deleted", Toast.LENGTH_SHORT).show();
                });
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
        });

        dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteItem(int pos) {
        data.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAll(ArrayList<Settings> d) {
        if (d.size() != data.size()) {
            clear();
            data.addAll(d);
            notifyItemRangeInserted(0, d.size());
        }
    }

    public void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    static
    class MyHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView Timer;
        TextView Break;
        TextView number;
        ImageView sound;
        ImageView vibration;
        ImageView preSound;
        ImageView sensor;
        ImageButton itemRemove;
        LinearLayout itemSelect;

        public MyHolder(@NonNull FavouriteListItemBinding binding) {
            super(binding.getRoot());
            name = binding.listItemName;
            Timer = binding.listItemTimer;
            Break = binding.listItemBreak;
            number = binding.listItemNumber;
            sound = binding.listItemSoundState;
            vibration = binding.listItemVibrationState;
            preSound = binding.listItemPreSoundState;
            sensor = binding.listItemSensorState;
            itemRemove = binding.listItemRemove;
            itemSelect = binding.listItemParentCick;
        }
    }
}
