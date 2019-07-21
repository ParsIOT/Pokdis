package ir.parsiot.pokdis1.Views;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import ir.parsiot.pokdis1.Items.Items;
import ir.parsiot.pokdis1.R;

public class ItemContent extends AppCompatActivity {

    ImageView itemImage;
    TextView name;
    TextView description;
    TextView price;
    TextView dimens;
    TextView weight;
    TextView brand;
    ImageView goToMap;
    ImageView add_to_cart;
    ItemOfList item;
    boolean isCollapsed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);


        name = findViewById(R.id.item_name);
        itemImage = findViewById(R.id.item_image);
        price = findViewById(R.id.item_price);
        description = findViewById(R.id.item_description);
        dimens = findViewById(R.id.item_dimens);
        weight = findViewById(R.id.item_weight);
        brand = findViewById(R.id.item_brand);
        Typeface ir_font = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Yekan.ttf");
        price.setTypeface(ir_font);
        name.setTypeface(ir_font);



        String itemId = getIntent().getStringExtra("itemId");
        Items items = new Items();
        ItemOfList item = items.get_item(itemId);
        if (item != null){

            String nameTxt = item.getName();
            name.setText(nameTxt);
            description.setText(item.getDescription()+"\n\n"+item.getLongDescription());
            price.setText(item.getPrice());
            dimens.setText(item.getDimens());
            weight.setText(item.getWeigth());
            brand.setText(item.getBrand());


            try {
                InputStream ims = this.getApplicationContext().getAssets().open(item.getImageName());
                Drawable drw = Drawable.createFromStream(ims, null);
                itemImage.setImageDrawable(drw);
                findViewById(R.id.image_none_text).setVisibility(View.INVISIBLE);
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


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
