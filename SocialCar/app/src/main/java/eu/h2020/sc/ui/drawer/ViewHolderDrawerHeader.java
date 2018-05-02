package eu.h2020.sc.ui.drawer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.utils.PicassoHelper;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class ViewHolderDrawerHeader extends RecyclerView.ViewHolder {

    private ImageView imageViewProfile;
    private TextView textViewEmail;
    private TextView textViewName;
    private SocialCarStore socialCarStore;

    public ViewHolderDrawerHeader(View itemView) {
        super(itemView);

        this.socialCarStore = SocialCarApplication.getInstance();

        this.textViewName = (TextView) itemView.findViewById(R.id.name);
        this.textViewEmail = (TextView) itemView.findViewById(R.id.email);
        this.imageViewProfile = (ImageView) itemView.findViewById(R.id.user_picture);
    }

    public void init() {

        User user = this.socialCarStore.getUser();

        if (user != null) {
            this.textViewName.setText(user.getName());
            this.textViewEmail.setText(user.getEmail());
            this.imageViewProfile.setVisibility(View.VISIBLE);

        } else {
            this.textViewName.setVisibility(View.GONE);
            this.textViewEmail.setVisibility(View.GONE);
        }

        PicassoHelper.loadCircleImage(itemView.getContext(), imageViewProfile, this.socialCarStore.retrieveUserPicture());

    }
}
