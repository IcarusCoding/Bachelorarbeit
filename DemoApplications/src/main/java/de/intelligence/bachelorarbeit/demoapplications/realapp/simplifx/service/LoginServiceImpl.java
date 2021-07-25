package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.service;

public final class LoginServiceImpl implements ILoginService {

    @Override
    public boolean login(String username, String password) {
        return username.equals(password);
    }

}
