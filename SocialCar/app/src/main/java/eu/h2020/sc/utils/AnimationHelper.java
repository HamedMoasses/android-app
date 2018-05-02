package eu.h2020.sc.utils;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;


/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class AnimationHelper {
    public static int lastAnimatedPosition;
    public static boolean animationsLocked;

    public static void runEnterAnimationRow(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            ViewHelper.setTranslationY(view, 100);
            ViewHelper.setAlpha(view, 0.f);
            com.nineoldandroids.view.ViewPropertyAnimator.animate(view)
                    .translationY(0).alpha(1.f)
                    .setStartDelay(40 * (position))
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }
}
