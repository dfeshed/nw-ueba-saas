export const BASE_COLUMNS = [
  { field: 'time', title: 'Event Time', width: 100 },
  { field: 'medium', title: 'Event Type' }
];

export default [
  {
    id: 'EMAIL1',
    name: 'Custom 1',
    columns: [
      {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'orig_ip',
        title: 'Originating IP Address'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'tcp.dstport',
        title: 'TCP Destination Port'
      }, {
        field: 'ip.dstport',
        title: 'Destination Port'
      }, {
        field: 'alias.host',
        title: 'Hostname Alias Record'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }],
    ootb: false
  }, {
    id: 'MALWARE1',
    name: 'Custom 2',
    columns: [
      {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'alias.host',
        title: 'Hostname Alias Record'
      }, {
        field: 'referer',
        title: 'Referer'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }],
    ootb: false
  },
  {
    id: 'SUMMARY',
    name: 'Summary List',
    ootb: true,
    columns: BASE_COLUMNS.concat([
      { field: 'custom.theme', title: 'Theme' },
      { field: 'size', title: 'Size' },
      { field: 'custom.meta-summary', title: 'Summary', width: 'auto' }
    ])
  }, {
    id: 'EMAIL',
    name: 'Email Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'orig_ip',
        title: 'Originating IP Address'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'tcp.dstport',
        title: 'TCP Destination Port'
      }, {
        field: 'ip.dstport',
        title: 'Destination Port'
      }, {
        field: 'alias.host',
        title: 'Hostname Alias Record'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'org.src',
        title: 'Source Organization'
      }, {
        field: 'org.dst',
        title: 'Destination Organization'
      }, {
        field: 'subject',
        title: 'Subject'
      }, {
        field: 'email.src',
        title: 'Source E-mail Address'
      }, {
        field: 'email.dst',
        visible: false,
        title: 'Destination E-mail Address'
      }, {
        field: 'domain.dst',
        visible: false,
        title: 'Destination Domain'
      }, {
        field: 'client',
        visible: false,
        title: 'Client Application'
      }, {
        field: 'server',
        visible: false,
        title: 'Server Application'
      }, {
        field: 'content',
        visible: false,
        title: 'Content Type'
      }, {
        field: 'action',
        visible: false,
        title: 'Action Event'
      }, {
        field: 'attachment',
        visible: false,
        title: 'Attachment'
      }, {
        field: 'extension',
        visible: false,
        title: 'Extension'
      }, {
        field: 'filetype',
        visible: false,
        title: 'Forensic Fingerprint'
      }, {
        field: 'filetitle',
        visible: false,
        title: 'Filename'
      }, {
        field: 'usertitle',
        visible: false,
        title: 'User Account'
      }, {
        field: 'user.src',
        visible: false,
        title: 'Source User Account'
      }, {
        field: 'user.dst',
        visible: false,
        title: 'Destination User Account'
      }, {
        field: 'error',
        visible: false,
        title: 'Error'
      }, {
        field: 'crypto',
        visible: false,
        title: 'Crypto Key'
      }, {
        field: 'ssl.subject',
        visible: false,
        title: 'SSL Subject'
      }, {
        field: 'ssl.ca',
        visible: false,
        title: 'SSL CA'
      }, {
        field: 'risk.info',
        visible: false,
        title: 'Risk: Informational'
      }, {
        field: 'risk.suspicious',
        visible: false,
        title: 'Risk: Suspicious'
      }, {
        field: 'risk.warning',
        visible: false,
        title: 'Risk: Warning'
      }, {
        field: 'threat.category',
        visible: false,
        title: 'Threat Category'
      }, {
        field: 'threat.desc',
        visible: false,
        title: 'Threat Description'
      }, {
        field: 'threat.source',
        visible: false,
        title: 'Threat Source'
      }, {
        field: 'alert',
        visible: false,
        title: 'Alerts'
      }, {
        field: 'sourcefile',
        visible: false,
        title: 'Source File'
      }, {
        field: 'did',
        visible: false,
        title: 'Decoder Source'
      }
    ]),
    ootb: true
  }, {
    id: 'MALWARE',
    name: 'Malware Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'alias.host',
        title: 'Hostname Alias Record'
      }, {
        field: 'referer',
        title: 'Referer'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'client',
        title: 'Client Application'
      }, {
        field: 'server',
        title: 'Server Application'
      }, {
        field: 'content',
        title: 'Content Type'
      }, {
        field: 'action',
        title: 'Action Event'
      }, {
        field: 'attachment',
        title: 'Attachment'
      }, {
        field: 'extension',
        visible: false,
        title: 'Extension'
      }, {
        field: 'filetype',
        visible: false,
        title: 'Forensic Fingerprint'
      }, {
        field: 'filetitle',
        visible: false,
        title: 'Filename'
      }, {
        field: 'directory',
        visible: false,
        title: 'Directory'
      }, {
        field: 'sql',
        visible: false,
        title: 'Sql Query'
      }, {
        field: 'usertitle',
        visible: false,
        title: 'User Account'
      }, {
        field: 'risk.info',
        visible: false,
        title: 'Risk: Informational'
      }, {
        field: 'risk.suspicious',
        visible: false,
        title: 'Risk: Suspicious'
      }, {
        field: 'risk.warning',
        visible: false,
        title: 'Risk: Warning'
      }, {
        field: 'alert',
        visible: false,
        title: 'Alerts'
      }, {
        field: 'sourcefile',
        visible: false,
        title: 'Source File'
      }, {
        field: 'did',
        visible: false,
        title: 'Decoder Source'
      }
    ]),
    ootb: true
  }, {
    id: 'THREAT',
    name: 'Threat Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'threat.category',
        title: 'Threat Category'
      }, {
        field: 'threat.desc',
        title: 'Threat Description'
      }, {
        field: 'threat.source',
        title: 'Threat Source'
      }, {
        field: 'risk.info',
        title: 'Risk: Informational'
      }, {
        field: 'risk.suspicious',
        title: 'Risk: Suspicious'
      }, {
        field: 'risk.warning',
        title: 'Risk: Warning'
      }, {
        field: 'alert',
        title: 'Alerts'
      }, {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'orig_ip',
        title: 'Originating IP Address'
      }, {
        field: 'alias.ip',
        title: 'IP Address Alias Record'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'ip.dstport',
        title: 'Destination Port'
      }, {
        field: 'tcp.dstport',
        visible: false,
        title: 'TCP Destination Port'
      }, {
        field: 'udp.srcport',
        visible: false,
        title: 'UDP Source Port'
      }, {
        field: 'udp.dstport',
        visible: false,
        title: 'UDP Target Port'
      }, {
        field: 'ip.proto',
        visible: false,
        title: 'IP Protocol'
      }, {
        field: 'eth.type',
        visible: false,
        title: 'Ethernet Protocol'
      }, {
        field: 'eth.src',
        visible: false,
        title: 'Ethernet Source Address'
      }, {
        field: 'eth.dst',
        visible: false,
        title: 'Ethernet Destination Address'
      }, {
        field: 'alias.host',
        visible: false,
        title: 'Hostname Alias Record'
      }, {
        field: 'country.src',
        visible: false,
        title: 'Source Country'
      }, {
        field: 'country.dst',
        visible: false,
        title: 'Destination Country'
      }, {
        field: 'domain.dst',
        visible: false,
        title: 'Destination Domain'
      }, {
        field: 'org.src',
        visible: false,
        title: 'Source Organization'
      }, {
        field: 'org.dst',
        visible: false,
        title: 'Destination Organization'
      }, {
        field: 'action',
        visible: false,
        title: 'Action Event'
      }, {
        field: 'usertitle',
        visible: false,
        title: 'User Account'
      }, {
        field: 'password',
        visible: false,
        title: 'Password'
      }, {
        field: 'device.type',
        visible: false,
        title: 'Device Type'
      }, {
        field: 'device.ip',
        visible: false,
        title: 'Device IP'
      }, {
        field: 'device.ipv6',
        visible: false,
        title: 'Device IPv6'
      }, {
        field: 'device.host',
        visible: false,
        title: 'Device Host'
      }, {
        field: 'device.class',
        visible: false,
        title: 'Device Class'
      }, {
        field: 'paddr',
        visible: false,
        title: 'Device Address'
      }, {
        field: 'device.title',
        visible: false,
        title: 'Device Name'
      }, {
        field: 'event.source',
        visible: false,
        title: 'Event Source'
      }, {
        field: 'event.desc',
        visible: false,
        title: 'Event Description'
      }, {
        field: 'ec.subject',
        visible: false,
        title: 'Event Subject'
      }, {
        field: 'ec.activity',
        visible: false,
        title: 'Event Activity'
      }, {
        field: 'ec.theme',
        visible: false,
        title: 'Event Theme'
      }, {
        field: 'ec.outcome',
        visible: false,
        title: 'Event Outcome'
      }, {
        field: 'event.cat.title',
        visible: false,
        title: 'Event Category Name'
      }, {
        field: 'device.group',
        visible: false,
        title: 'Event Source Group'
      }, {
        field: 'event.class',
        visible: false,
        title: 'Event Classification'
      }, {
        field: 'sql',
        visible: false,
        title: 'Sql Query'
      }, {
        field: 'category',
        visible: false,
        title: 'Category'
      }, {
        field: 'query',
        visible: false,
        title: 'Query'
      }, {
        field: 'OS',
        visible: false,
        title: 'Operating System'
      }, {
        field: 'browser',
        visible: false,
        title: 'Browsers'
      }, {
        field: 'version',
        visible: false,
        title: 'Versions'
      }, {
        field: 'policy.title',
        visible: false,
        title: 'Policyname'
      }, {
        field: 'sourcefile',
        visible: false,
        title: 'Source File'
      }, {
        field: 'lc.cid',
        visible: false,
        title: 'Collector ID'
      }, {
        field: 'did',
        visible: false,
        title: 'Decoder Source'
      }
    ]),
    ootb: true
  }, {
    id: 'WEB',
    name: 'Web Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'orig_ip',
        title: 'Originating IP Address'
      }, {
        field: 'alias.ip',
        title: 'IP Address Alias Record'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'tcp.dstport',
        title: 'TCP Destination Port'
      }, {
        field: 'alias.host',
        title: 'Hostname Alias Record'
      }, {
        field: 'referer',
        title: 'Referer'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'org.src',
        title: 'Source Organization'
      }, {
        field: 'org.dst',
        title: 'Destination Organization'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'client',
        visible: false,
        title: 'Client Application'
      }, {
        field: 'server',
        visible: false,
        title: 'Server Application'
      }, {
        field: 'content',
        visible: false,
        title: 'Content Type'
      }, {
        field: 'action',
        visible: false,
        title: 'Action Event'
      }, {
        field: 'filetitle',
        visible: false,
        title: 'Filename'
      }, {
        field: 'usertitle',
        visible: false,
        title: 'User Account'
      }, {
        field: 'password',
        visible: false,
        title: 'Password'
      }, {
        field: 'ssl.ca',
        visible: false,
        title: 'SSL CA'
      }, {
        field: 'ssl.subject',
        visible: false,
        title: 'SSL Subject'
      }, {
        field: 'error',
        visible: false,
        title: 'Error'
      }, {
        field: 'query',
        visible: false,
        title: 'Query'
      }, {
        field: 'directory',
        visible: false,
        title: 'Directory'
      }, {
        field: 'browser',
        visible: false,
        title: 'Browsers'
      }, {
        field: 'category',
        visible: false,
        title: 'Category'
      }, {
        field: 'policy.title',
        visible: false,
        title: 'Policyname'
      }, {
        field: 'device.type',
        visible: false,
        title: 'Device Type'
      }, {
        field: 'device.ip',
        visible: false,
        title: 'Device IP'
      }, {
        field: 'device.ipv6',
        visible: false,
        title: 'Device IPv6'
      }, {
        field: 'device.host',
        visible: false,
        title: 'Device Host'
      }, {
        field: 'device.class',
        visible: false,
        title: 'Device Class'
      }, {
        field: 'paddr',
        visible: false,
        title: 'Device Address'
      }, {
        field: 'device.title',
        visible: false,
        title: 'Device Name'
      }, {
        field: 'event.source',
        visible: false,
        title: 'Event Source'
      }, {
        field: 'event.desc',
        visible: false,
        title: 'Event Description'
      }, {
        field: 'ec.subject',
        visible: false,
        title: 'Event Subject'
      }, {
        field: 'ec.activity',
        visible: false,
        title: 'Event Activity'
      }, {
        field: 'ec.theme',
        visible: false,
        title: 'Event Theme'
      }, {
        field: 'ec.outcome',
        visible: false,
        title: 'Event Outcome'
      }, {
        field: 'event.cat.title',
        visible: false,
        title: 'Event Category Name'
      }, {
        field: 'device.group',
        visible: false,
        title: 'Event Source Group'
      }, {
        field: 'event.class',
        visible: false,
        title: 'Event Classification'
      }, {
        field: 'risk.info',
        visible: false,
        title: 'Risk: Informational'
      }, {
        field: 'risk.suspicious',
        visible: false,
        title: 'Risk: Suspicious'
      }, {
        field: 'risk.warning',
        visible: false,
        title: 'Risk: Warning'
      }, {
        field: 'alert',
        visible: false,
        title: 'Alert'
      }, {
        field: 'sourcefile',
        visible: false,
        title: 'Source File'
      }, {
        field: 'lc.cid',
        visible: false,
        title: 'Collector ID'
      }, {
        field: 'did',
        visible: false,
        title: 'Decoder Source'
      }
    ]),
    ootb: true
  }, {
    name: 'Endpoint Analysis',
    id: 'ENDPOINT',
    columns: BASE_COLUMNS.concat([
      {
        field: 'device.type',
        title: 'Device Type'
      }, {
        field: 'timezone',
        title: 'Time Zone'
      }, {
        field: 'category',
        title: 'Category'
      }, {
        field: 'alias.host',
        title: 'Hostname Aliases'
      }, {
        field: 'ip.dst',
        title: 'Destination IP address'
      }, {
        field: 'action',
        title: 'Action Event'
      }, {
        field: 'filename',
        title: 'Filename'
      }, {
        field: 'filename.size',
        title: 'File Size'
      }, {
        field: 'filename.src',
        title: 'Filename Source'
      }, {
        field: 'filename.dst',
        title: 'Filename Destination'
      }, {
        field: 'file.vendor',
        title: 'File Vendor'
      }, {
        field: 'file.entropy',
        title: 'File Entropy'
      }, {
        field: 'extension',
        title: 'Extension'
      }, {
        field: 'checksum',
        visible: false,
        title: 'Checksum'
      }, {
        field: 'directory',
        visible: false,
        title: 'Directory'
      }, {
        field: 'username',
        visible: false,
        title: 'User Account'
      }, {
        field: 'task.name',
        visible: false,
        title: 'Task Name'
      }, {
        field: 'owner',
        visible: false,
        title: 'Owner'
      }, {
        field: 'domain',
        visible: false,
        title: 'Domain'
      }, {
        field: 'dn',
        visible: false,
        title: 'Domain OU'
      }, {
        field: 'OS',
        visible: false,
        title: 'Operating System'
      }, {
        field: 'host.role',
        visible: false,
        title: 'Host Role'
      }, {
        field: 'version',
        visible: false,
        title: 'Versions'
      }, {
        field: 'client',
        visible: false,
        title: 'Client Application'
      }, {
        field: 'cert.subject',
        visible: false,
        title: 'Certificate Subject'
      }, {
        field: 'cert.common',
        visible: false,
        title: 'Certificate Common Name'
      }, {
        field: 'cert.checksum',
        visible: false,
        title: 'Certificate Checksum'
      }, {
        field: 'cert.ca',
        visible: false,
        title: 'Certificate Authority'
      }, {
        field: 'bytes.src',
        visible: false,
        title: 'Bytes Sent'
      }, {
        field: 'rbytes',
        visible: false,
        title: 'Bytes Received'
      }
    ]),
    ootb: true
  }
];
