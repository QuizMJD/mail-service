package vn.hub.mailservice.service;

import vn.hub.mailservice.entity.LoginHistory;

public interface LoginService {
    LoginHistory login(String username, String password);

    LoginHistory logout(String username);

    LoginHistory isUserLoggedIn(String username);

    int getTotalWorkingMinutesForToday(String username);

    void calculateAndNotifyWorkingHours();
}