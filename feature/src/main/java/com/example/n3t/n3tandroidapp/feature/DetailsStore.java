package com.example.n3t.n3tandroidapp.feature;

public class DetailsStore {

    private static String IMEI= "";
    private static String firstName="";
    private static String lastName="";
    private static String typeOfDriver="";
    private static String age="";
    private static String gender="";
    private static String expOnOVR="";

    public static String getFirstName() {
        return firstName;
    }

    public static String getIMEI() {
        return IMEI;
    }

    public static void setIMEI(String IMEI) {
        DetailsStore.IMEI = IMEI;
    }

    public static void setFirstName(String firstName) {
        DetailsStore.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        DetailsStore.lastName = lastName;
    }

    public static String getTypeOfDriver() {
        return typeOfDriver;
    }

    public static void setTypeOfDriver(String typeOfDriver) {
        DetailsStore.typeOfDriver = typeOfDriver;
    }

    public static String getAge() {
        return age;
    }

    public static void setAge(String age) {
        DetailsStore.age = age;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        DetailsStore.gender = gender;
    }

    public static String getExpOnOVR() {
        return expOnOVR;
    }

    public static void setExpOnOVR(String expOnOVR) {
        DetailsStore.expOnOVR = expOnOVR;
    }
}
