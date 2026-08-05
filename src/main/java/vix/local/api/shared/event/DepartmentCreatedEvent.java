package vix.local.api.shared.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class DepartmentCreatedEvent extends ApplicationEvent {
    private final UUID departmentId;
    private final String code;
    private final String name;

    public DepartmentCreatedEvent(Object source, UUID departmentId, String code, String name) {
        super(source);
        this.departmentId = departmentId;
        this.code = code;
        this.name = name;
    }
}
