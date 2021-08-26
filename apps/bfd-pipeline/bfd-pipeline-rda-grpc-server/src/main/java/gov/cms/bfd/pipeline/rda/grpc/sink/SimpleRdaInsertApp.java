package gov.cms.bfd.pipeline.rda.grpc.sink;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.zaxxer.hikari.HikariDataSource;
import gov.cms.bfd.model.rda.PreAdjFissClaim;
import gov.cms.bfd.model.rda.PreAdjFissClaimContainer;
import gov.cms.bfd.model.rda.PreAdjFissDiagnosisCode;
import gov.cms.bfd.model.rda.PreAdjFissPayer;
import gov.cms.bfd.model.rda.PreAdjFissProcCode;
import gov.cms.bfd.model.rif.schema.DatabaseSchemaManager;
import gov.cms.bfd.pipeline.sharedutils.DatabaseOptions;
import gov.cms.bfd.pipeline.sharedutils.PipelineApplicationState;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRdaInsertApp {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.printf("usage: %s configfile%n", DirectRdaLoadApp.class.getSimpleName());
      System.exit(1);
    }
    Properties props = new Properties();
    try (Reader in = new BufferedReader(new FileReader(args[0]))) {
      props.load(in);
    }
    final MetricRegistry metrics = new MetricRegistry();
    final Slf4jReporter reporter =
        Slf4jReporter.forRegistry(metrics)
            .outputTo(LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
    reporter.start(5, TimeUnit.SECONDS);

    final DatabaseOptions databaseConfig = readDatabaseOptions(props);
    HikariDataSource pooledDataSource =
        PipelineApplicationState.createPooledDataSource(databaseConfig, metrics);
    DatabaseSchemaManager.createOrUpdateSchema(pooledDataSource);
    try (PipelineApplicationState appState =
        new PipelineApplicationState(
            metrics,
            pooledDataSource,
            PipelineApplicationState.RDA_PERSISTENCE_UNIT_NAME,
            Clock.systemUTC())) {
      EntityManager entityManager = null;
      try {
        entityManager = appState.getEntityManagerFactory().createEntityManager();
        final PreAdjFissClaim claim =
            PreAdjFissClaim.builder()
                .dcn("1")
                .hicNo("h1")
                .currStatus('1')
                .currLoc1('A')
                .currLoc2("1A")
                .pracLocCity("city name can be very long indeed")
                .lastUpdated(Instant.now())
                .build();

        final PreAdjFissProcCode procCode0 =
            PreAdjFissProcCode.builder()
                .dcn(claim.getDcn())
                .priority((short) 0)
                .procCode("P")
                .procFlag("F")
                .procDate(LocalDate.now())
                .lastUpdated(Instant.now())
                .build();
        claim.getProcCodes().add(procCode0);

        final PreAdjFissDiagnosisCode diagCode0 =
            PreAdjFissDiagnosisCode.builder()
                .dcn(claim.getDcn())
                .priority((short) 0)
                .diagCd2("cd2")
                .diagPoaInd("Q")
                .lastUpdated(Instant.now())
                .build();
        claim.getDiagCodes().add(diagCode0);

        final PreAdjFissPayer payer0 =
            PreAdjFissPayer.builder()
                .dcn(claim.getDcn())
                .priority((short) 0)
                .payerType(PreAdjFissPayer.PayerType.BeneZ)
                .estAmtDue(new BigDecimal("1.23"))
                .lastUpdated(Instant.now())
                .build();
        claim.getPayers().add(payer0);

        final PreAdjFissClaimContainer container =
            PreAdjFissClaimContainer.builder()
                .dcn(claim.getDcn())
                .lastUpdated(claim.getLastUpdated())
                .claim(claim)
                .build();
        entityManager.getTransaction().begin();
        entityManager.merge(container);
        entityManager.getTransaction().commit();

      } finally {
        if (entityManager != null) {
          entityManager.close();
        }
        reporter.report();
      }
    }
  }

  private static DatabaseOptions readDatabaseOptions(Properties props) {
    return new DatabaseOptions(
        props.getProperty("database.url"),
        props.getProperty("database.user"),
        props.getProperty("database.password"),
        10);
  }
}
