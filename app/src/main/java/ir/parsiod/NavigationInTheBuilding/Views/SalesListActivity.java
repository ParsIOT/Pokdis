package ir.parsiod.NavigationInTheBuilding.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import ir.parsiod.NavigationInTheBuilding.Items.Items;
import ir.parsiod.NavigationInTheBuilding.R;

public class SalesListActivity extends AppCompatActivity {
    private ListView listView;
    private ItemAdapter adapter;
    private Items items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);

        initViews();


        refreshDisplay();
    }

    private void initViews() {
        listView = findViewById(R.id.listSales);

    }


    private void refreshDisplay() {
        items = new Items();
        adapter = new ItemAdapter(SalesListActivity.this,items.items,true);
        listView.setAdapter(adapter);
    }
}
