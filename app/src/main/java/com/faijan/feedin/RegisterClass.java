package com.faijan.feedin;

public class RegisterClass {

    private String UserId, Name, Email, Mobile, Password, Address, ProfilePic, UserType, LastActive, GeoIp, OrganizationName, OrganizationCertificate;
    private int TotalDonation, TotalFlames;
    public RegisterClass() {
    }


    public RegisterClass(String userId , String name , String email , String mobile , String password , String address , String profilePic , String userType , String lastActive , String geoIp , int totalDonation , int  totalFlames,String organizationName , String organizationCertificate) {
        UserId = userId;
        Name = name;
        Email = email;
        Mobile = mobile;
        Password = password;
        Address = address;
        ProfilePic = profilePic;
        UserType = userType;
        LastActive = lastActive;
        GeoIp = geoIp;
        TotalDonation = totalDonation;
        TotalFlames = totalFlames;
        OrganizationName = organizationName;
        OrganizationCertificate = organizationCertificate;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getLastActive(String date) {
        return LastActive;
    }

    public void setLastActive(String lastActive) {
        LastActive = lastActive;
    }

    public String getGeoIp() {
        return GeoIp;
    }

    public void setGeoIp(String geoIp) {
        GeoIp = geoIp;
    }

    public int getTotalDonation() {
        return TotalDonation;
    }

    public void setTotalDonation(int totalDonation) {
        TotalDonation = totalDonation;
    }

    public int getTotalFlames() {
        return TotalFlames;
    }

    public void setTotalFlames(int totalFlames) {
        TotalFlames = totalFlames;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public String getOrganizationCertificate() {
        return OrganizationCertificate;
    }

    public void setOrganizationCertificate(String organizationCertificate) {
        OrganizationCertificate = organizationCertificate;
    }
}
