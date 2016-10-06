module.exports = {
  entityTypes: [
    {
      name: 'IP',
      enabled: true,
      metaKeys: [
        'ip.src',
        'ip.dst',
        'device.ip',
        'paddr',
        'ip.addr',
        'alias.ip'
      ]
    },
    {
      name: 'USER',
      enabled: true,
      metaKeys: [
        'user.src',
        'user.dst',
        'username'
      ]
    },
    {
      name: 'DOMAIN',
      enabled: true,
      metaKeys: [
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
        'alias.host',
        'device.host'
      ]
    }
  ]
};
