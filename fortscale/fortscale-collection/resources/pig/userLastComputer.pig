fs -rm -f -R $tmpOutputData
user2comp       = LOAD '$inputData' USING PigStorage(',') AS (timestamp:long,user:chararray,computer:chararray,ip:chararray,loginCount:int,eventTime:chararray,eventTimeEpoch:long);
usersComps      = GROUP user2comp BY (user,computer);
result          = FOREACH usersComps GENERATE FLATTEN($0),MAX(user2comp.eventTimeEpoch),SUM(user2comp.loginCount);
resultFullFields= JOIN result BY ($0,$1,$2), user2comp BY (user,computer,eventTimeEpoch);
allResults      = FOREACH resultFullFields GENERATE $0,$1,$3,$7,$9,$10;
finalResult     = DISTINCT allResults;
store finalResult into '$tmpOutputData' using PigStorage(',','-noschema');
fs -rm -f -R $outputData
fs -mkdir $outputData
fs -cp $tmpOutputData* $outputData