SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

REGISTER '$jarFilePath1';
REGISTER '$jarFilePath2';
raw             	= LOAD '$inputData' USING PigStorage('|') AS (date_time:chararray,date_time_unixTime:long,username:chararray,source_ip:chararray,local_ip:chararray,status:chararray,country_name:chararray);
loginByTime     	= FILTER raw by date_time_unixTime > (long)'$deltaTime';
loginOrdered 		= FOREACH loginByTime GENERATE date_time,LOWER(username) as username,source_ip,LOWER(status) as status,LOWER(country_name) as country_name,date_time_unixTime,local_ip;
success         	= FILTER loginOrdered by status == 'success';
fail            	= FILTER loginOrdered by status == 'fail';
userSuccess     	= GROUP success by username PARALLEL 1;
userFail     		= GROUP fail by username PARALLEL 1;
result1         	= FOREACH userSuccess GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,success ) );
result2         	= FOREACH userFail GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,fail ) );
resultSucessScore	= FOREACH result1 GENERATE $14,$15;
resultSuccesDistinct= DISTINCT resultSucessScore;
newFail             = JOIN resultSuccesDistinct BY $1, result2 by $15;
resultFail          = FOREACH newFail GENERATE $2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15,$0,$17;
result              = UNION resultFail, result1;
finalResult         = FOREACH result GENERATE $0,$6,$2,$3,$7,$4,$5,$8,$9,$10,$11,$12,$13,$14,$15;
store finalResult into '$outputData' using PigStorage(',','-noschema');