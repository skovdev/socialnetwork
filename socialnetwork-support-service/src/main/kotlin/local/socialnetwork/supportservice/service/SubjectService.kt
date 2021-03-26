package local.socialnetwork.supportservice.service

import local.socialnetwork.supportservice.model.support.Subject

interface SubjectService {
    fun findAll(): MutableList<Subject>
}