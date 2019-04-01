package ru.relastic.meet010room;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final ArrayList<Bundle> data;

    MyAdapter(ArrayList<Bundle> mData) {
        data = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_linear_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.textViewNum.setText(String.valueOf(data.get(i).getInt(DBManager.FIELD_POS)));
        myViewHolder.textViewBreef.setText(data.get(i).getString(DBManager.FIELD_NOTE));
        myViewHolder.mButtonOpenById.setText("Open");
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getIdByPosition(int position){
        return data.get(position).getInt(DBManager.FIELD_ID);
    }



    class MyViewHolder extends  RecyclerView.ViewHolder {
        public TextView textViewNum=null;
        public TextView textViewBreef=null;
        public  Button mButtonOpenById=null;
        MainActivity activity = null;

        public MyViewHolder(View itemView){
            super(itemView);
            textViewNum = itemView.findViewById(R.id.textView1);
            textViewBreef = itemView.findViewById(R.id.textView2);
            mButtonOpenById = itemView.findViewById(R.id.button);
            activity = (MainActivity)itemView.getContext();

            if (mButtonOpenById!=null) {
                mButtonOpenById.setOnClickListener( new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        activity.openNotes(getIdByPosition(MyViewHolder.this.getAdapterPosition()));
                    }
                });
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.selectPosition(getIdByPosition(MyViewHolder.this.getAdapterPosition()));
                }
            });
        }
    }
}
