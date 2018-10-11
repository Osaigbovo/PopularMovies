package com.osaigbovo.udacity.popularmovies.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.osaigbovo.udacity.popularmovies.data.model.Crew;
import com.osaigbovo.udacity.popularmovies.data.model.Genre;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
* @author Osaigbovo Odiase.
* */
public class ViewsUtils {

    private ViewsUtils() {
    }

    public static final Property<Drawable, Integer> DRAWABLE_ALPHA
            = AnimUtils.createIntProperty(new AnimUtils.IntProp<Drawable>("alpha") {
        @Override
        public void set(Drawable drawable, int alpha) {
            drawable.setAlpha(alpha);
        }

        @Override
        public int get(Drawable drawable) {
            return drawable.getAlpha();
        }
    });

    public static String getYearOfRelease(String releaseDate) {
        String[] parts = releaseDate.split("-");
        return parts[0];
    }

    public static String getDate(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date newDate = null;
        try {
            newDate = df.parse(dateString);
            df = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df.format(newDate);
    }

    public static String getDisplayRuntime(int runtime) {
        int hours = runtime / 60;
        int minutes = runtime % 60;
        return String.valueOf(hours + " h. " + minutes + " m.");
    }

    public static String getDisplayGenres(ArrayList<Genre> genres) {
        if (genres.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(genres.get(0).getName());
        for (int i = 1; i < genres.size(); i++) {
            sb.append(", ").append(genres.get(i).getName());
        }
        return sb.toString();
    }

    public static String getDisplayDirecting(ArrayList<Crew> crews) {
        if (crews.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < crews.size(); i++) {
            if (crews.get(i).getJob().equals("Director")) {
                sb.append(crews.get(i).getName());
            }
        }
        return sb.toString();
    }

    public static Crew getDisplayDirector(@NonNull ArrayList<Crew> crews) {
        if (crews.isEmpty()) return null;
        Crew crew = null;

        for (int i = 0; i < crews.size(); i++) {
            if (crews.get(i).getJob().equals("Director")) {
                crew = crews.get(i);
            }
        }
        return crew;
    }

    public static final ViewOutlineProvider CIRCULAR_OUTLINE = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getWidth() - view.getPaddingRight(),
                    view.getHeight() - view.getPaddingBottom());
        }
    };

    /**
     * Converts drawable to bitmap
     */
    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
