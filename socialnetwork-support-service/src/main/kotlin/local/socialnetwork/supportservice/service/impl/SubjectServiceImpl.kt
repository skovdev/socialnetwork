package local.socialnetwork.supportservice.service.impl

import local.socialnetwork.supportservice.model.support.Subject

import local.socialnetwork.supportservice.repository.SubjectRepository

import local.socialnetwork.supportservice.service.SubjectService

import org.springframework.stereotype.Service

@Service
class SubjectServiceImpl(
    private val subjectRepository : SubjectRepository
) : SubjectService {

    override fun findAll(): MutableList<Subject> {
        return subjectRepository.findAll()
    }
}