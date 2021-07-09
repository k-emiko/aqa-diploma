package data;

import com.github.javafaker.Faker;

import java.util.Locale;

public class CardDataGenerator {

    public static class DateGenerator {

        public static String RandomValidMonth(int min) {
            int month = (int) Math.floor(Math.random()*(12-min)+min);
            if (month < 10) {
                return "0" + month;
            }
            return String.valueOf(month);
        }

        public static int RandomValidYear(int max, int min) {
            return (int) Math.floor(Math.random()*(max-min)+min);
        }
    }

    public String generateRandomValidName(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.name().fullName();
    }

    public String generateRandomCvcCode() {
        int cvc = (int) Math.floor(Math.random()*(999-0+1)+100);
        if (cvc < 10) {
            return "00" + cvc;
        }
        else if (cvc < 100) {
            return "0" + cvc;
        }
        return String.valueOf(cvc);
    }

}
