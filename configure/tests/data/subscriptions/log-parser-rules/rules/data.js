export default [
  {
    'name': 'ipv4',
    'literals': [
      {
        'value': 'ipv4= '
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'ipv4',
          'index': '1'
        }
      ],
      'format': 'ipv4'
    },
    'ruleMetas': []
  },
  {
    'name': 'ipv6',
    'literals': [
      {
        'value': 'ipv6= '
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'ipv6',
          'index': '1'
        }
      ],
      'format': 'ipv6'
    },
    'ruleMetas': []
  }
];