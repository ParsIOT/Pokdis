package ir.parsiot.pokdis.Views;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.Items.CartItems;
import ir.parsiot.pokdis.R;
//import ir.parsiot.pokdis.ViewWidgets.StickyBottomBehavior;

public class CartActivity extends AppCompatActivity {
    private RvItemAdapter adapter;
    private RecyclerView listView;
    private List<ItemOfList> items;
    CartItems cartItems = new CartItems();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setTitle("سبد خرید");
        initViews();

        items = new ArrayList<>();
        items = cartItems.get_items();


//        Button button = findViewById(R.id.continue_button);
//        ((CoordinatorLayout.LayoutParams) button.getLayoutParams()).setBehavior(new StickyBottomBehavior(R.id.anchor, getResources().getDimensionPixelOffset(R.dimen.margins)));

        refreshDisplay();
    }

    private void initViews() {
        listView = (RecyclerView)findViewById(R.id.list_cart);
        listView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        listView.setLayoutManager(llm);

    }


    private void refreshDisplay() {

        adapter = new RvItemAdapter(CartActivity.this,items,false);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuItem add = menu.add("add").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {
               Intent intent = new Intent(CartActivity.this, SalesListActivity.class);
//               finish();
               startActivity(intent);
               return false;
           }
       });



        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        add.setIcon(R.drawable.add);


        return super.onCreateOptionsMenu(menu);
    }
}
