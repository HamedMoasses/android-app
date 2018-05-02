package eu.h2020.sc.domain.report;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;

/**
 * Created by pietro on 20/07/2017.
 */
public class Report implements Serializable {

    public static final String JSON_KEY_REPORTS = "reports";

    @Expose(serialize = false)
    @SerializedName("_id")
    private String id;

    @Expose
    @SerializedName("category")
    private Category category;

    @Expose
    @SerializedName("severity")
    private Severity severity;

    @Expose
    @SerializedName("source")
    private Source source;

    @Expose(serialize = false)
    @SerializedName("timestamp")
    private Date timestamp;

    @Expose
    @SerializedName("location")
    private Location location;

    public Report() {
        this.source = Source.USER;
    }

    public Category getCategory() {
        return category;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static List<Report> fromJsonToReportList(String jsonReports) throws JSONException {
        JSONObject objectStopList = new JSONObject(jsonReports);
        JSONArray arrayStopList = objectStopList.getJSONArray(JSON_KEY_REPORTS);
        return SocialCarApplication.getGson().fromJson(arrayStopList.toString(), new TypeToken<List<Report>>() {
        }.getType());
    }

    public static Report fromJsonToReport(String jsonReport) throws JSONException {
        return SocialCarApplication.getGson().fromJson(jsonReport, Report.class);
    }

    public String toJson() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }

    public String generateReportMessage() {
        return String.format(SocialCarApplication.getContext().getString(R.string.report_message), this.getLocation().getAddress());
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", category=" + category +
                ", severity=" + severity +
                ", source=" + source +
                ", timestamp=" + timestamp +
                ", location=" + location +
                '}';
    }
}