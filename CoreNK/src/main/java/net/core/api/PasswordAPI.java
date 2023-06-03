package net.core.api;

import java.util.Random;

public class PasswordAPI extends API {
    @Override
    public String getAuthor() {
        return "xxFLORII";
    }

    @Override
    public double getVersion() {
        return 1.0;
    }

    @Override
    public String getName() {
        return "PasswordAPI";
    }

    public static String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            password.append(chars.charAt(randomIndex));
        }
        return password.toString();
    }
}
