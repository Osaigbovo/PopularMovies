package com.osaigbovo.udacity.popularmovies.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.osaigbovo.udacity.popularmovies.data.local.converters.CastConverter;
import com.osaigbovo.udacity.popularmovies.data.local.converters.CrewConverter;
import com.osaigbovo.udacity.popularmovies.data.local.converters.GenreConverter;
import com.osaigbovo.udacity.popularmovies.data.local.converters.VideosConverter;
import com.osaigbovo.udacity.popularmovies.data.local.dao.FavoriteDao;
import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;

/**
 * The Room database that contains the FavoriteMovies table
 * Define an abstract class that extends RoomDatabase.
 * This class is annotated with @Database, lists the entities contained in the database,
 * and the DAOs which access them.
 */
@Database(entities = {MovieDetail.class}, version = 1/*, exportSchema = false*/)
@TypeConverters({GenreConverter.class, CastConverter.class, CrewConverter.class, VideosConverter.class})
public abstract class PopularMoviesDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();

}
