package com.example.project;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Address {
    private String apartmentNum;
    private String streetNum;
    private String city;
    private String postalCode;


    public Address(String apartmentNum, String streetNum, String city, String postalCode) {
        this.apartmentNum = apartmentNum;
        this.streetNum = streetNum;
        this.city = city;
        this.postalCode = postalCode;
    }

    public Address(){}//defaul constructor

    public static boolean isValidCanadianPostalCode(String postalCode) {
        // Regular expression for Canadian postal code (case-insensitive)
        String postalCodePattern = "^[A-Z]\\d[A-Z] ?\\d[A-Z]\\d$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(postalCodePattern);
        Matcher matcher = pattern.matcher(postalCode);

        // Return true if the postal code matches the pattern
        return matcher.matches();
    }


    public String getApartmentNum() {
        return apartmentNum;
    }

    public void setApartmentNum(String apartmentNum) {
        this.apartmentNum = apartmentNum;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}

