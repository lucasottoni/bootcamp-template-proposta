package br.com.zup.bootcamp.proposta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.bootcamp.proposta.model.Biometric;

@Repository
public interface BiometricRepository extends JpaRepository<Biometric, String> {
}
