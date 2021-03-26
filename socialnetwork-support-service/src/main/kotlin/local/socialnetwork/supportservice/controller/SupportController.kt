package local.socialnetwork.supportservice.controller

import local.socialnetwork.supportservice.model.support.Subject

import local.socialnetwork.supportservice.service.SupportService

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/supports")
class SupportController(
        private val supportService: SupportService
) {

    @GetMapping
    fun findAllSubjects() : MutableList<Subject> {
        return supportService.findAllSubjects()
    }
}