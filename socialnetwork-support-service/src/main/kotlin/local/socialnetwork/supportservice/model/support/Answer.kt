package local.socialnetwork.supportservice.model.support

import local.socialnetwork.supportservice.model.AbstractModel

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "sn_answers")
class Answer : AbstractModel() {

    @ManyToOne
    @JoinColumn(name = "support_id")
    private lateinit var support: Support

}