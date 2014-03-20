SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

raw             	= LOAD '$inputData' USING PigStorage('|') AS (date_time:chararray,date_time_unixTime:long,username:chararray,source_ip:chararray,local_ip:chararray,status:chararray,country_name:chararray,region_name:chararray,city_name:chararray,isp_name:chararray,ipusage_name:chararray,hostname:chararray,normalized_username:chararray);
loginByTime     	= FILTER raw by date_time_unixTime > (long)'$deltaTime';
loginOrdered 		= FOREACH loginByTime GENERATE date_time, LOWER(hostname) as hostname,LOWER(country_name) as country_name,LOWER(region_name) as region_name,LOWER(city_name) as city_name,LOWER(isp_name) as isp_name,LOWER(ipusage_name) as ipusage_name,normalized_username,LOWER(username) as username,source_ip,LOWER(status) as status,local_ip,date_time_unixTime;
user     	= GROUP loginOrdered by normalized_username PARALLEL 1;
finalResult        	= FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,7,0,loginOrdered ) );
store finalResult into '$outputData' using PigStorage(',','-noschema');