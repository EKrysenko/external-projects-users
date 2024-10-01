package com.krysenko4sky.service;

public interface PasswordService {

    String hashPassword(String password);

    boolean checkPassword(String plainPassword, String hashedPassword);

}
