package com.example.realestateagency_back.repository;

import com.example.realestateagency_back.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserId(Long userId);

    List<Message> findByPropertyId(Long propertyId);

    List<Message> findByUserIdAndPropertyId(Long userId, Long propertyId);

    List<Message> findByPropertyIdOrderBySentDateDesc(Long propertyId);

    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
}