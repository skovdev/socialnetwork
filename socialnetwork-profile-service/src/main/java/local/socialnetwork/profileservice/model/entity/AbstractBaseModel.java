package local.socialnetwork.profileservice.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.util.UUID;

@MappedSuperclass
public class AbstractBaseModel {

    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    public AbstractBaseModel() {
        id = UUID.randomUUID();
    }

    @JsonProperty(value = "id")
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}