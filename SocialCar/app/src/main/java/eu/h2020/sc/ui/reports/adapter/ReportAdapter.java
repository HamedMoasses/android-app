package eu.h2020.sc.ui.reports.adapter;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.utils.AnimationHelper;
import eu.h2020.sc.utils.DateUtils;

/**
 * Created by pietro on 20/07/2017.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportViewHolder> {

    private List<Report> reports;

    public ReportAdapter(List<Report> reports) {
        this.reports = reports;
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewReportRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reports_row, parent, false);
        return new ReportViewHolder(viewReportRow);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder reportViewHolder, int position) {
        Report report = this.getItem(position);

        report = resolveUnknownAddress(report);

        reportViewHolder.getTextViewReportMessage().setText(report.getLocation().getAddress());

        Date reportTimeStamp = report.getTimestamp();
        String hour = DateUtils.formatToHoursAndMinutes(reportTimeStamp, DateFormat.is24HourFormat(SocialCarApplication.getContext()));
        reportViewHolder.getTextViewReportHour().setText(hour);
        reportViewHolder.getTextViewReportDate().setText(DateUtils.dateLongToStringCanonic(reportTimeStamp));

        switch (report.getSeverity()) {
            case LOW:
                reportViewHolder.getTextViewReportSeverity().setText(SocialCarApplication.getContext().getResources().getString(R.string.report_severity_low));
                reportViewHolder.getTextViewReportSeverity().setTextColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.primary_color));
                break;
            case MEDIUM:
                reportViewHolder.getTextViewReportSeverity().setText(SocialCarApplication.getContext().getResources().getString(R.string.report_severity_medium));
                reportViewHolder.getTextViewReportSeverity().setTextColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.yellow));
                break;
            case HIGH:
                reportViewHolder.getTextViewReportSeverity().setText(SocialCarApplication.getContext().getResources().getString(R.string.report_severity_high));
                reportViewHolder.getTextViewReportSeverity().setTextColor(ContextCompat.getColor(SocialCarApplication.getContext(), R.color.red));
                break;
            default:
                break;
        }

        switch (report.getCategory()) {
            case TRAFFIC:
                reportViewHolder.getTextViewReportCategory().setText(SocialCarApplication.getContext().getResources().getString(R.string.report_category_traffic));
                break;
            case WORKS:
                reportViewHolder.getTextViewReportCategory().setText(SocialCarApplication.getContext().getResources().getString(R.string.report_category_works));
                break;
            case ACCIDENT:
                reportViewHolder.getTextViewReportCategory().setText(SocialCarApplication.getContext().getResources().getString(R.string.report_category_accident));
                break;
            default:
                break;
        }
    }

    private Report resolveUnknownAddress(Report report) {
        try {
            Geocoder geoCoder = new Geocoder(SocialCarApplication.getContext());
            String address = report.getLocation().getAddress().trim().toLowerCase();
            if (address.isEmpty() || address.contains("null") || address.contains("unknown")) {
                double latitude = report.getLocation().getGeometry().getCoordinateList().get(1);
                double longitude = report.getLocation().getGeometry().getCoordinateList().get(0);
                List<Address> matches = null;
                matches = geoCoder.getFromLocation(latitude, longitude, 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                if (bestMatch != null) {
                    report.getLocation().setAddress(bestMatch.getAddressLine(0));
                }
            }
        } catch (Exception e) {
            return report;
        }
        return report;
    }

    @Override
    public int getItemCount() {
        return this.reports.size();
    }


    private Report getItem(int position) {
        return this.reports.get(position);
    }


    public void refreshRecyclerView(List<Report> reports) {
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.reports.clear();
        this.reports.addAll(reports);
        this.notifyDataSetChanged();
    }
}
