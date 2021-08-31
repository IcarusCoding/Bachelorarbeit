package de.intelligence.bachelorarbeit.demoapplications.di;

import com.google.inject.AbstractModule;

import de.intelligence.bachelorarbeit.demoapplications.service.ILoginService;
import de.intelligence.bachelorarbeit.demoapplications.service.LoginServiceImpl;

public final class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        super.bind(ILoginService.class).to(LoginServiceImpl.class);
    }

}
