package eu.h2020.sc.ui.waitingtime;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Stop;
import eu.h2020.sc.domain.Transit;
import eu.h2020.sc.domain.TransitItem;
import eu.h2020.sc.ui.waitingtime.adapter.WaitingTimeAdapter;
import eu.h2020.sc.ui.waitingtime.task.WaitingTimeTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class WaitingTimeActivity extends GeneralActivity implements View.OnClickListener {

    public static final String WAITING_TIME_KEY = WaitingTimeActivity.class.getCanonicalName();

    private RecyclerView transitsRecyclerView;
    private ProgressBar progressBarWaitingTime;
    private TextView stopNameTextView;
    private View noNoDataLayout;
    private Button btnNoDataRetry;
    private TextView noDataTextView;

    private WaitingTimeAdapter waitingTimeAdapter;
    private List<TransitItem> transitItems;
    private Stop stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_time);

        this.stop = (Stop) getIntent().getSerializableExtra(WAITING_TIME_KEY);

        this.initUI();
        this.initEvents();
        this.initWaitingTimeAdapterComponents();
        this.doGetWaitingTime(this.stop.getStopCode());
    }

    private void initUI() {
        initToolBar(false);
        initBack();

        this.transitsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_transits);
        this.progressBarWaitingTime = (ProgressBar) findViewById(R.id.progress_bar_waiting_time);

        this.stopNameTextView = (TextView) findViewById(R.id.waitingtime_stop_header_textview).findViewById(R.id.stop_name);
        this.stopNameTextView.setText(this.stop.getName());

        this.noNoDataLayout = this.findViewById(R.id.layout_waiting_time_error_retry);
        this.btnNoDataRetry = (Button) this.noNoDataLayout.findViewById(R.id.btn_no_data_retry);
        this.noDataTextView = (TextView) this.noNoDataLayout.findViewById(R.id.no_data_text_view);
    }

    private void initWaitingTimeAdapterComponents() {
        this.transitsRecyclerView.setHasFixedSize(true);
        this.transitsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.transitsRecyclerView.setLayoutManager(mLayoutManager);

        List<TransitItem> transitItems = new ArrayList<>();
        this.waitingTimeAdapter = new WaitingTimeAdapter(transitItems);
        this.transitsRecyclerView.setAdapter(this.waitingTimeAdapter);
    }


    private void initItems(Stop stop) {
        this.transitItems = new ArrayList<>();

        for (Transit transit : stop.getTransits()) {
            this.transitItems.add(new TransitItem(transit));
        }
    }

    private void doGetWaitingTime(String stopCode) {

        WaitingTimeTask waitingTimeTask = new WaitingTimeTask(this);
        waitingTimeTask.execute(stopCode);

        showProgress();
    }

    public void updateWaitingTimeSolutions(List<TransitItem> newTransitsSolution) {
        this.waitingTimeAdapter.refreshRecyclerView(newTransitsSolution);
    }

    public void showProgress() {
        this.noNoDataLayout.setVisibility(View.GONE);
        this.transitsRecyclerView.setVisibility(View.GONE);
        this.progressBarWaitingTime.setVisibility(View.VISIBLE);
    }

    public void dismissProgressDialog() {
        this.noNoDataLayout.setVisibility(View.GONE);
        this.progressBarWaitingTime.setVisibility(View.GONE);
    }

    private void initEvents() {
        this.btnNoDataRetry.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            this.doGetWaitingTime(this.stop.getStopCode());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showNotFoundStop() {
        this.transitsRecyclerView.setVisibility(View.GONE);
        this.noDataTextView.setText(getResources().getString(R.string.no_solutions_found));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    public void showWaitingTime(Stop stop) {
        this.initItems(stop);

        if (this.transitItems.size() > 0) {
            this.updateWaitingTimeSolutions(this.transitItems);
            this.transitsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            this.transitsRecyclerView.setVisibility(View.GONE);
            this.noDataTextView.setText(getResources().getString(R.string.no_solutions_found));
            this.noNoDataLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onInternalServerError() {
        dismissProgressDialog();
        this.transitsRecyclerView.setVisibility(View.GONE);
        this.noDataTextView.setText(getResources().getString(R.string.generic_error));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    public void onConnectionError() {
        dismissProgressDialog();
        this.transitsRecyclerView.setVisibility(View.GONE);
        this.noDataTextView.setText(getResources().getString(R.string.no_connection_error));
        this.noNoDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        this.noNoDataLayout.setVisibility(View.GONE);
        this.doGetWaitingTime(this.stop.getStopCode());
    }
}
