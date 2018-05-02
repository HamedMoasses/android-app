package eu.h2020.sc.ui.feedback;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.PicassoHelper;

/**
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 16/09/2016.
 * © All rights reserved by Movenda S.p.A..
 */
public class FeedbackViewHolder {

    private RelativeLayout feedbackView;

    public FeedbackViewHolder(LayoutInflater layoutInflater, RelativeLayout feedbackLayout) {
        this.feedbackView = (RelativeLayout) layoutInflater.inflate(R.layout.row_review, feedbackLayout, false);
    }

    public RelativeLayout getFeedbackView() {
        return feedbackView;
    }

    public void build(Feedback feedback) {

        this.initStars(feedbackView, feedback);

        TextView textViewWriter = (TextView) feedbackView.findViewById(R.id.row_feedback_text_view_writer);
        textViewWriter.setText(feedback.getReviewer());

        checkReview(feedbackView, feedback);

        TextView textViewDate = (TextView) feedbackView.findViewById(R.id.textViewFeedbackDate);
        textViewDate.setText(DateUtils.dateLongToStringCanonic(feedback.getDate()));

        ImageView userAvatar = (ImageView) feedbackView.findViewById(R.id.row_feedback_image_view_default_avatar);
        PicassoHelper.loadPictureByUserID(feedbackView.getContext(), userAvatar, feedback.getReviewerID(), R.drawable.img_default_feedback_avatar, null, true);
    }

    private void initStars(RelativeLayout feedbackView, Feedback feedback) {

        ImageView imageViewStar1 = (ImageView) feedbackView.findViewById(R.id.star1);
        ImageView imageViewStar2 = (ImageView) feedbackView.findViewById(R.id.star2);
        ImageView imageViewStar3 = (ImageView) feedbackView.findViewById(R.id.star3);
        ImageView imageViewStar4 = (ImageView) feedbackView.findViewById(R.id.star4);
        ImageView imageViewStar5 = (ImageView) feedbackView.findViewById(R.id.star5);

        ImageView[] imageViewsStars = new ImageView[]{imageViewStar1, imageViewStar2, imageViewStar3, imageViewStar4,
                imageViewStar5};

        for (int i = 0; i < feedback.getRating(); i++) {
            imageViewsStars[i].setColorFilter(ContextCompat.getColor(feedbackView.getContext(), R.color.primary_color));
        }
    }

    private void checkReview(RelativeLayout feedbackView, Feedback feedback) {
        TextView textViewReview = (TextView) feedbackView.findViewById(R.id.text_view_review);

        if (feedback.getReview() != null && !feedback.getReview().isEmpty()) {
            textViewReview.setText(feedback.getReview());
        } else {
            textViewReview.setVisibility(View.GONE);
        }
    }
}
