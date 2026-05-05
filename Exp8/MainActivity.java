package com.example.foodiemenu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String food = "";

        if (id == R.id.action_pizza) food = "Pizza";
        else if (id == R.id.action_burger) food = "Burger";
        else if (id == R.id.action_pasta) food = "Pasta";
        else if (id == R.id.action_coke) food = "Coke";

        if (!food.isEmpty()) {
            Toast.makeText(this, "Ordering " + food + "...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
