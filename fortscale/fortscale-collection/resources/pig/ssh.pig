SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

raw             = LOAD '$inputData' USING PigStorage(',') AS (date_time:chararray,date_time_epoch:long,ip:chararray,target_machine:chararray,user_name:chararray,status:chararray,auth_method:chararray,client_hostname:chararray,normalized_src_machine:chararray,normalized_dst_machine:chararray,is_nat:chararray,normalized_username:chararray);
loginByTime     = FILTER raw by date_time_epoch > (long)'$deltaTime';
selectedFields = FOREACH loginByTime GENERATE date_time,LOWER(user_name) as user_name,LOWER(normalized_dst_machine) as score_target_machine,(normalized_src_machine is null? '' : LOWER(normalized_src_machine)) as normalized_src_machine,LOWER(status) as status,LOWER(auth_method) as auth_method,normalized_username,target_machine,(client_hostname is null? ip : (client_hostname=='' ? ip : LOWER(client_hostname))) as client_hostname;
user            = GROUP selectedFields by normalized_username PARALLEL 1;
result          = FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,6,0,selectedFields ) );
sshFinalScore	= FOREACH result GENERATE $0 as time,$1 as normalizedtime,$2 as username,$8 as target_machine,$9 as client_machine, $5 as status, $6 as auth_method, $7 as normalized_username, $10 as timescore, $11 as usernameScore, $12 as targetScore, $13 as clientScore, $14 as statusScore, $15 as auth_method_score, $16 as eventscore,$17 as globalscore,$18 as entitygroupedbyid;
store sshFinalScore into '$outputData' using PigStorage(',','-noschema');
