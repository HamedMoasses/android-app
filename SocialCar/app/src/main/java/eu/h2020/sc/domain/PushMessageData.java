package eu.h2020.sc.domain;

import java.io.Serializable;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class PushMessageData implements Serializable {

    private Lift lift;
    private User user;
    private byte[] picture;

    public PushMessageData(byte[] picture, Lift lift, User user) {
        this.picture = picture;
        this.user = user;
        this.lift = lift;
    }

    public User getUser() {
        return user;
    }

    public Lift getLift() {
        return lift;
    }

    public byte[] getPicture() {
        return picture;
    }
}
