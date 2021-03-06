package app.coronawarn.testresult;

import app.coronawarn.testresult.entity.TestResultEntity;
import java.time.LocalDateTime;
import java.time.Period;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestResultCleanup {

  private final TestResultConfig testResultConfig;
  private final TestResultRepository testResultRepository;

  /**
   * All test results that are older than configured days should be marked as redeemed.
   */
  @Scheduled(
    fixedDelayString = "${testresult.cleanup.redeem.rate}"
  )
  @Transactional
  public void redeem() {
    log.info("redeem()");
    Integer redeemed = testResultRepository.updateResultByResultDateBefore(
      TestResultEntity.Result.REDEEMED.ordinal(),
      LocalDateTime.now().minus(Period.ofDays(testResultConfig.getCleanup().getRedeem().getDays())));
    log.info("redeem() {}", redeemed);
  }

  /**
   * All test results that are older than configured days should get deleted.
   */
  @Scheduled(
    fixedDelayString = "${testresult.cleanup.delete.rate}"
  )
  @Transactional
  public void delete() {
    log.info("delete()");
    Integer deleted = testResultRepository.deleteByResultDateBefore(
      LocalDateTime.now().minus(Period.ofDays(testResultConfig.getCleanup().getDelete().getDays())));
    log.info("delete() {}", deleted);
  }
}
