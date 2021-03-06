export default
[
  {
    'name': 'accurev',
    'prettyName': 'AccuRev',
    'description': 'FileCollection specification for eventsource type "Accurev" using file handler type "accurev"',
    'sourceDefaults': {
      'filePaths': [
        '/c/Program Files/Apache Group/Apache[2-9]/*.log',
        '/c/Program Files/Apache Group/Apache[2-9]/logs/*.log'
      ]
    }
  },
  {
    'name': 'apache',
    'prettyName': 'Apache Web Server',
    'description': 'FileCollection specification for eventsource type "Apache Web Server" using file handler type "APACHE"',
    'sourceDefaults': {
      'filePaths': [
        '/c/Program Files/Apache Group/Apache[2-9]/*.log',
        '/c/Program Files/Apache Group/Apache[2-9]/logs/*.log'
      ]
    }
  },
  {
    'name': 'exchange',
    'prettyName': 'Microsoft Exchange',
    'description': 'FileCollection specification for eventsource type "Exchange" using file handler type "EXCHANGE"',
    'sourceDefaults': {
      'filePaths': [
        '/c/Program Files/Apache Group/Exchange[2-9]/*.log',
        '/c/Program Files/Apache Group/Exchange[2-9]/logs/*.log'
      ]
    }
  }
];