package vix.local.api.modules.permission.infrastructure.security;

import vix.local.api.modules.permission.domain.model.ActionCode;
import vix.local.api.modules.permission.domain.model.ResourceCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireDeptPermission {
    ResourceCode resource();
    ActionCode action();
}
