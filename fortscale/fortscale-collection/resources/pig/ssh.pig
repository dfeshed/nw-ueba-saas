SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

REGISTER '$jarFilePath1';
REGISTER '$jarFilePath2';
raw             = LOAD '$inputData' USING PigStorage(',') AS (date_time:chararray,date_time_epoch:long,ip:chararray,target_machine:chararray,user_name:chararray,status:chararray,auth_method:chararray,client_hostname:chararray);
loginByTime     = FILTER raw by date_time_epoch > (long)'$deltaTime';
selectedFields = FOREACH loginByTime GENERATE date_time,LOWER(user_name),LOWER(target_machine),(client_hostname is null? ip : (client_hostname=='' ? ip : LOWER(client_hostname))),LOWER(status),LOWER(auth_method);
user            = GROUP selectedFields by user_name PARALLEL 1;
result          = FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,6,0,selectedFields ) );
store result into '$outputData' using PigStorage(',','-noschema');