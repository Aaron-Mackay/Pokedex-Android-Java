package com.example.pokescroll;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Pokemon.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PokeDao pokeDao();
}
