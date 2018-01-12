package com.pupukkaltim.monitoringbudget;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.txusballesteros.widgets.FitChart;

import java.util.List;

/**
 * Created by ROG-STRIX on 05/01/2018.
 */

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.MyViewHolder> {
    private Context mContext;
    private List<Graph> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public FitChart fitchart;
        public CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            title    = (TextView) view.findViewById(R.id.title);
            count    = (TextView) view.findViewById(R.id.count);
            fitchart = (FitChart) view.findViewById(R.id.fitChart);
            cardView = (CardView) view.findViewById(R.id.card_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("click","clickeed");
                }
            });
        }
    }
    public GraphAdapter(GraphFragment mContext, List<Graph> albumList) {
        this.mContext = mContext.getContext();
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.graph_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Graph album = albumList.get(position);
        holder.title.setText(album.getCommitedItemName());
        holder.count.setText(album.getPersentase() + "%");
        holder.fitchart.setValue(Integer.valueOf(album.getPersentase()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                final NavigationView navigationView = ((DashboardActivity) activity).navigationView;
                navigationView.getMenu().getItem(1).setChecked(true);

                ReportFragment fragment = new ReportFragment();
                Bundle arguments = new Bundle();
                arguments.putInt( "report_index" , position);
                fragment.setArguments(arguments);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            }
        });
        // loading album cover using Glide library
        // Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
