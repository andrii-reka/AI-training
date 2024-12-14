package com.epam.training.gen.ai.service.imp;

import com.epam.training.gen.ai.model.Currency;
import com.epam.training.gen.ai.service.CurrencyService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BasicCurrencyService implements CurrencyService {

    private final Map<Currency, Double> exchangeRates = new HashMap<>();

    @PostConstruct
    private void init() {
        updateExchangeRates();
    }

    @Override
    public Mono<List<Currency>> getSupportedCurrencies() {
        return Mono.just(List.copyOf(exchangeRates.keySet()));
    }

    @Override
    public Mono<Double> getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        Double fromRate = exchangeRates.get(fromCurrency);
        Double toRate = exchangeRates.get(toCurrency);

        if (fromRate == null || toRate == null) {
            return Mono.error(new IllegalArgumentException("Currency not supported."));
        }

        return Mono.just(toRate / fromRate);
    }

    public void updateExchangeRates() {
        exchangeRates.put(Currency.USD, 1.0);
        exchangeRates.put(Currency.EUR, 0.85);
        exchangeRates.put(Currency.JPY, 110.0);
        exchangeRates.put(Currency.UAH, 44.0);
    }


}
