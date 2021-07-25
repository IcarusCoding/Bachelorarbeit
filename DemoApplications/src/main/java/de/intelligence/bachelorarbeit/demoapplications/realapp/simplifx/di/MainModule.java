package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.di;

import com.google.inject.AbstractModule;

import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.service.ILoginService;
import de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx.service.LoginServiceImpl;

public final class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        super.bind(ILoginService.class).to(LoginServiceImpl.class);
    }

}
