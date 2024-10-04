package com.krysenko4sky.auth.service;

public interface PasswordService {

    String hashPassword(String password);

    boolean isPasswordCorrect(String plainPassword, String hashedPassword);

}
