export const BASE_COLUMNS = [
  { field: 'time', title: 'Event Time', width: 100 },
  { field: 'medium', title: 'Event Type' }
];

export const OOTBColumnGroups = [
  {
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
        title: 'Destination E-mail Address'
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
        title: 'Extension'
      }, {
        field: 'filetype',
        title: 'Forensic Fingerprint'
      }, {
        field: 'filetitle',
        title: 'Filename'
      }, {
        field: 'usertitle',
        title: 'User Account'
      }, {
        field: 'user.src',
        title: 'Source User Account'
      }, {
        field: 'user.dst',
        title: 'Destination User Account'
      }, {
        field: 'error',
        title: 'Error'
      }, {
        field: 'crypto',
        title: 'Crypto Key'
      }, {
        field: 'ssl.subject',
        title: 'SSL Subject'
      }, {
        field: 'ssl.ca',
        title: 'SSL CA'
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
        field: 'threat.category',
        title: 'Threat Category'
      }, {
        field: 'threat.desc',
        title: 'Threat Description'
      }, {
        field: 'threat.source',
        title: 'Threat Source'
      }, {
        field: 'alert',
        title: 'Alerts'
      }, {
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'did',
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
        title: 'Extension'
      }, {
        field: 'filetype',
        title: 'Forensic Fingerprint'
      }, {
        field: 'filetitle',
        title: 'Filename'
      }, {
        field: 'directory',
        title: 'Directory'
      }, {
        field: 'sql',
        title: 'Sql Query'
      }, {
        field: 'usertitle',
        title: 'User Account'
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
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'did',
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
        title: 'TCP Destination Port'
      }, {
        field: 'udp.srcport',
        title: 'UDP Source Port'
      }, {
        field: 'udp.dstport',
        title: 'UDP Target Port'
      }, {
        field: 'ip.proto',
        title: 'IP Protocol'
      }, {
        field: 'eth.type',
        title: 'Ethernet Protocol'
      }, {
        field: 'eth.src',
        title: 'Ethernet Source Address'
      }, {
        field: 'eth.dst',
        title: 'Ethernet Destination Address'
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
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'org.src',
        title: 'Source Organization'
      }, {
        field: 'org.dst',
        title: 'Destination Organization'
      }, {
        field: 'action',
        title: 'Action Event'
      }, {
        field: 'usertitle',
        title: 'User Account'
      }, {
        field: 'password',
        title: 'Password'
      }, {
        field: 'device.type',
        title: 'Device Type'
      }, {
        field: 'device.ip',
        title: 'Device IP'
      }, {
        field: 'device.ipv6',
        title: 'Device IPv6'
      }, {
        field: 'device.host',
        title: 'Device Host'
      }, {
        field: 'device.class',
        title: 'Device Class'
      }, {
        field: 'paddr',
        title: 'Device Address'
      }, {
        field: 'device.title',
        title: 'Device Name'
      }, {
        field: 'event.source',
        title: 'Event Source'
      }, {
        field: 'event.desc',
        title: 'Event Description'
      }, {
        field: 'ec.subject',
        title: 'Event Subject'
      }, {
        field: 'ec.activity',
        title: 'Event Activity'
      }, {
        field: 'ec.theme',
        title: 'Event Theme'
      }, {
        field: 'ec.outcome',
        title: 'Event Outcome'
      }, {
        field: 'event.cat.title',
        title: 'Event Category Name'
      }, {
        field: 'device.group',
        title: 'Event Source Group'
      }, {
        field: 'event.class',
        title: 'Event Classification'
      }, {
        field: 'sql',
        title: 'Sql Query'
      }, {
        field: 'category',
        title: 'Category'
      }, {
        field: 'query',
        title: 'Query'
      }, {
        field: 'OS',
        title: 'Operating System'
      }, {
        field: 'browser',
        title: 'Browsers'
      }, {
        field: 'version',
        title: 'Versions'
      }, {
        field: 'policy.title',
        title: 'Policyname'
      }, {
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'lc.cid',
        title: 'Collector ID'
      }, {
        field: 'did',
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
        field: 'filetitle',
        title: 'Filename'
      }, {
        field: 'usertitle',
        title: 'User Account'
      }, {
        field: 'password',
        title: 'Password'
      }, {
        field: 'ssl.ca',
        title: 'SSL CA'
      }, {
        field: 'ssl.subject',
        title: 'SSL Subject'
      }, {
        field: 'error',
        title: 'Error'
      }, {
        field: 'query',
        title: 'Query'
      }, {
        field: 'directory',
        title: 'Directory'
      }, {
        field: 'browser',
        title: 'Browsers'
      }, {
        field: 'category',
        title: 'Category'
      }, {
        field: 'policy.title',
        title: 'Policyname'
      }, {
        field: 'device.type',
        title: 'Device Type'
      }, {
        field: 'device.ip',
        title: 'Device IP'
      }, {
        field: 'device.ipv6',
        title: 'Device IPv6'
      }, {
        field: 'device.host',
        title: 'Device Host'
      }, {
        field: 'device.class',
        title: 'Device Class'
      }, {
        field: 'paddr',
        title: 'Device Address'
      }, {
        field: 'device.title',
        title: 'Device Name'
      }, {
        field: 'event.source',
        title: 'Event Source'
      }, {
        field: 'event.desc',
        title: 'Event Description'
      }, {
        field: 'ec.subject',
        title: 'Event Subject'
      }, {
        field: 'ec.activity',
        title: 'Event Activity'
      }, {
        field: 'ec.theme',
        title: 'Event Theme'
      }, {
        field: 'ec.outcome',
        title: 'Event Outcome'
      }, {
        field: 'event.cat.title',
        title: 'Event Category Name'
      }, {
        field: 'device.group',
        title: 'Event Source Group'
      }, {
        field: 'event.class',
        title: 'Event Classification'
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
        title: 'Alert'
      }, {
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'lc.cid',
        title: 'Collector ID'
      }, {
        field: 'did',
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
        title: 'Checksum'
      }, {
        field: 'directory',
        title: 'Directory'
      }, {
        field: 'username',
        title: 'User Account'
      }, {
        field: 'task.name',
        title: 'Task Name'
      }, {
        field: 'owner',
        title: 'Owner'
      }, {
        field: 'domain',
        title: 'Domain'
      }, {
        field: 'dn',
        title: 'Domain OU'
      }, {
        field: 'OS',
        title: 'Operating System'
      }, {
        field: 'host.role',
        title: 'Host Role'
      }, {
        field: 'version',
        title: 'Versions'
      }, {
        field: 'client',
        title: 'Client Application'
      }, {
        field: 'cert.subject',
        title: 'Certificate Subject'
      }, {
        field: 'cert.common',
        title: 'Certificate Common Name'
      }, {
        field: 'cert.checksum',
        title: 'Certificate Checksum'
      }, {
        field: 'cert.ca',
        title: 'Certificate Authority'
      }, {
        field: 'bytes.src',
        title: 'Bytes Sent'
      }, {
        field: 'rbytes',
        title: 'Bytes Received'
      }
    ]),
    ootb: true
  }
];
