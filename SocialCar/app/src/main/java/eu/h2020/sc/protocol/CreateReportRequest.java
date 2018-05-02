package eu.h2020.sc.protocol;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.transport.HttpConstants;


public class CreateReportRequest extends SocialCarRequest {

    public static final String REPORTS_URI = "/reports";
    private static final String TAG = CreateReportRequest.class.getSimpleName();
    private Report report;

    public CreateReportRequest(Report report) {
        this.report = report;
    }
    
    @Override
    public URL urllize() {

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(REPORTS_URI);

            return new URL(stringBuilderInitial.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }

        return null;
    }

    @Override
    public String getBody() {
        return this.report.toJson();
    }

    @Override
    public String getApiName() {
        return TAG;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.POST;
    }

}
