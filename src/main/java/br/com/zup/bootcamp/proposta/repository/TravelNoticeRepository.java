package br.com.zup.bootcamp.proposta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.model.TravelNotice;

@Repository
public interface TravelNoticeRepository extends JpaRepository<TravelNotice, String> {
}
