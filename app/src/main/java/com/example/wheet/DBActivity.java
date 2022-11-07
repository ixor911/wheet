package com.example.wheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DBActivity extends AppCompatActivity {
    private final Integer wheatPrice = 309;
    private Button addRecordButton, returnButton;
    private LinearLayout dataTableLayout, bestDataTableLayout;
    private TextView avgPriceText;


    //year, weight, price
    ArrayList<ArrayList<Long>> data = null;
    ArrayList<ArrayList<Long>> bestData = null;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmain_menu);

        addRecordButton = findViewById(R.id.DBAddButton);
        dataTableLayout = findViewById(R.id.dbMenuDataTable);
        bestDataTableLayout = findViewById(R.id.dbMenuBestDataTable);
        avgPriceText = findViewById(R.id.dbAvgPriceText);
        returnButton = findViewById(R.id.dbMenuReturnButton);

        data = readArrayListFromFile("wheat_db.txt");
        dataSort(data, 0, false);
        createTable(data, dataTableLayout, true);

        bestData = getRecordsByValues(data, 1, 25000000L);
        createTable(bestData, bestDataTableLayout);

        Long avgPrice = getAvgDataValue(data, 2);
        avgPriceText.setText(avgPriceText.getText() + " " + avgPrice);


        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_db_add_row);

                EditText yearUserText   = findViewById(R.id.dbAddMenuYearUserText),
                         weightUserText = findViewById(R.id.dbAddMenuWeightUserText);
                Button   saveButton     = findViewById(R.id.dbAddMenuSaveButton),
                         returnButton   = findViewById(R.id.dbAddMenuReturnButton);


                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!(yearUserText.getText().toString().equals("")) &&
                                !(weightUserText.getText().toString().equals(""))) {

                            try {
                                Long year = Long.parseLong(yearUserText.getText().toString()),
                                        weight = Long.parseLong(weightUserText.getText().toString()),
                                        price = weight * wheatPrice;

                                ArrayList<Long> record = new ArrayList<>();
                                record.add(year);
                                record.add(weight);
                                record.add(price);

                                data.add(record);
                                writeArrayListToFile("wheat_db.txt", data);

                                restartActivity();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Fields should be numbers", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "All fields should be done", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                returnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        yearUserText.clearComposingText();
                        weightUserText.clearComposingText();

                        restartActivity();
                    }
                });

            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivity(new Intent(DBActivity.this, MainActivity.class));
            }
        });

    }

        private void writeArrayListToFile(String fileName, ArrayList<ArrayList<Long>> data) {
            File path = getApplicationContext().getFilesDir();

            try {
                File file = new File(path, fileName);
                BufferedWriter bf = new BufferedWriter(new FileWriter(file));

                for (ArrayList<Long> record: data) {
                    bf.write(record.get(0) + "-" + record.get(1) + "-" + record.get(2));
                    bf.newLine();
                }

                bf.flush();
                bf.close();

                Toast.makeText(getApplicationContext(), "Wrote to file: " + fileName, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private ArrayList<ArrayList<Long>> readArrayListFromFile(String fileName) {
            File path = getApplicationContext().getFilesDir();
            ArrayList<ArrayList<Long>> data = new ArrayList<>();

            try {
                File file = new File(path, fileName);
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("-");

                    ArrayList<Long> record = new ArrayList<>();
                    record.add(Long.parseLong(parts[0]));
                    record.add(Long.parseLong(parts[1]));
                    record.add(Long.parseLong(parts[2]));

                    data.add(record);
                }

                br.close();
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

    private void dataSort(ArrayList<ArrayList<Long>> data, int colIndex) {
        boolean descending = true;
        dataSort(data, colIndex, descending);
    }
    private void dataSort(ArrayList<ArrayList<Long>> data, int colIndex, boolean descending) {
        if (descending) {
            Collections.sort(data, new Comparator<ArrayList<Long>>() {
                @Override
                public int compare(ArrayList<Long> a, ArrayList<Long> b) {
                    return b.get(colIndex).compareTo(a.get(colIndex));
                }
            });
        } else {
            Collections.sort(data, new Comparator<ArrayList<Long>>() {
                @Override
                public int compare(ArrayList<Long> a, ArrayList<Long> b) {
                    return a.get(colIndex).compareTo(b.get(colIndex));
                }
            });
        }
    }

    private ArrayList<ArrayList<Long>> getRecordsByValues(
            ArrayList<ArrayList<Long>> data, int colIndex, Long value) {
        return getRecordsByValues(data, colIndex, value, true);
    }
    private ArrayList<ArrayList<Long>> getRecordsByValues(
            ArrayList<ArrayList<Long>> data, int colIndex, Long value, boolean bigger) {

        ArrayList<ArrayList<Long>> bestData = new ArrayList<>();

        for(ArrayList<Long> record: data) {
            if (bigger) {
                if (record.get(colIndex) > value)
                    bestData.add(record);

            } else {
                if (record.get(colIndex) < value)
                    bestData.add(record);
            }
        }

        return bestData;
    }

    private Long getAvgDataValue(ArrayList<ArrayList<Long>> data, int colIndex) {
        Long avg = 0L;

        if (data.size() > 0) {
            for(ArrayList<Long> record: data) {
                avg += record.get(colIndex);
            }

            avg /= data.size();
        }

        return avg;
    }

    private void createTable(ArrayList<ArrayList<Long>> data, LinearLayout layout) {
        createTable(data, layout, false);
    }
    @SuppressLint("SetTextI18n")
    private void createTable(ArrayList<ArrayList<Long>> data, LinearLayout layout, boolean setDelButton) {

        int length = layout.getChildCount();
        for (int i = 1; i < length; i++) {
            layout.removeViewAt(1);
        }

        int i = 1;
        for(ArrayList<Long> record: data) {
            LinearLayout viewRecordLayout = new LinearLayout(this);
            viewRecordLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView viewYearText = new TextView(this);
            viewYearText.setText(record.get(0).toString());
            setTextViewParams(viewYearText);

            TextView viewWeightText = new TextView(this);
            viewWeightText.setText(record.get(1).toString());
            setTextViewParams(viewWeightText);

            TextView viewPriceText = new TextView(this);
            viewPriceText.setText(record.get(2).toString());
            setTextViewParams(viewPriceText);



            viewRecordLayout.addView(viewYearText, 0);
            viewRecordLayout.addView(viewWeightText, 1);
            viewRecordLayout.addView(viewPriceText, 2);

            if(setDelButton) {
                Button delButton = new Button(this);
                delButton.setId(i);
                setDelButtonSettings(delButton);

                viewRecordLayout.addView(delButton, 3);
            }

            layout.addView(viewRecordLayout, i);

            i++;
        }

    }

    private void setTextViewParams(TextView view) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        view.setLayoutParams(param);
        view.setTextSize(20);
        view.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
    }

    @SuppressLint("SetTextI18n")
    private void setDelButtonSettings(Button delButton) {
        delButton.setAllCaps(false);
        delButton.setText("Delete");

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove((int)delButton.getId() - 1);
                writeArrayListToFile("wheat_db.txt", data);

                restartActivity();
            }
        });
    }
    private void restartActivity() {
        finish();
        Intent intent = new Intent(DBActivity.this, DBActivity.class);
        startActivity(intent);
    }

    private void switchActivity(Intent intent) {
        try {
            finish();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Cant switch activity", Toast.LENGTH_LONG).show();
        }
    }

}