export default {
  subscriptionDestination: '/user/queue/investigate/languages',
  requestDestination: '/ws/investigate/languages',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

const data = [
  { count: 0, format: 'TimeT', metaName: 'time', flags: -2147482605, displayName: 'Time' },
  { count: 0, format: 'Text', metaName: 'risk.info', flags: -2147483133, displayName: 'Risk: Informational' },
  { count: 0, format: 'Text', metaName: 'risk.suspicious', flags: -2147483133, displayName: 'Risk: Suspicious' },
  { count: 0, format: 'Text', metaName: 'risk.warning', flags: -2147483133, displayName: 'Risk: Warning' },
  { count: 0, format: 'Text', metaName: 'threat.source', flags: -2147482621, displayName: 'Threat Source' },
  { count: 0, format: 'Text', metaName: 'threat.category', flags: -2147482621, displayName: 'Threat Category' },
  { count: 0, format: 'Text', metaName: 'threat.desc', flags: -2147482621, displayName: 'Threat Description' },
  { count: 0, format: 'UInt32', metaName: 'service', flags: -2147483053, displayName: 'Service Type' },
  { count: 0, format: 'Text', metaName: 'tld', flags: -2147482621, displayName: 'Top Level Domains' },
  { count: 0, format: 'Text', metaName: 'alias.host', flags: -2147482621, displayName: 'Hostname Aliases' },
  { count: 0, format: 'IPv4', metaName: 'ip.src', flags: -2147482605, displayName: 'Source IP Address' },
  { count: 0, format: 'IPv4', metaName: 'ip.dst', flags: -2147482605, displayName: 'Destination IP address' },
  { count: 0, format: 'IPv6', metaName: 'ipv6.src', flags: -2147482605, displayName: 'Source IPv6 Address' },
  { count: 0, format: 'IPv6', metaName: 'ipv6.dst', flags: -2147482605, displayName: 'Destination IPv6 address' },
  { count: 0, format: 'Text', metaName: 'action', flags: -2147482621, displayName: 'Action Event' },
  { count: 0, format: 'Text', metaName: 'username', flags: -2147482621, displayName: 'User Account' },
  { count: 0, format: 'Text', metaName: 'email', flags: -2147482621, displayName: 'E-mail Address' },
  { count: 0, format: 'Text', metaName: 'content', flags: -2147482621, displayName: 'Content Type' },
  { count: 0, format: 'Text', metaName: 'error', flags: -2147482621, displayName: 'Errors' },
  { count: 0, format: 'Text', metaName: 'extension', flags: -2147482621, displayName: 'Extension' },
  { count: 0, format: 'Text', metaName: 'filetype', flags: -2147482621, displayName: 'Forensic Fingerprint' },
  { count: 0, format: 'Text', metaName: 'attachment', flags: -2147482621, displayName: 'Attachment' },
  { count: 0, format: 'Text', metaName: 'filename', flags: -2147482878, displayName: 'Filename' },
  { count: 0, format: 'Text', metaName: 'directory', flags: -2147482878, displayName: 'Directory' },
  { count: 0, format: 'Text', metaName: 'client', flags: -2147482621, displayName: 'Client Application' },
  { count: 0, format: 'Text', metaName: 'server', flags: -2147482621, displayName: 'Server Application' },
  { count: 0, format: 'Text', metaName: 'OS', flags: -2147482621, displayName: 'Operating System' },
  { count: 0, format: 'Text', metaName: 'version', flags: -2147482621, displayName: 'Versions' },
  { count: 0, format: 'Text', metaName: 'browser', flags: -2147482621, displayName: 'Browsers' },
  { count: 0, format: 'Text', metaName: 'language', flags: -2147482621, displayName: 'Languages' },
  { count: 0, format: 'UInt16', metaName: 'tcp.srcport', flags: -2147482541, displayName: 'TCP Source Port' },
  { count: 0, format: 'UInt16', metaName: 'tcp.dstport', flags: -2147482541, displayName: 'TCP Destination Port' },
  { count: 0, format: 'UInt16', metaName: 'udp.srcport', flags: -2147482541, displayName: 'UDP Source Port' },
  { count: 0, format: 'UInt16', metaName: 'udp.dstport', flags: -2147482541, displayName: 'UDP Target Port' },
  { count: 0, format: 'Text', metaName: 'ad.username.src', flags: -2147482621, displayName: 'Active Directory Username Source' },
  { count: 0, format: 'Text', metaName: 'ad.username.dst', flags: -2147483391, displayName: 'Active Directory Username Destination' },
  { count: 0, format: 'Text', metaName: 'ad.computer.src', flags: -2147482621, displayName: 'Active Directory Workstation Source' },
  { count: 0, format: 'Text', metaName: 'ad.computer.dst', flags: -2147483391, displayName: 'Active Directory Workstation Destination' },
  { count: 0, format: 'Text', metaName: 'ad.domain.src', flags: -2147482621, displayName: 'Active Directory Domain Source' },
  { count: 0, format: 'Text', metaName: 'ad.domain.dst', flags: -2147483391, displayName: 'Active Directory Domain Destination' },
  { count: 0, format: 'Text', metaName: 'country.src', flags: -2147482605, displayName: 'Source Country' },
  { count: 0, format: 'Text', metaName: 'country.dst', flags: -2147482605, displayName: 'Destination Country' },
  { count: 0, format: 'Text', metaName: 'org.src', flags: -2147482605, displayName: 'Source Organization' },
  { count: 0, format: 'Text', metaName: 'org.dst', flags: -2147482605, displayName: 'Destination Organization' },
  { count: 0, format: 'Text', metaName: 'city.src', flags: -2147482605, displayName: 'Source City' },
  { count: 0, format: 'Text', metaName: 'city.dst', flags: -2147482605, displayName: 'Destination City' },
  { count: 0, format: 'Text', metaName: 'domain.src', flags: -2147482605, displayName: 'Source Domain' },
  { count: 0, format: 'Text', metaName: 'domain.dst', flags: -2147482605, displayName: 'Destination Domain' },
  { count: 0, format: 'Float32', metaName: 'latdec.src', flags: -2147483375, displayName: 'Source Latitude' },
  { count: 0, format: 'Float32', metaName: 'longdec.src', flags: -2147483375, displayName: 'Source Longitude' },
  { count: 0, format: 'Float32', metaName: 'latdec.dst', flags: -2147483375, displayName: 'Destination Latitude' },
  { count: 0, format: 'Float32', metaName: 'longdec.dst', flags: -2147483375, displayName: 'Destination Longitude' },
  { count: 0, format: 'UInt16', metaName: 'eth.type', flags: -2147482541, displayName: 'Ethernet Protocol' },
  { count: 0, format: 'UInt8', metaName: 'ip.proto', flags: -2147482541, displayName: 'IP Protocol' },
  { count: 0, format: 'UInt8', metaName: 'ipv6.proto', flags: -2147482605, displayName: 'IP V6 Protocol' },
  { count: 0, format: 'Text', metaName: 'password', flags: -2147482878, displayName: 'Password' },
  { count: 0, format: 'IPv4', metaName: 'ip.addr', flags: -2147482879, displayName: 'IP Address' },
  { count: 0, format: 'UInt16', metaName: 'ip.dstport', flags: -2147483391, displayName: 'Destination Port' },
  { count: 0, format: 'Text', metaName: 'user.src', flags: -2147483391, displayName: 'Source User Account' },
  { count: 0, format: 'Text', metaName: 'user.dst', flags: -2147482621, displayName: 'Destination User Account' },
  { count: 0, format: 'Text', metaName: 'virusname', flags: -2147483133, displayName: 'Virus Name' },
  { count: 0, format: 'Text', metaName: 'device.type', flags: -2147483134, displayName: 'Device Type' },
  { count: 0, format: 'IPv4', metaName: 'device.ip', flags: -2147483134, displayName: 'Device IP' },
  { count: 0, format: 'IPv6', metaName: 'device.ipv6', flags: -2147483134, displayName: 'Device IPv6' },
  { count: 0, format: 'Text', metaName: 'device.host', flags: -2147483134, displayName: 'Device Host' },
  { count: 0, format: 'Text', metaName: 'device.class', flags: -2147483133, displayName: 'Device Class' },
  { count: 0, format: 'IPv4', metaName: 'paddr', flags: -2147483134, displayName: 'Device Address' },
  { count: 0, format: 'Text', metaName: 'device.name', flags: -2147483134, displayName: 'Device Name' },
  { count: 0, format: 'Text', metaName: 'event.type', flags: -2147483134, displayName: 'Event Type' },
  { count: 0, format: 'Text', metaName: 'event.source', flags: -2147483134, displayName: 'Event Source' },
  { count: 0, format: 'Text', metaName: 'event.desc', flags: -2147483134, displayName: 'Event Description' },
  { count: 0, format: 'Text', metaName: 'ec.subject', flags: -2147483135, displayName: 'Event Subject' },
  { count: 0, format: 'Text', metaName: 'ec.activity', flags: -2147483135, displayName: 'Event Activity' },
  { count: 0, format: 'Text', metaName: 'ec.theme', flags: -2147483134, displayName: 'Event Theme' },
  { count: 0, format: 'Text', metaName: 'ec.outcome', flags: -2147483135, displayName: 'Event Outcome' },
  { count: 0, format: 'Text', metaName: 'event.cat.name', flags: -2147483135, displayName: 'Event Category Name' },
  { count: 0, format: 'Text', metaName: 'device.group', flags: -2147483133, displayName: 'Event Source Group' },
  { count: 0, format: 'Text', metaName: 'event.class', flags: -2147483133, displayName: 'Event Classification' },
  { count: 0, format: 'Text', metaName: 'parse.error', flags: -2147482879, displayName: 'Parse Error' },
  { count: 0, format: 'UInt8', metaName: 'medium', flags: -2147483309, displayName: 'Medium' },
  { count: 0, format: 'Text', metaName: 'reference.id', flags: -2147483135, displayName: 'Reference ID' },
  { count: 0, format: 'Text', metaName: 'msg', flags: -2147483135, displayName: 'Message' },
  { count: 0, format: 'Text', metaName: 'result.code', flags: -2147483135, displayName: 'Result Code' },
  { count: 0, format: 'Text', metaName: 'logon.type', flags: -2147483135, displayName: 'Logon Type' },
  { count: 0, format: 'Text', metaName: 'msg.id', flags: -2147483135, displayName: 'Message ID' },
  { count: 0, format: 'Text', metaName: 'process', flags: -2147483133, displayName: 'Process' },
  { count: 0, format: 'Text', metaName: 'obj.name', flags: -2147482877, displayName: 'Object Name' },
  { count: 0, format: 'Text', metaName: 'obj.type', flags: -2147483133, displayName: 'Object Type' },
  { count: 0, format: 'Text', metaName: 'email.src', flags: -2147482621, displayName: 'Source E-mail Address' },
  { count: 0, format: 'Text', metaName: 'email.dst', flags: -2147482621, displayName: 'Destination E-mail Address' },
  { count: 0, format: 'IPv4', metaName: 'tunnel.ip.src', flags: -2147483647, displayName: 'Tunnel Source IP Address' },
  { count: 0, format: 'IPv4', metaName: 'tunnel.ip.dst', flags: -2147483647, displayName: 'Tunnel Destination IP Address' },
  { count: 0, format: 'IPv6', metaName: 'tunnel.ipv6.src', flags: -2147483647, displayName: 'Tunnel Source IPv6 Address' },
  { count: 0, format: 'IPv6', metaName: 'tunnel.ipv6.dst', flags: -2147483647, displayName: 'Tunnel Destination IPv6 Address' }
];

