package local.socialnetwork.supportservice.model.support

import local.socialnetwork.supportservice.model.AbstractModel

import javax.persistence.CascadeType

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "sn_supports")
class Support : AbstractModel() {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private lateinit var supportStatus: SupportStatus

    @OneToMany(mappedBy = "support", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private lateinit var subjects : MutableList<Subject>

    @OneToMany(mappedBy = "support", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private lateinit var questions : MutableList<Question>

    @OneToMany(mappedBy = "support", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private lateinit var answers : MutableList<Answer>

}