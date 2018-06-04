package com.example.root.popularmoviesapp;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity implements AdapterMovie.AdapterMovieOnClickListener, AdapterView.OnItemSelectedListener {
    @BindView(R.id.movie_list) RecyclerView movieList;
    @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;
    @BindView(R.id.no_data_view) TextView noDataView;
    private AdapterMovie mAdapter;
    private final Callback<ResponseMovie> ResponseMovieCallback = new Callback<ResponseMovie>() {
        @Override
        public void onResponse(@NonNull Call<ResponseMovie> call, @NonNull Response<ResponseMovie> response) {
            handleResponse(response);
        }
        @Override
        public void onFailure(@NonNull Call<ResponseMovie> call, @NonNull Throwable t) {
            handleFailure();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, Const.SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        movieList.setLayoutManager(layoutManager);
        mAdapter = new AdapterMovie(this);
        movieList.setAdapter(mAdapter);
        fetchPopular();
    }
    private void fetchPopular() {
        setViewLoadingState();
        if (Cl.getInstance().isOnline(this)) {
            Cl.getInstance().movieDatabaseService.popularMovies().enqueue(ResponseMovieCallback);
        } else {
            handleNoInternetConnection();
        }
    }
    private void fetchTopRated() {
        setViewLoadingState();
        if (Cl.getInstance().isOnline(this)) {
            Cl.getInstance().movieDatabaseService.topRatedMovies().enqueue(ResponseMovieCallback);
        } else {
            handleNoInternetConnection();
        }
    }
    private void handleResponse(Response<ResponseMovie> response) {
        ResponseMovie ResponseMovie = response.body();
        if (ResponseMovie != null && !ResponseMovie.getResults().isEmpty()) {
            handleSuccess(ResponseMovie.getResults());
        } else {
            handleFailure();
        }
    }
    private void handleFailure() {
        setFailureState();
        String failureMessage = getResources().getString(R.string.something_went_wrong);
        Toast.makeText(this, failureMessage, Toast.LENGTH_LONG).show();
    }
    private void handleNoInternetConnection() {
        setFailureState();
        String noInternetConnectionMessage = getResources().getString(R.string.no_internet_connection);
        Toast.makeText(this, noInternetConnectionMessage, Toast.LENGTH_LONG).show();
    }
    private void handleSuccess(List<Movie> movies) {
        setViewLoadedState();
        mAdapter.setDataSource(movies);
    }
    private void setViewLoadingState() {
        movieList.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
        noDataView.setVisibility(View.INVISIBLE);
    }
    private void setViewLoadedState() {
        movieList.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        noDataView.setVisibility(View.INVISIBLE);
    }
    private void setFailureState() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        if (mAdapter.getItemCount() > 0) {
            movieList.setVisibility(View.VISIBLE);
            noDataView.setVisibility(View.INVISIBLE);
        } else {
            movieList.setVisibility(View.INVISIBLE);
            noDataView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (pos) {
            case 0: {
                fetchPopular();
                break;
            }
            default: {
                fetchTopRated();
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {// Do nothing
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spinner, menu);
        MenuItem item = menu.findItem(R.id.spinner_item);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_by_options, R.layout.item_movie);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return true;
    }
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, ActivityMovie.class);
        intent.putExtra(Const.MOVIE_EXTRA, movie);
        startActivity(intent);
    }
}