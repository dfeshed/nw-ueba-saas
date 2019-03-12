const STANDARD = {
  path: '/',
  operations: [
    {
      name: 'ls',
      params: [
        {
          name: 'depth',
          displayName: 'depth',
          type: 'number',
          optional: true
        },
        {
          name: 'options',
          displayName: 'options',
          type: 'text',
          optional: true
        },
        {
          name: 'exclude',
          displayName: 'exclude',
          type: 'text',
          optional: true
        }
      ],
      description: 'Test operation help text\nsecurity.roles: sys.manage\n'
    },
    {
      name: 'mon',
      params: [
        {
          name: 'depth',
          displayName: 'depth',
          type: 'number',
          optional: true
        }
      ],
      description: 'Test operation help text\nsecurity.roles: everyone\n'
    },
    {
      name: 'info',
      params: []
    },
    {
      name: 'help',
      params: [
        {
          name: 'msg',
          displayName: 'msg',
          type: 'text',
          optional: true,
          description: 'The name of the message to retrieve detailed help about (aliases are \'m\' or \'message\')'
        },
        {
          name: 'op',
          displayName: 'op',
          type: 'enum-one',
          optional: true,
          acceptableValues: [
            'messages',
            'parameters',
            'description',
            'values',
            'roles',
            'extra',
            'manual'
          ],
          description: 'The specific help operation to perform (e.g., op=manual would return a man page on this node or the specified message)'
        },
        {
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
        }
      ],
      description: 'Test operation help text\nsecurity.roles: sys.manage,logs.manage\n'
    },
    {
      name: 'count',
      params: []
    },
    {
      name: 'stopMon',
      params: []
    }
  ],
  nodes: [
    {
      path: '/collections',
      name: 'collections',
      handle: 122,
      parentHandle: 1,
      nodeType: 299067162755072,
      display: ''
    },
    {
      path: '/connections',
      name: 'connections',
      handle: 17,
      parentHandle: 1,
      nodeType: 1706442046308352,
      display: ''
    },
    {
      path: '/database',
      name: 'database',
      handle: 186,
      parentHandle: 1,
      nodeType: 299067162755072,
      display: ''
    },
    {
      path: '/decoder',
      name: 'decoder',
      handle: 267,
      parentHandle: 1,
      nodeType: 299067162755072,
      display: ''
    } // ...and so on
  ],
  description: 'A container node for other node types'
};

export {
  STANDARD
};