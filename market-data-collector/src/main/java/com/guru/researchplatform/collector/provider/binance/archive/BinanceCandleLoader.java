package com.guru.researchplatform.collector.provider.binance.archive;

import com.guru.researchplatform.collector.provider.binance.archive.mapping.BinanceCandleMapper;
import com.guru.researchplatform.collector.provider.binance.archive.model.BinanceKlineRecord;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidationResult;
import com.guru.researchplatform.collector.provider.binance.archive.validation.BinanceKlineValidator;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.domain.Candle;
import com.guru.researchplatform.common.enums.Timeframe;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BinanceCandleLoader {

    private final BinanceKlineFileReader fileReader;
    private final BinanceKlineValidator validator;
    private final BinanceCandleMapper mapper;

    public BinanceCandleLoader(
            BinanceKlineFileReader fileReader,
            BinanceKlineValidator validator,
            BinanceCandleMapper mapper) {

        this.fileReader = Objects.requireNonNull(fileReader);
        this.validator = Objects.requireNonNull(validator);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public List<Candle> load(
            Path csvFile,
            Asset asset,
            Timeframe timeframe) {

        Objects.requireNonNull(csvFile);
        Objects.requireNonNull(asset);
        Objects.requireNonNull(timeframe);

        List<BinanceKlineRecord> records =
                fileReader.readAll(csvFile);

        BinanceKlineValidationResult validation =
                validator.validate(records);

        if (validation.invalidRows() > 0) {
            throw new IllegalArgumentException(
                    "CSV validation failed. Invalid rows: "
                            + validation.invalidRows()
            );
        }

        List<Candle> candles =
                new ArrayList<>(records.size());

        for (BinanceKlineRecord record : records) {

            candles.add(
                    mapper.map(
                            record,
                            asset,
                            timeframe
                    )
            );
        }

        return candles;
    }
}
