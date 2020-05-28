package com.example.consigliaviaggi;

public interface PresenterToRecensioneDAO {

    void writeReview(String titolo, String corpo, int usernamecheck, int rating, String usernameutente);
    void obtainReviewsByUsername(String username);
    void obtainReviewsById(int id, int rating);
}
