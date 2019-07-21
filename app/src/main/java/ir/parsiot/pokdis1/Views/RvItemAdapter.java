package ir.parsiot.pokdis1.Views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.parsiot.pokdis1.R;

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
        ImageView goToMap;
        ImageView add_to_cart;
        ItemOfList item;

        ItemViewHolder(final View convertView) {
            super(convertView);
            itemImage = convertView.findViewById(R.id.itemImage);
            name = convertView.findViewById(R.id.nameOfItem);
            description = convertView.findViewById(R.id.descriptionOfItem);
            price = convertView.findViewById(R.id.priceOfItem);
            goToMap = convertView.findViewById(R.id.goToMap);
            add_to_cart = convertView.findViewById(R.id.add_to_cart);

            if(showAddToCart){
                add_to_cart.setVisibility(View.VISIBLE);
            }else {
                add_to_cart.setVisibility(View.GONE);
            }
            listeners();

        }

        public void fill (ItemOfList item){
            this.item = item;
            itemImage.setImageResource(item.getItemImage());
            name.setText(item.getName());
            description.setText(item.getDescription());
            price.setText(item.getPrice());
        }

        private void listeners(){
            goToMap.setOnClickListener(new View.OnClickListener() {
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

            add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                }
            });


        }
    }



}
