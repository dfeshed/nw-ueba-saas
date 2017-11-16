/* eslint-env node */
export default {
  'packageConfig': {
    'id': '59894c9984518a5cfb8fbec2',
    'server': '10.101.34.245',
    'port': 443,
    'becon': 0.0,
    'forceOverwrite': false,
    'serviceName': 'NWE Agent',
    'displayName': 'NWE Agent',
    'description': 'NetWitness Agent Service',
    'certificateValidation': 'thumbprint'
  },
  'logCollectionConfig': {

  },
  'listOfService': [
    {
      'id-1': {
        'name': 'test',
        'ip': '10.12.12.12',
        'device': 'log decoder'
      }
    },
    {
      'id-2': {
        'name': 'asd',
        'ip': '10.12.12.10',
        'device': 'log collector'
      }
    }]
};
