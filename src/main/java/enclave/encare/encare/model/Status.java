package enclave.encare.encare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long statusId;
    private String name;
    private String description;

    @OneToMany(mappedBy = "status")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    private List<Appointment> appointmentList;
}
