# Quarkus ConfigMapping Issue

# About

This repo demonstrates the following issue. Imagine your app config (or parts of it) are stored in a HashiCorp Vault cluster. Your app is able to connect and authenticate against Vault, policies are set up correctly and you are able to access the secrets.

Now you want to use an interface class annotated with `@ConfigMapping` in your app. Furthermore your config shall be kind of dynamic and not limited to static properties, so your config class might look something like this (of course heavily simplfied):

```java
@ConfigMapping(prefix = "mycfg")
public interface MyConfig {

    @WithParentName
    Map<String, InnerConfig> innerConfigs();

    interface InnerConfig {
        String value();
    }

}
```

This usually allows for specifying the following config:

```properties
mycfg.x.value=d
mycfg.y.value=e
mycfg.z.value=f
```

So we have multiple "inner configs" with different values each. The `innerConfigs` map would have a size of 3. As long as we specifiy these properties in a **properties file** everything is **working as expected**.

If we store these properties in **Vault** however the `innerConfigs` map **won't get initialized** (size 0) although Quarkus (read: the Quarkus Vault client) was **perfectly able to read the secrets**.

We can even add some other (static) config properties to the `MyConfig` class and they would return the expected values from Vault. The issue seems to be related to the `Map`.

## Project Structure

The project is basically a simple application bootstrapped from `code.quarkus.io` with extensions `resteasy-reactive` and `vault` being added. The starter code provides an endpoint at `/hello` which responds with information about the number of "inner configs" in the map and the actual values of the config properties.

This application requires a Vault server running on `localhost:8200` which can easily be set up (including prepopulated secret at `/secret/myapp`) via the Docker Compose file at `vault/docker-compose.yaml`.

If you run the application using the Gradle `quarkusDev` task it connects against the local Vault server and adds path `myapp` below `secret` mount point as `VaultConfigSource`. Querying the `/hello` endpoint will yield the following:

```
I have 0 inner configs - mapped values:  - explicit values: x=a, y=b, z=c
```

This is unexpected. As you can see the secret values from `vault/prepopulate/myapp-secret.json` are being read correctly but the `MyConfig` class does not provide any "inner config".

If you start the app via `./gradlew quarkusDev -Dquarkus.profile=thisworks` then the config from `application-thisworks.properties` kicks in and the `/hello` endpoint responds with:

```
I have 3 inner configs - mapped values: x=a, y=b, z=c - explicit values: x=a, y=b, z=c
```

So now the number of "inner configs" is correct but interestingly their values are not the values from the properties file but those from Vault. 
