package local.socialnetwork.supportservice.repository

import local.socialnetwork.supportservice.model.support.Subject

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository

import java.util.UUID

@Repository
interface SubjectRepository : JpaRepository<Subject, UUID> {

}