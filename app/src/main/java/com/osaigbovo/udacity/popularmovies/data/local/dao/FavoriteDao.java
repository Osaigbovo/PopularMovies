package com.osaigbovo.udacity.popularmovies.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Data Access Object that contains methods used for accessing the database.
 *
 * @author Osaigbovo Odiase.
 */
@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    LiveData<List<MovieDetail>> getFavoriteMoviess();

    @Query("SELECT * FROM favorite")
    Flowable<List<MovieDetail>> getFavoriteMovies();

    @Query("SELECT * FROM favorite WHERE id=:id")
    LiveData<MovieDetail> isFavoriteMovie(int id);

    @Query("SELECT * from favorite where id = :id")
    LiveData<MovieDetail> getFavoriteMovie(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveFavoriteMovie(MovieDetail movieDetail);

    @Delete
    void deleteFavoriteMovie(MovieDetail movieDetail);

    @Query("DELETE FROM favorite WHERE id=:id")
    void deleteFavoriteMovie(int id);
}
