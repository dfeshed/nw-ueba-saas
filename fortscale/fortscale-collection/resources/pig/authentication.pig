SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

-- calculate score for successful ldap network authentication events
rawAuth         = LOAD '$inputData' USING PigStorage('|') AS (generatedTimeRaw:chararray,generatedTime:chararray,categoryString:chararray,eventCode:chararray,logfile:chararray,recordNumber:chararray,sourceName:chararray,account_name:chararray,account_domain:chararray,service_name:chararray,service_id:chararray,client_address:chararray,ticket_options:chararray,failure_code:chararray,source_network_address:chararray,generatedTimeUnixTime:long,computer_name:chararray,is_nat:chararray,dst_class:chararray,src_class:chararray,normalized_src_machine:chararray,normalized_dst_machine:chararray,normalized_username:chararray);
authByTime      = FILTER rawAuth by generatedTimeUnixTime > (long)'$deltaTime';
onlyUsers       = FILTER authByTime by NOT (LOWER(account_name) MATCHES LOWER('$accountRegex'));
onlyUsersNoDC   = FILTER onlyUsers  by NOT (LOWER(service_name) MATCHES LOWER('$dcRegex'));
notSameHost		= FILTER onlyUsersNoDC by NOT ((computer_name is null) OR (computer_name=='') OR (STARTSWITH(LOWER(computer_name),LOWER(service_name))));
-- all failure codes except the list below are counted as success in the scoring algorithm.
-- the failure codes 0x12 and 0x22 are sent to the scoring algorithm with the event time value in order to get high score for them.
-- in case of source ip address which is nat, put don't care value for scoring
userWithComp    = FOREACH notSameHost GENERATE generatedTime,LOWER(account_name) as account_name,LOWER(normalized_dst_machine),(LOWER(is_nat)=='true'? '': (normalized_src_machine is null?client_address:(normalized_src_machine==''?client_address:LOWER(normalized_src_machine)))),((failure_code is null) or ((failure_code != '0x12') and (failure_code != '0x22')) ? '0x0' : generatedTime),failure_code,normalized_username,eventCode,(computer_name is null?client_address:(computer_name==''?client_address:LOWER(computer_name))),src_class,dst_class,LOWER(service_name);
user            = GROUP userWithComp by normalized_username PARALLEL 1;
authScoringResult	= FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,userWithComp ) );
authFinalScoringResult = FOREACH authScoringResult GENERATE $0 as time,$1 as normalizedtime,$2 as userid,$12 as targetid,$11 as dst_class,$9 as sourceip,$10 as src_class,$6 as errorcode,$7 as normalized_username,$8 as eventCode, $13 as timescore,$14 as useridscore,$15 as targetidscore,$16 as sourceipscore,$17 as errorcodescore,$18 as eventscore,$19 as globalscore,$20 as entitygroupedbyid;
store authFinalScoringResult into '$outputData' using PigStorage(',','-noschema');