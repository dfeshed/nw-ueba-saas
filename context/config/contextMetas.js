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
      'alias_ip',
      'ip_all'
    ],
    USER: [
      'user_src',
      'user_dst',
      'username',
      'user_all'
    ],
    DOMAIN: [
      'domain_src',
      'domain_dst',
      'full_domain',
      'dns_domain',
      'domain_all'
    ],
    'MAC_ADDRESS': [
      'alias_mac',
      'eth_src',
      'eth_dst',
      'mac_address',
      'eth_all'
    ],
    'FILE_NAME': [
      'file',
      'filename',
      'sourcefile',
      'filename_src',
      'filename_dst',
      'filename_all'
    ],
    'FILE_HASH': [
      'checksum',
      'hash',
      'checksum_src',
      'checksum_dst',
      'checksum_all'
    ],
    HOST: [
      'domain',
      'alias_host',
      'device_host',
      'dns_hostname',
      'host_all'
    ]
  }
};
