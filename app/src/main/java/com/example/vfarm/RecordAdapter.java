package com.example.vfarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    private ArrayList<Record> mRecordList;
    public static class RecordViewHolder extends RecyclerView.ViewHolder{

        public TextView mID;
        public TextView CMD;
        public TextView Address;
        public TextView StratDT;
        public TextView NAME;


        public RecordViewHolder(View itemView){
            super(itemView);
            mID = itemView.findViewById(R.id.adp_ID);
            CMD = itemView.findViewById(R.id.adp_CMD);
            Address = itemView.findViewById(R.id.adp_Address);
            StratDT = itemView.findViewById(R.id.adp_StartDT);
            NAME = itemView.findViewById(R.id.adp_NAME);
        }
    }

    public RecordAdapter(ArrayList<Record> RecordList){
        mRecordList = RecordList;
    }
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_record, parent, false);
        RecordViewHolder rvh = new RecordViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = mRecordList.get(position);

        holder.mID.setText(Integer.toString(position));
        holder.CMD.setText(record.getCMD());
        holder.Address.setText(record.getADDRESS());
        holder.StratDT.setText(record.getSTART_TIME());
        holder.NAME.setText(record.getNAME());

    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }
}
