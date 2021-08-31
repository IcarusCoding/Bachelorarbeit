package de.intelligence.bachelorarbeit.demoapplications.service;

public final class LoginServiceImpl implements ILoginService {

    @Override
    public boolean login(String username, String password) {
        return username.equals(password);
    }

}
