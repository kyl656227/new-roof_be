package com.newroof.shelter;

import com.newroof.shelter.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository extends JpaRepository<Shelter, String> {
}
