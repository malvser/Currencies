package serhii.malov.currencies.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import serhii.malov.currencies.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, String> {

    @EntityGraph(attributePaths = {"currency"})
    List<ExchangeRate> findAll();

}
