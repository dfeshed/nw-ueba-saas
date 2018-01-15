export default {
  selectedEmailServer: 'my-favorite-server',
  emailServers: [
    {
      id: 'my-email-server',
      name: 'My Email Server',
      description: 'My email server description',
      enabled: true
    },
    {
      id: 'my-favorite-server',
      name: 'My Favorite Server',
      description: 'My favorite email server description',
      enabled: true
    },
    {
      id: 'my-disbled-server',
      name: 'My Disabled Server',
      description: 'My disbled email server description',
      enabled: false
    }
  ],
  socManagers: ['soc@rsa.com'],
  notificationSettings: [
    {
      type: 'incident-created',
      sendToAssignee: true,
      sendToSocManagers: false
    },
    {
      type: 'incident-state-changed',
      sendToAssignee: false,
      sendToSocManagers: true
    }
  ]
};
