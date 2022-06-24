package enclave.encare.encare.service.impl;

import enclave.encare.encare.config.TimeConfig;
import enclave.encare.encare.form.AppointmentForm;
import enclave.encare.encare.form.FreeTimeForm;
import enclave.encare.encare.model.*;
import enclave.encare.encare.modelResponse.AppointmentResponse;
import enclave.encare.encare.repository.AppointmentRepository;
import enclave.encare.encare.service.AppointmentService;
import enclave.encare.encare.service.DoctorService;
import enclave.encare.encare.service.StatusService;
import enclave.encare.encare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public AppointmentResponse findById(long id) {
        return transformData(appointmentRepository.findByAppointmentId(id));
    }

    @Override
    public boolean newAppointment(AppointmentForm appointmentForm) {

        Date day = TimeConfig.getDate(appointmentForm.getDay());
        int time = appointmentForm.getTime();
        if (findTimeAndDay(appointmentForm.getDoctorId(), time, day)){
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
            appointment.setCreateDate(new Date());

            appointmentRepository.save(appointment);
            return true;
        }
        return false;
    }

    @Override
    public List<AppointmentResponse> historyAppointmentUser(long userId) {
        User user = new User(userId);
        List<Appointment> appointmentList = appointmentRepository.findByUser(user);
        List<AppointmentResponse> appointmentResponseList = new ArrayList<AppointmentResponse>();
        for (Appointment appointment:appointmentList){
            AppointmentResponse appointmentResponse = transformData(appointment);
            appointmentResponseList.add(appointmentResponse);
        }
        return appointmentResponseList;
    }

    @Override
    public List<Integer> listFreeTime(FreeTimeForm freeTimeForm) {
        Doctor doctor = new Doctor(freeTimeForm.getDoctorId());
        Date date = TimeConfig.getDate(freeTimeForm.getTime());
        boolean check = true;
        List<Appointment> appointmentList = appointmentRepository.findByDoctorAndDay(doctor, date);
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 8; i<=11; i++){
            check = true;
            for (int j=0; j<appointmentList.size(); j++){
                if (i == appointmentList.get(j).getTime()){
                    check = false;
                    break;
                }
            }
            if (check == true){
                list.add(i);
            }
        }
        for (int i = 13; i<=17; i++){
            check = true;
            for (int j=0; j<appointmentList.size(); j++){
                if (i == appointmentList.get(j).getTime()){
                    check = false;
                    break;
                }
            }
            if (check == true){
                list.add(i);
            }
        }

        return list;
    }

    public boolean findTimeAndDay(long doctorId, int time, Date date){
//        Appointment appointment = appointmentRepository.findByTimeAndDay(time, date);
        Doctor doctor = new Doctor(doctorId);
        List<Appointment> appointment = appointmentRepository.findByDoctorAndTimeAndDayEquals(doctor, time, date);
        if (appointment.size() > 0){
            return false;
        }
        return true;
    }

    private AppointmentResponse transformData(Appointment appointment){
        AppointmentResponse appointmentResponse = new AppointmentResponse();

        appointmentResponse.setAppointmentId(appointment.getAppointmentId());
        appointmentResponse.setSymptoms(appointment.getSymptoms());
        appointmentResponse.setDescription(appointment.getDescription());
        appointmentResponse.setTime(appointment.getTime());
        appointmentResponse.setDay(TimeConfig.getTime(appointment.getDay()));
        appointmentResponse.setCreateDate(TimeConfig.getTime(appointment.getCreateDate()));
        appointmentResponse.setDoctorResponse(doctorService.findById(appointment.getDoctor().getDoctorId()));
        appointmentResponse.setUserResponse(userService.findById(appointment.getUser().getUserId()));
        appointmentResponse.setStatusResponse(statusService.findById(appointment.getStatus().getStatusId()));

        return appointmentResponse;
    }
}