package com.example.root.popularmoviesapp;
import java.util.List;
class ResponseMovie {
    private final List<Movie> results;
    public ResponseMovie(List<Movie> results) {
        this.results = results;
    }
    public List<Movie> getResults() {
        return results;
    }
}