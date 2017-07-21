/* eslint-env node */

// For each endpoint, maps entity types to event properties.
module.exports = {
  IM: { // endpoint = NetWitness Incident Management module
    IP: [
      'ip_src',
      'ip_dst',
      'device_ip',
      'paddr',
      'sourceIp',
      'destinationIp',
      'ip_addr',
      'ip_address',
      'ip_source',
      'alias_ip'
    ],
    USER: [
      'user_src',
      'user_dst',
      'username'
    ],
    DOMAIN: [
      'domain_src',
      'domain_dst',
      'full_domain',
      'dns_domain'
    ],
    'MAC_ADDRESS': [
      'alias_mac',
      'eth_src',
      'eth_dst',
      'mac_address'
    ],
    'FILE_NAME': [
      'file',
      'filename',
      'sourcefile'
    ],
    'FILE_HASH': [
      'checksum',
      'hash'
    ],
    HOST: [
      'domain',
      'alias_host',
      'device_host',
      'dns_hostname'
    ]
  }
};
