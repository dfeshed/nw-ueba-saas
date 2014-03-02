SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

REGISTER '$jarFilePath1';
REGISTER '$jarFilePath2';
raw             	= LOAD '$inputData' USING PigStorage('|') AS (date_time:chararray,date_time_unixTime:long,username:chararray,source_ip:chararray,local_ip:chararray,status:chararray,country_name:chararray,hostname:chararray,normalized_username:chararray);
loginByTime     	= FILTER raw by date_time_unixTime > (long)'$deltaTime';
loginOrdered 		= FOREACH loginByTime GENERATE date_time,LOWER(username) as username,source_ip,LOWER(status) as status,LOWER(country_name) as country_name,normalized_username,local_ip,date_time_unixTime;
userSuccess     	= GROUP loginOrdered by normalized_username PARALLEL 1;
finalResult        	= FOREACH userSuccess GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,loginOrdered ) );
store finalResult into '$outputData' using PigStorage(',','-noschema');