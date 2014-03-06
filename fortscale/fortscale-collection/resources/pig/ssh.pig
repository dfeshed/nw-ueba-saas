SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

raw             = LOAD '$inputData' USING PigStorage(',') AS (date_time:chararray,date_time_epoch:long,ip:chararray,target_machine:chararray,user_name:chararray,status:chararray,auth_method:chararray,client_hostname:chararray,normalized_username:chararray);
loginByTime     = FILTER raw by date_time_epoch > (long)'$deltaTime';
selectedFields = FOREACH loginByTime GENERATE date_time,LOWER(user_name) as user_name,LOWER(target_machine) as target_machine,(client_hostname is null? ip : (client_hostname=='' ? ip : LOWER(client_hostname))),LOWER(status) as status,LOWER(auth_method) as auth_method,normalized_username;
user            = GROUP selectedFields by normalized_username PARALLEL 1;
result          = FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,6,0,selectedFields ) );
store result into '$outputData' using PigStorage(',','-noschema');