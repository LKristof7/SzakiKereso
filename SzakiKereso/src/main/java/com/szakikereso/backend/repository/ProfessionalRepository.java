package com.szakikereso.backend.repository;

import com.szakikereso.backend.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    // Név szerinti keresés (részszöveg)
    List<Professional> findByNameContainingIgnoreCase(String name);

    // Város szerint
    List<Professional> findByCityIgnoreCase(String city);

    // Szakterület (pontosan vagy részben)
    List<Professional> findBySpecialtyContainingIgnoreCase(String specialty);

    // Akár több feltétel kombinálva is lehet
    List<Professional> findByCityIgnoreCaseAndSpecialtyContainingIgnoreCase(String city, String specialty);


    @Query("SELECT DISTINCT p.specialty FROM Professional p WHERE LOWER(p.specialty) LIKE CONCAT(:prefix, '%')")
    List<String> findDistinctSpecialtiesStartingWith(@Param("prefix")String prefix);

    @Query("SELECT DISTINCT p.name FROM Professional p WHERE LOWER(p.name) LIKE CONCAT(:prefix, '%')")
    List<String> findDistinctNameStartingWith(@Param("prefix") String prefix);

    @Query("SELECT DISTINCT p.city FROM Professional p WHERE LOWER(p.city) LIKE CONCAT(:prefix, '%')")
    List<String> findDistinctCityStartingWith(@Param("prefix") String prefix);


    @Query("SELECT p FROM Professional p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:specialty IS NULL OR LOWER(p.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))) AND " +
            "(:slot IS NULL OR EXISTS (SELECT ts FROM TimeSlot ts WHERE ts.professional = p AND ts.startTime = :slot AND ts.isBooked = false)) AND " +
            "(:urgent = false OR p.urgentAvailable = true)")
    List<Professional> advancedSearch(
            @Param("name") String name,
            @Param("city") String city,
            @Param("specialty") String specialty,
            @Param("slot") LocalDateTime slot,
            @Param("urgent") boolean urgent
    );
}
