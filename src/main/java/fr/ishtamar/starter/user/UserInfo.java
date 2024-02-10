package fr.ishtamar.starter.user;

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
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max=30)
    private String name;

    @NotNull
    @Size(max=63)
    private String email;

    @NotNull
    @Size(max=60)
    private String password;

    @NotNull
    private String roles;
}
