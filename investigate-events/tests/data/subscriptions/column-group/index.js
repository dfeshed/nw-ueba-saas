const BASE_COLUMNS = [
  { metaName: 'time', displayName: 'Collection Time', width: 175, position: 0, visible: true },
  { metaName: 'medium', displayName: 'Type', width: 100, position: 1, visible: true }
];
export const columnGroups = [
  {
    id: 'EMAIL',
    name: 'RSA Email Analysis',
    columns: [...BASE_COLUMNS, {
      metaName: 'service',
      displayName: 'Service Type',
      position: 1,
      width: 100,
      visible: true
    }, {
      metaName: 'orig_ip',
      displayName: 'Originating IP Address',
      position: 2,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 3,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.dst',
      displayName: 'Destination IP Address',
      position: 4,
      width: 100,
      visible: true
    }, {
      metaName: 'tcp.dstport',
      displayName: 'TCP Destination Port',
      position: 5,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.dstport',
      displayName: 'Destination Port',
      position: 6,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.host',
      displayName: 'Hostname Aliases',
      position: 7,
      width: 100,
      visible: true
    }, {
      metaName: 'country.src',
      displayName: 'Source Country',
      position: 8,
      width: 100,
      visible: true
    }, {
      metaName: 'country.dst',
      displayName: 'Destination Country',
      position: 9,
      width: 100,
      visible: true
    }, {
      metaName: 'org.src',
      displayName: 'Source Organization',
      position: 10,
      width: 100,
      visible: true
    }, {
      metaName: 'org.dst',
      displayName: 'Destination Organization',
      position: 11,
      width: 100,
      visible: true
    }, {
      metaName: 'subject',
      displayName: 'Subject',
      position: 12,
      width: 100,
      visible: true
    }, {
      metaName: 'email.src',
      displayName: 'Source E-mail Address',
      position: 13,
      width: 100,
      visible: true
    }, {
      metaName: 'email.dst',
      displayName: 'Destination E-mail Address',
      position: 14,
      width: 100,
      visible: false
    }, {
      metaName: 'domain.dst',
      displayName: 'Destination Domain',
      position: 16,
      width: 120,
      visible: false
    }, {
      metaName: 'client',
      displayName: 'Client Application',
      position: 17,
      width: 100,
      visible: false
    }, {
      metaName: 'server',
      displayName: 'Server Application',
      position: 18,
      width: 100,
      visible: false
    }, {
      metaName: 'content',
      displayName: 'Content Type',
      position: 19,
      width: 100,
      visible: false
    }, {
      metaName: 'action',
      displayName: 'Action Event',
      position: 20,
      width: 100,
      visible: false
    }, {
      metaName: 'attachment',
      displayName: 'Attachment',
      position: 21,
      width: 100,
      visible: false
    }, {
      metaName: 'extension',
      displayName: 'Extension',
      position: 22,
      width: 100,
      visible: false
    }, {
      metaName: 'filetype',
      displayName: 'Forensic Fingerprint',
      position: 23,
      width: 100,
      visible: false
    }, {
      metaName: 'filename',
      displayName: 'Filename',
      position: 24,
      width: 100,
      visible: false
    }, {
      metaName: 'username',
      displayName: 'User Account',
      position: 25,
      width: 100,
      visible: false
    }, {
      metaName: 'user.src',
      displayName: 'Source User Account',
      position: 26,
      width: 100,
      visible: false
    }, {
      metaName: 'user.dst',
      displayName: 'Destination User Account',
      position: 27,
      width: 100,
      visible: false
    }, {
      metaName: 'error',
      displayName: 'Errors',
      position: 28,
      width: 100,
      visible: false
    }, {
      metaName: 'crypto',
      displayName: 'Cipher Name',
      position: 29,
      width: 100,
      visible: false
    }, {
      metaName: 'ssl.subject',
      displayName: 'SSL Subject',
      position: 30,
      width: 100,
      visible: false
    }, {
      metaName: 'ssl.ca',
      displayName: 'SSL CA',
      position: 31,
      width: 100,
      visible: false
    }, {
      metaName: 'ioc',
      displayName: 'Indicators of Compromise',
      position: 32,
      width: 100,
      visible: false
    }, {
      metaName: 'boc',
      displayName: 'Behaviours of Compromise',
      position: 33,
      width: 100,
      visible: false
    }, {
      metaName: 'eoc',
      displayName: 'Enablers of Compromise',
      position: 34,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.session',
      displayName: 'Session Analysis',
      position: 35,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.service',
      displayName: 'Service Analysis',
      position: 36,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.file',
      displayName: 'File Analysis',
      position: 37,
      width: 100,
      visible: false
    }, {
      metaName: 'threat.category',
      displayName: 'Threat Category',
      position: 38,
      width: 100,
      visible: false
    }, {
      metaName: 'threat.desc',
      displayName: 'Threat Description',
      position: 39,
      width: 100,
      visible: false
    }, {
      metaName: 'threat.source',
      displayName: 'Threat Source',
      position: 40,
      width: 100,
      visible: false
    }, {
      metaName: 'alert',
      displayName: 'Alerts',
      position: 41,
      width: 100,
      visible: false
    }, {
      metaName: 'sourcefile',
      displayName: 'Source Filename',
      position: 42,
      width: 100,
      visible: false
    }, {
      metaName: 'did',
      displayName: 'Decoder Source',
      position: 43,
      width: 100,
      visible: false
    }],
    contentType: 'OOTB',
    createdBy: 'system',
    createdOn: 1573451624514,
    lastModifiedBy: 'system',
    lastModifiedOn: 1573451624514
  }, {
    id: 'ENDPOINT',
    name: 'RSA Endpoint Analysis',
    columns: [...BASE_COLUMNS, {
      metaName: 'event.time',
      displayName: 'Event Time',
      position: 1,
      width: 100,
      visible: true
    }, {
      metaName: 'device.type',
      displayName: 'Device Type',
      position: 2,
      width: 100,
      visible: true
    }, {
      metaName: 'forward.ip',
      displayName: 'Event Relay IPv4 Address',
      position: 3,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.host',
      displayName: 'Hostname Aliases',
      position: 4,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.ip',
      displayName: 'IP Aliases',
      position: 5,
      width: 100,
      visible: true
    }, {
      metaName: 'category',
      displayName: 'Category',
      position: 6,
      width: 100,
      visible: true
    }, {
      metaName: 'filename',
      displayName: 'Filename',
      position: 7,
      width: 100,
      visible: true
    }, {
      metaName: 'filename.src',
      displayName: 'Filename Source',
      position: 8,
      width: 100,
      visible: true
    }, {
      metaName: 'directory.src',
      displayName: 'Source File Directory',
      position: 9,
      width: 100,
      visible: true
    }, {
      metaName: 'param.src',
      displayName: 'Source Parameter',
      position: 10,
      width: 100,
      visible: true
    }, {
      metaName: 'dir.path.src',
      displayName: 'Directory Path Source',
      position: 11,
      width: 100,
      visible: true
    }, {
      metaName: 'action',
      displayName: 'Action Event',
      position: 12,
      width: 100,
      visible: true
    }, {
      metaName: 'filename.dst',
      displayName: 'Filename Destination',
      position: 13,
      width: 100,
      visible: true
    }, {
      metaName: 'directory.dst',
      displayName: 'Target File Directory',
      position: 14,
      width: 100,
      visible: false
    }, {
      metaName: 'task.name',
      displayName: 'Task Name',
      position: 15,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.file',
      displayName: 'File Analysis',
      position: 16,
      width: 100,
      visible: false
    }, {
      metaName: 'OS',
      displayName: 'Operating System',
      position: 17,
      width: 100,
      visible: false
    }, {
      metaName: 'user.src',
      displayName: 'Source User Account',
      position: 18,
      width: 100,
      visible: false
    }, {
      metaName: 'host.role',
      displayName: 'Host Role',
      position: 19,
      width: 100,
      visible: false
    }, {
      metaName: 'domain',
      displayName: 'Domain',
      position: 20,
      width: 100,
      visible: false
    }, {
      metaName: 'dn',
      displayName: 'Domain OU',
      position: 21,
      width: 100,
      visible: false
    }, {
      metaName: 'file.vendor',
      displayName: 'File Vendor',
      position: 22,
      width: 100,
      visible: false
    }, {
      metaName: 'file.entropy',
      displayName: 'File Entropy',
      position: 23,
      width: 100,
      visible: false
    }, {
      metaName: 'filename.size',
      displayName: 'File Size',
      position: 24,
      width: 100,
      visible: false
    }, {
      metaName: 'cert.common',
      displayName: 'Certificate Common Name',
      position: 25,
      width: 100,
      visible: false
    }, {
      metaName: 'cert.subject',
      displayName: 'Certificate Subject',
      position: 26,
      width: 100,
      visible: false
    }, {
      metaName: 'cert.ca',
      displayName: 'Certificate Authority',
      position: 27,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.proto',
      displayName: 'IP Protocol',
      position: 28,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 29,
      width: 100,
      visible: false
    }, {
      metaName: 'domain.dst',
      displayName: 'Destination Domain',
      position: 30,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.dst',
      displayName: 'Destination IP address',
      position: 31,
      width: 100,
      visible: false
    }],
    contentType: 'OOTB',
    createdBy: 'system',
    createdOn: 1573451624593,
    lastModifiedBy: 'system',
    lastModifiedOn: 1573451624593
  }, {
    id: 'MALWARE',
    name: 'RSA Malware Analysis',
    columns: [...BASE_COLUMNS, {
      metaName: 'service',
      displayName: 'Service Type',
      position: 1,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 2,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.dst',
      displayName: 'Destination IP Address',
      position: 3,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.host',
      displayName: 'Hostname Aliases',
      position: 4,
      width: 100,
      visible: true
    }, {
      metaName: 'referer',
      displayName: 'Referer',
      position: 5,
      width: 100,
      visible: true
    }, {
      metaName: 'country.src',
      displayName: 'Source Country',
      position: 6,
      width: 100,
      visible: true
    }, {
      metaName: 'country.dst',
      displayName: 'Destination Country',
      position: 7,
      width: 100,
      visible: true
    }, {
      metaName: 'domain.dst',
      displayName: 'Destination Domain',
      position: 8,
      width: 100,
      visible: true
    }, {
      metaName: 'client',
      displayName: 'Client Application',
      position: 9,
      width: 100,
      visible: true
    }, {
      metaName: 'server',
      displayName: 'Server Application',
      position: 10,
      width: 100,
      visible: true
    }, {
      metaName: 'content',
      displayName: 'Content Type',
      position: 11,
      width: 100,
      visible: true
    }, {
      metaName: 'action',
      displayName: 'Action Event',
      position: 12,
      width: 100,
      visible: true
    }, {
      metaName: 'attachment',
      displayName: 'Attachment',
      position: 13,
      width: 100,
      visible: true
    }, {
      metaName: 'extension',
      displayName: 'Extension',
      position: 14,
      width: 100,
      visible: false
    }, {
      metaName: 'filetype',
      displayName: 'Forensic Fingerprint',
      position: 15,
      width: 100,
      visible: false
    }, {
      metaName: 'filename',
      displayName: 'Filename',
      position: 16,
      width: 100,
      visible: false
    }, {
      metaName: 'directory',
      displayName: 'Directory',
      position: 17,
      width: 100,
      visible: false
    }, {
      metaName: 'sql',
      displayName: 'Sql Query',
      position: 18,
      width: 100,
      visible: false
    }, {
      metaName: 'username',
      displayName: 'User Account',
      position: 19,
      width: 100,
      visible: false
    }, {
      metaName: 'ioc',
      displayName: 'Indicators of Compromise',
      position: 20,
      width: 100,
      visible: false
    }, {
      metaName: 'boc',
      displayName: 'Behaviours of Compromise',
      position: 21,
      width: 100,
      visible: false
    }, {
      metaName: 'eoc',
      displayName: 'Enablers of Compromise',
      position: 22,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.session',
      displayName: 'Session Analysis',
      position: 23,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.service',
      displayName: 'Service Analysis',
      position: 24,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.file',
      displayName: 'File Analysis',
      position: 25,
      width: 100,
      visible: false
    }, {
      metaName: 'alert',
      displayName: 'Alerts',
      position: 26,
      width: 100,
      visible: false
    }, {
      metaName: 'sourcefile',
      displayName: 'Source Filename',
      position: 27,
      width: 100,
      visible: false
    }, {
      metaName: 'did',
      displayName: 'Decoder Source',
      position: 28,
      width: 100,
      visible: false
    }],
    contentType: 'OOTB',
    createdBy: 'system',
    createdOn: 1573451624883,
    lastModifiedBy: 'system',
    lastModifiedOn: 1573451624883
  }, {
    id: 'THREAT',
    name: 'RSA Threat Analysis',
    columns: [...BASE_COLUMNS, {
      metaName: 'threat.category',
      displayName: 'Threat Category',
      position: 1,
      width: 100,
      visible: true
    }, {
      metaName: 'threat.source',
      displayName: 'Threat Source',
      position: 2,
      width: 100,
      visible: true
    }, {
      metaName: 'threat.desc',
      displayName: 'Threat Description',
      position: 3,
      width: 100,
      visible: true
    }, {
      metaName: 'ioc',
      displayName: 'Indicators of Compromise',
      position: 4,
      width: 100,
      visible: true
    }, {
      metaName: 'boc',
      displayName: 'Behaviours of Compromise',
      position: 5,
      width: 100,
      visible: true
    }, {
      metaName: 'eoc',
      displayName: 'Enablers of Compromise',
      position: 6,
      width: 100,
      visible: true
    }, {
      metaName: 'analysis.session',
      displayName: 'Session Analysis',
      position: 7,
      width: 100,
      visible: true
    }, {
      metaName: 'analysis.service',
      displayName: 'Service Analysis',
      position: 8,
      width: 100,
      visible: true
    }, {
      metaName: 'analysis.file',
      displayName: 'File Analysis',
      position: 9,
      width: 100,
      visible: true
    }, {
      metaName: 'alert',
      displayName: 'Alerts',
      position: 10,
      width: 100,
      visible: true
    }, {
      metaName: 'service',
      displayName: 'Service Type',
      position: 11,
      width: 100,
      visible: true
    }, {
      metaName: 'orig_ip',
      displayName: 'Originating IP Address',
      position: 12,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.ip',
      displayName: 'IP Aliases',
      position: 13,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 14,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.dst',
      displayName: 'Destination IP Address',
      position: 15,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.dstport',
      displayName: 'Destination Port',
      position: 16,
      width: 100,
      visible: false
    }, {
      metaName: 'tcp.dstport',
      displayName: 'TCP Destination Port',
      position: 17,
      width: 100,
      visible: false
    }, {
      metaName: 'udp.srcport',
      displayName: 'UDP Source Port',
      position: 18,
      width: 100,
      visible: false
    }, {
      metaName: 'udp.dstport',
      displayName: 'UDP Target Port',
      position: 19,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.proto',
      displayName: 'IP Protocol',
      position: 20,
      width: 100,
      visible: false
    }, {
      metaName: 'eth.type',
      displayName: 'Ethernet Protocol',
      position: 21,
      width: 100,
      visible: false
    }, {
      metaName: 'eth.src',
      displayName: 'Ethernet Source',
      position: 22,
      width: 100,
      visible: false
    }, {
      metaName: 'eth.dst',
      displayName: 'Ethernet Destination',
      position: 23,
      width: 100,
      visible: false
    }, {
      metaName: 'alias.host',
      displayName: 'Hostname Aliases',
      position: 24,
      width: 100,
      visible: false
    }, {
      metaName: 'country.src',
      displayName: 'Source Country',
      position: 25,
      width: 100,
      visible: false
    }, {
      metaName: 'country.dst',
      displayName: 'Destination Country',
      position: 26,
      width: 100,
      visible: false
    }, {
      metaName: 'domain.dst',
      displayName: 'Destination Domain',
      position: 27,
      width: 100,
      visible: false
    }, {
      metaName: 'org.src',
      displayName: 'Source Organization',
      position: 28,
      width: 100,
      visible: false
    }, {
      metaName: 'org.dst',
      displayName: 'Destination Organization',
      position: 29,
      width: 100,
      visible: false
    }, {
      metaName: 'action',
      displayName: 'Action Event',
      position: 30,
      width: 100,
      visible: false
    }, {
      metaName: 'username',
      displayName: 'User Account',
      position: 31,
      width: 100,
      visible: false
    }, {
      metaName: 'password',
      displayName: 'Password',
      position: 32,
      width: 100,
      visible: false
    }, {
      metaName: 'device.type',
      displayName: 'Device Type',
      position: 33,
      width: 100,
      visible: false
    }, {
      metaName: 'device.ip',
      displayName: 'Device IP',
      position: 34,
      width: 100,
      visible: false
    }, {
      metaName: 'device.host',
      displayName: 'Device Host',
      position: 36,
      width: 100,
      visible: false
    }, {
      metaName: 'device.class',
      displayName: 'Device Class',
      position: 37,
      width: 100,
      visible: false
    }, {
      metaName: 'paddr',
      displayName: 'Device Address',
      position: 38,
      width: 100,
      visible: false
    }, {
      metaName: 'event.source',
      displayName: 'Event Source',
      position: 41,
      width: 100,
      visible: false
    }, {
      metaName: 'event.desc',
      displayName: 'Event Description',
      position: 42,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.subject',
      displayName: 'Event Subject',
      position: 43,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.activity',
      displayName: 'Event Activity',
      position: 44,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.theme',
      displayName: 'Event Theme',
      position: 45,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.outcome',
      displayName: 'Event Outcome',
      position: 46,
      width: 100,
      visible: false
    }, {
      metaName: 'device.group',
      displayName: 'Event Source Group',
      position: 48,
      width: 100,
      visible: false
    }, {
      metaName: 'event.class',
      displayName: 'Event Classification',
      position: 49,
      width: 100,
      visible: false
    }, {
      metaName: 'sql',
      displayName: 'Sql Query',
      position: 50,
      width: 100,
      visible: false
    }, {
      metaName: 'category',
      displayName: 'Category',
      position: 51,
      width: 100,
      visible: false
    }, {
      metaName: 'query',
      displayName: 'Querystring',
      position: 52,
      width: 100,
      visible: false
    }, {
      metaName: 'OS',
      displayName: 'Operating System',
      position: 53,
      width: 100,
      visible: false
    }, {
      metaName: 'browser',
      displayName: 'Browsers',
      position: 54,
      width: 100,
      visible: false
    }, {
      metaName: 'version',
      displayName: 'Versions',
      position: 55,
      width: 100,
      visible: false
    }, {
      metaName: 'sourcefile',
      displayName: 'Source Filename',
      position: 57,
      width: 100,
      visible: false
    }, {
      metaName: 'lc.cid',
      displayName: 'Collector ID',
      position: 58,
      width: 100,
      visible: false
    }, {
      metaName: 'did',
      displayName: 'Decoder Source',
      position: 59,
      width: 100,
      visible: false
    }],
    contentType: 'OOTB',
    createdBy: 'system',
    createdOn: 1573451624827,
    lastModifiedBy: 'system',
    lastModifiedOn: 1573451624827
  }, {
    id: 'WEB',
    name: 'RSA Web Analysis',
    columns: [...BASE_COLUMNS, {
      metaName: 'service',
      displayName: 'Service Type',
      position: 1,
      width: 100,
      visible: true
    }, {
      metaName: 'orig_ip',
      displayName: 'Originating IP Address',
      position: 2,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.ip',
      displayName: 'IP Aliases',
      position: 3,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 4,
      width: 100,
      visible: true
    }, {
      metaName: 'ip.dst',
      displayName: 'Destination IP Address',
      position: 5,
      width: 100,
      visible: true
    }, {
      metaName: 'tcp.dstport',
      displayName: 'TCP Destination Port',
      position: 6,
      width: 100,
      visible: true
    }, {
      metaName: 'alias.host',
      displayName: 'Hostname Aliases',
      position: 7,
      width: 100,
      visible: true
    }, {
      metaName: 'referer',
      displayName: 'Referer',
      position: 8,
      width: 100,
      visible: true
    }, {
      metaName: 'country.src',
      displayName: 'Source Country',
      position: 9,
      width: 100,
      visible: true
    }, {
      metaName: 'country.dst',
      displayName: 'Destination Country',
      position: 10,
      width: 100,
      visible: true
    }, {
      metaName: 'org.src',
      displayName: 'Source Organization',
      position: 11,
      width: 100,
      visible: true
    }, {
      metaName: 'org.dst',
      displayName: 'Destination Organization',
      position: 12,
      width: 100,
      visible: true
    }, {
      metaName: 'domain.dst',
      displayName: 'Destination Domain',
      position: 13,
      width: 100,
      visible: true
    }, {
      metaName: 'client',
      displayName: 'Client Application',
      position: 14,
      width: 100,
      visible: false
    }, {
      metaName: 'server',
      displayName: 'Server Application',
      position: 15,
      width: 100,
      visible: false
    }, {
      metaName: 'content',
      displayName: 'Content Type',
      position: 16,
      width: 100,
      visible: false
    }, {
      metaName: 'action',
      displayName: 'Action Event',
      position: 17,
      width: 100,
      visible: false
    }, {
      metaName: 'filename',
      displayName: 'Filename',
      position: 18,
      width: 100,
      visible: false
    }, {
      metaName: 'username',
      displayName: 'User Account',
      position: 19,
      width: 100,
      visible: false
    }, {
      metaName: 'password',
      displayName: 'Password',
      position: 20,
      width: 100,
      visible: false
    }, {
      metaName: 'ssl.ca',
      displayName: 'SSL CA',
      position: 21,
      width: 100,
      visible: false
    }, {
      metaName: 'ssl.subject',
      displayName: 'SSL Subject',
      position: 22,
      width: 100,
      visible: false
    }, {
      metaName: 'error',
      displayName: 'Errors',
      position: 23,
      width: 100,
      visible: false
    }, {
      metaName: 'query',
      displayName: 'Querystring',
      position: 24,
      width: 100,
      visible: false
    }, {
      metaName: 'directory',
      displayName: 'Directory',
      position: 25,
      width: 100,
      visible: false
    }, {
      metaName: 'browser',
      displayName: 'Browsers',
      position: 26,
      width: 100,
      visible: false
    }, {
      metaName: 'category',
      displayName: 'Category',
      position: 27,
      width: 100,
      visible: false
    }, {
      metaName: 'device.type',
      displayName: 'Device Type',
      position: 28,
      width: 100,
      visible: false
    }, {
      metaName: 'device.ip',
      displayName: 'Device IP',
      position: 29,
      width: 100,
      visible: false
    }, {
      metaName: 'device.ipv6',
      displayName: 'Device IPv6',
      position: 30,
      width: 100,
      visible: false
    }, {
      metaName: 'device.host',
      displayName: 'Device Host',
      position: 31,
      width: 100,
      visible: false
    }, {
      metaName: 'device.class',
      displayName: 'Device Class',
      position: 32,
      width: 100,
      visible: false
    }, {
      metaName: 'paddr',
      displayName: 'Device Address',
      position: 33,
      width: 100,
      visible: false
    }, {
      metaName: 'event.source',
      displayName: 'Event Source',
      position: 34,
      width: 100,
      visible: false
    }, {
      metaName: 'event.desc',
      displayName: 'Event Description',
      position: 35,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.subject',
      displayName: 'Event Subject',
      position: 36,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.activity',
      displayName: 'Event Activity',
      position: 37,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.theme',
      displayName: 'Event Theme',
      position: 38,
      width: 100,
      visible: false
    }, {
      metaName: 'ec.outcome',
      displayName: 'Event Outcome',
      position: 39,
      width: 100,
      visible: false
    }, {
      metaName: 'device.group',
      displayName: 'Event Source Group',
      position: 40,
      width: 100,
      visible: false
    }, {
      metaName: 'event.class',
      displayName: 'Event Classification',
      position: 41,
      width: 100,
      visible: false
    }, {
      metaName: 'ioc',
      displayName: 'Indicators of Compromise',
      position: 42,
      width: 100,
      visible: false
    }, {
      metaName: 'boc',
      displayName: 'Behaviours of Compromise',
      position: 43,
      width: 100,
      visible: false
    }, {
      metaName: 'eoc',
      displayName: 'Enablers of Compromise',
      position: 44,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.session',
      displayName: 'Session Analysis',
      position: 45,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.service',
      displayName: 'Service Analysis',
      position: 46,
      width: 100,
      visible: false
    }, {
      metaName: 'analysis.file',
      displayName: 'File Analysis',
      position: 47,
      width: 100,
      visible: false
    }, {
      metaName: 'alert',
      displayName: 'Alert',
      position: 48,
      width: 100,
      visible: false
    }, {
      metaName: 'sourcefile',
      displayName: 'Source Filename',
      position: 49,
      width: 100,
      visible: false
    }, {
      metaName: 'lc.cid',
      displayName: 'Collector ID',
      position: 50,
      width: 100,
      visible: false
    }, {
      metaName: 'did',
      displayName: 'Decoder Source',
      position: 51,
      width: 100,
      visible: false
    }],
    contentType: 'OOTB',
    createdBy: 'system',
    createdOn: 1573451624724,
    lastModifiedBy: 'system',
    lastModifiedOn: 1573451624724
  }, {
    id: 'SUMMARY',
    name: 'Summary List',
    columns: [...BASE_COLUMNS, {
      metaName: 'custom.theme',
      displayName: 'Theme',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'size',
      displayName: 'Size',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'custom.meta-summary',
      displayName: 'Summary',
      position: 0,
      width: 100,
      visible: true
    }],
    contentType: 'OOTB',
    createdOn: 0,
    lastModifiedOn: 0
  }, {
    id: 'CUSTOM1',
    name: 'Custom 1',
    columns: [...BASE_COLUMNS, {
      metaName: 'ad.computer.dst',
      displayName: 'Active Directory Workstation Destination',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'checksum.dst',
      displayName: 'Target Checksum',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'checksum.src',
      displayName: 'Source Checksum',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'cid',
      displayName: 'Concentrator Source',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'city.dst',
      displayName: 'Destination City',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'email.all',
      displayName: 'All Email Address Keys',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'email.dst',
      displayName: 'Destination E-mail Address',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'filename.dst',
      displayName: 'Filename Destination',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'filename.size',
      displayName: 'File Size',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'filename.src',
      displayName: 'Filename Source',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'filetype',
      displayName: 'File Type',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'filter',
      displayName: 'Filter',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'float32.whatever',
      displayName: 'float32.whatever',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'forward.ip',
      displayName: 'Event Relay IPv4 Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'forward.ipv6',
      displayName: 'Event Relay IPv6 Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'found',
      displayName: 'Found Search',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'fqdn',
      displayName: 'FQDN',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'function',
      displayName: 'Function',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'host.all',
      displayName: 'All Hostname Keys',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'host.orig',
      displayName: 'Hostname Originating',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'host.role',
      displayName: 'Host Role',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'host.src',
      displayName: 'Source Hostname',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'inv.category',
      displayName: 'Investigation Category',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ioc',
      displayName: 'Indicators of Compromise',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.addr',
      displayName: 'IP Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.all',
      displayName: 'All IPv4 Keys',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.dst',
      displayName: 'Destination IP Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.dstport',
      displayName: 'Destination Port',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.orig',
      displayName: 'IP Address Originating',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.proto',
      displayName: 'IP Protocol',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.srcport',
      displayName: 'IP Source Port',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ip.whatever',
      displayName: 'ip.whatever',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ipv4.whatever',
      displayName: 'ipv4.whatever',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ja3',
      displayName: 'JA3 Fingerprint',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'ja3s',
      displayName: 'JA3S Fingerprint',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'language',
      displayName: 'Languages',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'latdec.dst',
      displayName: 'Destination Latitude',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'latdec.src',
      displayName: 'Source Latitude',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'lc.cid',
      displayName: 'Collector ID',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'lifetime',
      displayName: 'Session Lifetime',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'link',
      displayName: 'Link to Data',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'logon.type',
      displayName: 'Logon Type',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'logon.type.desc',
      displayName: 'Description of Logon Type',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'longdec.dst',
      displayName: 'Destination Longitude',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'longdec.src',
      displayName: 'Source Longitude',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'mac.whatever',
      displayName: 'mac.whatever',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'match',
      displayName: 'Match Search Item',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'msg',
      displayName: 'Message',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'msg.id',
      displayName: 'Message ID',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'netname',
      displayName: 'Network Name',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'network.port',
      displayName: 'Network Port',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'nwe.callback_id',
      displayName: 'NWE Callback Id',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'obj.name',
      displayName: 'Object Name',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'obj.type',
      displayName: 'Object Type',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'org',
      displayName: 'Organization',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'org.dst',
      displayName: 'Destination Organization',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'org.src',
      displayName: 'Source Organization',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'orig_ip',
      displayName: 'Originating IP Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'OS',
      displayName: 'Operating System',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'owner',
      displayName: 'Owner',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'packets',
      displayName: 'Session Packet Count',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'paddr',
      displayName: 'Device Address',
      position: 0,
      width: 100,
      visible: false
    }, {
      metaName: 'param.all',
      displayName: 'All Parameter Keys',
      position: 0,
      width: 100,
      visible: false
    }],
    contentType: 'USER',
    createdBy: 'admin',
    createdOn: 1573488613320,
    lastModifiedBy: 'admin',
    lastModifiedOn: 1573488613320
  }, {
    id: 'CUSTOM2',
    name: 'Custom 2',
    columns: [...BASE_COLUMNS, {
      metaName: 'ip.src',
      displayName: 'Source IP Address',
      position: 0,
      width: 100,
      visible: true
    }, {
      metaName: 'custom.meta-details', // a restricted column
      displayName: 'Details',
      position: 0,
      width: 100,
      visible: true
    }],
    contentType: 'USER',
    createdBy: 'admin',
    createdOn: 1573492605270,
    lastModifiedBy: 'admin',
    lastModifiedOn: 1573492605270
  }
];

export default columnGroups;
