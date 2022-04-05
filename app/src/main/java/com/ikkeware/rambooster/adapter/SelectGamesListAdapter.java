package com.ikkeware.rambooster.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikkeware.rambooster.model.AppDetail;
import com.ikkeware.rambooster.R;
import com.ikkeware.rambooster.onListChanged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectGamesListAdapter extends RecyclerView.Adapter<SelectGamesListAdapter.MyViewHolder> implements Filterable {
    public List<AppDetail> appDetails;
    public List<AppDetail> appDetailsFiltered;
    public List<String> gamesToAddList = new ArrayList<>();
    private HashMap<String, Boolean> checkedSwitchButtons = new HashMap<>();
    public List<AppDetail> test = new ArrayList<>();
    Context context;
    private onListChanged listChangedCallback;


    public SelectGamesListAdapter(List<AppDetail> appDetails, Context context, onListChanged listChangedCallback) {

        this.appDetails = appDetails;
        test = appDetails;
        this.context = context;
        appDetailsFiltered = new ArrayList<>(appDetails);
        this.listChangedCallback = listChangedCallback;

    }


    @Override
    public Filter getFilter() {
        return new FilterGames();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtAppNameSelect;
        ImageView imgAppIconSelect;
        Switch swtSelectGame;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAppNameSelect = itemView.findViewById(R.id.txtAppNameSelect);
            imgAppIconSelect = itemView.findViewById(R.id.imgAppIconSelect);
            swtSelectGame = itemView.findViewById(R.id.swtSelectGame);
        }

        public MyViewHolder(@NonNull View itemView, boolean s) {
            super(itemView);
            txtAppNameSelect = itemView.findViewById(R.id.txtAppNameSelect);
            imgAppIconSelect = itemView.findViewById(R.id.imgAppIconSelect);
            swtSelectGame = itemView.findViewById(R.id.swtSelectGame);
        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout row = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_select_row, parent, false);

        return new MyViewHolder(row);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.txtAppNameSelect.setText(appDetailsFiltered.get(position).getAppName());
        holder.imgAppIconSelect.setImageDrawable(appDetailsFiltered.get(position).getAppIconImage());

        if (checkedSwitchButtons.containsKey(appDetailsFiltered.get(position).getPackName())) {
            holder.swtSelectGame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                }
            });

            holder.swtSelectGame.setChecked(checkedSwitchButtons.get(appDetailsFiltered.get(position).getPackName()));
        }

        holder.swtSelectGame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    gamesToAddList.add(appDetailsFiltered.get(position).getPackName());
                    checkedSwitchButtons.put(appDetailsFiltered.get(position).getPackName(), true);
                } else {
                    //Remove the item from the list if switch disabled
                    if (!gamesToAddList.isEmpty()) {
                        gamesToAddList.remove(appDetailsFiltered.get(position).getPackName());
                        checkedSwitchButtons.put(appDetailsFiltered.get(position).getPackName(), false);
                    }
                }
                listChangedCallback.onListItemsChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return appDetailsFiltered.size();
    }

    public List<String> getGamesToAdd() {
        return gamesToAddList;
    }


    private void refreshAppsList(){
        notifyDataSetChanged();
    }



    class FilterGames extends Filter {

        String lowerCaseTxt="";

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            appDetailsFiltered.clear();

            if (charSequence != null) {
                //Can search by Title, Author, Profile Name
                for (int i=0; i < appDetails.size(); i++){


                    Log.d("FILTER",appDetails.get(i).getAppName());
                    lowerCaseTxt=appDetails.get(i).getAppName().toLowerCase();

                    if(lowerCaseTxt.contains(charSequence.toString().toLowerCase())){
                        appDetailsFiltered.add(new AppDetail(appDetails.get(i).getAppName(),appDetails.get(i).getAppIconImage(),appDetails.get(i).getPackName()));
                    }


                }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = appDetailsFiltered;
                    filterResults.count = appDetailsFiltered.size();
                    return filterResults;
                }
            else {
                new FilterResults();
                appDetailsFiltered=new ArrayList<>(appDetails);
                refreshAppsList();
            }

            return null;
        }



        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            refreshAppsList();
        }

    }
}
