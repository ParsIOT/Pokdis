package ir.parsiot.pokdis.Views;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
        initBottomBar(this, 0);

        getSupportActionBar().setTitle("جستجو محصولات");
        refreshDisplay();
    }

    private void initViews() {
        listItems = (RecyclerView)findViewById(R.id.listSales);
        listItems.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        listItems.setLayoutManager(llm);
    }


    protected void initBottomBar(final Context context, int iconNum) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(iconNum); // Map icon
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_map_page:
                        if (context.getClass() != MainActivity.class) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;

                    case R.id.ic_search_page:
                        if (context.getClass() != SalesListActivity.class) {
                            Intent intent = new Intent(context, SalesListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                    case R.id.ic_buy_page:
                        if (context.getClass() != CartActivity.class) {
                            Intent intent = new Intent(context, CartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                }

                return false;
            }
        });

    }

    private void refreshDisplay() {
        items = new Items();
//        adapter = new ItemAdapter(SalesListActivity.this,items.items,true);
        adapter = new RvItemAdapter(SalesListActivity.this,null,items.items,true, false, true,false);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SalesListActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
