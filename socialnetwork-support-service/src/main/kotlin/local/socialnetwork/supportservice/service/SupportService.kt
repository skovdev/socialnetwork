package local.socialnetwork.supportservice.service

import local.socialnetwork.supportservice.model.support.Subject

interface SupportService {
    fun findAllSubjects() : MutableList<Subject>
}