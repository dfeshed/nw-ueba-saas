export default [
  {
    id: 'SUMMARY1',
    name: 'Summary1',
    metaGroup: {
      name: 'RSA Email Analysis'
    },
    columnGroupView: 'SUMMARY_VIEW',
    preQuery: 'service=24,25,109,110,995,143,220,993',
    contentType: 'OOTB'
  },
  {
    id: 'EMAIL',
    name: 'RSA Email Analysis',
    metaGroup: {
      name: 'RSA Email Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Email Analysis',
      id: 'EMAIL'
    },
    preQuery: 'service=24,25,109,110,995,143,220,993',
    contentType: 'OOTB'
  },
  {
    id: 'FILE',
    name: 'RSA File Analysis',
    metaGroup: {
      name: 'RSA Malware Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Malware Analysis',
      id: 'MALWARE'
    },
    preQuery: "filename exists || extension exists || filetype exists || sourcefile exists  || content = 'application/octet-stream'",
    contentType: 'OOTB'
  },
  {
    id: 'THREAT',
    name: 'RSA Threat Analysis',
    metaGroup: {
      name: 'RSA Threat Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Threat Analysis',
      id: 'THREAT'
    },
    preQuery: 'threat.desc exists || threat.source exists || threat.category exists || boc exists || ioc exists || eoc exists || alert exists',
    contentType: 'OOTB'
  },
  {
    id: 'WEB',
    name: 'RSA Web Analysis',
    metaGroup: {
      name: 'RSA Web Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Web Analysis',
      id: 'WEB'
    },
    preQuery: 'service=80,8080,443',
    contentType: 'OOTB'
  },
  {
    id: 'ENDPOINT',
    name: 'RSA Endpoint Analysis',
    metaGroup: {
      name: 'RSA Endpoint Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Endpoint Analysis',
      id: 'ENDPOINT'
    },
    preQuery: "device.type='nwendpoint'",
    contentType: 'OOTB'
  },
  {
    id: 'USER_ENTITY_BEHAVIOR',
    name: 'RSA User & Entity Behavior Analysis',
    metaGroup: {
      name: 'RSA User & Entity Behavior Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA User & Entity Behavior Analysis',
      id: 'USER_ENTITY_BEHAVIOR'
    },
    preQuery: 'user.dst exists || username exists',
    contentType: 'OOTB'
  }
];
