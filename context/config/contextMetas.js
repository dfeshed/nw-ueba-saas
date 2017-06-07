/* eslint-env node */

// For each endpoint, maps entity types to event properties (i.e., meta keys).
module.exports = {
  IM: [ // endpoint = NetWitness Incident Management module
    {
      name: 'IP',
      enabled: true,
      metaKeys: [
        'ip',
        'ip.src',
        'ip_src',
        'ip_dst',
        'ip.dst',
        'device.ip',
        'device_ip',
        'paddr',
        'ip.addr',
        'sourceIp',
        'destinationIp',
        'ip_addr',
        'ip_address',
        'ip_source',
        'alias.ip',
        'alias_ip'
      ]
    },
    {
      name: 'USER',
      enabled: true,
      metaKeys: [
        'user',
        'user.src',
        'user_src',
        'user.dst',
        'user_dst',
        'username'
      ]
    },
    {
      name: 'DOMAIN',
      enabled: true,
      metaKeys: [
        'domain',
        'domain.src',
        'domain_src',
        'domain.dst',
        'domain_dst',
        'full_domain',
        'dns_domain'
      ]
    },
    {
      name: 'MAC_ADDRESS',
      enabled: true,
      metaKeys: [
        'alias.mac',
        'alias_mac',
        'eth.src',
        'eth_src',
        'eth.dst',
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
        'host',
        'alias.host',
        'alias_host',
        'device.host',
        'device_host',
        'dns_hostname'
      ]
    }
  ]
};
