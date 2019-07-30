package com.example.team_project.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.example.team_project.R;
import com.example.team_project.model.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    CalendarAdapter mAdapter;
    String splitindicator = "\\(\\)";
    private Drawable icon;
    private final ColorDrawable background;


    public SwipeToDeleteCallback(CalendarAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(mAdapter.context,
                R.drawable.ic_delete_white_36);
        background = new ColorDrawable(Color.parseColor("#B71C1C"));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int position = viewHolder.getAdapterPosition();

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);

        ArrayList<String> rvEvents = mAdapter.events;
        String eventToDelete = rvEvents.get(position);

        // delete the event in the parse database
        for (int x = 0; x < parseevents.size(); x++) {
            if (eventToDelete.equals(parseevents.get(x).split(splitindicator)[3])) {
                parseevents.remove(x);
                user.put(User.KEY_ADDED_EVENTS, parseevents);
            }
        }

        if (eventToDelete != "NONE!"){
            mAdapter.deleteItem(position);
        }
        user.saveInBackground();
        mAdapter.notifyDataSetChanged();
    }

    // to enable the garbage image and color to be present when event is swiped
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft+ icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        icon.draw(c);
    }
}


