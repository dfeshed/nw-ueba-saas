SET pig.tmpfilecompression true
SET pig.tmpfilecompression.codec gz
SET pig.tmpfilecompression.storage seqfile
SET pig.maxCombinedSplitSize 2147483648

raw					= LOAD '$inputData' USING PigStorage('|') AS (date_time:chararray,date_time_unixTime:long,username:chararray,source_ip:chararray,local_ip:chararray,status:chararray,country_name:chararray,region_name:chararray,city_name:chararray,isp_name:chararray,ipusage:chararray,hostname:chararray,totalbytes:long,readbytes:long,writebytes:long,duration:int,databucket:int,normalized_username:chararray);
loginByTime			= FILTER raw by date_time_unixTime > (long)'$deltaTime';
loginByTimeAndStatus = FILTER loginByTime by status=='CLOSED';
loginOrdered		= FOREACH loginByTimeAndStatus GENERATE date_time, LOWER(hostname) as hostname,LOWER(country_name) as country_name,((ipusage is null) or (ipusage != 'mob') ? LOWER(region_name) : ''),((ipusage is null) or (ipusage != 'mob') ? LOWER(city_name) : ''),databucket,LOWER(isp_name) as isp_name,ipusage,LOWER(region_name) as region_name,LOWER(city_name) as city_name,normalized_username,LOWER(username) as username,source_ip,LOWER(status) as status,local_ip,date_time_unixTime,totalbytes,readbytes,writebytes,duration;
user				= GROUP loginOrdered by normalized_username PARALLEL 1;
userWithAvgFlatten= FOREACH user GENERATE FLATTEN( loginOrdered), (long)AVG(loginOrdered.totalbytes) as dataavg PARALLEL 1;
userWithAvgFlattenGroup= GROUP userWithAvgFlatten by normalized_username PARALLEL 1;
scoringResult		= FOREACH userWithAvgFlattenGroup GENERATE FLATTEN( fortscale.ebs.EBSPigUDF( group,6,0,userWithAvgFlatten ) );
finalResult			= FOREACH scoringResult GENERATE $0 as date_time,$1 as normalizedtime,$2 as hostname,$3 as country,$9 as region,$10 as city,$6 as databucket,$7 as isp,$8 as ipusage,$11 as normalized_username,$12 as username,$13 as source_ip,$14 as status,$15 as local_ip,$16 as date_time_unix,$17 as totalbytes,$18 as readbytes,$19 as writebytes,$20 as duration,$21 as bytesavg,$22 as date_timescore,$23 as hostnamescore,$24 as countryscore,$25 as regionscore,$26 as cityscore,$27 as databucketscore,$28 as eventscore,$29 as globalscore,$30 as entitygroupedbyid;
store finalResult into '$outputData' using PigStorage(',','-noschema');