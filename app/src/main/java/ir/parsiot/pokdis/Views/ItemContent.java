package ir.parsiot.pokdis.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.io.IOException;
import java.io.InputStream;

import ir.parsiot.pokdis.Items.CartItems;
import ir.parsiot.pokdis.Items.Items;
import ir.parsiot.pokdis.R;

public class ItemContent extends AppCompatActivity {

    ImageView itemImage;
    TextView name;
    TextView description;
    TextView price;
    TextView discountPrice;
    TextView dimens;
    TextView weight;
    TextView brand;
    ItemOfList item;
    boolean isCollapsed;
    CartItems cartItems;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);
        cartItems = new CartItems();
        context = getApplicationContext();
        name = findViewById(R.id.item_name);
        itemImage = findViewById(R.id.item_image);
        price = findViewById(R.id.item_price);
        discountPrice = findViewById(R.id.item_discount_price);
        description = findViewById(R.id.item_description);
        dimens = findViewById(R.id.item_dimens);
        weight = findViewById(R.id.item_weight);
        brand = findViewById(R.id.item_brand);
        Typeface ir_font = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        price.setTypeface(ir_font);
        discountPrice.setTypeface(ir_font);
        name.setTypeface(ir_font);


        initBottomBar(this, 0);

        String itemId = getIntent().getStringExtra("itemId");
        Items items = new Items();
        item = items.get_item(itemId);
        if (item != null){

            String nameTxt = item.getName();
            name.setText(nameTxt);
            description.setText(item.getDescription()+"\n\n"+item.getLongDescription());

            price.setText(item.getPrice());
            String discountPriceStr = item.getDiscountPrice();
            if (!discountPriceStr.equals("")){
                discountPrice.setText(discountPriceStr);;
                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                discountPrice.setVisibility(View.GONE);
            }

            dimens.setText(item.getDimens());
            weight.setText(item.getWeigth());
            brand.setText(item.getBrand());


            try {
                InputStream ims = this.getApplicationContext().getAssets().open(item.getImageName());
                Drawable drw = Drawable.createFromStream(ims, null);
                itemImage.setImageDrawable(drw);
                findViewById(R.id.image_none_text).setVisibility(View.GONE);
            }
            catch(IOException ex) {
                return;
            }
        }



        final ExpandableRelativeLayout itemDesContainer = (ExpandableRelativeLayout) findViewById(R.id.item_description_container);
        final ImageButton btnDescExpand = (ImageButton) findViewById(R.id.description_expand_btn);
        isCollapsed = true;
        itemDesContainer.collapse();
        btnDescExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    itemDesContainer.expand();
                    btnDescExpand.setImageResource(R.drawable.ic_expand_less);
                    isCollapsed = false;
                } else {
                    itemDesContainer.collapse();
                    btnDescExpand.setImageResource(R.drawable.ic_expand_more);
                    isCollapsed = true;
                }
            }
        });

        // Set a blinking animation for btnDescExpand
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        btnDescExpand.startAnimation(animation); //to start animation



        FloatingActionButton showMapFab = findViewById(R.id.show_map_fab);
        showMapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ItemContent.this,MainActivity.class);
                intent.putExtra("locationMarker",item.getLocation());
                intent.putExtra("itemName",item.getName());
                intent.putExtra("itemImgSrc", item.getImageName());
                intent.putExtra("itemId",item.getId());
//                Log.e("tag",item.getId());
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        FloatingActionButton addToCartFab = findViewById(R.id.add_to_card_fab);
        addToCartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtMessage;
                if (cartItems.put_item(item)){
                    txtMessage = "این محصول به لیست خرید اضافه شد.";
                }else{
                    txtMessage = "این محصول در سبد خرید از قبل وجود داشته است";
                }
                Snackbar mSnackbar = Snackbar.make(view, txtMessage, Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View mView = mSnackbar.getView();
                TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                else
                    mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                mSnackbar.show();
            }
        });
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

}
