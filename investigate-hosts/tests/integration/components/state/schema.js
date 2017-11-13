export default {
  schema: [
    {
      name: 'id',
      description: 'Agent Id',
      dataType: 'STRING',
      searchable: true,
      defaultProjection: true,
      wrapperType: 'STRING'
    },
    {
      name: 'machine.agentVersion',
      description: 'Agent Version',
      dataType: 'STRING',
      searchable: true,
      defaultProjection: true,
      wrapperType: 'STRING'
    },
    {
      name: 'machine.machineOsType',
      description: 'Operating System',
      dataType: 'STRING',
      values: [
        'windows',
        'linux',
        'mac'
      ],
      searchable: true,
      defaultProjection: true,
      wrapperType: 'STRING'
    },
    {
      name: 'machine.machineName',
      description: 'Machine Name',
      dataType: 'STRING',
      searchable: true,
      defaultProjection: true,
      wrapperType: 'STRING'
    },
    {
      name: 'machine.scanStartTime',
      description: 'Last Scan Time',
      dataType: 'DATE',
      searchable: true,
      defaultProjection: true,
      wrapperType: 'STRING'
    }
  ]

};