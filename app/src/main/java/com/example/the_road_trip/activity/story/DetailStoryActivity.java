package com.example.the_road_trip.activity.story;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.the_road_trip.R;
import com.example.the_road_trip.activity.MainActivity;
import com.example.the_road_trip.model.Story.Story;

import java.util.List;

public class DetailStoryActivity extends AppCompatActivity {
    private TextView btnClose, txtName, txtTime, txtTitle;
    private ImageView imgStory, imgAuthor;
    private static int SWIPE_THRESHOLD = 70;
    private static int SWIPE_VELOCITY_THRESHOLD = 70;
    private GestureDetector myGesture;
    private List<Story> list;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_story);
        initUI();
        if (getIntent().hasExtra("LIST_STORY")) {
            list = (List<Story>) getIntent().getExtras().get("LIST_STORY");
            uid = (String) getIntent().getExtras().get("ID_STORY");
            int index = getIndexStory();
            Story story = list.get(index);
            displayStory(story);
            myGesture = new GestureDetector(this, new MyGesture());
            imgStory.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    myGesture.onTouchEvent(motionEvent);
                    return true;
                }
            });
        } else {
            Toast.makeText(this, "Error with this story, please do again!",
                    Toast.LENGTH_SHORT);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DetailStoryActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }
        btnClose.setOnClickListener(view -> {
            Intent intent = new Intent(DetailStoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void initUI() {
        btnClose = findViewById(R.id.close_detail_story);
        txtName = findViewById(R.id.detail_story_name);
        txtTime = findViewById(R.id.time_detail_story);
        imgAuthor = findViewById(R.id.img_author_story_detail);
        imgStory = findViewById(R.id.image_detail_story);
        txtTitle = findViewById(R.id.detail_story_title);
    }

    private void displayStory(Story story) {
        txtName.setText(story.getTitle());
        txtTime.setText(story.getTime_created() + " h ago");
        txtTitle.setText(story.getTitle());
        Glide.with(this).load(story.getImage())
                .into(imgStory);
        Glide.with(this).load(story.getUserId().getAvatar_url())
                .into(imgAuthor);
    }

    class MyGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX() - e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                Story story = getPrevious();
                displayStory(story);
            }
            if (e1.getX() - e2.getX() > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                Story story = getNext();
                displayStory(story);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public Story getNext() {
        int idx = getIndexStory();
        if (idx < 0 || idx + 1 == list.size()){
            uid = list.get(0).get_id();
            return list.get(0);
        };
        uid = list.get(idx + 1).get_id();
        return list.get(idx + 1);
    }

    public Story getPrevious() {
        int idx = getIndexStory();
        if (idx <= 0) {
            uid = list.get(list.size() - 1).get_id();
            return list.get(list.size() - 1);
        };
        uid = list.get(idx - 1).get_id();
        return list.get(idx - 1);
    }

    private int getIndexStory() {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get_id().equals(uid)) {
                index = i;
                break;
            }
        }
        return index;
    }
}