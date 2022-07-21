package enclave.encare.encare.repository;

import enclave.encare.encare.form.AppointmentForm;
import enclave.encare.encare.model.Appointment;
import enclave.encare.encare.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Appointment findByAppointmentId(long appointmentId);
    Appointment findByUser_Account_Phone(String phone);
    List<Appointment> findByTimeAndDay(int time, Date date);
    List<Appointment> findByTimeAndDayEquals(int time, Date date);
    List<Appointment> findByDoctorDoctorId(long doctorId);
    List<Appointment> findByDoctor_Hospital_HospitalId(long hospitalId);
    List<Appointment> findByStatusStatusId(long statusId);
    @Query("SELECT ap from Appointment ap WHERE ap.user.account.phone like %?1% and ap.doctor.account.accountId= ?2")
    List<Appointment> find_by_phone_and_accountId(String phone,Long accountId);
    @Query("SELECT ap from Appointment ap WHERE ap.user.account.name like %?1% and ap.doctor.account.accountId= ?2 ")
    List<Appointment> find_by_name_and_accountId(String name,Long accountId);
    @Query("SELECT ap from Appointment ap WHERE ap.description like %?1% and ap.doctor.account.accountId= ?2 ")
    List<Appointment> find_by_descriptions_and_accountId(String descriptions,Long accountId);
    @Query("SELECT ap from Appointment ap WHERE ap.symptoms like %?1% and ap.doctor.account.accountId= ?2")
    List<Appointment> find_by_symptoms_and_accountId(String symptoms,Long accountId);
    @Query("SELECT ap from Appointment ap " +
            "WHERE (ap.symptoms like %?1% or ap.description like %?1% or ap.user.account.name like %?1% " +
            "or ap.user.account.phone like %?1% or ap.doctor.category.name like %?1%) and ap.doctor.account.accountId= ?2")
    List<Appointment> find_by_key_and_accountId(String key,Long accountId);
    List<Appointment> findByStatusStatusIdAndAndDoctor_Account_AccountIdOrderByCreateDate(Long statusId,Long accountId);
    Appointment findByAppointmentIdAndAndDoctor_Account_AccountIdOrderByCreateDate(Long appointmentId,Long accountId);
}