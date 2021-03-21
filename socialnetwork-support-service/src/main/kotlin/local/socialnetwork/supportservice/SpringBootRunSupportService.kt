package local.socialnetwork.supportservice

import org.springframework.boot.SpringApplication

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SpringBootRunSupportService {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SpringBootRunSupportService::class.java, *args)
        }
    }
}