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
@Table(name = "kasha_deliveries")
public class KashaDeliveries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "person_id")
    private int person_id;

    @Column(name = "identifier")
    private String identifier;


    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "customer_reachable_on_phone")
    private String customer_reachable_on_phone;

    @Column(name = "delivery_attempt")
    private String delivery_attempt;

    @Column(name = "delivery_address")
    private String delivery_address;

    @Column(name = "delivery_lat")
    private String delivery_lat;

    @Column(name = "delivery_long")
    private String delivery_long;

    @Column(name = "delivery_successful")
    private String deliverySuccessful;

    @Column(name = "delivery_failure_reason")
    private String delivery_failure_reason;

    @Column(name = "delivery_returned_to_pharmacist")
    private String delivery_returned_to_pharmacist;


    @Column(name = "address")
    private String address;

    @Column(name = "estate_village")
    private String estate_village;

    @Column(name = "county")
    private String county;

    @Column(name = "in_amrs")
    private int inAMRS;

    @Column(name = "nearest_landmark")
    private String nearest_landmark;

    @Column(name = "created_by")
    private int created_by;

    @Column(name = "created_on")
    private Date created_on;

    @Column(name = "created_at")
    private String created_at;

    @Column(name = "updated_at")
    private String updated_at;

    @Column(name = "modified_by", nullable = true)
    private int modified_by;

    @Column(name = "modified_on", nullable = true)
    private Date modified_on;

    @Column(name = "mflcode")
    private String mflcode;

    @Column(name = "facility")
    private String facility;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEstate_village() {
        return estate_village;
    }

    public void setEstate_village(String estate_village) {
        this.estate_village = estate_village;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getNearest_landmark() {
        return nearest_landmark;
    }

    public void setNearest_landmark(String nearest_landmark) {
        this.nearest_landmark = nearest_landmark;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public int getModified_by() {
        return modified_by;
    }

    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }

    public Date getModified_on() {
        return modified_on;
    }

    public void setModified_on(Date modified_on) {
        this.modified_on = modified_on;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getOrder_number() {
        return orderNumber;
    }

    public void setOrder_number(String order_number) {
        this.orderNumber = order_number;
    }

    public String getCustomer_reachable_on_phone() {
        return customer_reachable_on_phone;}

    public void setCustomer_reachable_on_phone(String customer_reachable_on_phone) {
        this.customer_reachable_on_phone = customer_reachable_on_phone;
    }

    public String getDelivery_attempt() {
        return delivery_attempt;
    }

    public void setDelivery_attempt(String delivery_attempt) {
        this.delivery_attempt = delivery_attempt;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getDelivery_lat() {
        return delivery_lat;
    }

    public void setDelivery_lat(String delivery_lat) {
        this.delivery_lat = delivery_lat;
    }

    public String getDelivery_long() {
        return delivery_long;
    }

    public void setDelivery_long(String delivery_long) {
        this.delivery_long = delivery_long;
    }

    public String getDeliverySuccessful() {
        return deliverySuccessful;
    }

    public void setDeliverySuccessful(String deliverySuccessful) {
        this.deliverySuccessful = deliverySuccessful;
    }

    public String getDelivery_failure_reason() {
        return delivery_failure_reason;
    }

    public void setDelivery_failure_reason(String delivery_failure_reason) {
        this.delivery_failure_reason = delivery_failure_reason;
    }

    public String getDelivery_returned_to_pharmacist() {
        return delivery_returned_to_pharmacist;
    }

    public void setDelivery_returned_to_pharmacist(String delivery_returned_to_pharmacist) {
        this.delivery_returned_to_pharmacist = delivery_returned_to_pharmacist;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getInAMRS() {
        return inAMRS;
    }

    public void setInAMRS(int inAMRS) {
        this.inAMRS = inAMRS;
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
}
