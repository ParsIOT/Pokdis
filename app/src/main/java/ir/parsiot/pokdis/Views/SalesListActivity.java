package ir.parsiot.pokdis.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ir.parsiot.pokdis.Items.Items;
import ir.parsiot.pokdis.R;

public class SalesListActivity extends AppCompatActivity {
    private RecyclerView listItems;
//    private ItemAdapter adapter;
    private RvItemAdapter adapter;
    private Items items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);

        initViews();


        refreshDisplay();
    }

    private void initViews() {
        listItems = (RecyclerView)findViewById(R.id.listSales);
        listItems.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        listItems.setLayoutManager(llm);
    }


    private void refreshDisplay() {
        items = new Items();
//        adapter = new ItemAdapter(SalesListActivity.this,items.items,true);
        adapter = new RvItemAdapter(SalesListActivity.this,items.items,true);
        listItems.setAdapter(adapter);
    }
}
