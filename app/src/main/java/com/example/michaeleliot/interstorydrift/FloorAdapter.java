package com.example.michaeleliot.interstorydrift;

/**
 * Created by michaeleliot on 1/31/18.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.FloorAdapterViewHolder> {
    private Context mContext;

    private List<Floor> mFloorData;

    private final FloorAdapterOnClickHandler mClickHandler;

    public interface FloorAdapterOnClickHandler {
        void onClick(Floor floor);
    }

    public FloorAdapter(FloorAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    public class FloorAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mFloorNumberView;
        public final TextView mFloorNameView;
        public final TextView mXFloorSwayView;
        public final TextView mZFloorSwayView;


        public FloorAdapterViewHolder(View view) {
            super(view);
            mFloorNumberView = view.findViewById(R.id.floor_number);
            mFloorNameView =  view.findViewById(R.id.floor_name);
            mXFloorSwayView = view.findViewById(R.id.Xsway);
            mZFloorSwayView = view.findViewById(R.id.Zsway);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Floor floor = mFloorData.get(adapterPosition);
            mClickHandler.onClick(floor);
        }
    }

    @Override
    public FloorAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.floor_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FloorAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FloorAdapterViewHolder floorAdapterViewHolder, int position) {
        Floor selectedFloor = mFloorData.get(position);
        double xsway = selectedFloor.getXSway();
        double zsway = selectedFloor.getZSway();
        if (xsway < 6 & xsway > -6 | zsway < 6 & zsway > -6) {
            floorAdapterViewHolder.itemView.setBackgroundColor(Color.GREEN);
        } else if (xsway < 11 & xsway > -11 | zsway < 11 & zsway > -11) {
            floorAdapterViewHolder.itemView.setBackgroundColor(Color.YELLOW);
        } else {
            floorAdapterViewHolder.itemView.setBackgroundColor(Color.RED);
        }

        floorAdapterViewHolder.mFloorNumberView.setText("Floor " + Integer.toString(selectedFloor.getFloorNumber()));
        floorAdapterViewHolder.mFloorNameView.setText(selectedFloor.getFloorName());
        floorAdapterViewHolder.mXFloorSwayView.setText("X Sway: " + String.format("%f", xsway));
        floorAdapterViewHolder.mZFloorSwayView.setText("Z Sway: " + String.format("%f", zsway));


    }

    @Override
    public int getItemCount() {
        if (null == mFloorData) return 0;
        return mFloorData.size();
    }

    public void setFloorData(List<Floor> floorData) {
        mFloorData = floorData;
        notifyDataSetChanged();
    }
}
