package com.example.consigliaviaggi.model;

public class Utente {

    private static String username, password, nome, cognome, email;
    private static boolean isLogged;

    public static void setNome(String nome){ Utente.nome = nome; }

    public static void setCognome(String cognome){ Utente.cognome = cognome; }

    public static void setUsername(String username){ Utente.username=username; }

    public static void setPassword(String password){ Utente.password=password; }

    public static void setEmail(String email){ Utente.email=email; }

    public static void setLogged(Boolean isLogged){ Utente.isLogged=isLogged; }

    public static String getNome(){ return Utente.nome; }

    public static String getCognome(){ return Utente.cognome; }

    public static String getEmail(){ return Utente.email; }

    public static String getUsername(){ return Utente.username; }

    public static String getPassword(){ return Utente.password; }

    public static boolean isLogged(){ return Utente.isLogged; }
}