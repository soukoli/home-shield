package jakub.com.homeshield;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jakub.com.homeshield.helper.DatabaseHandler;
import jakub.com.homeshield.model.DoorState;

public class MainActivity extends AppCompatActivity{

    FloatingActionButton fab;



    private Toolbar mToolbar;

    private LinearLayout chartLyt;
    private GraphicalView mChart;


    private ListView listView;
    private List<DoorState> listOfStates = new ArrayList<>();
    private DoorStateAdap adapter;
    //private PrefManager pref;

    private DatabaseHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar init
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        adapter = new DoorStateAdap(this);

        // db init
        database = new DatabaseHandler(this);
        database.getWritableDatabase();

        List<DoorState> values = database.getAllStates();
        for (DoorState val : values) {
            listOfStates.add(0, val);
        }

        // Chart init
        chartLyt = (LinearLayout) findViewById(R.id.graph);
        chartLyt.addView(createTempGraph(), 0);

        // ListView init
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //Intent intent = getIntent();

        //ParseAnalytics.trackAppOpenedInBackground(getIntent());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        /***
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */
    }
    @Override
    protected void onResume() {
        database.getWritableDatabase();
        super.onResume();
    }

    @Override
    protected void onPause() {
        database.close();
        super.onPause();
    }


    public void addState() {
        DoorState m = new DoorState("test", 1, System.currentTimeMillis());
        listOfStates.add(0, m);
        database.addDoorState(m);
        adapter.notifyDataSetChanged();
        fab.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String message = intent.getStringExtra("message");
        String state = intent.getStringExtra("state");

        if (state.equals("open")) {
            DoorState m = new DoorState(message, 1, System.currentTimeMillis());
            listOfStates.add(0, m);
            database.addDoorState(m);
        } else {
            DoorState m = new DoorState("Door closed", 0, System.currentTimeMillis());

            listOfStates.remove(0);
            listOfStates.add(0, m);
        }

        adapter.notifyDataSetChanged();
    }


    private class DoorStateAdap extends BaseAdapter {

        LayoutInflater inflater;

        public DoorStateAdap(Activity activity) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return listOfStates.size();
        }

        @Override
        public Object getItem(int position) {
            return listOfStates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_doorstate, null);
            }

            ImageView image = (ImageView) view.findViewById(R.id.tvimage);
            TextView txtMessage = (TextView) view.findViewById(R.id.tvmsg);
            TextView txtTimestamp = (TextView) view.findViewById(R.id.tvtimestamp);


            DoorState state = listOfStates.get(position);

            // State icon
            if (state.getState() == 1) {
                image.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                image.setImageResource(android.R.drawable.btn_star_big_off);
            }

            // Message
            txtMessage.setText(state.getMsg());

            // Data info
            String state_date = DateFormat.getDateTimeInstance().format(state.getTimestamp());
            CharSequence state_ago = DateUtils.getRelativeTimeSpanString(state.getTimestamp(), System.currentTimeMillis(),
                    0L, DateUtils.FORMAT_ABBREV_ALL);
            txtTimestamp.setText(state_date + " (" +String.valueOf(state_ago) + ")");

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_settings:
                //setAddress();
                addState();
                if (fab.isShown()) {
                    fab.hide();
                } else {
                    fab.show();
                }

                break;
            case R.id.action_errase:
                int size = adapter.getCount();
                if (size > 0) {
                    DoorState to_del = (DoorState) adapter.getItem(size - 1);
                    database.deleteDoorState(to_del);
                    listOfStates.remove(to_del);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.action_exit: {
                exit_action();
                //addState();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private View createTempGraph() {

        // Creating TimeSeries
        XYSeries statesSeries = new XYSeries("States");


        // We start filling the series
        Calendar cal = Calendar.getInstance();

        int i = 1;
        for (DoorState states : listOfStates) {
            //cal.setTimeInMillis(states.getTimestamp());
            //Date date = cal.getTime();
            statesSeries.add(states.getTimestamp(), 1);

        }

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.MAGENTA);
        // Include low and max value
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        //renderer.setPointStrokeWidth(2);
        //renderer.setDisplayChartValues(true);



        // Now we add our series -- Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(statesSeries);

        // Finaly we create the multiple series renderer to control the graph
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        //mRenderer.setPanEnabled(false, false)
        mRenderer.setDisplayValues(true);
        mRenderer.setPanEnabled(true, false);
        //mRenderer.setYAxisMax(1);
        mRenderer.setYAxisMin(0);
        //mRenderer.setChartTitle("Door open states history");
        //mRenderer.setChartTitleTextSize(28);
        mRenderer.setAxisTitleTextSize(24);
        mRenderer.setLabelsTextSize(22);
        mRenderer.setYLabelsPadding(5);
        //mRenderer.setYLabelsAngle(90);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setShowGrid(true); // we show the grid
        mRenderer.setYLabels(0);
        mRenderer.addYTextLabel(0, "Closed");
        mRenderer.addYTextLabel(1, "Open");
        mRenderer.setXLabels(0);
        mRenderer.setZoomEnabled(true, true);
        mRenderer.setAntialiasing(true);
        mRenderer.setYLabelsAlign(Paint.Align.LEFT);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setScale(20f);
        mRenderer.setBarWidth(2f);

        mRenderer.setBarSpacing(1);


        cal.setTimeInMillis(listOfStates.get(0).getTimestamp());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        Date last = cal.getTime();

        cal.setTimeInMillis(listOfStates.get(listOfStates.size() - 1).getTimestamp());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date first = cal.getTime();

        long diff = last.getTime() - first.getTime();

        long num_of_days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

        for (int day = 0; day <= num_of_days; day++) {

            Date date = cal.getTime();
            mRenderer.addXTextLabel(date.getTime(), format.format(date));
            if (day == 0) {
                mRenderer.setXAxisMin(date.getTime());
            }
            if (day == num_of_days) {
                mRenderer.setXAxisMax(date.getTime());
            }
            cal.add(Calendar.DATE, 1);
        }
        // Creating a Time Chart
        //mChart = (GraphicalView) ChartFactory.getTimeChartView(getBaseContext(), dataset, mRenderer, "dd-MMM-yyyy");


        mChart = ChartFactory.getBarChartView(getBaseContext(), dataset, mRenderer, BarChart.Type.STACKED);

        mRenderer.setSelectableBuffer(10);

        // Enable chart click
        mRenderer.setClickEnabled(true);
        /***
        chartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyAnim(v, createPressGraph());
            }
        });
        */

        return mChart;
    }

    private void exit_action() {
        System.exit(0);
    }

}
