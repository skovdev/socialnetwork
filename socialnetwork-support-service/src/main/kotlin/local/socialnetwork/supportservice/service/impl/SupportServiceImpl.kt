package local.socialnetwork.supportservice.service.impl

import local.socialnetwork.supportservice.repository.SupportRepository

import local.socialnetwork.supportservice.service.SupportService

import org.springframework.stereotype.Service

@Service
class SupportServiceImpl (
        private val supportRepository: SupportRepository
        ): SupportService {

}