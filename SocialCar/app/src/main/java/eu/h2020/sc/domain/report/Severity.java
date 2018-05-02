package eu.h2020.sc.domain.report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pietro on 20/07/2017.
 */

public enum Severity {

    @SerializedName("LOW")
    @Expose
    LOW,

    @SerializedName("MEDIUM")
    @Expose
    MEDIUM,

    @SerializedName("HIGH")
    @Expose
    HIGH
}
