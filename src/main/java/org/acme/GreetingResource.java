package org.acme;

import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/hello")
public class GreetingResource {

    @Inject
    MyConfig myConfig;

    @ConfigProperty(name = "mycfg.x.value")
    String xValue;

    @ConfigProperty(name = "mycfg.y.value")
    String yValue;

    @ConfigProperty(name = "mycfg.z.value")
    String zValue;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        String mappedValues = myConfig.innerConfigs().entrySet().stream()
            .map(e -> "%s=%s".formatted(e.getKey(), e.getValue().value()))
            .collect(Collectors.joining(", "));
        return "I have %d inner configs - mapped values: %s - explicit values: x=%s, y=%s, z=%s"
            .formatted(myConfig.innerConfigs().size(), mappedValues, xValue, yValue, zValue);
    }

}
