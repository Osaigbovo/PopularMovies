package com.osaigbovo.udacity.popularmovies.ui.search;

import android.app.SearchManager;
import android.app.SharedElementCallback;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.TransitionRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.model.Movie;
import com.osaigbovo.udacity.popularmovies.data.model.SearchResponse;
import com.osaigbovo.udacity.popularmovies.ui.transitions.CircularReveal;
import com.osaigbovo.udacity.popularmovies.util.ImeUtils;
import com.osaigbovo.udacity.popularmovies.util.TransitionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = SearchActivity.class.getName();
    public static final String EXTRA_QUERY = "EXTRA_QUERY";
    public static final int RESULT_CODE_SAVE = 7;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SearchViewModel searchViewModel;

    @BindView(R.id.searchback)
    ImageButton searchBack;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(android.R.id.empty)
    ProgressBar progress;
    @BindView(R.id.search_results)
    RecyclerView searchRecyclerView;
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.results_container)
    ViewGroup resultsContainer;
    @BindView(R.id.scrim)
    View scrim;
    @BindView(R.id.results_scrim)
    View resultsScrim;

    SearchMoviesAdapter searchMoviesAdapter;
    private TextView noResults;
    private SparseArray<Transition> transitions = new SparseArray<>();
    private boolean focusQuery = true;

    LinearLayoutManager linearLayoutManager;
    List<Movie> searchMoviesList;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

        setupSearchView();

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        linearLayoutManager = new LinearLayoutManager(this);
        searchMoviesAdapter = new SearchMoviesAdapter(this, mTwoPane);

        searchRecyclerView.setAdapter(searchMoviesAdapter);
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setHasFixedSize(true);

        setupTransitions();
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    protected void onPause() {
        ImeUtils.hideIme(searchView);
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @OnClick({R.id.scrim, R.id.searchback})
    protected void dismiss() {
        // clear the background else the touch ripple moves with the translation which looks bad
        searchBack.setBackground(null);
        finishAfterTransition();
    }

    @Override
    public void onEnterAnimationComplete() {
        if (focusQuery) {
            // focus the search view once the enter transition finishes
            searchView.requestFocus();
            ImeUtils.showIme(searchView);
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);

        compositeDisposable.add(
                RxSearchObservable.fromView(searchView)
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .filter(text -> text.length() > 0)
                        .distinctUntilChanged()
                        .switchMap((Function<String, ObservableSource<SearchResponse>>) query -> searchViewModel
                                .search(query)
                                .subscribeOn(Schedulers.io()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(searchResponse -> {
                            progress.setVisibility(View.VISIBLE);
                            ImeUtils.hideIme(searchView);
                            searchView.clearFocus();

                            if (searchResponse.getResults() != null
                                    && searchResponse.getResults().size() > 0) {
                                searchMoviesList = searchResponse.getResults();
                                Log.d(TAG, "Result:" + searchMoviesList.get(0).getOriginalTitle());

                                if (searchRecyclerView.getVisibility() != View.VISIBLE) {
                                    setNoResultsVisibility(View.GONE);
                                    TransitionManager.beginDelayedTransition(container,
                                            getTransition(R.transition.search_show_results));
                                    progress.setVisibility(View.GONE);
                                    searchRecyclerView.setVisibility(View.VISIBLE);
                                    //fab.setVisibility(View.VISIBLE);
                                }
                                searchMoviesAdapter.setSearchMoviesList(searchMoviesList);
                            } else {
                                TransitionManager.beginDelayedTransition(
                                        container, getTransition(R.transition.auto));
                                progress.setVisibility(View.GONE);
                                setNoResultsVisibility(View.VISIBLE);
                            }

                        }, throwable -> {
                            Log.d(TAG, throwable.getMessage());
                            clearResults();
                        }));

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            /*if (hasFocus && confirmSaveContainer.getVisibility() == View.VISIBLE) {
                //hideSaveConfirmation();
            }*/
        });

    }

    void searchFor(String query) {
        //clearResults();
        progress.setVisibility(View.VISIBLE);
        ImeUtils.hideIme(searchView);
        searchView.clearFocus();
        searchViewModel.search(query);
    }

    void clearResults() {
        TransitionManager.beginDelayedTransition(container, getTransition(R.transition.auto));
        searchMoviesList.clear();
        searchRecyclerView.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        resultsScrim.setVisibility(View.GONE);
        setNoResultsVisibility(View.GONE);
    }

    void setNoResultsVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {
                noResults = (TextView) ((ViewStub)
                        findViewById(R.id.stub_no_search_results)).inflate();
                noResults.setOnClickListener(v -> {
                    searchView.setQuery("", false);
                    searchView.requestFocus();
                    ImeUtils.showIme(searchView);
                });
            }
            String message = String.format(
                    getString(R.string.no_search_results), searchView.getQuery().toString());
            SpannableStringBuilder ssb = new SpannableStringBuilder(message);
            ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                    message.indexOf('“') + 1,
                    message.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noResults.setText(ssb);
        }
        if (noResults != null) {
            noResults.setVisibility(visibility);
        }
    }

    private void setupTransitions() {
        // grab the position that the search icon transitions in *from*
        // & use it to configure the return transition
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(
                    List<String> sharedElementNames,
                    List<View> sharedElements,
                    List<View> sharedElementSnapshots) {
                if (sharedElements != null && !sharedElements.isEmpty()) {
                    View searchIcon = sharedElements.get(0);
                    if (searchIcon.getId() != R.id.searchback) return;
                    int centerX = (searchIcon.getLeft() + searchIcon.getRight()) / 2;
                    CircularReveal hideResults = (CircularReveal) TransitionUtils.findTransition(
                            (TransitionSet) getWindow().getReturnTransition(),
                            CircularReveal.class, R.id.results_container);
                    if (hideResults != null) {
                        hideResults.setCenter(new Point(centerX, 0));
                    }
                }
            }
        });
    }

    Transition getTransition(@TransitionRes int transitionId) {
        Transition transition = transitions.get(transitionId);
        if (transition == null) {
            transition = TransitionInflater.from(this).inflateTransition(transitionId);
            transitions.put(transitionId, transition);
        }
        return transition;
    }

}
