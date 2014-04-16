SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

raw					= LOAD '$inputData' USING PigStorage('|') AS (date_time:chararray,date_time_unixTime:long,username:chararray,source_ip:chararray,local_ip:chararray,status:chararray,country_name:chararray,region_name:chararray,city_name:chararray,isp_name:chararray,ipusage:chararray,hostname:chararray,totalbytes:long,readbytes:long,writebytes:long,duration:int,databucket:int,normalized_username:chararray); 
loginByTime			= FILTER raw by date_time_unixTime > (long)'$deltaTime';
loginByTimeAndStatus = FILTER loginByTime by NOT (status=='CLOSED');
loginOrdered		= FOREACH loginByTimeAndStatus GENERATE date_time, LOWER(hostname) as hostname,((country_name=='Reserved Range')? '' : LOWER(country_name)) as country_name,((ipusage is null) or (ipusage != 'mob') ? LOWER(region_name) : ''),((ipusage is null) or (ipusage != 'mob') ? LOWER(city_name) : ''),LOWER(isp_name) as isp_name,ipusage,LOWER(region_name) as region_name,LOWER(city_name) as city_name,normalized_username,LOWER(username) as username,source_ip,LOWER(status) as status,local_ip,date_time_unixTime, LOWER(country_name) as country_name;
user				= GROUP loginOrdered by normalized_username PARALLEL 1;
scoringResult		= FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,5,0,loginOrdered ) );
finalResult			= FOREACH scoringResult GENERATE $0 as date_time,$1 as normalizedtime,$2 as hostname,$16 as country,$8 as region,$9 as city,$6 as isp,$7 as ipusage,$10 as normalized_username,$11 as username,$12 as source_ip,$13 as status,$14 as local_ip,$15 as date_time_unix,$17 as date_timescore,$18 as hostnamescore,$19 as countryscore,$20 as regionscore,$21 as cityscore,$22 as eventscore,$23 as globalscore,$24 as entitygroupedbyid;
store finalResult into '$outputData' using PigStorage(',','-noschema');