export const profiles = [
  {
    name: 'RSA Email Analysis',
    metaGroup: {
      name: 'RSA Email Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Email Analysis'
    },
    preQuery: 'service=24,25,109,110,995,143,220,993',
    contentType: 'OOTB'
  },
  {
    name: 'RSA File Analysis',
    metaGroup: {
      name: 'RSA Malware Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Malware Analysis'
    },
    preQuery: 'filename exists OR extension exists OR filetype exists OR sourcefile exists  OR content = \'application/octet-stream\'',
    contentType: 'OOTB'
  },
  {
    name: 'RSA Threat Analysis',
    metaGroup: {
      name: 'RSA Threat Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Threat Analysis'
    },
    preQuery: 'threat.desc exists OR threat.source exists OR threat.category exists OR boc exists OR ioc exists OR eoc exists OR alert exists',
    contentType: 'OOTB'
  },
  {
    name: 'RSA Web Analysis',
    metaGroup: {
      name: 'RSA Web Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Web Analysis'
    },
    preQuery: 'service=80,8080,443',
    contentType: 'OOTB'
  },
  {
    name: 'RSA Endpoint Analysis',
    metaGroup: {
      name: 'RSA Endpoint Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Endpoint Analysis'
    },
    preQuery: 'device.type=\'nwendpoint\'',
    contentType: 'OOTB'
  },
  {
    name: 'RSA User & Entity Behavior Analysis',
    metaGroup: {
      name: 'RSA User & Entity Behavior Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA User & Entity Behavior Analysis'
    },
    preQuery: 'user.dst exists OR username exists',
    contentType: 'OOTB'
  }
];

export default profiles;
