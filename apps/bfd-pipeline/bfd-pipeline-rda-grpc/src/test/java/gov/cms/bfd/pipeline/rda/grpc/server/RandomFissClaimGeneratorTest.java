package gov.cms.bfd.pipeline.rda.grpc.server;

import static junit.framework.TestCase.assertEquals;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import gov.cms.mpsm.rda.v1.fiss.FissClaim;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.Test;

public class RandomFissClaimGeneratorTest {
  @Test
  public void randomClaim() throws InvalidProtocolBufferException {
    final Clock july1 = Clock.fixed(Instant.ofEpochMilli(1625172944844L), ZoneOffset.UTC);
    final RandomFissClaimGenerator generator = new RandomFissClaimGenerator(1, true, july1);
    final FissClaim claim = generator.randomClaim();
    final String json = JsonFormat.printer().print(claim);
    assertEquals(
        "{\n"
            + "  \"dcn\": \"9845557\",\n"
            + "  \"hicNo\": \"904843533707\",\n"
            + "  \"currStatusEnum\": \"CLAIM_STATUS_MOVE\",\n"
            + "  \"currLoc1Enum\": \"PROCESSING_TYPE_OFFLINE\",\n"
            + "  \"currLoc2Enum\": \"CURRENT_LOCATION_2_POST_PAY_7563\",\n"
            + "  \"totalChargeAmount\": \"8.83\",\n"
            + "  \"recdDtCymd\": \"2021-03-03\",\n"
            + "  \"currTranDtCymd\": \"2021-05-14\",\n"
            + "  \"admDiagCode\": \"mszx\",\n"
            + "  \"npiNumber\": \"7690551274\",\n"
            + "  \"mbi\": \"8f242z2sssv46\",\n"
            + "  \"fedTaxNb\": \"8932960060\",\n"
            + "  \"medaProvId\": \"c6vcs2xqbzp5w\",\n"
            + "  \"pracLocAddr1\": \"fz5qbm7h08g\",\n"
            + "  \"pracLocAddr2\": \"sb9b66k2v3fs2wf24p5mxp3k0bth5s2vg\",\n"
            + "  \"pracLocCity\": \"x1njn3q6ftzx2266s0g4rqqx9qp24pb3rhw165ttc3cdnq42s3tkjz1hg\",\n"
            + "  \"pracLocState\": \"zg\",\n"
            + "  \"pracLocZip\": \"60\",\n"
            + "  \"fissDiagCodes\": [{\n"
            + "    \"diagCd2\": \"trjgjpb\",\n"
            + "    \"bitFlags\": \"mmrx\",\n"
            + "    \"diagPoaIndUnrecognized\": \"d\"\n"
            + "  }, {\n"
            + "    \"diagCd2\": \"jbqqht\",\n"
            + "    \"bitFlags\": \"bxv\",\n"
            + "    \"diagPoaIndUnrecognized\": \"x\"\n"
            + "  }],\n"
            + "  \"stmtCovFromCymd\": \"2021-01-16\",\n"
            + "  \"stmtCovToCymd\": \"2021-03-27\",\n"
            + "  \"lobCdUnrecognized\": \"h\",\n"
            + "  \"servTypeCdForSpecialFacilitiesEnum\": \"BILL_CLASSIFICATION_FOR_SPECIAL_FACILITIES_HOSPICE_HOSPITAL_BASED\",\n"
            + "  \"freqCdUnrecognized\": \"v\",\n"
            + "  \"billTypCd\": \"6fr\",\n"
            + "  \"fissPayers\": [{\n"
            + "    \"insuredPayer\": {\n"
            + "      \"payersIdUnrecognized\": \"r\",\n"
            + "      \"payersName\": \"hxdxfwjfnjqpqshqgkwwzt\",\n"
            + "      \"relIndEnum\": \"RELEASE_OF_INFORMATION_SIGNED_STATEMENT_IS_RESTRICTED_OR_LIMITED\",\n"
            + "      \"assignIndEnum\": \"ASSIGNMENT_OF_BENEFITS_INDICATOR_NO_BENEFITS_ASSIGNED\",\n"
            + "      \"providerNumber\": \"wsvnsgrmmmcgd\",\n"
            + "      \"adjDcnIcn\": \"btckhsxgghqkzchskzftknk\",\n"
            + "      \"priorPmt\": \"7477.92\",\n"
            + "      \"estAmtDue\": \"3000.40\",\n"
            + "      \"insuredRelEnum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_89\",\n"
            + "      \"insuredName\": \"fzmcwtbchhjgrktwq\",\n"
            + "      \"insuredSsnHic\": \"tkmhgzvpxtnms\",\n"
            + "      \"insuredGroupName\": \"mxcxpcmvcwdbbggrq\",\n"
            + "      \"insuredGroupNbr\": \"pvhfsrsftw\",\n"
            + "      \"treatAuthCd\": \"2v1zxw89k5b31z\",\n"
            + "      \"insuredSexEnum\": \"BENEFIXIARY_SEX_FEMALE\",\n"
            + "      \"insuredRelX12Enum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_40\",\n"
            + "      \"insuredDob\": \"2021-05-29\",\n"
            + "      \"insuredDobText\": \"05292021\"\n"
            + "    }\n"
            + "  }, {\n"
            + "    \"beneZPayer\": {\n"
            + "      \"payersIdUnrecognized\": \"x\",\n"
            + "      \"payersName\": \"x79m54mr7sj6k56g\",\n"
            + "      \"relIndEnum\": \"RELEASE_OF_INFORMATION_SIGNED_STATEMENT_WAS_OBTAINED\",\n"
            + "      \"assignIndEnum\": \"ASSIGNMENT_OF_BENEFITS_INDICATOR_BENEFITS_ASSIGNED\",\n"
            + "      \"providerNumber\": \"76\",\n"
            + "      \"adjDcnIcn\": \"zdh\",\n"
            + "      \"priorPmt\": \"776.15\",\n"
            + "      \"estAmtDue\": \"523.58\",\n"
            + "      \"beneRelEnum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_22\",\n"
            + "      \"beneLastName\": \"kcnpkvbnbft\",\n"
            + "      \"beneFirstName\": \"f\",\n"
            + "      \"beneMidInit\": \"f\",\n"
            + "      \"beneSsnHic\": \"6kkvfgt24x80mpd4ttn\",\n"
            + "      \"insuredGroupName\": \"tgcn\",\n"
            + "      \"beneDob\": \"2021-03-16\",\n"
            + "      \"beneSexEnum\": \"BENEFICIARY_SEX_UNKNOWN\",\n"
            + "      \"treatAuthCd\": \"d\",\n"
            + "      \"insuredSexEnum\": \"BENEFICIARY_SEX_MALE\",\n"
            + "      \"insuredRelX12Unrecognized\": \"78\"\n"
            + "    }\n"
            + "  }, {\n"
            + "    \"beneZPayer\": {\n"
            + "      \"payersIdUnrecognized\": \"v\",\n"
            + "      \"payersName\": \"12dc\",\n"
            + "      \"relIndUnrecognized\": \"w\",\n"
            + "      \"assignIndEnum\": \"ASSIGNMENT_OF_BENEFITS_INDICATOR_NO_BENEFITS_ASSIGNED\",\n"
            + "      \"providerNumber\": \"tq8v\",\n"
            + "      \"adjDcnIcn\": \"6mk\",\n"
            + "      \"priorPmt\": \"6034.20\",\n"
            + "      \"estAmtDue\": \"29074.69\",\n"
            + "      \"beneRelEnum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_37\",\n"
            + "      \"beneLastName\": \"bgjzxbnbkf\",\n"
            + "      \"beneFirstName\": \"bw\",\n"
            + "      \"beneMidInit\": \"g\",\n"
            + "      \"beneSsnHic\": \"v\",\n"
            + "      \"insuredGroupName\": \"drsgvhgdjqqqt\",\n"
            + "      \"beneDob\": \"2021-01-09\",\n"
            + "      \"beneSexEnum\": \"BENEFICIARY_SEX_MALE\",\n"
            + "      \"treatAuthCd\": \"z\",\n"
            + "      \"insuredSexEnum\": \"BENEFICIARY_SEX_MALE\",\n"
            + "      \"insuredRelX12Enum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_57\"\n"
            + "    }\n"
            + "  }, {\n"
            + "    \"insuredPayer\": {\n"
            + "      \"payersIdEnum\": \"PAYERS_CODE_WORKING_AGE\",\n"
            + "      \"payersName\": \"bzzdvgzfmbnjgccv\",\n"
            + "      \"relIndEnum\": \"RELEASE_OF_INFORMATION_SIGNED_STATEMENT_WAS_OBTAINED\",\n"
            + "      \"assignIndEnum\": \"ASSIGNMENT_OF_BENEFITS_INDICATOR_NO_BENEFITS_ASSIGNED\",\n"
            + "      \"providerNumber\": \"njzzmrsf\",\n"
            + "      \"adjDcnIcn\": \"ktghppzpnqdgpndhjstrwhk\",\n"
            + "      \"priorPmt\": \"57.08\",\n"
            + "      \"estAmtDue\": \"1.52\",\n"
            + "      \"insuredRelEnum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_39\",\n"
            + "      \"insuredName\": \"bgmmbzvwf\",\n"
            + "      \"insuredSsnHic\": \"tprn\",\n"
            + "      \"insuredGroupName\": \"jkpcjhhxbkzmd\",\n"
            + "      \"insuredGroupNbr\": \"pzthrdhpcrwdjgqpmkx\",\n"
            + "      \"treatAuthCd\": \"mkp5zh\",\n"
            + "      \"insuredSexEnum\": \"BENEFICIARY_SEX_UNKNOWN\",\n"
            + "      \"insuredRelX12Enum\": \"PATIENT_RELATIONSHIP_CODE_RESERVED_FOR_NATIONAL_ASSIGNMENT_29\",\n"
            + "      \"insuredDob\": \"2021-06-07\",\n"
            + "      \"insuredDobText\": \"06072021\"\n"
            + "    }\n"
            + "  }]\n"
            + "}",
        json);
  }
}
