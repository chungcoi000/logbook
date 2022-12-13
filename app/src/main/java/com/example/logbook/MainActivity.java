package com.example.logbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  EditText inputImageURL;
  ArrayList<Entity> arrayImageURL;
  Button addBtn;
  ImageButton nextBtn, prevBtn;
  TextView tv;
  ImageView imageView;
  int index = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    inputImageURL = findViewById(R.id.imageURL);
    tv = findViewById(R.id.tv_size);
    imageView = findViewById(R.id.imageView);
    addBtn = findViewById(R.id.addBtn);
    nextBtn = findViewById(R.id.nextBtn);
    prevBtn = findViewById(R.id.prevBtn);

    loadData(0);

    addBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String image = inputImageURL.getText().toString();
        if (TextUtils.isEmpty(image)) {
          inputImageURL.setError("You need to input image URL before add!");
          return;
        }
        if (!image.contains("https") || !image.contains("http")) {
          inputImageURL.setError("You need to input right image URL!");
          return;
        }
        saveData(image);
        inputImageURL.setText("");
        Toast.makeText(MainActivity.this, "Save image url successful", Toast.LENGTH_SHORT).show();
      }
    });


    nextBtn.setOnClickListener(view -> {
      if (index == arrayImageURL.size() - 1) {
        tv.setText("1/" + arrayImageURL.size());
        loadData(0);
        index = 0;
      } else {
        int currentPic = index + 1;
        tv.setText(currentPic + "/" + arrayImageURL.size());
        loadData(++index);
      }
    });

    prevBtn.setOnClickListener(view -> {
      if (index == 0) {
        tv.setText(arrayImageURL.size() + "/" + arrayImageURL.size());
        index = arrayImageURL.size() - 1;
        loadData(index);
      } else {
        int currentPic = index;
        tv.setText(currentPic + "/" + arrayImageURL.size());
        loadData(--index);
      }
    });
  }

  private void loadData(int addIndex) {
    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("TEST", MODE_PRIVATE);
    Gson gson = new Gson();
    String json = sharedPreferences.getString("image_url", null);

    Type type = new TypeToken<ArrayList<Entity>>() {
    }.getType();

    arrayImageURL = gson.fromJson(json, type);
    if (arrayImageURL == null) {
      arrayImageURL = new ArrayList<>();
      tv.setText("0/0");
    } else if (addIndex != 0) {
      index = addIndex;
      int current = addIndex + 1;
      tv.setText(current + "/" + arrayImageURL.size());
      Picasso
        .with(getApplicationContext())
        .load(arrayImageURL.get(addIndex).imageURL)
        .resize(320, 400)
        .placeholder(R.drawable.progress_animation)
        .into(imageView);
    } else {
      index = 0;
      tv.setText("1/" + arrayImageURL.size());
      Picasso
        .with(getApplicationContext())
        .load(arrayImageURL.get(0).imageURL)
        .resize(320, 400)
        .placeholder(R.drawable.progress_animation)
        .into(imageView);
    }
  }

  private void saveData(String imageUrl) {
    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("TEST", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    Gson gson = new Gson();
    arrayImageURL.add(new Entity(imageUrl));
    String json = gson.toJson(arrayImageURL);
    editor.putString("image_url", json);
    editor.apply();
    loadData(arrayImageURL.size() - 1);
  }
}