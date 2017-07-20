/* eslint-env node */

// For each endpoint, maps entity types to event properties (i.e., meta keys).
module.exports = {
  IM: [ // endpoint = NetWitness Incident Management module
    {
      name: 'IP',
      enabled: true,
      metaKeys: [
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
      ]
    },
    {
      name: 'USER',
      enabled: true,
      metaKeys: [
        'user_src',
        'user_dst',
        'username'
      ]
    },
    {
      name: 'DOMAIN',
      enabled: true,
      metaKeys: [
        'domain',
        'domain_src',
        'domain_dst',
        'full_domain',
        'dns_domain'
      ]
    },
    {
      name: 'MAC_ADDRESS',
      enabled: true,
      metaKeys: [
        'alias_mac',
        'eth_src',
        'eth_dst',
        'mac_address'
      ]
    },
    {
      name: 'FILE_NAME',
      enabled: true,
      metaKeys: [
        'file',
        'filename',
        'sourcefile'
      ]
    },
    {
      name: 'FILE_HASH',
      enabled: true,
      metaKeys: [
        'checksum',
        'hash'
      ]
    },
    {
      name: 'HOST',
      enabled: true,
      metaKeys: [
        'alias_host',
        'device_host',
        'dns_hostname'
      ]
    }
  ]
};
