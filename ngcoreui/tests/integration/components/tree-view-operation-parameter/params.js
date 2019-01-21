const optionalBoolean = {
  name: 'payloadOnly',
  displayName: 'payloadOnly',
  type: 'boolean',
  optional: true,
  description: 'If true (default), will only overwrite the packet payload'
};

const optionalDateTime = {
  name: 'time1',
  displayName: 'time1',
  type: 'date-time',
  optional: true,
  description: 'The starting time for retrieving stats'
};

const optionalEnumAny = {
  name: 'source',
  displayName: 'source',
  type: 'enum-any',
  optional: true,
  acceptableValues: [
    {
      name: 'm',
      code: 'm'
    },
    {
      name: 'p',
      code: 'p'
    }
  ],
  description: 'The types of data to wipe, meta and/or packets, default is just packets'
};

const optionalEnumOne = {
  name: 'format',
  displayName: 'format',
  type: 'enum-one',
  optional: true,
  acceptableValues: [
    'default',
    'xml',
    'html'
  ],
  description: 'The format of the response, default returns in a human friendly format'
};

const optionalNumber = {
  name: 'compressionLevel',
  displayName: 'compressionLevel',
  type: 'number',
  optional: true,
  description: 'Determines the compression level, 0-9, where 1 is fastest, 9 is the best compression and zero is a predetermined balance between speed and compression'
};

const optionalText = {
  name: 'msg',
  displayName: 'msg',
  type: 'text',
  optional: true,
  description: 'The name of the message to retrieve detailed help about (aliases are \'m\' or \'message\')'
};

const optionalUnknownType = {
  name: 'size',
  displayName: 'size',
  type: '<size>',
  optional: true,
  description: 'The amount of data to write for each test'
};

export {
  optionalBoolean,
  optionalDateTime,
  optionalEnumAny,
  optionalEnumOne,
  optionalNumber,
  optionalText,
  optionalUnknownType
};
