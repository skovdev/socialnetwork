package local.socialnetwork.shared.entity;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;

import lombok.Setter;
import lombok.Getter;

import java.util.UUID;

/**
 * Base class for JPA entities, providing a UUID primary key.
 */
@Setter
@Getter
@MappedSuperclass
public class AbstractBaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

}
