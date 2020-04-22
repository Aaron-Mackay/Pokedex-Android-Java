package com.example.pokescroll;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    int pokenum = 0;
    int pokecap = 151; //sets limit of pokedex
    List<String> typeList;
    JSONObject ListJSON;
    String key;
    Handler handler;
    Pokemon[] pokedex = new Pokemon[pokecap];
    ArrayList<Pokemon> pokemonArrayList;
    Boolean dbExists;
    Map<String, Integer> typeIDMap = new HashMap<String, Integer>();
    List<Pokemon> incompletePokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setCustomView(R.layout.customactionbar);

        //create map of type, and resource ID
        typeList = Arrays.asList(getResources().getStringArray(R.array.types));
        for (int i = 0; i < typeList.size(); i++) {
            String type = typeList.get(i);
            typeIDMap.put(type, getResources().getIdentifier(type.toLowerCase() + "ic", "drawable", getApplicationInfo().packageName));
        }

        final AppDatabase db = Room
                .databaseBuilder(getApplicationContext(), AppDatabase.class, "pokedex-db")
                .fallbackToDestructiveMigration()
                .build();

        if (doesDatabaseExist(this, "pokedex-db")) {
            dbExists = true;
        } else {
            dbExists = false;
        }
        //fetch from api, store in cache

        handler = new Handler();
        //use pokenum to get class, data etc
        Thread thread1 = new Thread() {
            public void run() {
                if (dbExists == false) {
                    ListJSON = APIFetch.getPokeList();
                    try {
                        JSONArray array = ListJSON.getJSONArray("results");
                        String name;
                        String url;

                        //Loads pokedex with pokemon
                        for (int i = 0; i < pokecap; i++) {

                            name = titleCase(array.getJSONObject(i).getString("name"));
                            url = array.getJSONObject(i).getString("url");
                            Pokemon currentPokemon = new Pokemon(name, url, i + 1);
                            //append to pokedex
                            pokedex[i] = currentPokemon;

                        }
                        db.pokeDao().insertMultiple(Arrays.asList(pokedex));

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Database fetched from API",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Fetching database from cache",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                pokemonArrayList = (ArrayList<Pokemon>) db.pokeDao().loadAllPokemon();
            }
        };
        thread1.start();
        try {
            thread1.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Database is now filled, next step, display as recyclerview?
        RecyclerView recyclerViewId = (RecyclerView) findViewById(R.id.recyclerViewId);

        final PokeAdapter adapter = new PokeAdapter(this, pokemonArrayList, typeIDMap);
        recyclerViewId.setAdapter(adapter);
        recyclerViewId.setLayoutManager(new LinearLayoutManager(this));

        //filling out DB with additional data (types etc). needs threading


        // THREAD todo
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int apiCallsLastMinute = 0;
                final int maxAPIcalls = 80;
                int batchSoFar = 0;
                int batchSize = 40;
                List<Pokemon> batch = new ArrayList<Pokemon>();
                long startTime;
                long currentTime = 0;
                double elapsedSeconds = 0;

                incompletePokemon = db.pokeDao().loadAllIncompletePokemon();
                int incompletePokemonNum = incompletePokemon.size();
                for (int i = 0; i < incompletePokemonNum; i++) {
                    int dexnum = incompletePokemon.get(i).getDexnum();
                    JSONObject pokeJSON = APIFetch.getPokemon(getApplicationContext(), dexnum);
                    Pokemon currentPokemon = parsePokemon(pokeJSON, dexnum);
                    batch.add(currentPokemon);
                    batchSoFar++;
                    if (batchSoFar >= batchSize || (batchSoFar >= incompletePokemonNum - i)) {
                        final int finalBatchSize = batchSoFar;
                        //load list of pokemon pokemon downloaded so far into database
                        db.pokeDao().updateMultiple(batch);
                        for (int j = 0; j < finalBatchSize; j++) {
                            Pokemon pokeForArray = batch.get(j);
                            pokemonArrayList.set(pokeForArray.getDexnum() - 1, pokeForArray);
                        }
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        String.format("%d full Pokemon cached", finalBatchSize),
                                        Toast.LENGTH_LONG).show();

                                adapter.notifyDataSetChanged();

                            }
                        });
                        batchSoFar = 0;
                        batch.clear();

                        //force viewholders to update with types? todo
                    }
                    apiCallsLastMinute++;
                    if (apiCallsLastMinute >= maxAPIcalls) {


                        //pause for 60 seconds due to API constraints
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        String.format("%d API Calls, waiting 60 seconds", maxAPIcalls),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        startTime = SystemClock.elapsedRealtime();
                        while (elapsedSeconds <= 60) {
                            currentTime = SystemClock.elapsedRealtime();
                            elapsedSeconds = (currentTime - startTime) / 1000.0;
                        }
                        apiCallsLastMinute = 0; //resets counter to 0
                    }
                }
            }
            //END OF THREAD todo
        };
        new Thread(runnable).start();
    }

    public static String titleCase(String string) {
        if (string == null) {
            return null;
        }

        String firstLetter = string.substring(0, 1).toUpperCase();
        String latterHalf = string.substring(1);

        return (firstLetter + latterHalf);
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public Pokemon parsePokemon(JSONObject json, Integer dexnum) {
        String type1 = new String();
        String type2 = new String();
        String name = new String();
        String url = String.format("https://pokeapi.co/api/v2/pokemon/%d/", dexnum);
        String spriteURL = String.format("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%d.png", dexnum);
        try {
            name = json.getString("name");
            spriteURL = json.getJSONObject("sprites").getString("front_default");
            try {
                type1 = json.getJSONArray("types").getJSONObject(1).getJSONObject("type").getString("name");
                type2 = json.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
            } catch (Exception type) {
                type1 = json.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Pokemon pokemon = new Pokemon(name, url, dexnum);
        pokemon.setType1(type1);
        pokemon.setType2(type2);
        pokemon.setSpriteURL(spriteURL);
        return pokemon;
    }
}



