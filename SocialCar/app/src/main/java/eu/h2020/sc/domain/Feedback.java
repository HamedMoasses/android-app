/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.domain;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import eu.h2020.sc.SocialCarApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Nicola
 */
public class Feedback implements Serializable {

    public static final String FEEDBACKS = "feedbacks";
    public static final String ROLE_DRIVER = "driver";
    public static final String ROLE_PASSENGER = "passenger";

    @SerializedName(value = "_id")
    @Expose(serialize = false)
    private String feedbackID;

    @SerializedName(value = "reviewed_id")
    @Expose
    private String reviewedID;

    @SerializedName(value = "reviewed_name")
    @Expose(serialize = false)
    private String reviewedName;

    @SerializedName(value = "reviewer_id")
    @Expose
    private String reviewerID;

    @SerializedName(value = "lift_id")
    @Expose
    private String liftID;

    @Expose(serialize = false)
    private String reviewer;

    @Expose
    private String review;

    @Expose
    private Integer rating;

    @Expose
    private String role;

    @Expose
    private Date date;

    @Expose(deserialize = false)
    private RatingFeature ratings;


    public Feedback(String reviewerID, String reviewedID, String liftID, Integer rating, Date date, String role) {
        this.reviewerID = reviewerID;
        this.reviewedID = reviewedID;
        this.liftID = liftID;
        this.rating = rating;
        this.date = date;
        this.role = role;
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public String getReviewer() {
        return reviewer;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getRole() {
        return role;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RatingFeature getRatings() {
        return ratings;
    }

    public void setRatings(RatingFeature ratings) {
        this.ratings = ratings;
    }

    public String getReviewedName() {
        return reviewedName;
    }

    public static List<Feedback> fromJsonList(String jsonFeedbackList) throws JSONException {

        JSONObject jsonResponse = new JSONObject(jsonFeedbackList);
        JSONArray feedback = jsonResponse.getJSONArray(Feedback.FEEDBACKS);

        return SocialCarApplication.getGson().fromJson(feedback.toString(), new TypeToken<List<Feedback>>() {
        }.getType());
    }

    public static Feedback fromJson(String feedbackJson) {
        return SocialCarApplication.getGson().fromJson(feedbackJson, Feedback.class);
    }

    public String toJson() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }
}
