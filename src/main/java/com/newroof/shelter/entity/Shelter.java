package com.newroof.shelter.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "shelter")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shelter {

    @Id
    @Column(name = "fclt_cd")
    private String fcltCd;

    @Column(name = "name")
    private String name;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "shelter_type")
    private String shelterType;   // 지상 / 지하

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "manager_phone")
    private String managerPhone;
}
