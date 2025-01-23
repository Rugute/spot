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
@Table(name = "vls_orders_new")
public class VlsOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "person_id")
    private int personId;

    @Column(name = "ccc")
    private String ccc;

    @Column(name = "encounter_uuid")
    private String encounterUuid;

    @Column(name = "obs_uuid")
    private String obsUuid;

    @Column(name = "ecounter_date")
    private String ecounterDate;

    @Column(name = "obs_date")
    private String obsdate;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "vl_result")
    private String vlresult;

    @Column(name = "comments")
    private String comments;

    @Column(name = "status")
    private String status;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "date_procossed")
    private Date dateProcossed;

    @Column(name = "date_modified")
    private Date dateModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public void setEncounterUuid(String encounterUuid) {
        this.encounterUuid = encounterUuid;
    }

    public String getObsUuid() {
        return obsUuid;
    }

    public void setObsUuid(String obsUuid) {
        this.obsUuid = obsUuid;
    }

    public String getEcounterDate() {
        return ecounterDate;
    }

    public void setEcounterDate(String ecounterDate) {
        this.ecounterDate = ecounterDate;
    }

    public String getObsdate() {
        return obsdate;
    }

    public void setObsdate(String obsdate) {
        this.obsdate = obsdate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getVlresult() {
        return vlresult;
    }

    public void setVlresult(String vlresult) {
        this.vlresult = vlresult;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCcc() {
        return ccc;
    }

    public void setCcc(String ccc) {
        this.ccc = ccc;
    }

    public Date getDateProcossed() {
        return dateProcossed;
    }

    public void setDateProcossed(Date dateProcossed) {
        this.dateProcossed = dateProcossed;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}
