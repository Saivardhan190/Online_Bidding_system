package com.application.example.online_bidding_system.repository;

import com.application.example.online_bidding_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {
    // Find by email (for login)
    Optional<User> findByStudentEmail(String studentEmail);



    Optional<User> findByCollageId(String collageId);

}
