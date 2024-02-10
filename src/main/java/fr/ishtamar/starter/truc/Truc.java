package fr.ishtamar.starter.truc;

import fr.ishtamar.starter.user.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Truc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max=63)
    private String name;

    @ManyToOne
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private UserInfo user;
}
