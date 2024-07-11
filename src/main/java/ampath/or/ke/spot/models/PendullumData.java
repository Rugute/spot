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
@Table(name = "pendullum_data_live")
public class PendullumData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "patient_identifier")
    private String patientIdentifier;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birthdate")
    private String birthdate;

    @Column(name = "encounter_Date")
    private String encounterDate;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "next_clinical_appointment")
    private String next_clinical_appointment;

    @Column(name = "diagnosis_Date")
    private String diagnosis_Date;

    @Column(name = "Tuberculosis_Treatment_Plan")
    private String Tuberculosis_Treatment_Plan;

    @Column(name = "Current_WHO_HIV_Stage")
    private String Current_WHO_HIV_Stage;

    @Column(name = "CD4_Count")
    private String CD4_Count;

    @Column(name = "Is_ART")
    private String Is_ART;

    @Column(name = "Is_project_beyond")
    private int project_beyond;

    @Column(name = "Is_PMTCT")
    private String Is_PMTCT;

    @Column(name = "Viral_Load")
    private String Viral_Load;

    @Column(name = "Adherence")
    private String Adherence;

    @Column(name = "Education_Level")
    private String Education_Level;

    @Column(name = "Screening_For_STI")
    private String Screening_For_STI;

    @Column(name = "Classification_Of_Malnutrition")
    private String Classification_Of_Malnutrition;

    @Column(name = "Isoniazid_Use")
    private String Isoniazid_Use;


    @Column(name = "Cotrimoxazole_Use")
    private String Cotrimoxazole_Use;

    @Column(name = "created_on")
    private Date dateCreated;



    public int getProject_beyond() {
        return project_beyond;
    }

    public void setProject_beyond(int project_beyond) {
        this.project_beyond = project_beyond;
    }

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

    public String getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(String encounterDate) {
        this.encounterDate = encounterDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }


    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNext_clinical_appointment() {
        return next_clinical_appointment;
    }

    public void setNext_clinical_appointment(String next_clinical_appointment) {
        this.next_clinical_appointment = next_clinical_appointment;
    }

    public String getDiagnosis_Date() {
        return diagnosis_Date;
    }

    public void setDiagnosis_Date(String diagnosis_Date) {
        this.diagnosis_Date = diagnosis_Date;
    }

    public String getTuberculosis_Treatment_Plan() {
        return Tuberculosis_Treatment_Plan;
    }

    public void setTuberculosis_Treatment_Plan(String tuberculosis_Treatment_Plan) {
        Tuberculosis_Treatment_Plan = tuberculosis_Treatment_Plan;
    }

    public String getCurrent_WHO_HIV_Stage() {
        return Current_WHO_HIV_Stage;
    }

    public void setCurrent_WHO_HIV_Stage(String current_WHO_HIV_Stage) {
        Current_WHO_HIV_Stage = current_WHO_HIV_Stage;
    }

    public String getCD4_Count() {
        return CD4_Count;
    }

    public void setCD4_Count(String CD4_Count) {
        this.CD4_Count = CD4_Count;
    }

    public String getIs_ART() {
        return Is_ART;
    }

    public void setIs_ART(String is_ART) {
        Is_ART = is_ART;
    }

    public String getIs_PMTCT() {
        return Is_PMTCT;
    }

    public void setIs_PMTCT(String is_PMTCT) {
        Is_PMTCT = is_PMTCT;
    }

    public String getViral_Load() {
        return Viral_Load;
    }

    public void setViral_Load(String viral_Load) {
        Viral_Load = viral_Load;
    }

    public String getAdherence() {
        return Adherence;
    }

    public void setAdherence(String adherence) {
        Adherence = adherence;
    }

    public String getEducation_Level() {
        return Education_Level;
    }

    public void setEducation_Level(String education_Level) {
        Education_Level = education_Level;
    }

    public String getScreening_For_STI() {
        return Screening_For_STI;
    }

    public void setScreening_For_STI(String screening_For_STI) {
        Screening_For_STI = screening_For_STI;
    }

    public String getClassification_Of_Malnutrition() {
        return Classification_Of_Malnutrition;
    }

    public void setClassification_Of_Malnutrition(String classification_Of_Malnutrition) {
        Classification_Of_Malnutrition = classification_Of_Malnutrition;
    }

    public String getIsoniazid_Use() {
        return Isoniazid_Use;
    }

    public void setIsoniazid_Use(String isoniazid_Use) {
        Isoniazid_Use = isoniazid_Use;
    }

    public String getCotrimoxazole_Use() {
        return Cotrimoxazole_Use;
    }

    public void setCotrimoxazole_Use(String cotrimoxazole_Use) {
        Cotrimoxazole_Use = cotrimoxazole_Use;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
