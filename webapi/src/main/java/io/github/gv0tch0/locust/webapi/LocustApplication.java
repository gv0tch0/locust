package io.github.gv0tch0.locust.webapi;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import io.github.gv0tch0.locust.core.BfLcs;
import io.github.gv0tch0.locust.core.Lcs;
import io.github.gv0tch0.locust.service.LocustService;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

/**
 * Configuration (web.xml, etc.), but in code.
 * @author Nik Kolev
 */
@ApplicationPath("/")
public class LocustApplication extends ResourceConfig {
    public LocustApplication() {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(BfLcs.class).to(Lcs.class);
                
                bindAsContract(LocustService.class).in(Singleton.class);
                bind(new LocustService()).to(LocustService.class);
            }
        });
        packages(true, LocustApplication.class.getPackage().getName());
    }
    
}
