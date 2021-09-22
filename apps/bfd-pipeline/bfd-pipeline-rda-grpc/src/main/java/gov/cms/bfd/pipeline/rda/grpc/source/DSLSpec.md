# RDA Entity and Transformation DSL Spec

# Overview

RDA API data arrives in the form of protobuf message objects. These objects follow a set of conventions established by
the RDA API team when they write the proto files. Currently, we have hand-written java code to copy data from these
message objects into our own database entity objects. The code to perform this copying is embodied in
the `FissClaimTransformer` and `McsClaimTransformer` classes. Those classes use lower level classes to handle the
underlying idioms used by the RDA API. For example `EnumStringExtractor` knows how to map the contents of an enum field
to a string in our entities and `DataTransformer` knows how to copy and validate a variety of types of data such as
dates, strings, and enums.

We would like to develop a higher level abstraction to generate similar code from a higher level specification. This
specification would take the form of a DSL based on some hierarchical data specification language such as YAML or JSON.
A code generator would process the DSL and use `javapoet` to generate both the JPA entity classes and the transformer
classes. The generated code would use the same low level classes as our hand-written code.

# Strategy

Ideally the DSL would be a more succinct version of the code in the current transformers and would map easily into
classes and method calls. The DSL would benefit from conventions to simplify the structure of its code. The method calls
in `FissClaimTransformer` would be mapped to objects in the DSL. Additional properties would be defined in these objects
to specify the field names. Many things that have to be handled explicitly in java code could be added by convention in
the DSL processor.

Here is an example of java code in an existing transformer.

````java
private static final EnumStringExtractor<FissClaim, FissCurrentLocation2> currLoc2 =
    new EnumStringExtractor<>(
        FissClaim::hasCurrLoc2Enum,
        FissClaim::getCurrLoc2Enum,
        FissClaim::hasCurrLoc2Unrecognized,
        FissClaim::getCurrLoc2Unrecognized,
        FissCurrentLocation2.UNRECOGNIZED,
        ImmutableSet.of(),
        ImmutableSet.of());
final PreAdjFissClaim to = new PreAdjFissClaim();
transformer
    .copyString(PreAdjFissClaim.Fields.dcn, false, 1, 23, from.getDcn(), to::setDcn)
    .copyEnumAsCharacter(
        PreAdjFissClaim.Fields.currStatus, currStatus.getEnumString(from), to::setCurrStatus)
    .copyEnumAsString(
        PreAdjFissClaim.Fields.currLoc2,
        false,
        1,
        5,
        currLoc2.getEnumString(from),
        to::setCurrLoc2)
    .copyOptionalString(
        PreAdjFissClaim.Fields.medaProvId,
        1,
        13,
        from::hasMedaProvId,
        from::getMedaProvId,
        to::setMedaProvId)
    .copyOptionalAmount(
        PreAdjFissClaim.Fields.totalChargeAmount,
        from::hasTotalChargeAmount,
        from::getTotalChargeAmount,
        to::setTotalChargeAmount)
    .copyOptionalDate(
        PreAdjFissClaim.Fields.receivedDate,
        from::hasRecdDtCymd,
        from::getRecdDtCymd,
        to::setReceivedDate)
````

Here is how it might be mapped to DSL code using YAML.

````yaml
mapping:
  id: PreAdjFissClaim
  message: gov.cms.mpsm.rda.v1.fiss.FissClaim
  entity: gov.cms.bfd.model.rda.PreAdjFissClaim
  fields:
    - from: dcn
      type: varchar(23)
      nullable: false
    - from: currStatus
      type: char(1)
      nullable: false
      enum: gov.cms.mpsm.rda.v1.fiss.FissClaimStatus
    - from: currLoc2
      type: varchar(5)
      nullable: false
      enum: gov.cms.mpsm.rda.v1.fiss.FissCurrentLocation2
    - from: medaProvId
      type: varchar(13)
    - from: totalChargeAmount
      type: decimal(11,2)
    - from: recdDtCymd
      to: receivedDate
      type: date
  arrays:
    - from: fissProcCodes
      to: procCodes
      mapping: PreAdjFissProcCode
  primaryKeyFields:
    - dcn
````

The DSL would benefit from the following conventions:

- The `to` property for every field would only be necessary if it differed from the `from` property.
- The `nullable` property would default to `true` so it could be omitted for optional fields. Also the DSL processor
  would know that nullable fields would be optional in the incoming message field.
- The mapping of specific SQL types in the `type` property would tell the DSL which transformation to apply to the
  incoming string to convert it to the proper database type as well as which java type to use for the JPA entity object
  field.
- The conventions for how enum fields are mapped in messages by the RDA API would be known by the DSL processor so the
  values previously defined in an `EnumStringExtractor` constructor would be inferred by the DSL processor. It should be
  sufficient to specify the enum class and the field name of the enum field in the message.
- Mappings could be referenced from other mappings. In this example arrays would be used to define the detail messages
  from the RDA API and the mapping for each array would simply be referenced by id. Alternatively we could nest the
  mappings but this might be harder to read for long data definitions.
- Arrays would have their primary key column and the `priority` column automatically generated by the DSL processor.

YAML can be somewhat wordy when compared to rows in a spreadsheet but it is also easier to edit, more flexible, and has
a hierarchical structure.

## Code Generation

The code generator could be defined as a maven plugin. The `pom.xml` would be configured to trigger the plugin during
the `generate-sources` lifecycle phase.

## TODO

This document is a work in progress. Here are a few of the things that it needs.

- How do we handle change objects? They will include a sequence number field and a field for the claim.
- How do we actually use the generated code? Maybe it just implements an interface and we still have some logic in the
  caller (i.e. the `RdaSource`) to pass in whatever is needed.
- How do we ensure our plugin runs after the protobuf plugin? Or do we even need to? If we plan to do any reflection
  then we would want those classes to be in our classpath.
