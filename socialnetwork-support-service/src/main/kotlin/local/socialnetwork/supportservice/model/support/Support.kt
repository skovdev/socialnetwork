package local.socialnetwork.supportservice.model.support

import local.socialnetwork.supportservice.model.AbstractModel

import javax.persistence.CascadeType

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "sn_supports")
class Support : AbstractModel() {

    @OneToMany(mappedBy = "support", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private lateinit var subjects : MutableList<Subject>

}