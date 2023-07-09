package com.fid3lles.marketsys.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fid3lles.marketsys.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> { }