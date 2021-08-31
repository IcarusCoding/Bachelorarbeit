package de.intelligence.bachelorarbeit.simplifx.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.intelligence.bachelorarbeit.simplifx.di.DIEnvironment;

/**
 * An implementation of the {@link DIEnvironment} interface which manages a spring {@link AnnotationConfigApplicationContext}
 * for dependency injection.
 */
final class SpringEnvironment implements DIEnvironment {

    private final AnnotationConfigApplicationContext ctx;

    /**
     * Creates a new instance of this {@link DIEnvironment}.
     *
     * @param modules An array of spring configuration module classes.
     */
    SpringEnvironment(Class<?>... modules) {
        this.ctx = new AnnotationConfigApplicationContext();
        this.ctx.register(modules);
        this.ctx.refresh();
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
