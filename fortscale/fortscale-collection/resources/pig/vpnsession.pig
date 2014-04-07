SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

raw					= LOAD '$inputData' USING PigStorage('|') AS (date_time:chararray,date_time_unixTime:long,username:chararray,source_ip:chararray,local_ip:chararray,status:chararray,country_name:chararray,region_name:chararray,city_name:chararray,isp_name:chararray,ipusage:chararray,hostname:chararray,totalbytes:long,readbytes:long,writebytes:long,duration:int,databucket:int,normalized_username:chararray);
loginByTime			= FILTER raw by date_time_unixTime > (long)'$deltaTime';
loginOrdered		= FOREACH loginByTime GENERATE date_time, LOWER(hostname) as hostname,LOWER(country_name) as country_name,((ipusage is null) or (ipusage != 'mob') ? LOWER(region_name) : ''),((ipusage is null) or (ipusage != 'mob') ? LOWER(city_name) : ''),databucket,LOWER(isp_name) as isp_name,ipusage,LOWER(region_name) as region_name,LOWER(city_name) as city_name,normalized_username,LOWER(username) as username,source_ip,LOWER(status) as status,local_ip,date_time_unixTime,totalbytes,readbytes,writebytes,duration;
user				= GROUP loginOrdered by normalized_username PARALLEL 1;
scoringResult		= FOREACH user GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,6,0,loginOrdered ) );
finalResult			= FOREACH scoringResult GENERATE $0 as date_time,$1 as normalizedtime,$2 as hostname,$3 as country,$9 as region,$10 as city,$6 as databucket,$7 as isp,$8 as ipusage,$11 as normalized_username,$12 as username,$13 as source_ip,$14 as status,$15 as local_ip,$16 as date_time_unix,$17 as totalbytes,$18 as readbytes,$19 as writebytes,$20 as duration,$21 as date_timescore,$22 as hostnamescore,$23 as countryscore,$24 as regionscore,$25 as cityscore,$26 as databucket,$27 as eventscore,$28 as globalscore,$29 as entitygroupedbyid;
store finalResult into '$outputData' using PigStorage(',','-noschema');