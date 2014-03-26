SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

-- calculate score for successful login events
rawLogin        = LOAD '$inputDataLogin' USING PigStorage('|') AS  (generatedTimeRaw:chararray,generatedTime:chararray,generatedTimeUnixTime:long,accountName:chararray,accountDomain:chararray,clientAddress:chararray,machineName:chararray,status:chararray,failureCode:chararray,authenticationType:chararray,ticketOptions:chararray,forwardable:chararray,forwarded:chararray,proxied:chararray,postdated:chararray,renew_request:chararray,constraint_delegation:chararray,normalized_username:chararray);
loginByTime     = FILTER rawLogin by generatedTimeUnixTime > (long)'$deltaTime';
loginUsersOnly  = FILTER loginByTime by NOT (LOWER(accountName) MATCHES LOWER('$accountRegex'));
loginSucessOnly = FILTER loginUsersOnly by status=='SUCCESS';
-- I'm not filtering dc servers here as it is interesting in this scenario more than in 4769 scenario
loginFields     = FOREACH loginSucessOnly GENERATE generatedTime,LOWER(accountName) as account_name,LOWER(machineName) as service_name,LOWER(machineName) as source_machine,failureCode,normalized_username;
loginPerUser    = GROUP loginFields by normalized_username PARALLEL 1;
loginScore      = FOREACH loginPerUser GENERATE FLATTEN(fortscale.ebs.EBSPigUDF(group,5,0,loginFields));

-- calculate score for successful ldap network authentication events
rawAuth         = LOAD '$inputData' USING PigStorage('|') AS (generatedTimeRaw:chararray,generatedTime:chararray,categoryString:chararray,eventCode:chararray,logfile:chararray,recordNumber:chararray,sourceName:chararray,account_name:chararray,account_domain:chararray,service_name:chararray,service_id:chararray,client_address:chararray,ticket_options:chararray,failure_code:chararray,source_network_address:chararray,generatedTimeUnixTime:long,computer_name:chararray,normalized_username:chararray);
authByTime      = FILTER rawAuth by generatedTimeUnixTime > (long)'$deltaTime';
onlyUsers       = FILTER authByTime by NOT (LOWER(account_name) MATCHES LOWER('$accountRegex'));
onlyUsersNoDC   = FILTER onlyUsers  by NOT (LOWER(service_name) MATCHES LOWER('$dcRegex'));
notSameHost		= FILTER onlyUsersNoDC by NOT ((computer_name is null) OR (computer_name=='') OR (STARTSWITH(LOWER(computer_name),LOWER(service_name))));
userWithComp    = FOREACH notSameHost GENERATE generatedTime,LOWER(account_name) as account_name,LOWER(service_name),(computer_name is null?client_address:(computer_name==''?client_address:LOWER(computer_name))),failure_code,normalized_username;
user            = GROUP userWithComp by normalized_username PARALLEL 1;
authScore       = FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,userWithComp ) );

result          = UNION loginScore, authScore;
store result into '$outputData' using PigStorage(',','-noschema');