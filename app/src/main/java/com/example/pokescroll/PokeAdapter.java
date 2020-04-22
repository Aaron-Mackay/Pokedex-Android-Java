package com.example.pokescroll;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class PokeAdapter extends
        RecyclerView.Adapter<PokeAdapter.ViewHolder> {

    static String SpriteURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%d.png"; //to fetch sprite for picasso

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView iconImageView;
        public ImageView type1ImageView;
        public ImageView type2ImageView;
        RelativeLayout parentLayout;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.firstLine);
            iconImageView = (ImageView) itemView.findViewById(R.id.icon);
            type1ImageView = (ImageView) itemView.findViewById(R.id.type1);
            type2ImageView = (ImageView) itemView.findViewById(R.id.type2);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    private List<Pokemon> mPokemonList;
    private Context mContext;
    private Map<String, Integer> mTypeIDMap;

    // Pass in the contact array into the constructor
    public PokeAdapter(Context context, List<Pokemon> pokemonList, Map<String, Integer> typeIDMap) {
        mPokemonList = pokemonList;
        mContext = context;
        mTypeIDMap = typeIDMap;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PokeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.rv_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PokeAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Pokemon pokemon = mPokemonList.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(MainActivity.titleCase(pokemon.getName()));

        ImageView sprite = viewHolder.iconImageView;
        Glide.with(mContext)
                .load(String.format(SpriteURL, position + 1))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(sprite);

        String type1 = pokemon.getType1();
        String type2 = pokemon.getType2();
        ImageView type1pic = viewHolder.type1ImageView;
        ImageView type2pic = viewHolder.type2ImageView;
        int unknownID = mTypeIDMap.get("unknown");

        if (type1 != null) {
            int type1id = mTypeIDMap.get(type1);
            type1pic.setImageResource(type1id);

            if (!type2.isEmpty()) {
                int type2id = mTypeIDMap.get(type2);
                type2pic.setImageResource(type2id);
            } else {
                type2pic.setImageDrawable(null);
            }
        } else {
            type1pic.setImageResource(unknownID);
            type2pic.setImageDrawable(null);
        }
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pokemon clickedPokemon = mPokemonList.get(position);
                Log.d("onclick", "onClick: clicked on: " + mPokemonList.get(position).getName());
                Toast.makeText(mContext, clickedPokemon.getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, PokemonActivity.class);

                //Convert Pokemon object to JSON string for intent extra
                Gson gson = new Gson();
                String json = gson.toJson(clickedPokemon);
                intent.putExtra("clickedPokemonJson", json);
                mContext.startActivity(intent);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPokemonList.size();
    }
}
