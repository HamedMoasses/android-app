package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 11/10/2016.
 * © All rights reserved by Movenda S.p.A..
 */

public abstract class Picture implements Serializable {

    public static final String PICTURES = "pictures";

    @Expose
    @SerializedName(value = "_id")
    protected String id;

    @Expose
    @SerializedName(value = "file")
    protected String mediaUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id='" + id + '\'' +
                ", mediaUri='" + mediaUri + '\'' +
                '}';
    }
}
