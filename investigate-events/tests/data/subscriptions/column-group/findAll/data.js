export const BASE_COLUMNS = [
  { metaName: 'time', displayName: 'Collection Time', width: 175 },
  { metaName: 'medium', displayName: 'Type' }
];

export default [
  {
    id: 'EMAIL1',
    name: 'Custom 1',
    columns: [
      {
        metaName: 'service',
        displayName: 'Service Type'
      }, {
        metaName: 'custom.logdata',
        displayName: 'Log Data'
      }, {
        metaName: 'custom.source',
        displayName: 'Source'
      }, {
        metaName: 'custom.destination',
        displayName: 'Destination'
      }, {
        metaName: 'orig_ip',
        displayName: 'Originating IP Address'
      }, {
        metaName: 'ip.src',
        displayName: 'Source IP Address'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP Address'
      }, {
        metaName: 'tcp.dstport',
        displayName: 'TCP Destination Port'
      }, {
        metaName: 'ip.dstport',
        displayName: 'Destination Port'
      }, {
        metaName: 'alias.host',
        displayName: 'Hostname Alias Record'
      }, {
        metaName: 'country.src',
        displayName: 'Source Country'
      }],
    contentType: 'USER'
  }, {
    id: 'MALWARE1',
    name: 'Custom 2',
    columns: [
      {
        metaName: 'service',
        displayName: 'Service Type'
      }, {
        metaName: 'ip.src',
        displayName: 'Source IP Address'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP Address'
      }, {
        metaName: 'alias.host',
        displayName: 'Hostname Alias Record'
      }, {
        metaName: 'referer',
        displayName: 'Referer'
      }, {
        metaName: 'country.src',
        displayName: 'Source Country'
      }, {
        metaName: 'country.dst',
        displayName: 'Destination Country'
      }, {
        metaName: 'domain.dst',
        displayName: 'Destination Domain'
      }],
    contentType: 'USER'
  },
  {
    id: 'SUMMARY',
    name: 'Summary List',
    contentType: 'OOTB',
    columns: BASE_COLUMNS.concat([
      { metaName: 'custom.theme', displayName: 'Theme' },
      { metaName: 'size', displayName: 'Size' },
      { metaName: 'custom.meta-summary', displayName: 'Summary', width: null }
    ])
  },
  {
    id: 'SUMMARY2',
    name: 'Summary List',
    contentType: 'OOTB',
    columns: BASE_COLUMNS.concat([
      { metaName: 'custom.theme', displayName: 'Theme' },
      { metaName: 'size', displayName: 'Size' },
      { metaName: 'custom.meta-details', displayName: 'Details', width: null }
    ])
  },
  {
    id: 'SUMMARY3',
    name: 'Summary List',
    contentType: 'OOTB',
    columns: BASE_COLUMNS.concat([
      { metaName: 'custom.theme', displayName: 'Theme' },
      { metaName: 'size', displayName: 'Size' },
      { metaName: 'ip.dst', displayName: 'Destination IP Address' },
      { metaName: 'custom.meta-summary', displayName: 'Summary', width: null }
    ])
  },
  {
    id: 'SUMMARY4',
    name: 'Summary List',
    contentType: 'OOTB',
    columns: BASE_COLUMNS.concat([
      { metaName: 'custom.theme', displayName: 'Theme' },
      { metaName: 'size', displayName: 'Size' },
      { metaName: 'ip.dst', displayName: 'Destination IP Address' },
      { metaName: 'custom.metasummary', displayName: 'Summary', width: null }
    ])
  },
  {
    id: 'EMAIL',
    name: 'Email Analysis',
    columns: BASE_COLUMNS.concat([
      {
        metaName: 'service',
        displayName: 'Service Type'
      }, {
        metaName: 'orig_ip',
        displayName: 'Originating IP Address'
      }, {
        metaName: 'ip.src',
        displayName: 'Source IP Address'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP Address'
      }, {
        metaName: 'tcp.dstport',
        displayName: 'TCP Destination Port'
      }, {
        metaName: 'ip.dstport',
        displayName: 'Destination Port'
      }, {
        metaName: 'alias.host',
        displayName: 'Hostname Alias Record'
      }, {
        metaName: 'country.src',
        displayName: 'Source Country'
      }, {
        metaName: 'country.dst',
        displayName: 'Destination Country'
      }, {
        metaName: 'org.src',
        displayName: 'Source Organization'
      }, {
        metaName: 'org.dst',
        displayName: 'Destination Organization'
      }, {
        metaName: 'subject',
        displayName: 'Subject'
      }, {
        metaName: 'email.src',
        displayName: 'Source E-mail Address'
      }, {
        metaName: 'email.dst',
        visible: false,
        displayName: 'Destination E-mail Address'
      }, {
        metaName: 'domain.dst',
        visible: false,
        displayName: 'Destination Domain'
      }, {
        metaName: 'client',
        visible: false,
        displayName: 'Client Application'
      }, {
        metaName: 'server',
        visible: false,
        displayName: 'Server Application'
      }, {
        metaName: 'content',
        visible: false,
        displayName: 'Content Type'
      }, {
        metaName: 'action',
        visible: false,
        displayName: 'Action Event'
      }, {
        metaName: 'attachment',
        visible: false,
        displayName: 'Attachment'
      }, {
        metaName: 'extension',
        visible: false,
        displayName: 'Extension'
      }, {
        metaName: 'filetype',
        visible: false,
        displayName: 'Forensic Fingerprint'
      }, {
        metaName: 'filetitle',
        visible: false,
        displayName: 'Filename'
      }, {
        metaName: 'usertitle',
        visible: false,
        displayName: 'User Account'
      }, {
        metaName: 'user.src',
        visible: false,
        displayName: 'Source User Account'
      }, {
        metaName: 'user.dst',
        visible: false,
        displayName: 'Destination User Account'
      }, {
        metaName: 'error',
        visible: false,
        displayName: 'Error'
      }, {
        metaName: 'crypto',
        visible: false,
        displayName: 'Crypto Key'
      }, {
        metaName: 'ssl.subject',
        visible: false,
        displayName: 'SSL Subject'
      }, {
        metaName: 'ssl.ca',
        visible: false,
        displayName: 'SSL CA'
      }, {
        metaName: 'risk.info',
        visible: false,
        displayName: 'Risk: Informational'
      }, {
        metaName: 'risk.suspicious',
        visible: false,
        displayName: 'Risk: Suspicious'
      }, {
        metaName: 'risk.warning',
        visible: false,
        displayName: 'Risk: Warning'
      }, {
        metaName: 'threat.category',
        visible: false,
        displayName: 'Threat Category'
      }, {
        metaName: 'threat.desc',
        visible: false,
        displayName: 'Threat Description'
      }, {
        metaName: 'threat.source',
        visible: false,
        displayName: 'Threat Source'
      }, {
        metaName: 'alert',
        visible: false,
        displayName: 'Alerts'
      }, {
        metaName: 'sourcefile',
        visible: false,
        displayName: 'Source File'
      }, {
        metaName: 'did',
        visible: false,
        displayName: 'Decoder Source'
      }
    ]),
    contentType: 'OOTB'
  }, {
    id: 'MALWARE',
    name: 'Malware Analysis',
    columns: BASE_COLUMNS.concat([
      {
        metaName: 'service',
        displayName: 'Service Type'
      }, {
        metaName: 'ip.src',
        displayName: 'Source IP Address'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP Address'
      }, {
        metaName: 'alias.host',
        displayName: 'Hostname Alias Record'
      }, {
        metaName: 'referer',
        displayName: 'Referer'
      }, {
        metaName: 'country.src',
        displayName: 'Source Country'
      }, {
        metaName: 'country.dst',
        displayName: 'Destination Country'
      }, {
        metaName: 'domain.dst',
        displayName: 'Destination Domain'
      }, {
        metaName: 'client',
        displayName: 'Client Application'
      }, {
        metaName: 'server',
        displayName: 'Server Application'
      }, {
        metaName: 'content',
        displayName: 'Content Type'
      }, {
        metaName: 'action',
        displayName: 'Action Event'
      }, {
        metaName: 'attachment',
        displayName: 'Attachment'
      }, {
        metaName: 'extension',
        visible: false,
        displayName: 'Extension'
      }, {
        metaName: 'filetype',
        visible: false,
        displayName: 'Forensic Fingerprint'
      }, {
        metaName: 'filetitle',
        visible: false,
        displayName: 'Filename'
      }, {
        metaName: 'directory',
        visible: false,
        displayName: 'Directory'
      }, {
        metaName: 'sql',
        visible: false,
        displayName: 'Sql Query'
      }, {
        metaName: 'usertitle',
        visible: false,
        displayName: 'User Account'
      }, {
        metaName: 'risk.info',
        visible: false,
        displayName: 'Risk: Informational'
      }, {
        metaName: 'risk.suspicious',
        visible: false,
        displayName: 'Risk: Suspicious'
      }, {
        metaName: 'risk.warning',
        visible: false,
        displayName: 'Risk: Warning'
      }, {
        metaName: 'alert',
        visible: false,
        displayName: 'Alerts'
      }, {
        metaName: 'sourcefile',
        visible: false,
        displayName: 'Source File'
      }, {
        metaName: 'did',
        visible: false,
        displayName: 'Decoder Source'
      }
    ]),
    contentType: 'OOTB'
  }, {
    id: 'THREAT',
    name: 'Threat Analysis',
    columns: BASE_COLUMNS.concat([
      {
        metaName: 'threat.category',
        displayName: 'Threat Category'
      }, {
        metaName: 'threat.desc',
        displayName: 'Threat Description'
      }, {
        metaName: 'threat.source',
        displayName: 'Threat Source'
      }, {
        metaName: 'risk.info',
        displayName: 'Risk: Informational'
      }, {
        metaName: 'risk.suspicious',
        displayName: 'Risk: Suspicious'
      }, {
        metaName: 'risk.warning',
        displayName: 'Risk: Warning'
      }, {
        metaName: 'alert',
        displayName: 'Alerts'
      }, {
        metaName: 'service',
        displayName: 'Service Type'
      }, {
        metaName: 'orig_ip',
        displayName: 'Originating IP Address'
      }, {
        metaName: 'alias.ip',
        displayName: 'IP Address Alias Record'
      }, {
        metaName: 'ip.src',
        displayName: 'Source IP Address'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP Address'
      }, {
        metaName: 'ip.dstport',
        displayName: 'Destination Port'
      }, {
        metaName: 'tcp.dstport',
        visible: false,
        displayName: 'TCP Destination Port'
      }, {
        metaName: 'udp.srcport',
        visible: false,
        displayName: 'UDP Source Port'
      }, {
        metaName: 'udp.dstport',
        visible: false,
        displayName: 'UDP Target Port'
      }, {
        metaName: 'ip.proto',
        visible: false,
        displayName: 'IP Protocol'
      }, {
        metaName: 'eth.type',
        visible: false,
        displayName: 'Ethernet Protocol'
      }, {
        metaName: 'eth.src',
        visible: false,
        displayName: 'Ethernet Source Address'
      }, {
        metaName: 'eth.dst',
        visible: false,
        displayName: 'Ethernet Destination Address'
      }, {
        metaName: 'alias.host',
        visible: false,
        displayName: 'Hostname Alias Record'
      }, {
        metaName: 'country.src',
        visible: false,
        displayName: 'Source Country'
      }, {
        metaName: 'country.dst',
        visible: false,
        displayName: 'Destination Country'
      }, {
        metaName: 'domain.dst',
        visible: false,
        displayName: 'Destination Domain'
      }, {
        metaName: 'org.src',
        visible: false,
        displayName: 'Source Organization'
      }, {
        metaName: 'org.dst',
        visible: false,
        displayName: 'Destination Organization'
      }, {
        metaName: 'action',
        visible: false,
        displayName: 'Action Event'
      }, {
        metaName: 'usertitle',
        visible: false,
        displayName: 'User Account'
      }, {
        metaName: 'password',
        visible: false,
        displayName: 'Password'
      }, {
        metaName: 'device.type',
        visible: false,
        displayName: 'Device Type'
      }, {
        metaName: 'device.ip',
        visible: false,
        displayName: 'Device IP'
      }, {
        metaName: 'device.ipv6',
        visible: false,
        displayName: 'Device IPv6'
      }, {
        metaName: 'device.host',
        visible: false,
        displayName: 'Device Host'
      }, {
        metaName: 'device.class',
        visible: false,
        displayName: 'Device Class'
      }, {
        metaName: 'paddr',
        visible: false,
        displayName: 'Device Address'
      }, {
        metaName: 'device.title',
        visible: false,
        displayName: 'Device Name'
      }, {
        metaName: 'event.source',
        visible: false,
        displayName: 'Event Source'
      }, {
        metaName: 'event.desc',
        visible: false,
        displayName: 'Event Description'
      }, {
        metaName: 'ec.subject',
        visible: false,
        displayName: 'Event Subject'
      }, {
        metaName: 'ec.activity',
        visible: false,
        displayName: 'Event Activity'
      }, {
        metaName: 'ec.theme',
        visible: false,
        displayName: 'Event Theme'
      }, {
        metaName: 'ec.outcome',
        visible: false,
        displayName: 'Event Outcome'
      }, {
        metaName: 'event.cat.title',
        visible: false,
        displayName: 'Event Category Name'
      }, {
        metaName: 'device.group',
        visible: false,
        displayName: 'Event Source Group'
      }, {
        metaName: 'event.class',
        visible: false,
        displayName: 'Event Classification'
      }, {
        metaName: 'sql',
        visible: false,
        displayName: 'Sql Query'
      }, {
        metaName: 'category',
        visible: false,
        displayName: 'Category'
      }, {
        metaName: 'query',
        visible: false,
        displayName: 'Query'
      }, {
        metaName: 'OS',
        visible: false,
        displayName: 'Operating System'
      }, {
        metaName: 'browser',
        visible: false,
        displayName: 'Browsers'
      }, {
        metaName: 'version',
        visible: false,
        displayName: 'Versions'
      }, {
        metaName: 'policy.title',
        visible: false,
        displayName: 'Policyname'
      }, {
        metaName: 'sourcefile',
        visible: false,
        displayName: 'Source File'
      }, {
        metaName: 'lc.cid',
        visible: false,
        displayName: 'Collector ID'
      }, {
        metaName: 'did',
        visible: false,
        displayName: 'Decoder Source'
      }
    ]),
    contentType: 'OOTB'
  }, {
    id: 'WEB',
    name: 'Web Analysis',
    columns: BASE_COLUMNS.concat([
      {
        metaName: 'service',
        displayName: 'Service Type'
      }, {
        metaName: 'orig_ip',
        displayName: 'Originating IP Address'
      }, {
        metaName: 'alias.ip',
        displayName: 'IP Address Alias Record'
      }, {
        metaName: 'ip.src',
        displayName: 'Source IP Address'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP Address'
      }, {
        metaName: 'tcp.dstport',
        displayName: 'TCP Destination Port'
      }, {
        metaName: 'alias.host',
        displayName: 'Hostname Alias Record'
      }, {
        metaName: 'referer',
        displayName: 'Referer'
      }, {
        metaName: 'country.src',
        displayName: 'Source Country'
      }, {
        metaName: 'country.dst',
        displayName: 'Destination Country'
      }, {
        metaName: 'org.src',
        displayName: 'Source Organization'
      }, {
        metaName: 'org.dst',
        displayName: 'Destination Organization'
      }, {
        metaName: 'domain.dst',
        displayName: 'Destination Domain'
      }, {
        metaName: 'client',
        visible: false,
        displayName: 'Client Application'
      }, {
        metaName: 'server',
        visible: false,
        displayName: 'Server Application'
      }, {
        metaName: 'content',
        visible: false,
        displayName: 'Content Type'
      }, {
        metaName: 'action',
        visible: false,
        displayName: 'Action Event'
      }, {
        metaName: 'filetitle',
        visible: false,
        displayName: 'Filename'
      }, {
        metaName: 'usertitle',
        visible: false,
        displayName: 'User Account'
      }, {
        metaName: 'password',
        visible: false,
        displayName: 'Password'
      }, {
        metaName: 'ssl.ca',
        visible: false,
        displayName: 'SSL CA'
      }, {
        metaName: 'ssl.subject',
        visible: false,
        displayName: 'SSL Subject'
      }, {
        metaName: 'error',
        visible: false,
        displayName: 'Error'
      }, {
        metaName: 'query',
        visible: false,
        displayName: 'Query'
      }, {
        metaName: 'directory',
        visible: false,
        displayName: 'Directory'
      }, {
        metaName: 'browser',
        visible: false,
        displayName: 'Browsers'
      }, {
        metaName: 'category',
        visible: false,
        displayName: 'Category'
      }, {
        metaName: 'policy.title',
        visible: false,
        displayName: 'Policyname'
      }, {
        metaName: 'device.type',
        visible: false,
        displayName: 'Device Type'
      }, {
        metaName: 'device.ip',
        visible: false,
        displayName: 'Device IP'
      }, {
        metaName: 'device.ipv6',
        visible: false,
        displayName: 'Device IPv6'
      }, {
        metaName: 'device.host',
        visible: false,
        displayName: 'Device Host'
      }, {
        metaName: 'device.class',
        visible: false,
        displayName: 'Device Class'
      }, {
        metaName: 'paddr',
        visible: false,
        displayName: 'Device Address'
      }, {
        metaName: 'device.title',
        visible: false,
        displayName: 'Device Name'
      }, {
        metaName: 'event.source',
        visible: false,
        displayName: 'Event Source'
      }, {
        metaName: 'event.desc',
        visible: false,
        displayName: 'Event Description'
      }, {
        metaName: 'ec.subject',
        visible: false,
        displayName: 'Event Subject'
      }, {
        metaName: 'ec.activity',
        visible: false,
        displayName: 'Event Activity'
      }, {
        metaName: 'ec.theme',
        visible: false,
        displayName: 'Event Theme'
      }, {
        metaName: 'ec.outcome',
        visible: false,
        displayName: 'Event Outcome'
      }, {
        metaName: 'event.cat.title',
        visible: false,
        displayName: 'Event Category Name'
      }, {
        metaName: 'device.group',
        visible: false,
        displayName: 'Event Source Group'
      }, {
        metaName: 'event.class',
        visible: false,
        displayName: 'Event Classification'
      }, {
        metaName: 'risk.info',
        visible: false,
        displayName: 'Risk: Informational'
      }, {
        metaName: 'risk.suspicious',
        visible: false,
        displayName: 'Risk: Suspicious'
      }, {
        metaName: 'risk.warning',
        visible: false,
        displayName: 'Risk: Warning'
      }, {
        metaName: 'alert',
        visible: false,
        displayName: 'Alert'
      }, {
        metaName: 'sourcefile',
        visible: false,
        displayName: 'Source File'
      }, {
        metaName: 'lc.cid',
        visible: false,
        displayName: 'Collector ID'
      }, {
        metaName: 'did',
        visible: false,
        displayName: 'Decoder Source'
      }
    ]),
    contentType: 'OOTB'
  }, {
    name: 'Endpoint Analysis',
    id: 'ENDPOINT',
    columns: BASE_COLUMNS.concat([
      {
        metaName: 'device.type',
        displayName: 'Device Type'
      }, {
        metaName: 'timezone',
        displayName: 'Time Zone'
      }, {
        metaName: 'category',
        displayName: 'Category'
      }, {
        metaName: 'alias.host',
        displayName: 'Hostname Aliases'
      }, {
        metaName: 'ip.dst',
        displayName: 'Destination IP address'
      }, {
        metaName: 'action',
        displayName: 'Action Event'
      }, {
        metaName: 'filename',
        displayName: 'Filename'
      }, {
        metaName: 'filename.size',
        displayName: 'File Size'
      }, {
        metaName: 'filename.src',
        displayName: 'Filename Source'
      }, {
        metaName: 'filename.dst',
        displayName: 'Filename Destination'
      }, {
        metaName: 'file.vendor',
        displayName: 'File Vendor'
      }, {
        metaName: 'file.entropy',
        displayName: 'File Entropy'
      }, {
        metaName: 'extension',
        displayName: 'Extension'
      }, {
        metaName: 'checksum',
        visible: false,
        displayName: 'Checksum'
      }, {
        metaName: 'directory',
        visible: false,
        displayName: 'Directory'
      }, {
        metaName: 'username',
        visible: false,
        displayName: 'User Account'
      }, {
        metaName: 'task.name',
        visible: false,
        displayName: 'Task Name'
      }, {
        metaName: 'owner',
        visible: false,
        displayName: 'Owner'
      }, {
        metaName: 'domain',
        visible: false,
        displayName: 'Domain'
      }, {
        metaName: 'dn',
        visible: false,
        displayName: 'Domain OU'
      }, {
        metaName: 'OS',
        visible: false,
        displayName: 'Operating System'
      }, {
        metaName: 'host.role',
        visible: false,
        displayName: 'Host Role'
      }, {
        metaName: 'version',
        visible: false,
        displayName: 'Versions'
      }, {
        metaName: 'client',
        visible: false,
        displayName: 'Client Application'
      }, {
        metaName: 'cert.subject',
        visible: false,
        displayName: 'Certificate Subject'
      }, {
        metaName: 'cert.common',
        visible: false,
        displayName: 'Certificate Common Name'
      }, {
        metaName: 'cert.checksum',
        visible: false,
        displayName: 'Certificate Checksum'
      }, {
        metaName: 'cert.ca',
        visible: false,
        displayName: 'Certificate Authority'
      }, {
        metaName: 'bytes.src',
        visible: false,
        displayName: 'Bytes Sent'
      }, {
        metaName: 'rbytes',
        visible: false,
        displayName: 'Bytes Received'
      }
    ]),
    contentType: 'OOTB'
  }
];
