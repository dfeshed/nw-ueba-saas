db.dhcp_log.ensureIndex({ "ip_address": 1 })
db.dhcp_log.ensureIndex({ "ip_address": 1 , "timestampepoch": -1})
db.dhcp_log.find({"datetimeparsed":{"$exists": false}}).forEach(function(doc){doc.datetimeparsed = new ISODate(doc.datetime);db.dhcp_log.save(doc);});
db.dhcp_log.ensureIndex({"datetimeparsed" : 1}, {"expireAfterSeconds" : 60*60*24*2})