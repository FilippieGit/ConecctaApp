package com.example.cardstackview;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity {
    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        idTopAppBar = findViewById(R.id.idTopAppBar);

        idDrawer = findViewById(R.id.idDrawer);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(DrawerActivity.this, idDrawer, idTopAppBar, R.string.open_drawer, R.string.close_drawer);
        idDrawer.addDrawerListener(toggle);

        toggle.syncState();
    }
}