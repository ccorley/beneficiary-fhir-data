${logic.hsql}
CREATE TYPE json AS longvarchar;

/**
  Simple container table to hold FissClaim objects as JSON with only columns needed for
  querying outside of the json.
 */
create table "pre_adj"."FissClaimsJson" (
    "dcn"         varchar(23) not null,
    "lastUpdated" timestamp with time zone,
    "claim"       json        not null,
    constraint "FissClaimsJson_pkey" primary key ("dcn")
);
