/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package edu.pdx.its.portal.routelandia;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

/**
 * Created by Nasim on 2/14/2015.
 */
public class AnimationProgressDialog extends Dialog{
    private ImageView imageView;

    public AnimationProgressDialog(Context context, int imageDrawableId) {
        super(context, R.style.AnimationProgressDialog_ProgressDialog);
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(windowManager);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        imageView = new ImageView(context);
        imageView.setImageResource(imageDrawableId);
        layout.addView(imageView, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation anim = new RotateAnimation(180, 180.0f , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator()); //Defines the interpolator used to smooth the animation movement in time.
        anim.setRepeatCount(Animation.INFINITE); //Defines how many times the animation should repeat.
        anim.setDuration(4000); //Amount of time (in milliseconds) for the animation to run.
        imageView.setAnimation(anim);
        imageView.startAnimation(anim);
    }
}
