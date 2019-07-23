package ir.parsiot.pokdis.Views;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.parsiot.pokdis.R;


public class ItemAdapter extends ArrayAdapter {
    private List<ItemOfList> items;
    private Context context;
    private LayoutInflater layoutInflater;
    boolean showAddToCart = false;


    public ItemAdapter( Context context,  List objects,Boolean showAddToCart) {
        super(context, R.layout.item_of_listview, objects);
    this.context =context;
    items =objects;
    this.showAddToCart =showAddToCart;



    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemOfList item = items.get(position);

        ViewHolder viewHolder;

        if (convertView == null){
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_of_listview,parent,false);
            viewHolder= new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.fill(item);



        return convertView;


    }



    private class ViewHolder{
        ImageView itemImage;
        TextView name;
        TextView description;
        TextView price;
        ImageView mapBtn;
        ImageView add_to_cart;
        ItemOfList item;


        public ViewHolder(final View convertView){
            itemImage =convertView.findViewById(R.id.itemImage);
            name =convertView.findViewById(R.id.nameOfItem);
            description =convertView.findViewById(R.id.descriptionOfItem);
            price =convertView.findViewById(R.id.priceOfItem);
            mapBtn =convertView.findViewById(R.id.map_btn);
            add_to_cart =convertView.findViewById(R.id.add_to_card_btn);

            if(showAddToCart){
                add_to_cart.setVisibility(View.VISIBLE);
            }else {
                add_to_cart.setVisibility(View.GONE);
            }


            listeners();
        }


        public void fill (ItemOfList item){
            this.item = item;
//            itemImage.setImageResource(item.getItemImage());
            name.setText(item.getName());
            description.setText(item.getDescription());
            price.setText(item.getPrice());


        }

       private void listeners(){
           mapBtn.setOnClickListener(new View.OnClickListener() {
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
