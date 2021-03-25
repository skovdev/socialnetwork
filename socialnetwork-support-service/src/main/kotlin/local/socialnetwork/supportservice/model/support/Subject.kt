package local.socialnetwork.supportservice.model.support

import local.socialnetwork.supportservice.model.AbstractModel

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "sn_subjects")
class Subject : AbstractModel() {

    @Column(name = "title")
    private lateinit var title: String

    @Column(name = "description")
    private lateinit var description: String

    @ManyToOne
    @JoinColumn(name = "support_id")
    private lateinit var support: Support

}