package local.socialnetwork.authserver.model.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public class AbstractBaseModel {

    @Id
    UUID id;

    public AbstractBaseModel() {
        this.id = UUID.randomUUID();
    }
}