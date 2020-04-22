package com.example.pokescroll;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

public class PokemonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setCustomView(R.layout.customactionbar);

        Gson gson = new Gson();
        String json = getIntent().getStringExtra("clickedPokemonJson");
        Pokemon pokemon = gson.fromJson(json, Pokemon.class);

        ImageView pokeImage = findViewById(R.id.image);
        Glide.with(this)
                .load(pokemon.getSpriteURL())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(pokeImage);
        TextView pokename = findViewById(R.id.image_description);
        pokename.setText(MainActivity.titleCase(pokemon.getName()));

    }

}
