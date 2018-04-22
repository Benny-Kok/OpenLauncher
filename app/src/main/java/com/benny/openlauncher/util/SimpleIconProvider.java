package com.benny.openlauncher.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.benny.openlauncher.interfaces.IconDrawer;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.viewutil.GroupIconDrawable;

public class SimpleIconProvider extends BaseIconProvider {

    protected Drawable _drawable;
    protected int _drawableResource;

    public SimpleIconProvider(Drawable drawable) {
        _drawable = drawable;
        _drawableResource = -1;
    }

    public SimpleIconProvider(int drawableResource) {
        _drawable = null;
        _drawableResource = drawableResource;
    }

    private Drawable getDrawable() {
        if (_drawable != null) {
            return _drawable;
        } else if (_drawableResource > 0) {
            return Setup.appContext().getResources().getDrawable(_drawableResource);
        }
        return null;
    }

    @Override
    public void loadIcon(IconTargetType type, int forceSize, Object target, Object... args) {
        switch (type) {
            case ImageView: {
                ImageView iv = (ImageView) target;
                Drawable d = getDrawable();
                d = scaleDrawable(d, forceSize);
                iv.setImageDrawable(d);
                break;
            }
            case TextView: {
                TextView tv = (TextView) target;
                int gravity = (Integer) args[0];
                Drawable d = getDrawable();
                d = scaleDrawable(d, forceSize);
                if (gravity == Gravity.LEFT || gravity == Gravity.START) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
                } else if (gravity == Gravity.RIGHT || gravity == Gravity.END) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
                } else if (gravity == Gravity.TOP) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
                } else if (gravity == Gravity.BOTTOM) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, d);
                }
                break;
            }
            case IconDrawer: {
                IconDrawer iconDrawer = (IconDrawer) target;
                int index = (Integer) args[0];
                // we simply load the drawable in a synchronised way
                iconDrawer.onIconAvailable(getDrawable(), index);
                break;
            }
        }
    }

    @Override
    public void cancelLoad(IconTargetType type, Object target) {
        // nothing to cancel... we load everything in an synchronous way
    }

    @Override
    public Drawable getDrawableSynchronously(int forceSize) {
        Drawable d = getDrawable();
        d = scaleDrawable(d, forceSize);
        return d;
    }

    @Override
    public boolean isGroupIconDrawable() {
        return _drawable != null && _drawable instanceof GroupIconDrawable;
    }

    private Drawable scaleDrawable(Drawable drawable, int forceSize) {
        if (drawable != null && forceSize != Definitions.NO_SCALE) {
            forceSize = Tool.dp2px(forceSize, Setup.appContext());
            drawable = new BitmapDrawable(Setup.appContext().getResources(), Bitmap.createScaledBitmap(Tool.drawableToBitmap(drawable), forceSize, forceSize, true));
        }
        return drawable;
    }
}