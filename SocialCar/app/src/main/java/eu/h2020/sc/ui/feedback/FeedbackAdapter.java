package eu.h2020.sc.ui.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.Feedback;

/**
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 16/09/2016.
 * © All rights reserved by Movenda S.p.A..
 */
public class FeedbackAdapter {

    private LinearLayout feedbackLinearLayout;
    private RelativeLayout feedbackLayout;
    private LayoutInflater layoutInflater;

    public FeedbackAdapter(Context context, LinearLayout feedbackLinearLayout) {
        this.layoutInflater = LayoutInflater.from(context);
        this.feedbackLinearLayout = feedbackLinearLayout;
        this.feedbackLayout = (RelativeLayout) layoutInflater.inflate(R.layout.row_review, feedbackLinearLayout, false);
    }

    public void createFeedbackView(List<Feedback> feedback) {

        for (Feedback f : feedback) {
            FeedbackViewHolder feedbackView = new FeedbackViewHolder(this.layoutInflater,this.feedbackLayout);
            feedbackView.build(f);
            this.feedbackLinearLayout.addView(feedbackView.getFeedbackView());
        }
    }

}
