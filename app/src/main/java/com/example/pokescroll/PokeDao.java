package com.example.pokescroll;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PokeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSingle(Pokemon pokemon);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMultiple(List<Pokemon> pokemonList);

    @Update
    public void update(Pokemon pokemon);

    @Update
    public void updateMultiple(List<Pokemon> pokemonList);

    @Delete
    public void delete(Pokemon pokemon);

    @Query("SELECT * FROM pokedexDB WHERE dexnum LIKE :dexnum LIMIT 1")
    Pokemon loadPokemon(Integer dexnum);

    @Query("SELECT * FROM pokedexDB")
    List<Pokemon> loadAllPokemon();

    @Query("SELECT * FROM pokedexDB WHERE type1 is null")
    List<Pokemon> loadAllIncompletePokemon();
}
