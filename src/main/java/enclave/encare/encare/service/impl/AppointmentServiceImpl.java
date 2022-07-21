package enclave.encare.encare.service.impl;

import enclave.encare.encare.config.TimeConfig;
import enclave.encare.encare.form.AppointmentForm;
import enclave.encare.encare.model.Appointment;
import enclave.encare.encare.model.Doctor;
import enclave.encare.encare.model.Status;
import enclave.encare.encare.model.User;
import enclave.encare.encare.modelResponse.AppointmentResponse;
import enclave.encare.encare.modelResponse.DoctorResponse;
import enclave.encare.encare.modelResponse.UserResponse;
import enclave.encare.encare.repository.AppointmentRepository;
import enclave.encare.encare.service.AppointmentService;
import enclave.encare.encare.service.DoctorService;
import enclave.encare.encare.service.StatusService;
import enclave.encare.encare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorService doctorService;

    @Autowired
    UserService userService;

    @Autowired
    StatusService statusService;


    @Override
    public List<AppointmentResponse> findAll() {
        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();
        appointmentRepository.findByAppointmentId(1);
        List<Appointment> appointmentList= appointmentRepository.findAll();
        if (appointmentList==null || appointmentList.size()<1) return null;
        appointmentResponseList = transformData(appointmentList);
        return appointmentResponseList;
    }

    @Override
    public List<AppointmentResponse> findByHospitalId(long hospitalId) {
        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();
        appointmentResponseList = transformData(appointmentRepository.findByDoctor_Hospital_HospitalId(hospitalId));
        return appointmentResponseList;
    }

    @Override
    public List<AppointmentResponse> findByStatusId(long statusId) {
        List<Appointment> appointmentList = appointmentRepository.findByStatusStatusId(statusId);
        if (appointmentList == null) return null;

        return transformData(appointmentList);
    }

    @Override
    public List<AppointmentResponse> doctorFindByStatus(long statusId, long accountId) {

        List<AppointmentResponse> appointmentResponseList   = new ArrayList<>();
        List<Appointment> appointmentList = appointmentRepository.findByStatusStatusIdAndAndDoctor_Account_AccountIdOrderByCreateDate(statusId,accountId);

        return transformData(appointmentList);

    }

    @Override
    public AppointmentResponse findByAppointmentIdAndAccountId(long appointmentId, long accountId) {
        AppointmentResponse appointmentResponse = new AppointmentResponse();
        Appointment appointment = appointmentRepository.findByAppointmentIdAndAndDoctor_Account_AccountIdOrderByCreateDate(appointmentId,accountId);

        if (appointment==null) return null;
        return transformData(appointment);
    }

    @Override
    public AppointmentResponse findById(long id) {
        Appointment appointment = appointmentRepository.findByAppointmentId(id);
        if (appointment == null) return null;

        return transformData(appointment);
    }

    @Override
    public List<AppointmentResponse> doctorFindByPhone(String phone,Long accountId) {
//        List<Appointment> appointmentList = appointmentRepository.findAll()
//                .stream().filter(appointment -> appointment.getUser().getAccount().getPhone().contains(phone)).collect(Collectors.toList());
        List<Appointment> appointmentList = appointmentRepository.find_by_phone_and_accountId(phone,accountId);
        if (appointmentList == null) return null;

        return transformData(appointmentList);
    }

    @Override
    public List<AppointmentResponse> doctorFindByName(String nameAccount,Long accountId) {
        List<Appointment> appointmentList = appointmentRepository.find_by_name_and_accountId(nameAccount,accountId);
        if (appointmentList == null) return null;

        return transformData(appointmentList);
    }

    @Override
    public List<AppointmentResponse> doctorFindByDescriptions(String descriptions,Long accountId) {
        List<Appointment> appointmentList = appointmentRepository.find_by_descriptions_and_accountId(descriptions,accountId);
        if (appointmentList == null) return null;

        return transformData(appointmentList);
    }

    @Override
    public List<AppointmentResponse> doctorFindBySymptoms(String symptoms,Long accountId) {
        List<Appointment> appointmentList = appointmentRepository.find_by_symptoms_and_accountId(symptoms,accountId);
        if (appointmentList == null) return null;

        return transformData(appointmentList);
    }

    @Override
    public List<AppointmentResponse> doctorFindByKey(String key, Long accountId) {
        List<Appointment> appointmentList = appointmentRepository.find_by_key_and_accountId(key,accountId);
        if (appointmentList == null) return null;

        return transformData(appointmentList);
    }

    @Override
    public boolean newAppointment(AppointmentForm appointmentForm) {

        Date day = TimeConfig.getDate(appointmentForm.getDay());
        int time = appointmentForm.getTime();
        if (findTimeAndDay(time, day)) {
            User user = new User(appointmentForm.getUserId());
            Doctor doctor = new Doctor(appointmentForm.getDoctorId());
            Status status = new Status(1); // Đang chờ xác nhận

            Appointment appointment = new Appointment();
            appointment.setUser(user);
            appointment.setDoctor(doctor);
            appointment.setTime(time);
            appointment.setDay(day);
            appointment.setDescription(appointmentForm.getDescription());
            appointment.setStatus(status);
            appointment.setSymptoms(appointmentForm.getSymptomps());

            appointmentRepository.save(appointment);
            return true;
        }
        return false;
    }

    @Override
    public String setDescription(long id, String description) {
        Appointment appointment = appointmentRepository.findByAppointmentId(id);
        if (appointment == null)
            return "Appointment is not exist !";
        else if (appointment.getStatus().getStatusId()==3) {
            appointment.setDescription(description);
            appointmentRepository.save(appointment);
            return "Success";
        }
        return "Appointment must be in confirmed status";
    }

    @Override
    public boolean isExistTime(int time, Date date, long appointmentId) {
        List<Appointment> appointmentList = appointmentRepository.findAll();
//        appointmentList.removeIf(appointment -> {
//            long statusId = appointment.getStatus().getStatusId();
//            return  statusId!=5 || statusId!=1 ;
//        });
        if (appointmentList == null)
            return false;
        DoctorResponse doctorResponse = doctorService.findById(appointmentId);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (Appointment appointment : appointmentList) {
            if (appointment.getAppointmentId() == appointmentId)
                continue;
            if (formatter.format(appointment.getDay()).equals(formatter.format(date))
                    && appointment.getTime() == time
                    && (appointment.getStatus().getStatusId() > 1 && appointment.getStatus().getStatusId() < 5)
                    && appointment.getAppointmentId() != appointmentId
                    && appointment.getDoctor().getDoctorId() == doctorResponse.getDoctorId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isExistTime(long appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        if (appointment == null) return true;
        List<Appointment> appointmentList = appointmentRepository.findByDoctorDoctorId(appointment.getDoctor().getDoctorId());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        for (Appointment a : appointmentList) {
            if (a.getAppointmentId() != appointmentId
                    && a.getTime() == appointment.getTime()
                    && formatter.format(a.getDay()).equals(formatter.format(appointment.getDay()))
                    && (a.getStatus().getStatusId() > 1 && a.getStatus().getStatusId() < 5)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<AppointmentResponse> findByDoctorId(long doctorId) {
        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();
        List<Appointment> appointmentList = appointmentRepository.findByDoctorDoctorId(doctorId);
        if (appointmentList != null) {
            appointmentResponseList = transformData(appointmentList);
        }
        return appointmentResponseList;
    }

    public boolean findTimeAndDay(int time, Date date) {
//        Appointment appointment = appointmentRepository.findByTimeAndDay(time, date);
        List<Appointment> appointment = appointmentRepository.findByTimeAndDayEquals(time, date);
        if (appointment.size() > 0) {
            return false;
        }
        return true;
    }

    private AppointmentResponse transformData(Appointment appointment) {
        AppointmentResponse appointmentResponse = new AppointmentResponse();

        appointmentResponse.setAppointmentId(appointment.getAppointmentId());
        appointmentResponse.setSymptoms(appointment.getSymptoms());
        appointmentResponse.setDescription(appointment.getDescription());
        appointmentResponse.setTime(appointment.getTime());
        appointmentResponse.setDay(TimeConfig.getTime(appointment.getDay()));
        appointmentResponse.setCreateDate(TimeConfig.getTime(appointment.getCreateDate()));
        DoctorResponse doctorResponse = doctorService.findById(appointment.getDoctor().getDoctorId());
        if (doctorResponse!=null)
        {
            appointmentResponse.setDoctorResponse(doctorResponse);
        }
        UserResponse userResponse = new UserResponse();
//        try {
//            userResponse = userService.findById(appointment.getUser().getUserId());
//        }catch (Exception e){
//            userResponse = null;
//        }
        userResponse = userService.findById(appointment.getUser().getUserId());
        if (userResponse!=null){
            appointmentResponse.setUserResponse(userResponse);
        }

        appointmentResponse.setStatusResponse(statusService.findById(appointment.getStatus().getStatusId()));

        return appointmentResponse;
    }

    private List<AppointmentResponse> transformData(List<Appointment> appointmentList) {

        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            appointmentResponseList.add(transformData(appointment));
        }
        return appointmentResponseList;
    }
}
