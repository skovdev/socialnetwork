package local.socialnetwork.supportservice.repository

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository

import java.util.UUID;

@Repository
interface SupportRepository : JpaRepository<Support, UUID> {

}