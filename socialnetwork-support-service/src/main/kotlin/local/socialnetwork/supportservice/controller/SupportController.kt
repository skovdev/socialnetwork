package local.socialnetwork.supportservice.controller

import local.socialnetwork.supportservice.service.SupportService

import org.springframework.web.bind.annotation.RestController

@RestController
class SupportController(
        private val supportService: SupportService
) {
}