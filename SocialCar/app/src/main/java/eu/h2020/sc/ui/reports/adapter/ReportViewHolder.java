package eu.h2020.sc.ui.reports.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import eu.h2020.sc.R;

/**
 * Created by pietro on 20/07/2017.
 */

public class ReportViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewReportCategory;
    private TextView textViewReportSeverity;
    private TextView textViewReportMessage;
    private TextView textViewReportDate;
    private TextView textViewReportHour;


    public ReportViewHolder(View itemView) {
        super(itemView);
        this.textViewReportCategory = (TextView) itemView.findViewById(R.id.text_view_report_category);
        this.textViewReportSeverity = (TextView) itemView.findViewById(R.id.text_view_report_severity);
        this.textViewReportMessage = (TextView) itemView.findViewById(R.id.text_view_report_message);
        this.textViewReportDate = (TextView) itemView.findViewById(R.id.text_view_report_date);
        this.textViewReportHour = (TextView) itemView.findViewById(R.id.text_view_report_hour);
    }


    public TextView getTextViewReportCategory() {
        return textViewReportCategory;
    }

    public TextView getTextViewReportSeverity() {
        return textViewReportSeverity;
    }

    public TextView getTextViewReportMessage() {
        return textViewReportMessage;
    }

    public TextView getTextViewReportDate() {
        return textViewReportDate;
    }

    public TextView getTextViewReportHour() {
        return textViewReportHour;
    }
}
