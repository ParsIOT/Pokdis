package ir.parsiot.pokdis.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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

import com.shawnlin.numberpicker.NumberPicker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ir.parsiot.pokdis.Items.CartItems;
import ir.parsiot.pokdis.Items.CartItemsClient;
import ir.parsiot.pokdis.Items.ItemClass;
import ir.parsiot.pokdis.R;

/*
 List of items that loads itemClass objects
 */

public class RvItemAdapter extends RecyclerView.Adapter<RvItemAdapter.ItemViewHolder> {
    private ArrayList<ItemClass> allItems = new ArrayList<ItemClass>();
    private ArrayList<ItemClass> filteredItems = new ArrayList<ItemClass>();
    private Context context;
    private LayoutInflater layoutInflater;
    boolean showAddToCart = false;
    boolean showNumPicker = false;
    boolean showDeleteBtn = false;
    boolean showMapBtn = false;
    private CartItemsClient cartItemsClient;
    private CartItems cartItems;


    public RvItemAdapter(Context context, CartItemsClient cartItemsClient, List objects, Boolean showAddToCart, Boolean showNumPicker, Boolean showMapBtn, Boolean showDeleteBtn) {
//        super(context, R.layout.item_of_listview, objects);
        this.context = context;
        cartItems = new CartItems(context);
        this.cartItemsClient = cartItemsClient;
        allItems = (ArrayList<ItemClass>) objects;
        filteredItems.addAll(allItems);
        this.showAddToCart = showAddToCart;
        this.showNumPicker = showNumPicker;
        this.showDeleteBtn = showDeleteBtn;
        this.showMapBtn = showMapBtn;

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
        if (allItems.size() == 0) {
            return;
        }
        final int pos = position;
        ItemClass item = filteredItems.get(position);
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

    public void filter(String query) {
        filteredItems.clear();
        query = query.toLowerCase();

        if (query.equals("")) {
            filteredItems.addAll(allItems);
        } else {
            for (ItemClass item : allItems) {
                if (item.getName().toLowerCase().contains(query)) {
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
        ImageView mapBtn;
        ImageView addToCartBtn;
        NumberPicker numPicker;
        TextView numPickerLabel;
        ImageView deleteBtn;
        ItemClass item;

        ItemViewHolder(final View convertView) {
            super(convertView);
            itemImage = convertView.findViewById(R.id.itemImage);
            name = convertView.findViewById(R.id.nameOfItem);
            description = convertView.findViewById(R.id.descriptionOfItem);
            price = convertView.findViewById(R.id.priceOfItem);
            discountPrice = convertView.findViewById(R.id.disccountPriceOfItem);
            mapBtn = convertView.findViewById(R.id.map_btn);
            addToCartBtn = convertView.findViewById(R.id.add_to_card_btn);
            numPicker = (NumberPicker) convertView.findViewById(R.id.number_picker);
            numPickerLabel = (TextView) convertView.findViewById(R.id.number_picker_label);
            deleteBtn = convertView.findViewById(R.id.delete_btn);

            // Set a blinking animation on mapBtn
            Animation animation = new AlphaAnimation(1, (float) 0.4);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            mapBtn.startAnimation(animation);

            Typeface ir_font = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/Yekan.ttf");
            name.setTypeface(ir_font);
            price.setTypeface(ir_font);
            numPickerLabel.setTypeface(ir_font);

            numPicker.setTypeface(ir_font);
            numPicker.setMinValue(1);
            numPicker.setWrapSelectorWheel(false);

            discountPrice.setTypeface(ir_font);

            if (showAddToCart) {
                addToCartBtn.setVisibility(View.VISIBLE);
            } else {
                addToCartBtn.setVisibility(View.GONE);
            }

            if (showNumPicker) {
                numPicker.setVisibility(View.VISIBLE);
                numPickerLabel.setVisibility(View.VISIBLE);

                numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        //Display the newly selected number from picker

                        price.setText(item.getPrice(newVal));

                        String discountPriceStr = item.getDiscountPrice(newVal);
                        if (!discountPriceStr.equals("")) {
                            discountPrice.setText(discountPriceStr);
                            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            discountPrice.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                numPicker.setVisibility(View.GONE);
                numPickerLabel.setVisibility(View.GONE);
            }

            if (showDeleteBtn) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }

            if (showMapBtn) {
                mapBtn.setVisibility(View.VISIBLE);
            } else {
                mapBtn.setVisibility(View.GONE);
            }

            listeners();

        }


        public void fill(ItemClass item) {
            this.item = item;
//            itemImage.setImageResource(item.getItemImage());
            name.setText(item.getName());
            description.setText(item.getDescription());
            price.setText(item.getPrice());

            String discountPriceStr = item.getDiscountPrice();
            if (!discountPriceStr.equals("")) {
                discountPrice.setText(discountPriceStr);
                ;
                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                discountPrice.setVisibility(View.GONE);
            }


            try {
                InputStream ims = context.getApplicationContext().getAssets().open(item.getImageName());
                Drawable drw = Drawable.createFromStream(ims, null);
                itemImage.setImageDrawable(drw);
            } catch (IOException ex) {
                return;
            }
        }

        private void listeners() {
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Bundle bundle = new Bundle();
                    //   bundle.putString("locationMarker",item.getLocation());
                    //    bundle.putString("itemId",item.getId());


                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("locationMarker", item.getLocation());
                    intent.putExtra("itemName", item.getName());
                    intent.putExtra("itemImgSrc", item.getImageName());
                    intent.putExtra("itemId", item.getId());
//                    Log.d("tag", item.getId());
                    //intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    //builder.setTitle("Dlete ");
                    builder.setMessage("آیا مایل هستید این محصول به سبد خرید اضافه شود؟")
                            .setCancelable(false)
                            .setPositiveButton("بله",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //1: Delete from allItems
                                            //2: Delete from HAWK

                                            String txtMessage;
                                            if (cartItems.put_item(item)) {
                                                txtMessage = "این محصول به لیست خرید اضافه شد.";
                                            } else {
                                                txtMessage = "این محصول در سبد خرید از قبل وجود داشته است";
                                            }
                                            Snackbar mSnackbar = Snackbar.make(view, txtMessage, Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null);
                                            View mView = mSnackbar.getView();
                                            TextView mTextView = (TextView) mView.findViewById(R.id.snackbar_text);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                                                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            else
                                                mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                                            mSnackbar.show();

//                                            allItems = cartItemsClient.deleteItem(filteredItems.get(position));
//                                            filteredItems.remove(position);
//
//                                            notifyDataSetChanged();
                                        }
                                    })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                }
                            });

                    builder.show();


                }
            });

            ArrayList<View> tempLis = new ArrayList<View>();
            tempLis.add(name);
            tempLis.add(price);
            tempLis.add(discountPrice);
            tempLis.add(description);
            tempLis.add(itemImage);
            for (View view : tempLis) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ItemContentActivity.class);
                        intent.putExtra("itemId", item.getId());
//                        Log.d("tag", item.getId());
                        //intent.putExtras(bundle);
                        context.startActivity(intent);

                    }
                });
            }

        }
    }


}
