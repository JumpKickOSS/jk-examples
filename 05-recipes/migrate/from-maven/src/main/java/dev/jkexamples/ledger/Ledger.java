package dev.jkexamples.ledger;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@code ledger FILE}: the balance of every account in a CSV of {@code account,amount} rows. */
public final class Ledger {

    private static final Logger LOG = LoggerFactory.getLogger(Ledger.class);

    private Ledger() {}

    public static void main(String[] args) throws IOException {
        try (Reader in = Files.newBufferedReader(Path.of(args[0]))) {
            balances(in).forEach((account, total) -> System.out.println(account + "\t" + total));
        }
    }

    static Map<String, BigDecimal> balances(Reader csv) throws IOException {
        Map<String, BigDecimal> totals = new TreeMap<>();
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get();
        for (CSVRecord row : format.parse(csv)) {
            totals.merge(row.get("account"), new BigDecimal(row.get("amount")), BigDecimal::add);
        }
        LOG.info("{} accounts", totals.size());
        return totals;
    }
}
