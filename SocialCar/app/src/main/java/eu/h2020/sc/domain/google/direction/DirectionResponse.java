package eu.h2020.sc.domain.google.direction;

import java.util.Collections;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;

/**
 * Created by Pietro on 01/09/16.
 */
public class DirectionResponse {

    private List<DirectionRoute> routes;
    private String status;

    public List<DirectionRoute> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static DirectionResponse fromJson(String jsonDirectionResponse) {
        return SocialCarApplication.getGson().fromJson(jsonDirectionResponse, DirectionResponse.class);
    }

}
