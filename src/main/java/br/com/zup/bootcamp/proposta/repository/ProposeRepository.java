package br.com.zup.bootcamp.proposta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.zup.bootcamp.proposta.model.Propose;

@Repository
public interface ProposeRepository extends JpaRepository<Propose, String> {

    Optional<Propose> findByDocument(String document);

    @Query(value = "FROM Propose WHERE financialAnalysisStatus = 'ELIGIBLE' AND card IS NULL")
    List<Propose> findPendingCards();

}
