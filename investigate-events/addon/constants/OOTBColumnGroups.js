export const BASE_COLUMNS = [
  { field: 'time', title: 'Event Time', width: 100 },
  { field: 'medium', title: 'Event Type' },
  { field: 'custom.theme', title: 'Theme' },
  { field: 'size', title: 'Size' }
];

export const OOTBColumnGroups = [
  {
    name: 'Email Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'email',
        title: 'Destination E-mail Address'
      }, {
        field: 'subject',
        title: 'Subject'
      }, {
        field: 'attachment',
        title: 'Attachment'
      }, {
        field: 'usertitle',
        title: 'User Account'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'content',
        title: 'Content Type'
      }, {
        field: 'filetype',
        title: 'Forensic Fingerprint'
      }, {
        field: 'extension',
        title: 'Extension'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'action',
        title: 'Action Event'
      }, {
        field: 'client',
        title: 'Client Application'
      }, {
        field: 'server',
        title: 'Server Application'
      }, {
        field: 'filetitle',
        title: 'Filetitle'
      }, {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'tcp.dstport',
        title: 'TCP Destination Port'
      }, {
        field: 'alias.host',
        title: 'Hosttitle Alias Record'
      }, {
        field: 'user.src',
        title: 'Source User Account'
      }, {
        field: 'user.dst',
        title: 'Destination User Account'
      }, {
        field: 'email.src',
        title: 'Source E-mail Address'
      }, {
        field: 'email.dst',
        title: 'Destination E-mail Address'
      }, {
        field: 'org.src',
        title: 'Source Organization'
      }, {
        field: 'org.dst',
        title: 'Destination Organization'
      }, {
        field: 'error',
        title: 'Error'
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
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'did',
        title: 'Decoder Source'
      }, {
        field: 'ip.dstport',
        title: 'Destination Port'
      }, {
        field: 'orig_ip',
        title: 'Originating IP Address'
      }, {
        field: 'crypto',
        title: 'Crypto Key'
      }, {
        field: 'ssl.ca',
        title: 'SSL CA'
      }, {
        field: 'ssl.subject',
        title: 'SSL Subject'
      }, {
        field: 'alert',
        title: 'Alerts'
      }
    ]),
    ootb: true
  }, {
    name: 'Malware Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'filetype',
        title: 'Forensic Fingerprint'
      }, {
        field: 'extension',
        title: 'Extension'
      }, {
        field: 'referer',
        title: 'Referer'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'action',
        title: 'Action Event'
      }, {
        field: 'client',
        title: 'Client Application'
      }, {
        field: 'content',
        title: 'Content Type'
      }, {
        field: 'filetitle',
        title: 'Filetitle'
      }, {
        field: 'attachment',
        title: 'Attachment'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'sql',
        title: 'Sql Query'
      }, {
        field: 'directory',
        title: 'Directory'
      }, {
        field: 'server',
        title: 'Server Application'
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
        field: 'alias.host',
        title: 'Hosttitle Alias Record'
      }, {
        field: 'did',
        title: 'Decoder Source'
      }, {
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'usertitle',
        title: 'User Account'
      }
    ]),
    ootb: true
  }, {
    name: 'Threat Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'threat.category',
        title: 'Threat Category'
      }, {
        field: 'threat.source',
        title: 'Threat Source'
      }, {
        field: 'threat.desc',
        title: 'Threat Description'
      }, {
        field: 'alert',
        title: 'Alerts'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'service',
        title: 'Service Type'
      }, {
        field: 'tcp.dstport',
        title: 'TCP Destination Port'
      }, {
        field: 'alias.host',
        title: 'Hosttitle Alias Record'
      }, {
        field: 'action',
        title: 'Action Event'
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
        field: 'country.src',
        title: 'Source Country'
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
        field: 'usertitle',
        title: 'User Account'
      }, {
        field: 'password',
        title: 'Password'
      }, {
        field: 'sql',
        title: 'Sql Query'
      }, {
        field: 'ec.activity',
        title: 'Event Activity'
      }, {
        field: 'event.cat.title',
        title: 'Event Category title'
      }, {
        field: 'event.class',
        title: 'Event Classification'
      }, {
        field: 'event.desc',
        title: 'Event Description'
      }, {
        field: 'ec.outcome',
        title: 'Event Outcome'
      }, {
        field: 'event.source',
        title: 'Event Source'
      }, {
        field: 'device.group',
        title: 'Event Source Group'
      }, {
        field: 'ec.subject',
        title: 'Event Subject'
      }, {
        field: 'ec.theme',
        title: 'Event Theme'
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
        field: 'device.class',
        title: 'Device Class'
      }, {
        field: 'device.title',
        title: 'Device title'
      }, {
        field: 'device.host',
        title: 'Device Host'
      }, {
        field: 'alias.ip',
        title: 'IP Address Alias Record'
      }, {
        field: 'org.dst',
        title: 'Destination Organization'
      }, {
        field: 'org.src',
        title: 'Source Organization'
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
        field: 'orig_ip',
        title: 'Originating IP Address'
      }, {
        field: 'ip.dstport',
        title: 'Destination Port'
      }, {
        field: 'category',
        title: 'Category'
      }, {
        field: 'policy.title',
        title: 'Policy title'
      }, {
        field: 'version',
        title: 'Versions'
      }, {
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'did',
        title: 'Decoder Source'
      }, {
        field: 'lc.cid',
        title: 'Collector ID'
      }, {
        field: 'paddr',
        title: 'Device Address'
      }
    ]),
    ootb: true
  }, {
    name: 'Web Analysis',
    columns: BASE_COLUMNS.concat([
      {
        field: 'client',
        title: 'Client Application'
      }, {
        field: 'referer',
        title: 'Referer'
      }, {
        field: 'ip.dst',
        title: 'Destination IP Address'
      }, {
        field: 'ip.src',
        title: 'Source IP Address'
      }, {
        field: 'domain.dst',
        title: 'Destination Domain'
      }, {
        field: 'country.dst',
        title: 'Destination Country'
      }, {
        field: 'tcp.dstport',
        title: 'TCP Destination Port'
      }, {
        field: 'alias.host',
        title: 'Hosttitle Alias Record'
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
        field: 'content',
        title: 'Content Type'
      }, {
        field: 'filetitle',
        title: 'Filetitle'
      }, {
        field: 'directory',
        title: 'Directory'
      }, {
        field: 'country.src',
        title: 'Source Country'
      }, {
        field: 'action',
        title: 'Action Event'
      }, {
        field: 'server',
        title: 'Server Application'
      }, {
        field: 'ssl.ca',
        title: 'SSL CA'
      }, {
        field: 'ssl.subject',
        title: 'SSL Subject'
      }, {
        field: 'org.src',
        title: 'Source Organization'
      }, {
        field: 'org.dst',
        title: 'Destination Organization'
      }, {
        field: 'error',
        title: 'Error'
      }, {
        field: 'query',
        title: 'Query'
      }, {
        field: 'alert',
        title: 'Alert'
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
        field: 'usertitle',
        title: 'User Account'
      }, {
        field: 'password',
        title: 'Password'
      }, {
        field: 'browser',
        title: 'Browsers'
      }, {
        field: 'category',
        title: 'Category'
      }, {
        field: 'policy.title',
        title: 'Policy title'
      }, {
        field: 'did',
        title: 'Decoder Source'
      }, {
        field: 'lc.cid',
        title: 'Collector ID'
      }, {
        field: 'sourcefile',
        title: 'Source File'
      }, {
        field: 'event.cat.title',
        title: 'Event Category title'
      }, {
        field: 'event.class',
        title: 'Event Classification'
      }, {
        field: 'ec.activity',
        title: 'Event Activity'
      }, {
        field: 'event.source',
        title: 'Event Source'
      }, {
        field: 'ec.outcome',
        title: 'Event Outcome'
      }, {
        field: 'event.desc',
        title: 'Event Description'
      }, {
        field: 'ec.subject',
        title: 'Event Subject'
      }, {
        field: 'ec.theme',
        title: 'Event Theme'
      }, {
        field: 'device.group',
        title: 'Event Source Group'
      }, {
        field: 'device.type',
        title: 'Device Type'
      }, {
        field: 'device.title',
        title: 'Device title'
      }, {
        field: 'device.ip',
        title: 'Device IP'
      }, {
        field: 'device.host',
        title: 'Device Host'
      }, {
        field: 'device.ipv6',
        title: 'Device IPv6'
      }, {
        field: 'paddr',
        title: 'Device Address'
      }, {
        field: 'device.class',
        title: 'Device Class'
      }
    ]),
    ootb: true
  }
];
