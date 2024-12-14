package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.Currency;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CurrencyService {
    Mono<List<Currency>> getSupportedCurrencies();

    Mono<Double> getExchangeRate(Currency fromCurrency, Currency toCurrency);
}
