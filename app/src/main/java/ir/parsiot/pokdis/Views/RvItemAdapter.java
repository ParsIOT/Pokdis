package ir.parsiot.pokdis.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.R;

public class RvItemAdapter extends RecyclerView.Adapter<RvItemAdapter.ItemViewHolder> {
    private List<ItemOfList> items;
    private Context context;
    private LayoutInflater layoutInflater;
    boolean showAddToCart = false;

    public RvItemAdapter(Context context, List objects, Boolean showAddToCart) {
//        super(context, R.layout.item_of_listview, objects);
        this.context = context;
        items = objects;
        this.showAddToCart = showAddToCart;

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_of_listview, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {

        ItemOfList item = items.get(position);

        itemViewHolder.fill(item);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;

        ImageView itemImage;
        TextView name;
        TextView description;
        TextView price;
        ImageView showMapBtn;
        ImageView addToCartBtn;
        ItemOfList item;

        ItemViewHolder(final View convertView) {
            super(convertView);
            itemImage = convertView.findViewById(R.id.itemImage);
            name = convertView.findViewById(R.id.nameOfItem);
            description = convertView.findViewById(R.id.descriptionOfItem);
            price = convertView.findViewById(R.id.priceOfItem);
            showMapBtn = convertView.findViewById(R.id.show_map_btn);
            addToCartBtn = convertView.findViewById(R.id.add_to_card_btn);


            // Set a blinking animation on goToMapBtn
            Animation animation = new AlphaAnimation(1, (float)0.6);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            showMapBtn.startAnimation(animation);

            Typeface ir_font = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/Yekan.ttf");
            name.setTypeface(ir_font);
            price.setTypeface(ir_font);

            if(showAddToCart){
                addToCartBtn.setVisibility(View.VISIBLE);
            }else {
                addToCartBtn.setVisibility(View.GONE);
            }
            listeners();

        }

        public void fill (ItemOfList item){
            this.item = item;
//            itemImage.setImageResource(item.getItemImage());
            name.setText(item.getName());
            description.setText(item.getDescription());
            price.setText(item.getPrice());



            try {
                InputStream ims = context.getApplicationContext().getAssets().open(item.getImageName());
                Drawable drw = Drawable.createFromStream(ims, null);
                itemImage.setImageDrawable(drw);
            }
            catch(IOException ex) {
                return;
            }
        }

        private void listeners(){
            showMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Bundle bundle = new Bundle();
                    //   bundle.putString("locationMarker",item.getLocation());
                    //    bundle.putString("itemId",item.getId());


                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra("locationMarker",item.getLocation());
                    intent.putExtra("itemName",item.getName());
                    intent.putExtra("itemID",item.getId());
                    Log.e("tag",item.getId());
                    //intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });


            ArrayList<View> tempLis= new ArrayList<View>();
            tempLis.add(name);
            tempLis.add(price);
            tempLis.add(description);
            tempLis.add(itemImage);
            for(View view : tempLis) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, ItemContent.class);
                        intent.putExtra("itemId", item.getId());
                        Log.e("tag", item.getId());
                        //intent.putExtras(bundle);
                        context.startActivity(intent);

                    }
                });
            }

        }
    }



}
