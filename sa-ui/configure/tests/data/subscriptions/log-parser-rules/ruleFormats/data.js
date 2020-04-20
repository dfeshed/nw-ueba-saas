export default [
  {
    name: 'Regex Pattern',
    pattern: '',
    matches: 'This matches Regex',
    type: 'regex'
  },
  {
    name: 'IPV4 Address',
    pattern: '(?:[0-9]{1,3}\\.){3}[0-9]{1,3}',
    matches: 'This matches IPV4 addresses',
    type: 'ipv4'
  },
  {
    name: 'IPV6 Address',
    pattern: '((([0-9A-Fa-f]{1,4}:){1,6}:)|(([0-9A-Fa-f]{1,4}:){7}))([0-9A-Fa-f]{1,4})|::1|::0',
    matches: 'This matches IPV6 addresses',
    type: 'ipv6'
  },
  {
    name: 'MAC Address',
    pattern: '([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})',
    matches: 'This matches MAC addresses',
    type: 'mac'
  },
  {
    name: 'Email Address',
    pattern: '(([^<>()\\[\\]\\\\.,;:\\s@"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@"]+)*)|(".+"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))',
    matches: 'This matches Email addresses',
    type: 'email'
  },
  {
    name: 'URI Address',
    pattern: '(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:\\/?#[\\]@!\\$&\'\\(\\)\\*\\+,;=.]+',
    matches: 'This matches URI addresses',
    type: 'uri'
  },
  {
    name: 'Hostname',
    pattern: '\\S*\\b',
    matches: 'This matches Hostname',
    type: 'hostname'
  },
  {
    name: 'Unsigned 8-bit Integer',
    pattern: '\\b(?:1\\d{2}|2[0-4]\\d|[1-9]?\\d|25[0-5])\\b',
    matches: 'This matches unsigned 8-bit integer',
    type: 'uint8'
  },
  {
    name: 'Unsigned 16-bit Integer',
    pattern: '\\d{1,5}\\b',
    matches: 'This matches unsigned 16-bit integer',
    type: 'uint16'
  },
  {
    name: 'Unsigned 32-bit Integer',
    pattern: '\\d{1,10}\\b',
    matches: 'This matches unsigned 32-bit integer',
    type: 'uint32'
  },
  {
    name: 'Unsigned 64-bit Integer',
    pattern: '\\d{1,20}\\b',
    matches: 'This matches unsigned 64-bit integer',
    type: 'uint64'
  },
  {
    name: 'Unsigned 128-bit Integer',
    pattern: '\\d{1,39}\\b',
    matches: 'This matches unsigned 128-bit integer',
    type: 'uint128'
  },
  {
    name: 'Signed 16-bit integer',
    pattern: '([\\-](\\d{1,5})|\\b(\\d{1,5}))\\b',
    matches: 'This matches signed 16-bit integer',
    type: 'int16'
  },
  {
    name: 'Signed 32-bit Integer',
    pattern: '([\\-](\\d{1,10})|\\b(\\d{1,10}))\\b',
    matches: 'This matches signed 32-bit integer',
    type: 'int32'
  },
  {
    name: 'Signed 64-bit Integer',
    pattern: '([\\-](\\d{1,19})|\\b(\\d{1,19}))\\b',
    matches: 'This matches signed 64-bit integer',
    type: 'int64'
  },
  {
    name: 'Decimal Number',
    pattern: '\\d+\\.\\d+',
    matches: 'This matches decimal numbers',
    type: 'float32'
  },
  {
    name: 'Decimal Number',
    pattern: '\\d+\\.\\d+',
    matches: 'This matches decimal numbers',
    type: 'float64'
  }
];
