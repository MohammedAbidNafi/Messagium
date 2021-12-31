package com.margsapp.messageium.Widgets;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messageium.Model.Chat;
import com.margsapp.messageium.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {





    Context context;
    Intent intent;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    List<Chat> chats = new ArrayList<>();
    private void initializeData() throws NullPointerException {


        try {
            chats.clear();

            databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        assert firebaseUser != null;
                        if(chat.getReceiver().equals(firebaseUser.getUid())) {
                            chats.add(chat);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        initializeData();

    }

    @Override
    public void onDataSetChanged() {
        initializeData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("TAG", "Total count is " + chats.size());
        return chats.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_unread_item);
        remoteViews.setTextViewText(R.id.username, chats.get(position).getSendername());
        remoteViews.setTextViewText(R.id.message,chats.get(position).getMessage());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
