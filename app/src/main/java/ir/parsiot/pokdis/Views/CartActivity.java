package ir.parsiot.pokdis.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.R;

public class CartActivity extends AppCompatActivity {
    private ItemAdapter adapter;
    private ListView listView;
    private List<ItemOfList> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        items = new ArrayList<>();

        initViews();


        refreshDisplay();
    }

    private void initViews() {
        listView = findViewById(R.id.listCart);

    }


    private void refreshDisplay() {

        adapter = new ItemAdapter(CartActivity.this,items,false);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuItem add = menu.add("add").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {
               return false;
           }
       });
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        add.setIcon(R.drawable.add);


        return super.onCreateOptionsMenu(menu);
    }
}
