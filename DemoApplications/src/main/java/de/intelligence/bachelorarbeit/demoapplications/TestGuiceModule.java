package de.intelligence.bachelorarbeit.demoapplications;

import com.google.inject.AbstractModule;

public final class TestGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        super.bind(ITestService.class).to(TestServiceImpl.class);
    }

}
