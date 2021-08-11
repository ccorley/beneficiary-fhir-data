insert into public.hha_claims ( 
	clm_id,
	bene_id,
	clm_grp_id,
	last_updated,
	clm_from_dt,
	clm_thru_dt,
	clm_pmt_amt,
	clm_admsn_dt,
	clm_fac_type_cd,
	clm_freq_cd,
	clm_hha_lupa_ind_cd,
	clm_hha_rfrl_cd,
	clm_hha_tot_visit_cnt,
	clm_mdcr_non_pmt_rsn_cd,
	clm_pps_ind_cd,
	clm_srvc_clsfctn_type_cd,
	fi_clm_proc_dt,
	fi_doc_clm_cntl_num,
	fi_num,
	fi_orig_clm_cntl_num,
	final_action,
	fst_dgns_e_cd,
	fst_dgns_e_vrsn_cd,
	nch_clm_type_cd,
	nch_near_line_rec_ident_cd,
	nch_prmry_pyr_cd,
	nch_prmry_pyr_clm_pd_amt,
	nch_wkly_proc_dt,
	org_npi_num,
	prncpal_dgns_cd,
	prncpal_dgns_vrsn_cd,
	prvdr_num,
	prvdr_state_cd,
	ptnt_dschrg_stus_cd,
	rev_cntr_tot_chrg_amt,
	at_physn_npi,
	at_physn_upin,
	icd_dgns_cd1,
	icd_dgns_cd2,
	icd_dgns_cd3,
	icd_dgns_cd4,
	icd_dgns_cd5,
	icd_dgns_cd6,
	icd_dgns_cd7,
	icd_dgns_cd8,
	icd_dgns_cd9,
	icd_dgns_cd10,
	icd_dgns_cd11,
	icd_dgns_cd12,
	icd_dgns_cd13,
	icd_dgns_cd14,
	icd_dgns_cd15,
	icd_dgns_cd16,
	icd_dgns_cd17,
	icd_dgns_cd18,
	icd_dgns_cd19,
	icd_dgns_cd20,
	icd_dgns_cd21,
	icd_dgns_cd22,
	icd_dgns_cd23,
	icd_dgns_cd24,
	icd_dgns_cd25,
	icd_dgns_e_cd1,
	icd_dgns_e_cd2,
	icd_dgns_e_cd3,
	icd_dgns_e_cd4,
	icd_dgns_e_cd5,
	icd_dgns_e_cd6,
	icd_dgns_e_cd7,
	icd_dgns_e_cd8,
	icd_dgns_e_cd9,
	icd_dgns_e_cd10,
	icd_dgns_e_cd11,
	icd_dgns_e_cd12,
	icd_dgns_e_vrsn_cd1,
	icd_dgns_e_vrsn_cd2,
	icd_dgns_e_vrsn_cd3,
	icd_dgns_e_vrsn_cd4,
	icd_dgns_e_vrsn_cd5,
	icd_dgns_e_vrsn_cd6,
	icd_dgns_e_vrsn_cd7,
	icd_dgns_e_vrsn_cd8,
	icd_dgns_e_vrsn_cd9,
	icd_dgns_e_vrsn_cd10,
	icd_dgns_e_vrsn_cd11,
	icd_dgns_e_vrsn_cd12,
	icd_dgns_vrsn_cd1,
	icd_dgns_vrsn_cd2,
	icd_dgns_vrsn_cd3,
	icd_dgns_vrsn_cd4,
	icd_dgns_vrsn_cd5,
	icd_dgns_vrsn_cd6,
	icd_dgns_vrsn_cd7,
	icd_dgns_vrsn_cd8,
	icd_dgns_vrsn_cd9,
	icd_dgns_vrsn_cd10,
	icd_dgns_vrsn_cd11,
	icd_dgns_vrsn_cd12,
	icd_dgns_vrsn_cd13,
	icd_dgns_vrsn_cd14,
	icd_dgns_vrsn_cd15,
	icd_dgns_vrsn_cd16,
	icd_dgns_vrsn_cd17,
	icd_dgns_vrsn_cd18,
	icd_dgns_vrsn_cd19,
	icd_dgns_vrsn_cd20,
	icd_dgns_vrsn_cd21,
	icd_dgns_vrsn_cd22,
	icd_dgns_vrsn_cd23,
	icd_dgns_vrsn_cd24,
	icd_dgns_vrsn_cd25
)
select
	cast("claimId" as bigint), 
	cast("beneficiaryId" as bigint), 
	cast("claimGroupId" as bigint),
	"lastupdated",
	"dateFrom",
	"dateThrough",
	"paymentAmount",
	"careStartDate",
	"claimFacilityTypeCode",
	"claimFrequencyCode",
	"claimLUPACode",
	"claimReferralCode",
	"totalVisitCount",
	"claimNonPaymentReasonCode",
	"prospectivePaymentCode",
	"claimServiceClassificationTypeCode",
	"fiscalIntermediaryClaimProcessDate",
	"fiDocumentClaimControlNumber",
	"fiscalIntermediaryNumber",
	"fiOriginalClaimControlNumber",
	"finalAction",
	"diagnosisExternalFirstCode",
	"diagnosisExternalFirstCodeVersion",
	"claimTypeCode",
	"nearLineRecordIdCode",
	"claimPrimaryPayerCode",
	"primaryPayerPaidAmount",
	"weeklyProcessDate",
	"organizationNpi",
	"diagnosisPrincipalCode",
	"diagnosisPrincipalCodeVersion",
	"providerNumber",
	"providerStateCode",
	"patientDischargeStatusCode",
	"totalChargeAmount",
	"attendingPhysicianNpi",
	"attendingPhysicianUpin",
	"diagnosis1Code",
	"diagnosis2Code",
	"diagnosis3Code",
	"diagnosis4Code",
	"diagnosis5Code",
	"diagnosis6Code",
	"diagnosis7Code",
	"diagnosis8Code",
	"diagnosis9Code",
	"diagnosis10Code",
	"diagnosis11Code",
	"diagnosis12Code",
	"diagnosis13Code",
	"diagnosis14Code",
	"diagnosis15Code",
	"diagnosis16Code",
	"diagnosis17Code",
	"diagnosis18Code",
	"diagnosis19Code",
	"diagnosis20Code",
	"diagnosis21Code",
	"diagnosis22Code",
	"diagnosis23Code",
	"diagnosis24Code",
	"diagnosis25Code",
	"diagnosisExternal1Code",
	"diagnosisExternal2Code",
	"diagnosisExternal3Code",
	"diagnosisExternal4Code",
	"diagnosisExternal5Code",
	"diagnosisExternal6Code",
	"diagnosisExternal7Code",
	"diagnosisExternal8Code",
	"diagnosisExternal9Code",
	"diagnosisExternal10Code",
	"diagnosisExternal11Code",
	"diagnosisExternal12Code",
	"diagnosisExternal1CodeVersion",
	"diagnosisExternal2CodeVersion",
	"diagnosisExternal3CodeVersion",
	"diagnosisExternal4CodeVersion",
	"diagnosisExternal5CodeVersion",
	"diagnosisExternal6CodeVersion",
	"diagnosisExternal7CodeVersion",
	"diagnosisExternal8CodeVersion",
	"diagnosisExternal9CodeVersion",
	"diagnosisExternal10CodeVersion",
	"diagnosisExternal11CodeVersion",
	"diagnosisExternal12CodeVersion",
	"diagnosis1CodeVersion",
	"diagnosis2CodeVersion",
	"diagnosis3CodeVersion",
	"diagnosis4CodeVersion",
	"diagnosis5CodeVersion",
	"diagnosis6CodeVersion",
	"diagnosis7CodeVersion",
	"diagnosis8CodeVersion",
	"diagnosis9CodeVersion",
	"diagnosis10CodeVersion",
	"diagnosis11CodeVersion",
	"diagnosis12CodeVersion",
	"diagnosis13CodeVersion",
	"diagnosis14CodeVersion",
	"diagnosis15CodeVersion",
	"diagnosis16CodeVersion",
	"diagnosis17CodeVersion",
	"diagnosis18CodeVersion",
	"diagnosis19CodeVersion",
	"diagnosis20CodeVersion",
	"diagnosis21CodeVersion",
	"diagnosis22CodeVersion",
	"diagnosis23CodeVersion",
	"diagnosis24CodeVersion",
	"diagnosis25CodeVersion"
from
	public."HHAClaims"
on conflict on constraint
	hha_claims_pkey
do nothing;