package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.model.Currency;
import com.epam.training.gen.ai.service.CurrencyService;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import reactor.core.publisher.Mono;

import java.util.List;

public class CurrencyConverterPlugin {

    private final CurrencyService currencyService;

    public CurrencyConverterPlugin(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @DefineKernelFunction(
            name = "get_supported_currencies",
            description = "Get the list of supported currencies.",
            returnType = "java.util.List")
    public Mono<List<Currency>> getSupportedCurrencies() {
        return currencyService.getSupportedCurrencies();
    }

    @DefineKernelFunction(
            name = "convert_currency",
            description = "Convert an amount from one currency to another.",
            returnDescription = "Returns the converted amount.",
            returnType = "java.lang.Double")
    public Mono<Double> convertCurrency(
            @KernelFunctionParameter(name = "fromCurrency", description = "The code of the currency to convert from", type = String.class, required = true)
            String fromCurrency,
            @KernelFunctionParameter(name = "toCurrency", description = "The code of the currency to convert to", type = String.class, required = true)
            String toCurrency,
            @KernelFunctionParameter(name = "amount", description = "The amount of money to convert", type = Double.class, required = true)
            Double amount
    ) {
        return currencyService.getExchangeRate(Currency.valueOf(fromCurrency), Currency.valueOf(toCurrency))
                .map(exchangeRate -> amount * exchangeRate);
    }
}
