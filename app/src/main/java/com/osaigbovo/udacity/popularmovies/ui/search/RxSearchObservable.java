package com.osaigbovo.udacity.popularmovies.ui.search;

import android.util.Log;
import android.widget.SearchView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * github.com/amitshekhariitbhu/RxJava2-Android-Samples/blob/master/app/src/main/java/com/rxjava2/
 * android/samples/ui/search/RxSearchObservable.java
 */
public class RxSearchObservable {

    private static final String TAG = RxSearchObservable.class.getName();

    private RxSearchObservable() {
        // no instance
    }

    public static Observable<String> fromView(SearchView searchView) {

        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                subject.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange:" + newText);
                subject.onNext(newText);
                return true;
            }
        });

        return subject;
    }
}