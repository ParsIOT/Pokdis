package ir.parsiot.pokdis.Views;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.SearchView;

import ir.parsiot.pokdis.Items.Items;
import ir.parsiot.pokdis.R;

public class SalesListActivity extends AppCompatActivity {
    private RecyclerView listItems;
//    private ItemAdapter adapter;
    private RvItemAdapter adapter;
    private Items items;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);

        initViews();

        getSupportActionBar().setTitle("جستجو محصولات");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.filter(query);
                return false;
            }
        });
        return true;
    }
}
