package net.majo24.naturally_trimmed.config.backend;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks an entry as deprecated. Deprecated entries will still be deserialized from the config file, but will no longer be serialized and saved
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Deprecated {
}
