package enclave.encare.encare.service;

import enclave.encare.encare.form.AppointmentForm;
import enclave.encare.encare.modelResponse.AppointmentResponse;

import java.util.Date;
import java.util.List;

public interface AppointmentService {
    List<AppointmentResponse> findAll();

    AppointmentResponse findById(long id);

    List<AppointmentResponse> doctorFindByPhone(String phone,Long accountId);

    List<AppointmentResponse> doctorFindByName(String nameAccount,Long accountId);

    List<AppointmentResponse> doctorFindByDescriptions(String descriptions,Long accountId);

    List<AppointmentResponse> doctorFindBySymptoms(String symptoms,Long accountId);

    List<AppointmentResponse> doctorFindByKey(String key,Long accountId);


    boolean newAppointment(AppointmentForm appointmentForm);

    String setDescription(long id, String description);

    boolean isExistTime(int time, Date date, long appointmentId);

    boolean isExistTime(long appointmentId);

    List<AppointmentResponse> findByDoctorId(long doctorId);

    List<AppointmentResponse> findByHospitalId(long hospitalId);

    List<AppointmentResponse> findByStatusId(long statusId);

    List<AppointmentResponse> doctorFindByStatus(long statusId,long accountId);

    AppointmentResponse findByAppointmentIdAndAccountId(long appointmentId,long accountId);
}
