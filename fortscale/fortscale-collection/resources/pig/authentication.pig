SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET  pig.tmpfilecompression.storage seqfile
SET  pig.maxCombinedSplitSize 2147483648

REGISTER '$jarFilePath1';
REGISTER '$jarFilePath2';
raw             = LOAD '$inputData' USING PigStorage('|') AS (generatedTimeRaw:chararray,generatedTime:chararray,categoryString:chararray,eventCode:chararray,logfile:chararray,recordNumber:chararray,sourceName:chararray,account_name:chararray,account_domain:chararray,service_name:chararray,service_id:chararray,client_address:chararray,ticket_options:chararray,failure_code:chararray,source_network_address:chararray,generatedTimeUnixTime:long,computer_name:chararray);
loginByTime     = FILTER raw by generatedTimeUnixTime > (long)'$deltaTime';
onlyUsers       = FILTER loginByTime by NOT (account_name MATCHES '$accountRegex');
onlyUsersNoDC   = FILTER onlyUsers  by NOT (service_name MATCHES '$dcRegex');
userWithCompOrdered = FOREACH onlyUsersNoDC GENERATE generatedTime,LOWER(account_name) as account_name,LOWER(service_name),(computer_name is null?client_address:(computer_name==''?client_address:LOWER(computer_name))),failure_code;
user            = GROUP userWithCompOrdered by account_name PARALLEL 1;
result          = FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,userWithCompOrdered ) );
store result into '$outputData' using PigStorage(',','-noschema');