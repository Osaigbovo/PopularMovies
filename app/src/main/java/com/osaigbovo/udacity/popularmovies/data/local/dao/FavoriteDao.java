package com.osaigbovo.udacity.popularmovies.data.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.osaigbovo.udacity.popularmovies.data.local.entity.MovieDetail;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    LiveData<List<MovieDetail>> getFavoriteMoviess();

    @Query("SELECT * FROM favorite")
    Flowable<List<MovieDetail>> getFavoriteMovies();

    @Query("SELECT * FROM favorite ORDER BY title ASC")
    DataSource.Factory<Integer, MovieDetail> sortASCMovie();

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
