package eu.h2020.sc.domain;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.utils.DateUtils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class User implements Serializable {

    public static final String PASSENGER = "passenger";
    public static final String DRIVER = "driver";
    public static final String EMAIL = "email";
    public static final String USERS = "users";
    public static final String DRIVER_ID = "driver_id";
    public static final String SOCIAL_PROVIDER = "social_provider";
    public static final String FCM_TOKEN = "fcm_token";


    @Expose(serialize = false)
    @SerializedName(value = "_id")
    private String id;
    @Expose
    @SerializedName(value = SOCIAL_PROVIDER)
    private SocialProvider socialProvider;
    @Expose
    private String password;
    @Expose
    private String name;
    @Expose
    private String email;
    @Expose
    private String phone;
    @Expose
    private String dob;
    @Expose
    private UserGender gender;
    @Expose
    private Integer rating;
    @Expose
    @SerializedName(value = FCM_TOKEN)
    private String fcmToken;
    @Expose
    private String platform;

    @Expose
    @SerializedName(value = "cars")
    private List<String> carsID;

    @Expose
    @SerializedName(value = "pictures")
    private List<UserPicture> userPictures;

    @Expose
    @SerializedName(value = "travel_preferences")
    private TravelPreferences travelPreferences;

    private transient boolean acceptedTerms;

    public User(String name) {
        super();
        this.name = name;
    }


    public int getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }

    public String getGenderString() {
        if (this.gender == UserGender.FEMALE)
            return SocialCarApplication.getContext().getString(R.string.female);

        return SocialCarApplication.getContext().getString(R.string.male);
    }

    public Integer obtainAvatarDefaultResource() {
        if (getGender() == UserGender.FEMALE)
            return R.mipmap.rounded_female_default_avatar;
        else
            return R.mipmap.rounded_male_default_avatar;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDob() {
        return dob;
    }

    public List<String> getCarsID() {
        if (this.carsID == null) {
            this.carsID = new ArrayList<>();
        }
        return Collections.unmodifiableList(carsID);
    }

    public void addPicture(UserPicture userPicture) {
        this.userPictures.add(0, userPicture);
    }

    public List<UserPicture> getUserPictures() {
        return userPictures;
    }

    public static User fromJson(String jsonUser) {
        return SocialCarApplication.getGson().fromJson(jsonUser, User.class);
    }

    public static User fromJsonList(String jsonListUser) throws JSONException, NotFoundException {

        JSONObject jsonResponse = new JSONObject(jsonListUser);
        JSONArray usersJson = jsonResponse.getJSONArray(User.USERS);

        ArrayList<User> listUsers = SocialCarApplication.getGson().fromJson(usersJson.toString(), new TypeToken<List<User>>() {
        }.getType());

        if (listUsers.isEmpty())
            throw new NotFoundException("List of users is empty...");

        return listUsers.get(0);
    }

    public String toJsonForRequest() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }

    public String toJson() {
        return SocialCarApplication.getGson().toJson(this);
    }

    public void addCarID(String carID) {
        if (this.carsID == null) {
            this.carsID = new ArrayList<>();
        }
        this.carsID.add(carID);
    }

    public boolean isDriver() {
        return this.carsID.size() > 0;
    }

    public boolean isGenderSet() {
        return (this.gender == UserGender.FEMALE || this.gender == UserGender.MALE);
    }

    public boolean isDobValid() {
        // TODO unnecessary
        try {
            DateUtils.parseDateDateOfBirth(this.dob);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isPhoneValid() {
        return !this.phone.isEmpty();
    }

    public boolean isEmailValid() {
        return Credentials.isEmailValid(this.email);
    }

    public boolean isUsernameValid() {
        return !this.name.isEmpty();
    }

    public boolean isPasswordValid() {
        return (!password.isEmpty() && !(password.length() < Globals.PASSWORD_LENGTH));
    }

    public SocialProvider getSocialProvider() {
        return socialProvider;
    }

    public void setSocialProvider(SocialProvider socialProvider) {
        this.socialProvider = socialProvider;
    }

    public static String getFirstNameFromCompleteName(String name) {
        return name.split(" ")[0];
    }

    public TravelPreferences getTravelPreferences() {
        return travelPreferences;
    }

    public void setTravelPreferences(TravelPreferences travelPreferences) {
        this.travelPreferences = travelPreferences;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", socialProvider=" + socialProvider +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dob='" + dob + '\'' +
                ", gender=" + gender +
                ", rating=" + rating +
                ", fcmToken='" + fcmToken + '\'' +
                ", platform='" + platform + '\'' +
                ", carsID=" + carsID +
                ", userPictures=" + userPictures +
                ", travelPreferences=" + travelPreferences +
                '}';
    }
}