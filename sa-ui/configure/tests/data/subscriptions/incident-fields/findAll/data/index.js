export default [
  {
    'name': 'Alert Name',
    'value': 'alert.name',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': null
  },
  {
    'name': 'Alert Rule Id',
    'value': 'alert.signature_id',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': null
  },
  {
    'name': 'Alert Type',
    'value': 'alert.type',
    'type': 'combobox',
    'operators': [
      0,
      1
    ],
    'list': [
      {
        'name': 'Log',
        'value': 'Log'
      },
      {
        'name': 'Network session',
        'value': 'Network'
      },
      {
        'name': 'Correlation',
        'value': 'Correlation'
      },
      {
        'name': 'Manual Upload',
        'value': 'Manual Upload'
      },
      {
        'name': 'Resubmit',
        'value': 'Resubmit'
      },
      {
        'name': 'On Demand',
        'value': 'On Demand'
      },
      {
        'name': 'File Share',
        'value': 'File Share'
      },
      {
        'name': 'Instant IOC',
        'value': 'Instant IOC'
      },
      {
        'name': 'Unknown',
        'value': 'Unknown'
      }
    ],
    'groupBy': true,
    'groupByField': 'alert.groupby_type'
  },
  {
    'name': 'Date Created',
    'value': 'alert.timestamp',
    'type': 'datefield',
    'operators': [
      0,
      1,
      6,
      7
    ],
    'list': null,
    'groupBy': true,
    'groupByField': null
  },
  {
    'name': 'Destination Country',
    'value': 'alert.destination_country',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_destination_country'
  },
  {
    'name': 'Destination IP Address',
    'value': 'alert.events.destination.device.ip_address',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_destination_ip'
  },
  {
    'name': 'Destination Port',
    'value': 'alert.events.destination.device.port',
    'type': 'numberfield',
    'operators': [
      0,
      1,
      2,
      3,
      4,
      5
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_destination_port'
  },
  {
    'name': 'Detector IP Address',
    'value': 'alert.events.detector.ip_address',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_detector_ip'
  },
  {
    'name': 'Domain',
    'value': 'alert.events.domain',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_domain'
  },
  {
    'name': 'Domain for Suspected C&C',
    'value': 'alert.events.domain',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_c2domain'
  },
  {
    'name': 'Filename',
    'value': 'alert.events.data.filename',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_filename'
  },
  {
    'name': 'File MD5 Hash',
    'value': 'alert.events.data.hash',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_data_hash'
  },
  {
    'name': 'Risk Score',
    'value': 'alert.risk_score',
    'type': 'numberfield',
    'operators': [
      0,
      1,
      2,
      3,
      4,
      5
    ],
    'list': null,
    'groupBy': true,
    'groupByField': null
  },
  {
    'name': 'Severity',
    'value': 'alert.severity',
    'type': 'numberfield',
    'operators': [
      0,
      1,
      2,
      3,
      4,
      5
    ],
    'list': null,
    'groupBy': true,
    'groupByField': null
  },
  {
    'name': 'Source',
    'value': 'alert.source',
    'type': 'combobox',
    'operators': [
      0,
      1
    ],
    'list': [
      {
        'name': 'Event Stream Analysis',
        'value': 'Event Stream Analysis'
      },
      {
        'name': 'Malware Analysis',
        'value': 'Malware Analysis'
      },
      {
        'name': 'NetWitness Endpoint',
        'value': 'ECAT'
      },
      {
        'name': 'Reporting Engine',
        'value': 'Reporting Engine'
      },
      {
        'name': 'Security Analytics Investigator',
        'value': 'Security Analytics Investigator'
      },
      {
        'name': 'Web Threat Detection',
        'value': 'Web Threat Detection'
      }
    ],
    'groupBy': true,
    'groupByField': null
  },
  {
    'name': 'Source Country',
    'value': 'alert.source_country',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_source_country'
  },
  {
    'name': 'Source IP Address',
    'value': 'alert.events.source.device.ip_address',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_source_ip'
  },
  {
    'name': 'Source Username',
    'value': 'alert.events.source.user.username',
    'type': 'textfield',
    'operators': [
      0,
      1,
      8,
      9,
      10,
      11,
      12,
      13
    ],
    'list': null,
    'groupBy': true,
    'groupByField': 'alert.groupby_source_username'
  }
];