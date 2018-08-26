package com.example.michaeleliot.interstorydrift;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michaeleliot.interstorydrift.FloorAdapter.FloorAdapterOnClickHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.xml.xpath.XPath;

// COMPLETED (8) Implement ForecastAdapterOnClickHandler from the MainActivity
public class MainActivity extends AppCompatActivity implements FloorAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private FloorAdapter mFloorAdapter;
    private ArrayList<Floor> floorData;
    private ArrayList<Path> mXPaths;
    private ArrayList<Path> mZPaths;
    private ArrayList<Integer> mXPath_offsets;
    private ArrayList<Integer> mZPath_offsets;
    private TextView mErrorMessageDisplay;
    private Canvas mCanvas;
    private Paint mPaint;
    private ChildEventListener mChildListener;


    private ProgressBar mLoadingIndicator;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("FloorData");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = findViewById(R.id.recyclerview_floors);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mFloorAdapter = new FloorAdapter(this, MainActivity.this);

        mRecyclerView.setAdapter(mFloorAdapter);

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                Floor newFloor = dataSnapshot.getValue(Floor.class);
//                if (newFloor.getFloorNumber() > floorData.size()) {
//                    floorData.add(newFloor);
//                    mFloorAdapter.setFloorData(floorData);
//                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Floor updatedFloor = dataSnapshot.getValue(Floor.class);
                Floor floor = floorData.get(updatedFloor.getFloorNumber() - 1);
                floor.setXSway(updatedFloor.getXSway());
                translate_floor(floor, updatedFloor.getXSway(), updatedFloor.getYSway(), updatedFloor.getZSway());
                mCanvas.drawColor(Color.LTGRAY);
                for (int index = 0; index < mXPaths.size(); index++) {
                    paint_path(mXPaths.get(index), floorData.get(index).getXSway());
                    paint_path(mZPaths.get(index), floorData.get(index).getZSway());
                }
                mFloorAdapter.setFloorData(floorData);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addChildEventListener(mChildListener);

        Intent response = getIntent();
        if (response.hasExtra(Intent.EXTRA_TEXT)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Floor>>(){}.getType();
            String floor_name = response.getStringExtra(Intent.EXTRA_TEXT);
            floorData = gson.fromJson(response.getStringExtra("floor_data"), type);;

            addNewFloor(floor_name);


        } else {
            floorData = new ArrayList<>();
            floorData.add(new Floor(1, "Lobby", 0, 0, 0));
            floorData.add(new Floor(2, "Business", 0, 0, 0));
            floorData.add(new Floor(3, "Executive", 0, 0, 0));
        }


            LinearLayout linearLayout = findViewById(R.id.building_view);
            final ImageView rectangle_view = new ImageView(this);
            linearLayout.addView(rectangle_view);
            Bitmap bitmap = Bitmap.createBitmap(
                    2000, // Width
                    1000, // Height
                    Bitmap.Config.ARGB_8888 // Config
            );


            mCanvas = new Canvas(bitmap);

            // Draw a solid color to the canvas background
            mCanvas.drawColor(Color.LTGRAY);

            // Initialize a new Paint instance to draw the Rectangle
            mPaint = new Paint();
            // Set a pixels value to padding around the rectangle
            mXPaths = new ArrayList<>();
            mZPaths = new ArrayList<>();
            mXPath_offsets = new ArrayList<>();
            mZPath_offsets = new ArrayList<>();


            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.GREEN);
            mPaint.setStrokeWidth(20);

            int shift_x = 350;
            int shift_y = 600;

            Path Xpath = makeXPath(shift_x, shift_y);
            Path Zpath = makeZPath(shift_x, shift_y);


            Matrix matrix = new Matrix();
            for (int i = 0; i < floorData.size(); i++) {
                Path path_copy = new Path(Xpath);

                //x axis
                matrix.setTranslate(0, -130 * i);
                path_copy.transform(matrix);
                mXPaths.add(path_copy);
                paint_path(path_copy, 0);

                mXPath_offsets.add(0);
            }

            for (int i = 0; i < floorData.size(); i++) {
                Path path_copy = new Path(Zpath);

                //z axis
                matrix.setTranslate(800, -130 * i);
                path_copy.transform(matrix);
                mZPaths.add(path_copy);
                paint_path(path_copy, 0);

                mZPath_offsets.add(0);
            }

        // Display the newly created bitmap on app interface
            rectangle_view.setImageBitmap(bitmap);
            myRef.setValue(floorData);
            mFloorAdapter.setFloorData(floorData);

    }

    private void paint_path(Path path, double sway) {
        if (sway < 0.006) {
            mPaint.setColor(Color.GREEN);
        } else if (sway < 0.006 & sway > 0.012) {
            mPaint.setColor(Color.YELLOW);
        } else {
            mPaint.setColor(Color.RED);
        }

        mPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawPath(path, mPaint);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawPath(path, mPaint);

    }

    private void addNewFloor(String floor_name) {
        Floor newFloor = new Floor(floorData.size() + 1, floor_name, 0, 0, 0);
        floorData.add(newFloor);
        myRef.setValue(floorData);
        mFloorAdapter.setFloorData(floorData);
    }

    private Path makeXPath(int shift_x, int shift_y){
        Path Xpath = new Path();
        Xpath.moveTo(0 + shift_x, 0 + shift_y); // Top
        Xpath.lineTo(0 + shift_x, 100 + shift_y); // Left
        Xpath.lineTo(300 + shift_x, 100 + shift_y); // Bottom
        Xpath.lineTo(300 + shift_x, 0 + shift_y); // Right
        Xpath.lineTo(0 + shift_x, 0 + shift_y); // Back to Top

        Xpath.moveTo(0 + shift_x, 0 + shift_y); // Top
        Xpath.lineTo(25 + shift_x, -50 + shift_y); // Top
        Xpath.lineTo(325 + shift_x, -50 + shift_y); // Top
        Xpath.lineTo(300 + shift_x, 0 + shift_y); // Bottom

        Xpath.moveTo(325 + shift_x, -50 + shift_y); // Top
        Xpath.lineTo(325 + shift_x, 50 + shift_y); // Top
        Xpath.lineTo(300 + shift_x, 100 + shift_y); // Bottom
        return Xpath;

    }
    private Path makeZPath(int shift_x, int shift_y){
        Path Zpath = new Path();
        Zpath.moveTo(0 + shift_x, 0 + shift_y); // Top
        Zpath.lineTo(0 + shift_x, 100 + shift_y); // Left
        Zpath.lineTo(150 + shift_x, 100 + shift_y); // Bottom
        Zpath.lineTo(150 + shift_x, 0 + shift_y); // Right
        Zpath.lineTo(0 + shift_x, 0 + shift_y); // Back to Top

        Zpath.moveTo(0 + shift_x, 0 + shift_y); // Top
        Zpath.lineTo(25 + shift_x, -250 + shift_y); // Top
        Zpath.lineTo(175 + shift_x, -250 + shift_y); // Top
        Zpath.lineTo(150 + shift_x, 0 + shift_y); // Bottom

        Zpath.moveTo(175 + shift_x, -250 + shift_y); // Top
        Zpath.lineTo(175 + shift_x, -200 + shift_y); // Top
        Zpath.lineTo(150 + shift_x, 100 + shift_y); // Bottom
        return Zpath;

    }

    private void translate_floor(Floor floor, double xsway, double ysway, double zsway) {
        int floor_number = floor.getFloorNumber();

        Path Xpath = mXPaths.get(floor_number - 1);
        int Xoffset = (int) xsway - mXPath_offsets.get(floor_number - 1);
        Xpath.offset(Xoffset * 10, 0);
        mXPath_offsets.set(floor_number - 1, (int) xsway);
        mXPaths.set(floor_number - 1, Xpath);


        Path Zpath = mZPaths.get(floor_number - 1);
        int Zoffset = (int) zsway - mZPath_offsets.get(floor_number - 1);
        Zpath.offset(10 * Zoffset, 0);
        mZPath_offsets.set(floor_number - 1, (int) zsway);
        mZPaths.set(floor_number - 1, Zpath);
    }


    private void simulateShift() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream earthquake = getApplicationContext().getAssets().open("Data_ElCentro250.txt");
                    BufferedReader earthquakeReader = new BufferedReader(new InputStreamReader(earthquake));
                    String line;
                    int currentHeader = 0;
                    int numberofHeaders = 6;
                    while (currentHeader < numberofHeaders) {
                        line = earthquakeReader.readLine();
                        currentHeader++;
                    }
                    while ((line = earthquakeReader.readLine()) != null ) {
                        String[] rowValues = line.split("\t");
                        double time = Double.parseDouble(rowValues[0]); //not used, but left to reference why we only get 1,2, and 3
                        double firstFloorSway = Double.parseDouble(rowValues[1]);
                        double secondFloorSway = Double.parseDouble(rowValues[2]);
                        double thirdFloorSway = Double.parseDouble(rowValues[3]);

                        floorData.get(0).setXSway(firstFloorSway);
                        floorData.get(1).setXSway(secondFloorSway);
                        floorData.get(2).setXSway(thirdFloorSway);

                        myRef.setValue(floorData);
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading file");
                    e.printStackTrace();
                } catch (InterruptedException interupted) {
                    System.out.println("Time Delay Interupted");
                    interupted.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onClick(Floor currentFloor) {
        Context context = this;
        Toast.makeText(context, currentFloor.getFloorName(), Toast.LENGTH_SHORT)
                .show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_room) {
            myRef.removeEventListener(mChildListener);
            Intent intent = new Intent(MainActivity.this, FloorCreate.class);
            Gson gson = new Gson();
            String jsonFloors = gson.toJson(floorData);
            intent.putExtra("floor_data", jsonFloors);
            startActivity(intent);
            return true;
        } else if (id == R.id.simulate_earthquake) {
            simulateShift();
        }
        return super.onOptionsItemSelected(item);
    }
}
