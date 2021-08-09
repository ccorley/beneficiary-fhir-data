do $$
DECLARE
  orig			record;
  curr			record;
  err_cnt	    INTEGER := 0;
  tot_err_cnt	INTEGER := 0;
  v_bene_id		bigint  := 0;
  v_tbl_name	varchar(40) := 'beneficiary_monthly';

BEGIN
	for counter in 1..1000
	loop
		-- randomly select a "beneficiaryId" from original table
		SELECT cast("parentBeneficiary" as bigint) into v_bene_id
		FROM public."BeneficiaryMonthly" TABLESAMPLE SYSTEM_ROWS(40)
		limit 1;

		select into curr
			bene_id as f_1,
			year_month as f_2,
			partd_contract_number_id as f_3,
			partc_contract_number_id as f_4,
			fips_state_cnty_code as f_5,
			medicare_status_code as f_6,
			entitlement_buy_in_ind as f_7,
			hmo_indicator_ind as f_8,
			partc_pbp_number_id as f_9,
			partc_plan_type_code as f_10,
			partd_pbp_number_id as f_11,
			partd_segment_number_id as f_12,
			partd_retiree_drug_subsidy_ind as f_13,
			partd_low_income_cost_share_group_code as f_14,
			medicaid_dual_eligibility_code as f_15
		from
			public.beneficiary_monthly
		WHERE
			bene_id = v_bene_id;
		

		SELECT INTO orig
			cast("parentBeneficiary" as bigint) as f_1,
			"yearMonth" as f_2,
			"partDContractNumberId" as f_3,
			"partCContractNumberId" as f_4,
			"fipsStateCntyCode" as f_5,
			"medicareStatusCode" as f_6,
			"entitlementBuyInInd" as f_7,
			"hmoIndicatorInd" as f_8,
			"partCPbpNumberId" as f_9,
			"partCPlanTypeCode" as f_10,
			"partDPbpNumberId" as f_11,
			"partDSegmentNumberId" as f_12,
			"partDRetireeDrugSubsidyInd" as f_13,
			"partDLowIncomeCostShareGroupCode" as f_14,
			"medicaidDualEligibilityCode" as f_15
		from
			public."BeneficiaryMonthly"
		WHERE
			"parentBeneficiary" = v_bene_id::text;
		
		if curr.f_1 <> orig.f_1 then err_cnt := err_cnt + 1; end if;
		if curr.f_2 <> orig.f_2 then err_cnt := err_cnt + 1; end if;
		if curr.f_3 <> orig.f_3 then err_cnt := err_cnt + 1; end if;
		if curr.f_4 <> orig.f_4 then err_cnt := err_cnt + 1; end if;
		if curr.f_5 <> orig.f_5 then err_cnt := err_cnt + 1; end if;
		if curr.f_6 <> orig.f_6 then err_cnt := err_cnt + 1; end if;
		if curr.f_7 <> orig.f_7 then err_cnt := err_cnt + 1; end if;
		if curr.f_8 <> orig.f_8 then err_cnt := err_cnt + 1; end if;
		if curr.f_9 <> orig.f_9 then err_cnt := err_cnt + 1; end if;
		if curr.f_10 <> orig.f_10 then err_cnt := err_cnt + 1; end if;
		if curr.f_11 <> orig.f_11 then err_cnt := err_cnt + 1; end if;
		if curr.f_12 <> orig.f_12 then err_cnt := err_cnt + 1; end if;
		if curr.f_13 <> orig.f_13 then err_cnt := err_cnt + 1; end if;
		if curr.f_14 <> orig.f_14 then err_cnt := err_cnt + 1; end if;
		if curr.f_15 <> orig.f_15 then err_cnt := err_cnt + 1; end if;
	
		if err_cnt > 0 then
			insert into migration_errors values(v_tbl_name, v_bene_id, null, null, err_cnt);
			tot_err_cnt := tot_err_cnt + err_cnt;
			raise info 'DISCREPANCY, table: %, bene_id: %', v_tbl_name, v_bene_id;
		end if;
	end loop;
	
	if tot_err_cnt > 0 then
		raise info 'DISCREPANCY, table: %', v_tbl_name;
	else
		raise info 'NO ERRORS, table: %', v_tbl_name;
	end if;
END;
$$;