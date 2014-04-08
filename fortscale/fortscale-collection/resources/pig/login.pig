SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

-- calculate score for successful login events
rawLogin        = LOAD '$inputData' USING PigStorage('|') AS  (generatedTimeRaw:chararray,generatedTime:chararray,generatedTimeUnixTime:long,accountName:chararray,accountDomain:chararray,userid:chararray,eventCode:chararray,clientAddress:chararray,machineName:chararray,status:chararray,failureCode:chararray,authenticationType:chararray,ticketOptions:chararray,forwardable:chararray,forwarded:chararray,proxied:chararray,postdated:chararray,renew_request:chararray,constraint_delegation:chararray,is_nat:chararray,src_class:chararray,normalized_username:chararray);
loginByTime     = FILTER rawLogin by generatedTimeUnixTime > (long)'$deltaTime';
loginUsersOnly  = FILTER loginByTime by NOT (LOWER(accountName) MATCHES LOWER('$accountRegex'));
-- Filtering failed login to dc machines with failure_code 0x18 and 0x17 
loginUsersOnlyNoFailedDC = FILTER loginUsersOnly by NOT (status=='FAILURE' and LOWER(machineName) MATCHES LOWER('$dcRegex'));
-- all failure codes except the list below are counted as success in the scoring algorithm.
-- the failure codes 0x12 and 0x22 are sent to the scoring algorithm with the event time value in order to get high score for them.
-- 0x12: Clients credentials have been revoked - Account disabled, expired, locked out, logon hours.
-- 0x22: Request is a replay
-- also, ignore logins from nat addresses
loginFields     = FOREACH loginUsersOnlyNoFailedDC GENERATE generatedTime,(LOWER(is_nat)=='true'? '': LOWER(machineName)),((failureCode is null) or ((failureCode != '0x12') and (failureCode != '0x22')) ? '0x0' : generatedTime),failureCode,normalized_username,eventCode,LOWER(accountName) as account_name,LOWER(machineName) as source_machine;
loginPerUser    = GROUP loginFields by normalized_username PARALLEL 1;
loginScore      = FOREACH loginPerUser GENERATE FLATTEN(fortscale.ebs.EBSPigUDF(group,3,0,loginFields));
loginFinalScore	= FOREACH loginScore GENERATE $0 as time,$1 as normalizedtime,$8 as hostname,$4 as errorcode,$5 as normalized_username,$6 as eventCode,$7 as accountname, $9 as timescore,$10 as hostnamescore,$11 as errorcodescore,$12 as eventscore,$13 as globalscore,$14 as entitygroupedbyid;
STORE loginFinalScore INTO '$outputData' USING PigStorage(',','-noschema');