package com.androidakbar.dz_6_1_2_and;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String SHARE_NAME = "notes";
    private static final String SHARE_KEY = "note_item";
    private final static String INDEX_KEY = "IndexDel";
    private SharedPreferences shpNote;
    private List<Map<String, String>> content;
    private SimpleAdapter simAdapter;
    private SwipeRefreshLayout swrList;

    private ArrayList<Integer> indexDel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar appToolbar = findViewById(R.id.app_toolbar);
        appToolbar.setTitle(R.string.name_dz);
        appToolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

        InitSharedPreferences();
        content = new ArrayList<>();
        setContent();

        String[] from = new String[]{"description", "num_letters"};
        int[] to = new int[]{R.id.txt_description, R.id.txt_num_letters};
        simAdapter = new SimpleAdapter(this, content, R.layout.list_item, from, to);

        ListView lstView = findViewById(R.id.lst_view);

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                content.remove(i);
                simAdapter.notifyDataSetChanged();
                indexDel.add(i);
            }
        });

        swrList = findViewById(R.id.swr_refresh_list);

        swrList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                content.clear();
                indexDel.clear();
                setContent();
                simAdapter.notifyDataSetChanged();
                swrList.setRefreshing(false);
            }
        });

        lstView.setAdapter(simAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(INDEX_KEY, indexDel);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        indexDel = savedInstanceState.getIntegerArrayList(INDEX_KEY);
        for(int i : indexDel) {
            content.remove(i);
        }
        simAdapter.notifyDataSetChanged();
    }

    private void InitSharedPreferences() {
        shpNote = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        List<Map<String, String>> content = new ArrayList<>();
        String[] txt = getResources().getStringArray(R.array.sa_description);
        Set<String> txtSet = shpNote.getStringSet(SHARE_KEY, new HashSet<String>());

        if(txtSet != null && txtSet.size() == 0) {
            Collections.addAll(txtSet, txt);
            shpNote.edit().putStringSet(SHARE_KEY, txtSet).apply();
        }

    }

    private void setContent() {
        Set<String> txtSet = shpNote.getStringSet(SHARE_KEY, new HashSet<String>());
        if(txtSet != null) {
            for (String s : txtSet) {
                Map<String, String> iRow = new HashMap<>();
                iRow.put("description", s);
                iRow.put("num_letters", String.valueOf(s.length()));
                content.add(iRow);
            }
        }
    }


}