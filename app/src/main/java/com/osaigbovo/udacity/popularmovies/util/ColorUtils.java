package com.osaigbovo.udacity.popularmovies.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

/**
 * Utility class for Color and Palette.
 *
 * @author Osaigbovo Odiase.
 */
public class ColorUtils {

    private ColorUtils() {
    }

    public static @Nullable
    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }

    public static final Palette.Filter WHITE_FILTER = new Palette.Filter() {
        private static final float BLACK_MAX_LIGHTNESS = 0.35F;
        private static final float WHITE_MIN_LIGHTNESS = 0.80F;

        public boolean isAllowed(int rgb, @NonNull float[] hsl) {
            return !this.isWhite(hsl) && !this.isBlack(hsl) && !this.isNearRedILine(hsl);
        }

        private boolean isBlack(float[] hslColor) {
            return hslColor[2] <= BLACK_MAX_LIGHTNESS;
        }

        private boolean isWhite(float[] hslColor) {
            return hslColor[2] >= WHITE_MIN_LIGHTNESS;
        }

        private boolean isNearRedILine(float[] hslColor) {
            return hslColor[0] >= 10.0F && hslColor[0] <= 37.0F && hslColor[1] <= 0.82F;
        }
    };

}
