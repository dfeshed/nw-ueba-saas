fs -rm -f -R $outputData
secEvent        = LOAD '$inputData' USING PigStorage('|') AS (generatedTimeRaw:chararray,generatedTime:chararray,categoryString:chararray,eventCode:chararray,logfile:chararray,recordNumber:chararray,sourceName:chararray,account_name:chararray,account_domain:chararray,service_name:chararray,service_id:chararray,client_address:chararray,ticket_options:chararray,failure_code:chararray,source_network_address:chararray,generatedTimeUnixTime:long);
--secEventByTime  = FILTER secEvent BY generatedTimeUnixTime > (long)'$deltaTime';
goodIP          = FILTER secEvent BY TRIM(source_network_address) != '-' AND TRIM(source_network_address) != '';
--noNAT           = STREAM goodIP THROUGH `grep -v $whitelist` AS (generatedTimeRaw:chararray,generatedTime:chararray,categoryString:chararray,eventCode:chararray,logfile:chararray,recordNumber:chararray,sourceName:chararray,account_name:chararray,account_domain:chararray,service_name:chararray,service_id:chararray,client_address:chararray,ticket_options:chararray,failure_code:chararray,source_network_address:chararray,generatedTimeUnixTime:long);
noNAT           = FILTER goodIP BY NOT (source_network_address MATCHES '192.168.0.22');
users           = FILTER noNAT BY NOT (account_name MATCHES '.*[$].*');
machines        = FILTER noNAT BY (account_name MATCHES '.*[$].*');
userToMachine   = JOIN users BY (source_network_address, ROUND(generatedTimeUnixTime/300)*300), machines BY (source_network_address, ROUND(generatedTimeUnixTime/300)*300);
userMatch       = FOREACH userToMachine GENERATE $1 as time,STRSPLIT($8,',',2).$1 as user,$15 as ip,$16 as unixtime:long,(STRSPLIT($25,',',2).$1 MATCHES  '.*[$].*' ? STRSPLIT($25,',',2).$1:STRSPLIT($25,',',2).$0) as computer;
userMatchGroups = GROUP userMatch BY (user,computer);
result          = FOREACH userMatchGroups GENERATE FLATTEN($0),COUNT(userMatch.user),MAX(userMatch.unixtime);
resultFullFields= JOIN result BY ($0,$1,$3), userMatch BY (user,computer,unixtime);
allResults      = FOREACH resultFullFields GENERATE $timeStamp,$0,$1,$6,$2,$4,$3;
finalResult     = DISTINCT allResults;
store finalResult into '$outputData' using PigStorage(',','-noschema');