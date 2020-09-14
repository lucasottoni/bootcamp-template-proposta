package br.com.zup.bootcamp.proposta.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.bootcamp.proposta.model.Propose;

@Repository
public interface ProposeRepository extends JpaRepository<Propose, String> {

    Optional<Propose> findByDocument(String document);
}
