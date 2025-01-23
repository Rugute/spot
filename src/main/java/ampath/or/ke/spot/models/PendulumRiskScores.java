package ampath.or.ke.spot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pendulum_risk_scores")
public class PendulumRiskScores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "patientIdentifier")
    private String patientIdentifier;

    @Column(name = "cccno")
    private String cccno;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "nextClinicalAppointment")
    private String nextClinicalAppointment;

    @Column(name = "noShowScore")
    private String noShowScore;

    @Column(name = "created_on")
    private Date dateCreated;

    @Column(name = "risk_smg")
    private String risksmg;

    @Column(name = "scale")
    private String scale;

    @Column(name = "mflcode")
    private String mflcode;

    @Column(name = "facility")
    private String facility;

    @Column(name = "DrugDeliverStatus")
    private String DrugDeliverStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public String getNextClinicalAppointment() {
        return nextClinicalAppointment;
    }

    public void setNextClinicalAppointment(String nextClinicalAppointment) {
        this.nextClinicalAppointment = nextClinicalAppointment;
    }

    public String getNoShowScore() {
        return noShowScore;
    }

    public void setNoShowScore(String noShowScore) {
        this.noShowScore = noShowScore;
    }

    public String getRisksmg() {
        return risksmg;
    }

    public void setRisksmg(String risksmg) {
        this.risksmg = risksmg;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCccno() {
        return cccno;
    }

    public void setCccno(String cccno) {
        this.cccno = cccno;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getMflcode() {
        return mflcode;
    }

    public void setMflcode(String mflcode) {
        this.mflcode = mflcode;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getDrugDeliverStatus() {
        return DrugDeliverStatus;
    }

    public void setDrugDeliverStatus(String drugDeliverStatus) {
        DrugDeliverStatus = drugDeliverStatus;
    }
}
