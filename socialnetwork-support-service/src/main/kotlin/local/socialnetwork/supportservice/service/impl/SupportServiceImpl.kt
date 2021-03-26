package local.socialnetwork.supportservice.service.impl

import local.socialnetwork.supportservice.model.support.Subject
import local.socialnetwork.supportservice.repository.SubjectRepository
import local.socialnetwork.supportservice.repository.SupportRepository

import local.socialnetwork.supportservice.service.SupportService

import org.springframework.stereotype.Service

@Service
class SupportServiceImpl (
    private val supportRepository: SupportRepository,
    private val subjectRepository: SubjectRepository
): SupportService {

    override fun findAllSubjects(): MutableList<Subject> {
        return subjectRepository.findAll()
    }
}