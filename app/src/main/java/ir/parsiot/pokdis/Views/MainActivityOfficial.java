package ir.parsiot.pokdis.Views;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ir.parsiot.pokdis.Controller.HorizontalListAdapter;
import ir.parsiot.pokdis.Controller.ItemClickListener;
import ir.parsiot.pokdis.Controller.SliderPagerAdapter;
import ir.parsiot.pokdis.Controller.TodayNewsAdapter;
import ir.parsiot.pokdis.Models.EmkanatRefahi;
import ir.parsiot.pokdis.Models.TodayNews;
import ir.parsiot.pokdis.R;
import ir.parsiot.pokdis.Utils.ViewPagerIndicator;
import com.squareup.picasso.Picasso;

public class MainActivityOfficial extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivityOffical";
    NavigationView navigationView;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;

    boolean isCollapsed;

    ArrayList<View> sliderList;
    LayoutInflater inflater;

    ViewPagerIndicator mIndicator;
    LinearLayout mLinearLayout;

    TodayNewsAdapter newsAdapter;
    HorizontalListAdapter emkanatAdapter;
    ArrayList<EmkanatRefahi> emkanatList;
    ArrayList<TodayNews> todayNewsList;
    SliderPagerAdapter sliderPagerAdapter;
    ViewPager viewPager;
    int currentPage = 0;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_official);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (Build.VERSION.SDK_INT >= 15) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }

        findViewById(R.id.img_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityOfficial.this, SalesListActivity.class));
            }
        });
        findViewById(R.id.img_btn_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityOfficial.this, CartActivity.class));
            }
        });
        findViewById(R.id.img_btn_booth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivityOfficial.this, BoothListActivity.class));
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityOfficial.this);
            builder.setMessage("بخش فروش مغازه ها در حال توسعه است ...");
                builder.setNegativeButton("بله", null);
                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                dialog.show();
            }
        });

        isCollapsed = true;
        inflater = LayoutInflater.from(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityOfficial.this, MainActivity.class));
            }
        });

        sliderPagerAdapter = new SliderPagerAdapter(MainActivityOfficial.this);
        viewPager = (ViewPager) findViewById(R.id.slider_main_pager);
        viewPager.setAdapter(sliderPagerAdapter);
        mLinearLayout = (LinearLayout) findViewById(R.id.slider_indicator);
        addToSlider("file:///android_asset/slider_images/slider_sample1.jpg", "تخفیف شگفت انگیر");
        addToSlider("file:///android_asset/slider_images/slider_sample2.png", "");
        addToSlider("file:///android_asset/slider_images/slider_sample3.jpg", "تخفیف ۳۰ درصدی محصولات ARDENE");

        mIndicator = new ViewPagerIndicator(this, mLinearLayout, viewPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(sliderPagerAdapter.getSize());
        mIndicator.show();

        show_emkanat();

        news();

    }
    private void news() {
        todayNewsList = new ArrayList<>();
        newsAdapter = new TodayNewsAdapter(todayNewsList, this);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_today_news);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(newsAdapter);
        recycler.addItemDecoration(new DividerItemDecoration(MainActivityOfficial.this, DividerItemDecoration.HORIZONTAL));
        recycler.setNestedScrollingEnabled(false);
        addNewsSample();
    }

    @Override
    protected void onRestart() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onRestart();
    }

    @Override
    protected void onPause() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onPause();
    }

    @Override
    protected void onResume() {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivityOfficial.this, "Permission granted, Loading Mission Control!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivityOfficial.this, "App need FINE LOCATION ACCESS to discover nearby Wifi APs",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
        }
    }

    private void addToSlider(String imageUrl, String text) {
        Log.e(TAG, "addToSlider: " + imageUrl + "  " + text);
        View v = inflater.inflate(R.layout.slider_item_layout, null, false);
        ImageView img1 = (ImageView) v.findViewById(R.id.slider_image);
        TextView tv1 = (TextView) v.findViewById(R.id.slider_text);
        img1.setContentDescription(imageUrl);
        tv1.setText(text);
        Picasso.with(MainActivityOfficial.this).load(imageUrl).error(R.mipmap.no_image).into(img1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv1.setTextColor(getResources().getColor(R.color.white, null));
        } else
            tv1.setTextColor(getResources().getColor(R.color.white));
        //sliderList.add(v);
        sliderPagerAdapter.addView(v);

/*

        v = inflater.inflate(R.layout.slider_item_main_activity, null, false);
        img1 = (ImageView) v.findViewById(R.id.slider_image);
        tv1 = (TextView) v.findViewById(R.id.slider_text);
        img1.setImageResource(R.mipmap.maxresdefault);
        tv1.setText("یارانه ۴۵۵۰۰ تومانی اردیبهشت سه شنبه واریز می شود");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv1.setTextColor(getResources().getColor(R.color.white, null));
        } else
            tv1.setTextColor(getResources().getColor(R.color.white));
        sliderPagerAdapter.addView(v);
*/

        sliderPagerAdapter.notifyDataSetChanged();
    }

    private void show_emkanat(){
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_emkanat_refahi);
        emkanatList = new ArrayList<>();
        emkanatAdapter = new HorizontalListAdapter(emkanatList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);

        emkanatAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivityOfficial.this, MainActivity.class);
//                intent.putExtra("emkanat", emkanatList.get(position).getXy());
                intent.putExtra("locationMarker",emkanatList.get(position).getXy());
                intent.putExtra("itemName",emkanatList.get(position).getTitle());
                intent.putExtra("itemImgSrc", emkanatList.get(position).getImgSrc());
                intent.putExtra("itemId","-1");
                intent.putExtra("isMainPage",false);
                startActivity(intent);
            }
        });

        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(emkanatAdapter);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        emkanatList.add(new EmkanatRefahi(0, R.mipmap.question, "اطلاعات", "-30,-120","item_images/question.png" ));
        emkanatList.add(new EmkanatRefahi(1, R.mipmap.praying_room, "نمازخانه", "530,530","item_images/praying_room.png" ));
        emkanatList.add(new EmkanatRefahi(2, R.mipmap.wc, "سرویس بهداشتی", "200,50","item_images/wc.png" ));
        emkanatList.add(new EmkanatRefahi(3, R.mipmap.parking, "پارگینگ", "-520,590","item_images/parking.png" ));
        emkanatList.add(new EmkanatRefahi(4, R.mipmap.door, "درب ورود و خروج", "530,530","item_images/door.png" ));
        emkanatList.add(new EmkanatRefahi(5, R.mipmap.restaurant, "رستوران", "-520,590","item_images/restaurant.png" ));
        emkanatAdapter.notifyDataSetChanged();
    }
    // Setting default values in case fo 1st run
//    private void setDefaultPrefs() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        boolean isFirstRun = sharedPreferences.contains(Constants.IS_FIRST_RUN);
//
//        if (!isFirstRun) {
//            editor.putString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
//            editor.putString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
//            editor.putString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
//            editor.putString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
//            editor.putInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
//            editor.putInt(Constants.LEARN_PERIOD, Constants.DEFAULT_LEARNING_PERIOD);
//            editor.putInt(Constants.LEARN_INTERVAL, Constants.DEFAULT_LEARNING_INTERVAL);
//            editor.putBoolean(Constants.IS_FIRST_RUN, false);
//            editor.apply();
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_map) {

        } else if (id == R.id.nav_product) {
            startActivity(new Intent(getApplicationContext(), SalesListActivity.class));
        } else if (id == R.id.nav_booth) {
//            startActivity(new Intent(getApplicationContext(), BoothListActivity.class));
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityOfficial.this);
            builder.setMessage("بخش فروش مغازه ها در حال توسعه است ...");
            builder.setNegativeButton("بله", null);
            AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("isMainPage",false);
            startActivity(intent);

        } else if (id == R.id.nav_aboutUs){
            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showNotif() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_action_info)
                .setTicker("{your tiny message}")
//                .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("this is title")
                .setContentInfo("");


        b.setContentText("salam shamgholi");
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }

    private void addNewsSample() {
        todayNewsList.add(new TodayNews(2, "باشگاه مشتریان",
                "با عضویت در باشگاه مشتریان از تخفیف های ویژه در اولین خرید لذت ببرید.",
                "file:///android_asset/news_images/customer-club.png"));

        todayNewsList.add(new TodayNews(1, "یه حال خوب با کانتی",
                "محصول شرکت کوپا",
                "file:///android_asset/news_images/coppa.jpg"));

        todayNewsList.add(new TodayNews(1, "سورپرایز جدید !",
                "راه اندازی کافه و رستوران مجموعه",
                "file:///android_asset/news_images/cafe.jpg"));

        todayNewsList.add(new TodayNews(1, "پرداخت الکترونیکی",
                "از این پس امکان پرداخت خرید ها از طریق اپلیکیشن تلفن همراه برای مشتریان و اعضا فراهم شده است.",
                "file:///android_asset/news_images/namad.png"));

        newsAdapter.notifyDataSetChanged();

    }

}

