package com.example.marianodato.identifyme_android_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("dni")
    @Expose
    private Long dni;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;

    @SerializedName("fingerprintId")
    @Expose
    private Long fingerprintId;

    @SerializedName("fingerprintStatus")
    @Expose
    private String fingerprintStatus;

    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;

    @SerializedName("lastUpdated")
    @Expose
    private String lastUpdated;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String username, String password, String name, Long dni, String gender, String email, String phoneNumber, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.dni = dni;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
    }

    public User(String password, String name, Long dni, String gender, String email, String phoneNumber, boolean isAdmin, String userFingerprintStatus) {
        this.password = password;
        this.name = name;
        this.dni = dni;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.fingerprintStatus = userFingerprintStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(Long fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public String getFingerprintStatus() {
        return fingerprintStatus;
    }

    public void setFingerprintStatus(String fingerprintStatus) {
        this.fingerprintStatus = fingerprintStatus;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}