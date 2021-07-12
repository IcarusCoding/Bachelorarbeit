package de.intelligence.bachelorarbeit.simplifx.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

final class SpringEnvironment implements DIEnvironment {

    private final AnnotationConfigApplicationContext ctx;

    SpringEnvironment(Object obj, Class<?>[] modules) {
        this.ctx = new AnnotationConfigApplicationContext();
        this.ctx.register(modules);
        this.ctx.refresh();
        this.inject(obj);
    }

    @Override
    public void inject(Object obj) {
        this.ctx.getAutowireCapableBeanFactory().autowireBean(obj);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return this.ctx.getAutowireCapableBeanFactory().getBean(clazz);
    }

}
