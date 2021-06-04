package com.example.auth.model.entity;

import com.example.persistence.generator.NamedSequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Token")
@Table(name = "table_refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sequence_refresh_token")
    @GenericGenerator(name = "sequence_refresh_token",
            strategy = "com.example.persistence.generator.NamedSequenceGenerator",
            parameters = {
                    @Parameter(name = NamedSequenceGenerator.INCREMENT_PARAM,
                            value = "50"),
                    @Parameter(name = NamedSequenceGenerator.VALUE_PREFIX_PARAMETER,
                            value = "token"),
                    @Parameter(name = NamedSequenceGenerator.NUMBER_FORMAT_PARAMETER,
                            value = "%05d")})
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration")
    private Date expiration;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",
            referencedColumnName = "id")
    private Account account;

}
