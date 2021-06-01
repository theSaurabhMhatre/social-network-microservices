package com.example.auth.model.entity;

import com.example.auth.model.constant.ERole;
import com.example.persistence.generator.NamedSequenceGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "Role")
@Table(name = "table_role")
public class Role {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sequence_role")
    @GenericGenerator(name = "sequence_role",
            strategy = "com.example.persistence.generator.NamedSequenceGenerator",
            parameters = {
                    @Parameter(name = NamedSequenceGenerator.VALUE_PREFIX_PARAMETER,
                            value = "role"),
                    @Parameter(name = NamedSequenceGenerator.NUMBER_FORMAT_PARAMETER,
                            value = "%05d")})
    private String id;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private ERole role;

}
