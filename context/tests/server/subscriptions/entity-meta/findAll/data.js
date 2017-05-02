export default [
  {
    name: 'IP',
    enabled: true,
    metaKeys: [
      'ip.src',
      'ip.dst',
      'device.ip',
      'paddr',
      'ip.addr',
      'alias.ip',
      'groupby_destination_ip',
      'groupby_detector_ip',
      'groupby_source_ip'
    ]
  },
  {
    name: 'USER',
    enabled: true,
    metaKeys: [
      'user.src',
      'user.dst',
      'username',
      'groupby_source_username'
    ]
  },
  {
    name: 'DOMAIN',
    enabled: true,
    metaKeys: [
      'domain.src',
      'domain.dst',
      'groupby_domain'
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
      'sourcefile',
      'groupby_filename'
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
];
