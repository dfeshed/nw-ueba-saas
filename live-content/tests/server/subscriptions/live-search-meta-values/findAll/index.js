const data = ['tor-exit-node-ip', 'bluetack.co.uk', 'idefense-threatindicators-domain', 'palevo tracker domain',
  'palevo tracker ip', 'spyeyetracker-domain', 'spyeyetracker-ip', 'sri', 'tor-node-ip', 'wikileaks-domain',
  'zeustracker-domain', 'zeustracker-ip', 'rsa-firstwatch', 'maxmind-asn', 'malwaredomainlist-ip',
  'malwaredomainlist-domain', 'malwaredomains.com', 'spamhaus_edrop_list_ip', 'rsa-fraudaction-ip', 'rsa-fraudaction-domain',
  'netwitness', 'spamhaus_drop_list_ip', 'nw05075', 'nw05080', 'nw05085', 'nw05090', 'nw05095', 'nw05100', 'nw05105', 'nw05110',
  'nw05115', 'fp_chm', 'nw25145', 'nw25150', 'known jackal ssl cert', 'nw05015', 'nw25170', 'access db', 'torrent', 'cab', 'chm',
  'css', 'gif', 'html', 'java jar', 'java class', 'javascript', 'jpg', 'lnk', 'mssql', 'office 2007', 'pdf', 'php script',
  'cert pkcs12', 'private encryption key pkcs12', 'png', 'private encryption key', 'private pgp encryption key',
  'putty pub and private key', 'rar', 'rtf', 'flash compressed v10', 'flash compressed v6', 'flash compressed v7',
  'flash compressed v8', 'flash compressed v9', 'flash v10', 'flash v6', 'flash v7', 'flash v8', 'flash v9', 'perl script',
  'python script', 'ruby script', 'unix shell script', 'windows msi installer', 'xml', 'nw32535', 'nw32540', 'nw32545',
  'nw32550', 'nw32555', 'nw32560', 'nw32565', 'nw32570', 'nw32575', 'nw32580', 'nw32585', 'nw32590', 'nw32595', 'nw32600',
  'nw32605', 'nw32610', 'nw32615', 'nw32620', 'nw32625', 'nw32630', 'nw32635', 'nw32640', 'nw32645', 'nw32650', 'nw32655',
  'nw32660', 'nw32665', 'nw32670', 'nw32675', 'nw32680', 'nw32685', 'nw32690', 'nw32695', 'nw32700', 'nw32705', 'nw32710',
  'nw32715', 'nw32720', 'nw32725', 'nw32730', 'nw32735', 'nw32740', 'nw32745', 'nw32750', 'nw32755', 'nw32760', 'nw32765',
  'nw32770', 'nw32775', 'nw05005', 'nw05030', 'nw05035', 'nw05040', 'nw05045', 'nw05050', 'nw05055', 'nw05060', 'nw05065',
  'nw05070', 'Linux', 'Mac OS X', 'Mobile BlackBerry or Nokia', 'Mobile Windows CE', 'Mobile iPad', 'Mobile iPhone',
  'Windows 2000', 'Windows 64 Bit OS', 'Windows 7 or Windows 2008', 'Windows NT 4', 'Windows Vista', 'Windows XP',
  'Windows XP 64Bit or Windows 2003', 'nw25005', 'nw25010', 'nw25015', 'nw25020', 'nw25025', 'nw25030', 'nw25035', 'nw25040',
  'nw25045', 'nw25050', 'nw25055', 'nw25060', 'nw25065', 'nw25070', 'nw25075', 'nw25080', 'nw25085', 'nw25090', 'nw25095',
  'nw25100', 'nw25105', 'nw25110', 'nw25115', 'nw25120', 'nw25125', 'nw25130', 'nw25135', 'nw25140', 'nw25165', 'highres',
  'nw05025', 'nw05026', 'NW45050', 'nw45005', 'nw45010', 'nw45025', 'nw45030', 'nw45035', 'nw45040', 'nw45045', 'nw45055',
  'nw45060', 'nw45065', 'nw45070', 'nw45090', 'nw45095', 'nw45100', 'nw45105', 'access_db', 'java_class', 'java_jar',
  'office_2007', 'office_95-2003', 'windows_installer', 'zip', 'exe_parser_failure', 'nw05120', 'nw05125', 'nw05130',
  'windows_dll', 'windows_executable', 'nw05203', 'nw05204', 'nw05205', 'nw05206', 'nw05207', 'nw05208', 'nw05209', 'nw05210',
  'nw05211', 'nw05212', 'nw05213', 'nw05214', 'nw05215', 'exe parser finished logic failure', 'exe parser madetoend failure',
  'nw05131', 'nw05132', 'nw05133', 'nw05134', 'nw05135', 'nw05136', 'nw05137', 'nw05138', 'nw05139', 'nw05140', 'nw05141',
  'nw05142', 'nw05143', 'nw05144', 'nw05145', 'nw05146', 'nw05147', 'nw05148', 'nw05149', 'nw05150', 'nw05151', 'nw05152',
  'nw05153', 'nw05154', 'nw05155', 'nw05156', 'nw05157', 'nw05158', 'nw05159', 'nw05160', 'nw05161', 'nw05162', 'nw05163',
  'nw05164', 'nw05165', 'nw05166', 'nw05167', 'nw05168', 'nw05169', 'nw05170', 'nw05171', 'nw05172', 'nw05173', 'nw05174',
  'nw05175', 'nw05176', 'nw05177', 'nw05178', 'nw05179', 'windows dll', 'windows executable', 'Chrome 0', 'Chrome 1',
  'Chrome 2', 'Chrome 3', 'Chrome 4', 'Firefox 0', 'Firefox 1', 'Firefox 2', 'Firefox 3', 'Firefox 4', 'IE 2', 'IE 3', 'IE 4',
  'IE 5', 'IE 6', 'IE 7', 'IE 8', 'Konqueror 3', 'Konqueror 4', 'Netscape Navigator', 'Opera 10', 'Opera 8', 'Opera 9', 'Safari',
  'base64 encoded exe', 'base64 encoded office', 'base64 encoded pdf', 'base64 encoded rar', 'base64 encoded rtf',
  'base64 encoded zip', 'nw05216', 'nw05217', 'nw05218', 'nw05219', 'nw05220', 'nw05221', 'nw05222', 'nw05223', 'nw05224',
  'nw05225', 'nw05226', 'nw05227', 'nw05228', 'nw05229', 'nw05230', 'nw05182', 'nw05183', 'nw05184', 'nw05185', 'nw05186',
  'nw05187', 'nw05188', 'nw05189', 'nw05190', 'nw05191', 'nw05192', 'nw05193', 'nw05194', 'nw05195', 'nw05196', 'nw05197',
  'nw05198', 'nw05199', 'nw05180', 'nw05181'];

export default {
  subscriptionDestination: '/cms/search/get-resource-meta-values',
  requestDestination: '/ws/cms/search/get-resource-meta-values',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};


