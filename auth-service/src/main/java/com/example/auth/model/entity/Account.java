package com.example.auth.model.entity;

import com.example.persistence.generator.NamedSequenceGenerator;
import com.example.persistence.model.entity.Updatable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "Account")
@Table(name = "table_account")
@EqualsAndHashCode(callSuper = false)
@EntityListeners(value = {AuditingEntityListener.class})
public class Account
        extends Updatable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sequence_account")
    @GenericGenerator(name = "sequence_account",
            strategy = "com.example.persistence.generator.NamedSequenceGenerator",
            parameters = {
                    @Parameter(name = NamedSequenceGenerator.INCREMENT_PARAM,
                            value = "50"),
                    @Parameter(name = NamedSequenceGenerator.VALUE_PREFIX_PARAMETER,
                            value = "account"),
                    @Parameter(name = NamedSequenceGenerator.NUMBER_FORMAT_PARAMETER,
                            value = "%05d")})
    private String id;

    @Column(name = "handle")
    private String handle;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "table_account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
