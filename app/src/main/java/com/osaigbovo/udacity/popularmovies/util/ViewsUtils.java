package com.osaigbovo.udacity.popularmovies.util;

import android.graphics.drawable.Drawable;
import android.util.Property;

import com.osaigbovo.udacity.popularmovies.data.model.Genre;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    public static String getTime(String dateString) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            Date date = format1.parse(dateString);
            DateFormat sdf = new SimpleDateFormat("h:mm a");
            Date netDate = (date);
            return sdf.format(netDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "xx";
        }
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
}
