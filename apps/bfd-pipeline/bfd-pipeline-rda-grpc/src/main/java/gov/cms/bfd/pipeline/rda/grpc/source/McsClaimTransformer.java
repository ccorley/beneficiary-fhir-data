package gov.cms.bfd.pipeline.rda.grpc.source;

import gov.cms.bfd.model.rda.PreAdjMcsClaim;
import gov.cms.bfd.pipeline.rda.grpc.RdaChange;
import gov.cms.bfd.pipeline.sharedutils.IdHasher;
import gov.cms.model.rda.codegen.library.DataTransformer;
import gov.cms.mpsm.rda.v1.McsClaimChange;
import gov.cms.mpsm.rda.v1.mcs.McsClaim;
import java.time.Clock;
import java.util.List;

public class McsClaimTransformer {
  private final Clock clock;
  private final McsClaimTransformer2 claimTransformer;

  public McsClaimTransformer(Clock clock, IdHasher idHasher) {
    this.clock = clock;
    claimTransformer =
        new McsClaimTransformer2(idHasher::computeIdentifierHash, EnumStringExtractor::new);
  }

  public RdaChange<PreAdjMcsClaim> transformClaim(McsClaimChange change) {
    McsClaim from = change.getClaim();

    final DataTransformer transformer = new DataTransformer();
    final PreAdjMcsClaim to = claimTransformer.transformMessage(from, transformer, clock.instant());
    to.setSequenceNumber(change.getSeq());

    final List<DataTransformer.ErrorMessage> errors = transformer.getErrors();
    if (errors.size() > 0) {
      String message =
          String.format(
              "failed with %d errors: clmHdIcn=%s errors=%s",
              errors.size(), from.getIdrClmHdIcn(), errors);
      throw new DataTransformer.TransformationException(message, errors);
    }
    return new RdaChange<>(
        change.getSeq(),
        RdaApiUtils.mapApiChangeType(change.getChangeType()),
        to,
        transformer.instant(change.getTimestamp()));
  }
}
