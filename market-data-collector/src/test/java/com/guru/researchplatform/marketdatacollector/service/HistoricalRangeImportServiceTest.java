package com.guru.researchplatform.marketdatacollector.service;

import com.guru.researchplatform.collector.service.HistoricalRangeImportService;
import com.guru.researchplatform.collector.service.ImportSummary;
import com.guru.researchplatform.collector.service.MonthlyHistoricalDataImporter;
import com.guru.researchplatform.common.domain.Asset;
import com.guru.researchplatform.common.enums.AssetStatus;
import com.guru.researchplatform.common.enums.Exchange;
import com.guru.researchplatform.common.enums.MarketType;
import com.guru.researchplatform.common.enums.Timeframe;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricalRangeImportServiceTest {

    @Test
    void shouldContinueImportingWhenOneMonthFails() {

        Asset asset = new Asset(
                Exchange.BINANCE,
                "BTCUSDT",
                "BTC",
                "USDT",
                MarketType.CRYPTO,
                AssetStatus.ACTIVE
        );

        MonthlyHistoricalDataImporter monthlyImporter =
                (importAsset, timeframe, month) -> {

                    if (month.equals(YearMonth.of(2024, 1))) {
                        throw new IllegalStateException(
                                "Simulated January failure"
                        );
                    }

                    if (month.equals(YearMonth.of(2024, 2))) {
                        return 41_760;
                    }

                    return 0;
                };

        HistoricalRangeImportService service =
                new HistoricalRangeImportService(monthlyImporter);

        ImportSummary summary = service.importMonthlyRange(
                asset,
                Timeframe.ONE_MINUTE,
                YearMonth.of(2024, 1),
                YearMonth.of(2024, 2)
        );

        assertEquals(1, summary.importedMonths());
        assertEquals(0, summary.skippedMonths());
        assertEquals(1, summary.failedMonths());
        assertEquals(41_760, summary.totalCandlesImported());
    }
}