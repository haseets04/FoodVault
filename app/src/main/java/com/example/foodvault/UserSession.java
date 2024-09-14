package com.example.foodvault;

public class UserSession {
    private static UserSession instance;
    private Integer user_id;

    /* // Retrieve the user ID from the singleton class
    Integer userId = UserSession.getInstance().getUserSessionId();*/

    //private constructor to prevent instantiation from other classes
    UserSession() {
    }

    //get singleton instance of UserSession
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserSessionId(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUserSessionId() {
        return user_id;
    }

    public void clearSession() { //call at logout: UserSession.getInstance().clearSession();
        user_id = null;
    }

}
