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

public class ConfigActivity extends AppCompatActivity {

    MaterialToolbar idTopAppBar;
    DrawerLayout idDrawer;
    NavigationView idNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);

        idTopAppBar = findViewById(R.id.idConfigTopAppBar);

        idDrawer = findViewById(R.id.idDrawer);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ConfigActivity.this, idDrawer, idTopAppBar, R.string.open_drawer, R.string.close_drawer);
        idDrawer.addDrawerListener(toggle);

        toggle.syncState();
    }
}