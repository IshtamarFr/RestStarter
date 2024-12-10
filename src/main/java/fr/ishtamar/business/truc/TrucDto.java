package fr.ishtamar.business.truc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrucDto {
    private Long id;
    private String name;
    private Long user_id;
}
