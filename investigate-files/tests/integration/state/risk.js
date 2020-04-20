export default {
  isRiskScoreReset: true,
  activeRiskSeverityTab: 'critical',
  currentEntityId: '224425af5a52d9bc67517221cb08e649c7e7822b05a6733996c97384d1ce5c9d',
  riskScoreContext: {
    id: '224425af5a52d9bc67517221cb08e649c7e7822b05a6733996c97384d1ce5c9d',
    distinctAlertCount: {
      critical: 1,
      high: 0,
      medium: 0,
      low: 0
    },
    categorizedAlerts: {
      Critical: {
        'Blacklisted File': {
          alertCount: 1,
          eventCount: 1
        }
      }
    }
  },
  riskScoreContextError: null,
  eventContext: [
    {
      id: '5c503e734ef1b73d23bc9f84',
      sourceId: 'c01561d7-527b-45d8-a2a4-e9c3c2853b7e',
      source: 'Respond'
    }
  ],
  eventContextError: null,
  eventsData: [
    {
      agent_id: '',
      data: [
        {
          filename: 'vlc-cache-gen.exe',
          size: 41,
          hash: '224425af5a52d9bc67517221cb08e649c7e7822b05a6733996c97384d1ce5c9d'
        }
      ],
      destination: {
        path: '',
        file_SHA256: '',
        filename: '',
        launch_argument: '',
        device: {
        },
        user: {
          email_address: '',
          ad_username: '',
          ad_domain: '',
          username: ''
        },
        hash: ''
      },
      description: '',
      domain_src: '',
      device_type: 'nwendpoint',
      event_source: '10.40.14.101:50005',
      source: {
        path: '',
        file_SHA256: '',
        filename: '',
        launch_argument: '',
        user: {
          email_address: '',
          ad_username: '',
          ad_domain: '',
          username: ''
        },
        hash: ''
      },
      type: 'Endpoint',
      analysis_file: 'blacklisted file',
      enrichment: '',
      user_src: '',
      hostname: '',
      analysis_service: '',
      file: 'vlc-cache-gen.exe',
      detected_by: '-nwendpoint',
      process_vid: '',
      host_src: '',
      action: '',
      operating_system: '',
      alias_ip: '',
      from: '',
      timestamp: '2019-01-29T11:52:14.000+0000',
      event_source_id: '10811357',
      related_links: [
        {
          type: 'investigate_original_event',
          url: '/investigation/host/10.40.14.101:50005/navigate/event/AUTO/10811357'
        }
      ],
      port_dst: '',
      domain_dst: '',
      user_dst: '',
      host_dst: '',
      size: 41,
      domain: '',
      user_account: '',
      to: '',
      category: 'File',
      detector: {
        device_class: '',
        ip_address: '',
        product_name: 'nwendpoint'
      },
      user: '',
      analysis_session: '',
      username: '',
      indicatorId: '5c503e734ef1b73d23bc9f84',
      eventIndex: 0,
      id: '5c503e734ef1b73d23bc9f84:0'
    }
  ],
  eventsLoadingStatus: 'completed',
  alertsError: null,
  selectedAlert: 'Blacklisted File',
  expandedEventId: null,
  isRespondServerOffline: false,
  alertsLoadingStatus: 'completed'
};