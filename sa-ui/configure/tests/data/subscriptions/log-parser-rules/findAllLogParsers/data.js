export default [
  {
    '_id': '5a8c578ee4b0f72441e1941b',
    '_class': 'com.rsa.smc.sa.esm.domain.bean.parser.LogDeviceParser',
    'name': 'builtin',
    'parserFiles': [
      {
        '_id': '5a8c578ee4b0f72441e1941c',
        'name': 'builtinmsg',
        'parserRules': [
          {
            '_id': '5a8c578ee4b0f72441e1941d',
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
            '_id': '5a8c578ee4b0f72441e1941e',
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
          },
          {
            '_id': '5a8c578ee4b0f72441e1941f',
            'name': 'mac',
            'literals': [
              {
                'value': 'mac= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'mac',
                  'index': '1'
                }
              ],
              'format': 'mac'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19420',
            'name': 'uint8',
            'literals': [
              {
                'value': 'uint8= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'uint8',
                  'index': '1'
                }
              ],
              'format': 'uint8'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19421',
            'name': 'uint16',
            'literals': [
              {
                'value': 'uint16= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'uint16',
                  'index': '1'
                }
              ],
              'format': 'uint16'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19422',
            'name': 'uint32',
            'literals': [
              {
                'value': 'uint32= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'uint32',
                  'index': '1'
                }
              ],
              'format': 'uint32'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19423',
            'name': 'uint64',
            'literals': [
              {
                'value': 'uint64= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'uint64',
                  'index': '1'
                }
              ],
              'format': 'uint64'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19424',
            'name': 'uint128',
            'literals': [
              {
                'value': 'uint128= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'uint128',
                  'index': '1'
                }
              ],
              'format': 'uint128'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19425',
            'name': 'int16',
            'literals': [
              {
                'value': 'int16= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'int16',
                  'index': '1'
                }
              ],
              'format': 'int16'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19426',
            'name': 'int32',
            'literals': [
              {
                'value': 'int32= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'int32',
                  'index': '1'
                }
              ],
              'format': 'int32'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19427',
            'name': 'int64',
            'literals': [
              {
                'value': 'int64= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'int64',
                  'index': '1'
                }
              ],
              'format': 'int64'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19428',
            'name': 'float32',
            'literals': [
              {
                'value': 'float32= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'float32',
                  'index': '1'
                }
              ],
              'format': 'float32'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e19429',
            'name': 'float64',
            'literals': [
              {
                'value': 'float64= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'float64',
                  'index': '1'
                }
              ],
              'format': 'float64'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e1942a',
            'name': 'email',
            'literals': [
              {
                'value': 'email= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'email',
                  'index': '1'
                }
              ],
              'format': 'email'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e1942b',
            'name': 'uri',
            'literals': [
              {
                'value': 'uri= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'uri',
                  'index': '1'
                }
              ],
              'format': 'uri'
            },
            'ruleMetas': []
          },
          {
            '_id': '5a8c578ee4b0f72441e1942c',
            'name': 'domain',
            'literals': [
              {
                'value': 'domain= '
              }
            ],
            'pattern': {
              'captures': [
                {
                  'key': 'domain',
                  'index': '1'
                }
              ],
              'format': 'domain'
            },
            'ruleMetas': []
          }
        ]
      }
    ]
  }
];