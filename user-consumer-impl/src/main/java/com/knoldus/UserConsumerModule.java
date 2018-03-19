package com.knoldus;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.api.ServiceInfo;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class UserConsumerModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServiceInfo(ServiceInfo.of("user-consumer-service"));
        //bindService(UserConsumerService.class, UserConsumerServiceImpl.class);
        bindClient(UserConsumerService.class);
        bind(UserConsumerClass.class).asEagerSingleton();
    }
}