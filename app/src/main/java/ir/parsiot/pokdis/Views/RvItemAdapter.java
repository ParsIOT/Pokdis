package ir.parsiot.pokdis.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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

import ir.parsiot.pokdis.Items.CartItems;
import ir.parsiot.pokdis.Items.CartItemsClient;
import ir.parsiot.pokdis.R;

public class RvItemAdapter extends RecyclerView.Adapter<RvItemAdapter.ItemViewHolder> {
    private ArrayList<ItemOfList> allItems = new ArrayList<ItemOfList>();
    private ArrayList<ItemOfList> filteredItems = new ArrayList<ItemOfList>();
    private Context context;
    private LayoutInflater layoutInflater;
    boolean showAddToCart = false;
    boolean showDeleteBtn = false;
    private CartItemsClient cartItemsClient;
    private CartItems cartItems;


    public RvItemAdapter(Context context, CartItemsClient cartItemsClient, List objects, Boolean showAddToCart, Boolean showDeleteBtn) {
//        super(context, R.layout.item_of_listview, objects);
        this.context = context;
        cartItems = new CartItems();
        this.cartItemsClient = cartItemsClient;
        allItems = (ArrayList<ItemOfList>)objects;
        filteredItems.addAll(allItems);
        this.showAddToCart = showAddToCart;
        this.showDeleteBtn = showDeleteBtn;

    }
    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_of_listview, viewGroup, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {
        if (allItems.size() == 0){
            return;
        }
        final int pos = position;
        ItemOfList item = filteredItems.get(position);
        itemViewHolder.fill(item);

        itemViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteItemFromList(v, pos);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void filter(String query){
        filteredItems.clear();
        query = query.toLowerCase();

        if (query.equals("")) {
            filteredItems.addAll(allItems);
        }else{
            for (ItemOfList item : allItems) {
                if (item.getName().toLowerCase().contains(query)){
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // confirmation dialog box to delete an unit
    private void deleteItemFromList(View v, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        //builder.setTitle("Dlete ");
        builder.setMessage("مطمئن هستید می خواهید این محصول را از لیست خرید حذف کنید ؟")
                .setCancelable(false)
                .setPositiveButton("بله",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //1: Delete from allItems
                                //2: Delete from HAWK
                                allItems = cartItemsClient.deleteItem(filteredItems.get(position));
                                filteredItems.remove(position);

                                notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

        builder.show();

    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;

        ImageView itemImage;
        TextView name;
        TextView description;
        TextView price;
        TextView discountPrice;
        ImageView showMapBtn;
        ImageView addToCartBtn;
        ImageView deleteBtn;
        ItemOfList item;

        ItemViewHolder(final View convertView) {
            super(convertView);
            itemImage = convertView.findViewById(R.id.itemImage);
            name = convertView.findViewById(R.id.nameOfItem);
            description = convertView.findViewById(R.id.descriptionOfItem);
            price = convertView.findViewById(R.id.priceOfItem);
            discountPrice = convertView.findViewById(R.id.disccountPriceOfItem);
            showMapBtn = convertView.findViewById(R.id.show_map_btn);
            addToCartBtn = convertView.findViewById(R.id.add_to_card_btn);
            deleteBtn = convertView.findViewById(R.id.delete_btn);

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
            discountPrice.setTypeface(ir_font);

            if(showAddToCart){
                addToCartBtn.setVisibility(View.VISIBLE);
            }else {
                addToCartBtn.setVisibility(View.GONE);
            }

            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else {
                deleteBtn.setVisibility(View.GONE);
            }

            listeners();

        }




        public void fill (ItemOfList item){
            this.item = item;
//            itemImage.setImageResource(item.getItemImage());
            name.setText(item.getName());
            description.setText(item.getDescription());
            price.setText(item.getPrice());

            String discountPriceStr = item.getDiscountPrice();
            if (!discountPriceStr.equals("")){
                discountPrice.setText(discountPriceStr);;
                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                discountPrice.setVisibility(View.GONE);
            }


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

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
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

            ArrayList<View> tempLis= new ArrayList<View>();
            tempLis.add(name);
            tempLis.add(price);
            tempLis.add(discountPrice);
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
