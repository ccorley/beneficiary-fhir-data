insert into public.hha_claim_lines ( 
	parent_claim,
	clm_line_num,
	line_pmt_amt,
	hcpcs_cd,
	hcpcs_1st_mdfr_cd,
	hcpcs_2nd_mdfr_cd,
	rndrng_physn_npi,
	rndrng_physn_upin,
	rev_cntr,
	rev_cntr_dt,
	rev_cntr_apc_hipps_cd,
	rev_cntr_ddctbl_coinsrnc_cd,
	rev_cntr_ndc_qty_qlfr_cd,
	rev_cntr_ndc_qty,
	rev_cntr_ncvrd_chrg_amt,
	rev_cntr_pmt_mthd_ind_cd,
	rev_cntr_rate_amt,
	rev_cntr_1st_ansi_cd,
	rev_cntr_stus_ind_cd,
	rev_cntr_tot_chrg_amt,
	rev_cntr_unit_cnt
)
select
	cast("parentClaim" as bigint),
	"lineNumber",
	"paymentAmount",
	"hcpcsCode",
	"hcpcsInitialModifierCode",
	"hcpcsSecondModifierCode",
	"revenueCenterRenderingPhysicianNPI",
	"revenueCenterRenderingPhysicianUPIN",
	"revenueCenterCode",
	"revenueCenterDate",
	"apcOrHippsCode",
	"deductibleCoinsuranceCd",
	"nationalDrugCodeQualifierCode",
	"nationalDrugCodeQuantity",
	"nonCoveredChargeAmount",
	"paymentMethodCode",
	"rateAmount",
	"revCntr1stAnsiCd",
	"statusCode",
	"totalChargeAmount",
	"unitCount"
from
	public."HHAClaimLines"
on conflict on constraint
	hha_claim_lines_pkey
do nothing;