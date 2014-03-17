db.dhcp_log.ensureIndex({ "ip_address": 1 })
db.dhcp_log.ensureIndex({ "ip_address": 1 , "timestampepoch": -1})
db.dhcp_log.ensureIndex({"datetimeparsed" : 1}, {"expireAfterSeconds" : 60*60*12})
