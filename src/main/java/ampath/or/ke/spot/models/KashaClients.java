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
@Table(name = "kasha_clients")
public class KashaClients {
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

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "secondary_phone_number")
    private String secondary_phone_number;

    @Column(name = "address")
    private String address;

    @Column(name = "estate_village")
    private String estate_village;

    @Column(name = "county")
    private String county;

    @Column(name = "expected_next_delivery_date")
    private Date expected_next_delivery_date;

    @Column(name = "nearest_landmark")
    private String nearest_landmark;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private String age;

    @Column(name = "mflcode")
    private String mflcode;

    @Column(name = "consented")
    private int consented;

    @Column(name = "eligible")
    private int eligible;

    @Column(name = "consented_on")
    private Date dateConsented;

    @Column(name = "created_by")
    private int created_by;

    @Column(name = "created_on")
    private Date dateCreated;

    @Column(name = "modified_by", nullable = true)
    private int modified_by;

    @Column(name = "modified_on", nullable = true)
    private Date modifiedOn;

    @Column(name = "synced_to_pendulum", nullable = true)
    private int syncedToPendulum;

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

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSecondary_phone_number() {
        return secondary_phone_number;
    }

    public void setSecondary_phone_number(String secondary_phone_number) {
        this.secondary_phone_number = secondary_phone_number;
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

    public Date getExpected_next_delivery_date() {
        return expected_next_delivery_date;
    }

    public void setExpected_next_delivery_date(Date expected_next_delivery_date) {
        this.expected_next_delivery_date = expected_next_delivery_date;
    }

    public String getNearest_landmark() {
        return nearest_landmark;
    }

    public void setNearest_landmark(String nearest_landmark) {
        this.nearest_landmark = nearest_landmark;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getConsented() {
        return consented;
    }

    public void setConsented(int consented) {
        this.consented = consented;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public Date getDateConsented() {
        return dateConsented;
    }

    public void setDateConsented(Date dateConsented) {
        this.dateConsented = dateConsented;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getModified_by() {
        return modified_by;
    }

    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getMflcode() {
        return mflcode;
    }

    public void setMflcode(String mflcode) {
        this.mflcode = mflcode;
    }

    public int getEligible() {
        return eligible;
    }

    public void setEligible(int eligible) {
        this.eligible = eligible;
    }

    public int getSyncedToPendulum() {
        return syncedToPendulum;
    }

    public void setSyncedToPendulum(int syncedToPendulum) {
        this.syncedToPendulum = syncedToPendulum;
    }
}

