package com.example.xb.dao;


import com.example.xb.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptDao extends JpaRepository<Dept,Long> {
}
