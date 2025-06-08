package com.szakikereso;

import org.springframework.context.ApplicationContext;

public class SpringContext {
    private static ApplicationContext applicationContext;
    public static void setContext(ApplicationContext ctx) {applicationContext=ctx;}
    public static ApplicationContext getContext() {return applicationContext;}
}
