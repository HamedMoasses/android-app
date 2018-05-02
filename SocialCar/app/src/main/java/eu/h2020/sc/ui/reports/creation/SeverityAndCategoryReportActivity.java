package eu.h2020.sc.ui.reports.creation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.HashMap;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.report.Category;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.domain.report.Severity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.TextViewCustom;

public class SeverityAndCategoryReportActivity extends GeneralActivity implements View.OnClickListener {

    private ImageView imageViewReportWorks;
    private ImageView imageViewReportTraffic;
    private ImageView imageViewReportAccident;

    private ImageView imageViewReportLow;
    private ImageView imageViewReportMedium;
    private ImageView imageViewReportHigh;

    private TextViewCustom textViewCustomReportLow;
    private TextViewCustom textViewCustomReportMedium;
    private TextViewCustom textViewCustomReportHigh;

    private Button buttonReportNext;

    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_severity_category_report);
        this.initUI();
        this.report = new Report();
    }

    private void initUI() {
        this.imageViewReportWorks = (ImageView) findViewById(R.id.image_view_report_works);
        this.imageViewReportTraffic = (ImageView) findViewById(R.id.image_view_report_traffic);
        this.imageViewReportAccident = (ImageView) findViewById(R.id.image_view_report_accident);

        this.imageViewReportLow = (ImageView) findViewById(R.id.image_view_report_low);
        this.imageViewReportMedium = (ImageView) findViewById(R.id.image_view_report_medium);
        this.imageViewReportHigh = (ImageView) findViewById(R.id.image_view_report_high);

        this.textViewCustomReportLow = (TextViewCustom) findViewById(R.id.text_view_report_low);
        this.textViewCustomReportMedium = (TextViewCustom) findViewById(R.id.text_view_report_medium);
        this.textViewCustomReportHigh = (TextViewCustom) findViewById(R.id.text_view_report_high);

        this.buttonReportNext = (Button) findViewById(R.id.button_report_next);
    }

    public void closeActivity(View view) {
        this.finish();
    }

    public void setReportCategory(View view) {
        switch (view.getId()) {
            case R.id.image_view_report_works:
                this.imageViewReportWorks.setImageResource(R.mipmap.img_report_work_selected);
                this.imageViewReportTraffic.setImageResource(R.mipmap.img_report_traffic);
                this.imageViewReportAccident.setImageResource(R.mipmap.img_report_accident);
                this.report.setCategory(Category.WORKS);
                break;
            case R.id.image_view_report_traffic:
                this.imageViewReportWorks.setImageResource(R.mipmap.img_report_work);
                this.imageViewReportTraffic.setImageResource(R.mipmap.img_report_traffic_selected);
                this.imageViewReportAccident.setImageResource(R.mipmap.img_report_accident);
                this.report.setCategory(Category.TRAFFIC);
                break;
            case R.id.image_view_report_accident:
                this.imageViewReportWorks.setImageResource(R.mipmap.img_report_work);
                this.imageViewReportTraffic.setImageResource(R.mipmap.img_report_traffic);
                this.imageViewReportAccident.setImageResource(R.mipmap.img_report_accident_selected);
                this.report.setCategory(Category.ACCIDENT);
                break;
        }
        this.initSeverityButton();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_report_low:
                this.imageViewReportLow.setImageResource(R.mipmap.img_report_level_low_selected);
                this.imageViewReportMedium.setImageResource(R.mipmap.img_report_level_medium);
                this.imageViewReportHigh.setImageResource(R.mipmap.img_report_level_high);
                this.imageViewReportMedium.setAlpha((float) 1);
                this.imageViewReportHigh.setAlpha((float) 1);
                this.report.setSeverity(Severity.LOW);
                break;
            case R.id.image_view_report_medium:
                this.imageViewReportLow.setImageResource(R.mipmap.img_report_level_low);
                this.imageViewReportMedium.setImageResource(R.mipmap.img_report_level_medium_selected);
                this.imageViewReportHigh.setImageResource(R.mipmap.img_report_level_high);
                this.imageViewReportLow.setAlpha((float) 1);
                this.imageViewReportHigh.setAlpha((float) 1);
                this.report.setSeverity(Severity.MEDIUM);
                break;
            case R.id.image_view_report_high:
                this.imageViewReportLow.setImageResource(R.mipmap.img_report_level_low);
                this.imageViewReportMedium.setImageResource(R.mipmap.img_report_level_medium);
                this.imageViewReportHigh.setImageResource(R.mipmap.img_report_level_high_selected);
                this.imageViewReportLow.setAlpha((float) 1);
                this.imageViewReportMedium.setAlpha((float) 1);
                this.report.setSeverity(Severity.HIGH);
                break;
        }
        this.initNextButton();
    }

    public void next(View view) {
        HashMap<String, Serializable> params = new HashMap();
        params.put(PositionReportActivity.REPORT_KEY, this.report);
        ActivityUtils.openActivityWithObjectParams(this, PositionReportActivity.class, params);
    }

    private void initSeverityButton() {
        this.imageViewReportLow.setAlpha((float) 1);
        this.imageViewReportMedium.setAlpha((float) 1);
        this.imageViewReportHigh.setAlpha((float) 1);

        this.textViewCustomReportLow.setAlpha((float) 1);
        this.textViewCustomReportMedium.setAlpha((float) 1);
        this.textViewCustomReportHigh.setAlpha((float) 1);

        this.imageViewReportLow.setOnClickListener(this);
        this.imageViewReportMedium.setOnClickListener(this);
        this.imageViewReportHigh.setOnClickListener(this);
    }

    private void initNextButton() {
        this.buttonReportNext.setAlpha((float) 1);
        this.buttonReportNext.setEnabled(true);
    }
}