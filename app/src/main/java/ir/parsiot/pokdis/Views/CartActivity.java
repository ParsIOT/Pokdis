package ir.parsiot.pokdis.Views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.Items.CartItems;
import ir.parsiot.pokdis.Items.CartItemsClient;
import ir.parsiot.pokdis.Items.ItemClass;
import ir.parsiot.pokdis.R;
//import ir.parsiot.pokdis.ViewWidgets.StickyBottomBehavior;

public class CartActivity extends AppCompatActivity implements CartItemsClient {
    private RvItemAdapter adapter;
    private RecyclerView listView;
    private List<ItemClass> items;
    Button cntButton;
    CartItems cartItems ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartItems = new CartItems(getApplicationContext());
        setContentView(R.layout.activity_cart);
//        getSupportActionBar().setTitle("سبد خرید");
        initViews();

        items = new ArrayList<>();
        items = cartItems.get_items();
        initBottomBar(this, 2);

        ImageView emptyCartImage = (ImageView)findViewById(R.id.empty_cart_image);

        if (items.size()==0){
            listView.setVisibility(View.GONE);
        }else{
            emptyCartImage.setVisibility(View.GONE);
        }
        cntButton = findViewById(R.id.continue_button);

        cntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, ItemContentActivity.class);
//                intent.putExtra("itemId", item.getId());
//                context.startActivity(intent);
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CartActivity.this);
                builder.setMessage("بخش فروش مغازه ها در حال توسعه است ...");
                builder.setNegativeButton("بله", null);
                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                dialog.show();

            }
        });
//        Button button = findViewById(R.id.continue_button);
//        ((CoordinatorLayout.LayoutParams) button.getLayoutParams()).setBehavior(new StickyBottomBehavior(R.id.anchor, getResources().getDimensionPixelOffset(R.dimen.margins)));

        refreshDisplay();
    }

    @Override
    public void onResume(){
        super.onResume();
        cartItems = cartItems = new CartItems(getApplicationContext());
        items = cartItems.get_items();
        refreshDisplay();

    }

    private void initViews() {
        listView = (RecyclerView)findViewById(R.id.list_cart);
        listView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        listView.setLayoutManager(llm);

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
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;

                    case R.id.ic_search_page:
                        if (context.getClass() != SalesListActivity.class) {
                            Intent intent = new Intent(context, SalesListActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        break;
                    case R.id.ic_buy_page:
                        if (context.getClass() != CartActivity.class) {
                            Intent intent = new Intent(context, CartActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        if (items.size() == 0){
            cntButton.setVisibility(View.GONE);
        }
        adapter = new RvItemAdapter(CartActivity.this,  this, items,false, true, false,true);
        listView.setAdapter(adapter);
    }

    @Override
    public ArrayList<ItemClass> deleteItem(ItemClass item){
        return cartItems.delete_item(item);
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

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(CartActivity.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
