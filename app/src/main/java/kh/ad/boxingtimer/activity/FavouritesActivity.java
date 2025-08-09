package kh.ad.boxingtimer.activity;

import static kh.ad.boxingtimer.tools.HelperMethods.getStorage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import kh.ad.boxingtimer.R;
import kh.ad.boxingtimer.adapter.FavouritesAdapter;
import kh.ad.boxingtimer.custom.EmptyRecyclerView;
import kh.ad.boxingtimer.databinding.ActivityFavouritesBinding;
import kh.ad.boxingtimer.model.Settings;

public class FavouritesActivity extends AppCompatActivity {

    ActivityFavouritesBinding binding;
    EmptyRecyclerView favourites;

    FavouritesAdapter adapter;
    SharedPreferences STORAGE;
    SharedPreferences.Editor STORAGE_EDIT;
    ArrayList<Settings> data;
    Gson gson;

    TextView emptyFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // IMPORTANT: must be called BEFORE setContentView(...)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(1);
            finish();
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        favourites = binding.TysonTimerFavouritesList;
        emptyFav = binding.TysonTimerEmptyFavourites;

        STORAGE = getApplicationContext().getSharedPreferences(
                getString(R.string.SHARED_PREFRENCES_NAME),
                MODE_PRIVATE
        );
        if (!STORAGE.contains(getString(R.string.FAVOURITES_DATA))) {
            data = new ArrayList<>();
        } else {
            gson = new Gson();
            String favourite = STORAGE.getString(getString(R.string.FAVOURITES_DATA), null);
            Type type = new TypeToken<ArrayList<Settings>>() {
            }.getType();
            data = gson.fromJson(favourite, type);
        }

        adapter = new FavouritesAdapter(this, this, data);
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        favourites.setLayoutManager(mLinearLayout);
        favourites.setAdapter(adapter);
        favourites.setEmptyView(emptyFav);
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        STORAGE = getStorage(this);
        if (!STORAGE.contains(getString(R.string.FAVOURITES_DATA))) {
            data = new ArrayList<>();
        } else {
            gson = new Gson();
            String favourite = STORAGE.getString(getString(R.string.FAVOURITES_DATA), null);
            Type type = new TypeToken<ArrayList<Settings>>() {
            }.getType();
            data = gson.fromJson(favourite, type);
        }
        assert data != null;
        adapter.addAll(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        if (item.getItemId() == R.id.insertFavourite) {
            insertFavourite();
            return true;
        } else if (item.getItemId() == R.id.deleteAllFavourites) {
            deleteAllFavourites();
            return true;

        } else {
            return super.onOptionsItemSelected(item);

        }
    }

    private void insertFavourite() {
        Intent in = new Intent(FavouritesActivity.this, SettingsActivity.class);
        in.putExtra("WhoIsThere", "Favourite");
        startActivity(in);
    }

    private void deleteAllFavourites() {
        AlertDialog dialoge;
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setCancelable(true);
        builder.setTitle("Delete Favourites");
        builder.setMessage("Are you sure you want to delete all favourites?");
        builder.setPositiveButton("OK",
                (dialog, which) -> {
                    STORAGE = getApplicationContext().getSharedPreferences(getString(R.string.SHARED_PREFRENCES_NAME), MODE_PRIVATE);
                    STORAGE_EDIT = STORAGE.edit();
                    data.clear();
                    adapter.clear();
                    Gson gson = new Gson();
                    String json = gson.toJson(data);
                    STORAGE_EDIT.putString(getString(R.string.FAVOURITES_DATA), json);
                    STORAGE_EDIT.apply();
                    Toast.makeText(FavouritesActivity.this, "Favourites deleted", Toast.LENGTH_SHORT).show();
                });
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
        });

        dialoge = builder.create();
        dialoge.show();
    }
}