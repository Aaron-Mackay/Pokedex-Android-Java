package com.example.pokescroll;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;

@Entity(tableName = "pokedexDB")
public class Pokemon {
    @PrimaryKey
    private int dexnum;

    private String name;
    private String url;
    private String type1;
    private String type2;
    private String spriteURL;
    //private List typeList;



    //constructor
    public Pokemon(String name, String url, Integer dexnum) {//, List typeList) {
        this.name = name;
        this.url = url;
        this.dexnum = dexnum;
        //this.typeList = typeList;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setURL(String url)
    {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public String getSpriteURL() {
        return spriteURL;
    }

    public int getDexnum() {
        return dexnum;
    }

    public void setType1(String type1) {
        //if (typeList.contains(type1)){
            this.type1 = type1;
        //} else {
        //    Log.e("Type", "Failed to assign type1, not in array");
        //}
    }

    public void setType2(String type2) {
       // if (typeList.contains(type2)){
            this.type2 = type2;
       // } else {
         //   Log.e("Type", "Failed to assign type2, not in array");
        //}
    }

    public void setSpriteURL(String spriteURL) {
        this.spriteURL = spriteURL;
    }

    public void setDexnum(int dexnum) {
        this.dexnum = dexnum;
    }
}
