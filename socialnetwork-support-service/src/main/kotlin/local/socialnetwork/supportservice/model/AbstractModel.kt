package local.socialnetwork.supportservice.model

import java.util.UUID

import javax.persistence.Id
import javax.persistence.Column

import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractModel (

        @Id
        @Column(name = "id")
        val id: UUID = UUID.randomUUID()

)