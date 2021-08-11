insert into public.snf_claims (
	clm_id,
	bene_id,
	clm_grp_id,
	last_updated,
	clm_from_dt,
	clm_thru_dt,
	clm_admsn_dt,
	clm_drg_cd,
	clm_fac_type_cd,
	clm_freq_cd,
	clm_ip_admsn_type_cd,
	clm_mco_pd_sw,
	clm_mdcr_non_pmt_rsn_cd,
	clm_non_utlztn_days_cnt,
	clm_pmt_amt,
	clm_pps_cptl_dsprprtnt_shr_amt,
	clm_pps_cptl_excptn_amt,
	clm_pps_cptl_fsp_amt,
	clm_pps_cptl_ime_amt,
	clm_pps_cptl_outlier_amt,
	clm_pps_ind_cd,
	clm_pps_old_cptl_hld_hrmls_amt,
	clm_src_ip_admsn_cd,
	clm_srvc_clsfctn_type_cd,
	clm_utlztn_day_cnt,
	admtg_dgns_cd,
	admtg_dgns_vrsn_cd,
	at_physn_npi,
	at_physn_upin,
	bene_tot_coinsrnc_days_cnt,
	claim_query_code,
	fi_clm_actn_cd,
	fi_clm_proc_dt,
	fi_doc_clm_cntl_num,
	fi_num,
	fi_orig_clm_cntl_num,
	final_action,
	fst_dgns_e_cd,
	fst_dgns_e_vrsn_cd,
	nch_actv_or_cvrd_lvl_care_thru,
	nch_bene_blood_ddctbl_lblty_am,
	nch_bene_dschrg_dt,
	nch_bene_ip_ddctbl_amt,
	nch_bene_mdcr_bnfts_exhtd_dt_i,
	nch_bene_pta_coinsrnc_lblty_am,
	nch_blood_pnts_frnshd_qty,
	nch_clm_type_cd,
	nch_ip_ncvrd_chrg_amt,
	nch_ip_tot_ddctn_amt,
	nch_near_line_rec_ident_cd,
	nch_prmry_pyr_cd,
	nch_prmry_pyr_clm_pd_amt,
	nch_ptnt_status_ind_cd,
	nch_qlfyd_stay_from_dt,
	nch_qlfyd_stay_thru_dt,
	nch_vrfd_ncvrd_stay_from_dt,
	nch_vrfd_ncvrd_stay_thru_dt,
	nch_wkly_proc_dt,
	op_physn_npi,
	op_physn_upin,
	org_npi_num,
	ot_physn_npi,
	ot_physn_upin,
	prncpal_dgns_cd,
	prncpal_dgns_vrsn_cd,
	prvdr_num,
	prvdr_state_cd,
	ptnt_dschrg_stus_cd,
	rev_cntr_tot_chrg_amt,
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
	icd_dgns_vrsn_cd25,
	icd_prcdr_cd1,
	icd_prcdr_cd2,
	icd_prcdr_cd3,
	icd_prcdr_cd4,
	icd_prcdr_cd5,
	icd_prcdr_cd6,
	icd_prcdr_cd7,
	icd_prcdr_cd8,
	icd_prcdr_cd9,
	icd_prcdr_cd10,
	icd_prcdr_cd11,
	icd_prcdr_cd12,
	icd_prcdr_cd13,
	icd_prcdr_cd14,
	icd_prcdr_cd15,
	icd_prcdr_cd16,
	icd_prcdr_cd17,
	icd_prcdr_cd18,
	icd_prcdr_cd19,
	icd_prcdr_cd20,
	icd_prcdr_cd21,
	icd_prcdr_cd22,
	icd_prcdr_cd23,
	icd_prcdr_cd24,
	icd_prcdr_cd25,
	icd_prcdr_vrsn_cd1,
	icd_prcdr_vrsn_cd2,
	icd_prcdr_vrsn_cd3,
	icd_prcdr_vrsn_cd4,
	icd_prcdr_vrsn_cd5,
	icd_prcdr_vrsn_cd6,
	icd_prcdr_vrsn_cd7,
	icd_prcdr_vrsn_cd8,
	icd_prcdr_vrsn_cd9,
	icd_prcdr_vrsn_cd10,
	icd_prcdr_vrsn_cd11,
	icd_prcdr_vrsn_cd12,
	icd_prcdr_vrsn_cd13,
	icd_prcdr_vrsn_cd14,
	icd_prcdr_vrsn_cd15,
	icd_prcdr_vrsn_cd16,
	icd_prcdr_vrsn_cd17,
	icd_prcdr_vrsn_cd18,
	icd_prcdr_vrsn_cd19,
	icd_prcdr_vrsn_cd20,
	icd_prcdr_vrsn_cd21,
	icd_prcdr_vrsn_cd22,
	icd_prcdr_vrsn_cd23,
	icd_prcdr_vrsn_cd24,
	icd_prcdr_vrsn_cd25,
	prcdr_dt1,
	prcdr_dt2,
	prcdr_dt3,
	prcdr_dt4,
	prcdr_dt5,
	prcdr_dt6,
	prcdr_dt7,
	prcdr_dt8,
	prcdr_dt9,
	prcdr_dt10,
	prcdr_dt11,
	prcdr_dt12,
	prcdr_dt13,
	prcdr_dt14,
	prcdr_dt15,
	prcdr_dt16,
	prcdr_dt17,
	prcdr_dt18,
	prcdr_dt19,
	prcdr_dt20,
	prcdr_dt21,
	prcdr_dt22,
	prcdr_dt23,
	prcdr_dt24,
	prcdr_dt25
)
select
	cast("claimId" as bigint),
	cast("beneficiaryId" as bigint),
	cast("claimGroupId" as bigint),
	"lastupdated",
	"dateFrom",
	"dateThrough",
	"claimAdmissionDate",
	"diagnosisRelatedGroupCd",
	"claimFacilityTypeCode",
	"claimFrequencyCode",
	"admissionTypeCd",
	"mcoPaidSw",
	"claimNonPaymentReasonCode",
	"nonUtilizationDayCount",
	"paymentAmount",
	"claimPPSCapitalDisproportionateShareAmt",
	"claimPPSCapitalExceptionAmount",
	"claimPPSCapitalFSPAmount",
	"claimPPSCapitalIMEAmount",
	"claimPPSCapitalOutlierAmount",
	"prospectivePaymentCode",
	"claimPPSOldCapitalHoldHarmlessAmount",
	"sourceAdmissionCd",
	"claimServiceClassificationTypeCode",
	"utilizationDayCount",
	"diagnosisAdmittingCode",
	"diagnosisAdmittingCodeVersion",
	"attendingPhysicianNpi",
	"attendingPhysicianUpin",
	"coinsuranceDayCount",
	"claimQueryCode",
	"fiscalIntermediaryClaimActionCode",
	"fiscalIntermediaryClaimProcessDate",
	"fiDocumentClaimControlNumber",
	"fiscalIntermediaryNumber",
	"fiOriginalClaimControlNumber",
	"finalAction",
	"diagnosisExternalFirstCode",
	"diagnosisExternalFirstCodeVersion",
	"coveredCareThroughDate",
	"bloodDeductibleLiabilityAmount",
	"beneficiaryDischargeDate",
	"deductibleAmount",
	"medicareBenefitsExhaustedDate",
	"partACoinsuranceLiabilityAmount",
	"bloodPintsFurnishedQty",
	"claimTypeCode",
	"noncoveredCharge",
	"totalDeductionAmount",
	"nearLineRecordIdCode",
	"claimPrimaryPayerCode",
	"primaryPayerPaidAmount",
	"patientStatusCd",
	"qualifiedStayFromDate",
	"qualifiedStayThroughDate",
	"noncoveredStayFromDate",
	"noncoveredStayThroughDate",
	"weeklyProcessDate",
	"operatingPhysicianNpi",
	"operatingPhysicianUpin",
	"organizationNpi",
	"otherPhysicianNpi",
	"otherPhysicianUpin",
	"diagnosisPrincipalCode",
	"diagnosisPrincipalCodeVersion",
	"providerNumber",
	"providerStateCode",
	"patientDischargeStatusCode",
	"totalChargeAmount",
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
	"diagnosis25CodeVersion",
	"procedure1Code",
	"procedure2Code",
	"procedure3Code",
	"procedure4Code",
	"procedure5Code",
	"procedure6Code",
	"procedure7Code",
	"procedure8Code",
	"procedure9Code",
	"procedure10Code",
	"procedure11Code",
	"procedure12Code",
	"procedure13Code",
	"procedure14Code",
	"procedure15Code",
	"procedure16Code",
	"procedure17Code",
	"procedure18Code",
	"procedure19Code",
	"procedure20Code",
	"procedure21Code",
	"procedure22Code",
	"procedure23Code",
	"procedure24Code",
	"procedure25Code",
	"procedure1CodeVersion",
	"procedure2CodeVersion",
	"procedure3CodeVersion",
	"procedure4CodeVersion",
	"procedure5CodeVersion",
	"procedure6CodeVersion",
	"procedure7CodeVersion",
	"procedure8CodeVersion",
	"procedure9CodeVersion",
	"procedure10CodeVersion",
	"procedure11CodeVersion",
	"procedure12CodeVersion",
	"procedure13CodeVersion",
	"procedure14CodeVersion",
	"procedure15CodeVersion",
	"procedure16CodeVersion",
	"procedure17CodeVersion",
	"procedure18CodeVersion",
	"procedure19CodeVersion",
	"procedure20CodeVersion",
	"procedure21CodeVersion",
	"procedure22CodeVersion",
	"procedure23CodeVersion",
	"procedure24CodeVersion",
	"procedure25CodeVersion",
	"procedure1Date",
	"procedure2Date",
	"procedure3Date",
	"procedure4Date",
	"procedure5Date",
	"procedure6Date",
	"procedure7Date",
	"procedure8Date",
	"procedure9Date",
	"procedure10Date",
	"procedure11Date",
	"procedure12Date",
	"procedure13Date",
	"procedure14Date",
	"procedure15Date",
	"procedure16Date",
	"procedure17Date",
	"procedure18Date",
	"procedure19Date",
	"procedure20Date",
	"procedure21Date",
	"procedure22Date",
	"procedure23Date",
	"procedure24Date",
	"procedure25Date"
from
	public."SNFClaims"
on conflict on constraint
	snf_claims_pkey
do nothing;