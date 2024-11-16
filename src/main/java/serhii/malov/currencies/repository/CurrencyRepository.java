package serhii.malov.currencies.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import serhii.malov.currencies.model.Currency;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, String> {

    @EntityGraph(attributePaths = {"exchangeRates"})
    List<Currency> findAll();

}
