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
        'ip.dst',
        'device.ip',
        'paddr',
        'ip.addr',
        'alias.ip',
        'sourceIp',
        'destinationIp'
      ]
    },
    {
      name: 'USER',
      enabled: true,
      metaKeys: [
        'user',
        'user.src',
        'user.dst',
        'username'
      ]
    },
    {
      name: 'DOMAIN',
      enabled: true,
      metaKeys: [
        'domain',
        'domain.src',
        'domain.dst'
      ]
    },
    {
      name: 'MAC_ADDRESS',
      enabled: true,
      metaKeys: [
        'alias.mac',
        'eth.src',
        'eth.dst'
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
        'checksum'
      ]
    },
    {
      name: 'HOST',
      enabled: true,
      metaKeys: [
        'host',
        'alias.host',
        'device.host'
      ]
    }
  ]
};
