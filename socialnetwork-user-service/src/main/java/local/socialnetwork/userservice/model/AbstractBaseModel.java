package local.socialnetwork.userservice.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public class AbstractBaseModel {

    @Id
    @Type(type = "pg-uuid")
    UUID id;

    public AbstractBaseModel() {
        this.id = UUID.randomUUID();
    }
}