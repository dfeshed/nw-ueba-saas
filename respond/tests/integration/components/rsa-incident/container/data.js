import _ from 'lodash';

const reconState = {
  serviceData: {
    '555d9a6fe4b0d37c827d402d': {
      id: '555d9a6fe4b0d37c827d402d',
      displayName: 'loki-concentrator',
      name: 'CONCENTRATOR',
      version: '11.2.0.0',
      host: '10.4.61.33',
      port: 56005
    }
  },
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined
};

const incidentState = {
  id: 'INC-8858',
  info: {
    id: 'INC-8858',
    name: 'Suspected C&C with mail.google.com',
    summary: 'Security Analytics detected communications with mail.google.com that may be command and control malware.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review the domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigation module to locate other activity to or from it.',
    priority: 'HIGH',
    prioritySort: 2,
    riskScore: 80,
    status: 'NEW',
    statusSort: 0,
    alertCount: 109,
    averageAlertRiskScore: 80,
    sealed: false,
    totalRemediationTaskCount: 0,
    openRemediationTaskCount: 0,
    hasRemediationTasks: false,
    created: 1526415248636,
    lastUpdated: 1526913805381,
    lastUpdatedByUser: null,
    assignee: null,
    sources: [
      'Event Stream Analysis'
    ],
    ruleId: '5afaf1622de0807a9f6daaa0',
    firstAlertTime: 1526415060394,
    timeWindowExpiration: 1527019860394,
    groupByValues: [
      'mail.google.com'
    ],
    categories: [],
    notes: null,
    createdBy: 'Suspected Command & Control Communication By Domain',
    dateIndicatorAggregationStart: 1525551060394,
    breachExportStatus: 'NONE',
    breachData: null,
    breachTag: null,
    hasDeletedAlerts: false,
    deletedAlertCount: 0,
    groupByDomain: null,
    enrichment: null,
    eventCount: 109,
    groupBySourceIp: [
      '161.253.143.232',
      '161.253.57.42',
      '161.253.40.218',
      '161.253.9.21',
      '128.164.75.230',
      '161.253.33.88',
      '161.253.20.70',
      '128.164.215.8',
      '128.164.241.229',
      '161.253.6.186',
      '128.164.157.187',
      '128.164.35.181',
      '161.253.7.212',
      '161.253.53.123',
      '161.253.50.112',
      '128.164.29.61',
      '161.253.26.42',
      '161.253.25.0',
      '128.164.240.9',
      '161.253.17.156',
      '161.253.32.208',
      '161.253.13.248',
      '128.164.218.253',
      '161.253.40.60',
      '161.253.23.166',
      '161.253.7.175',
      '128.164.215.200',
      '161.253.31.37',
      '161.253.9.15',
      '161.253.8.31',
      '161.253.30.72',
      '128.164.214.106',
      '161.253.15.138',
      '161.253.13.164',
      '161.253.46.253',
      '161.253.30.55',
      '161.253.24.250',
      '161.253.18.129',
      '161.253.6.141',
      '128.164.34.232',
      '128.164.83.225',
      '161.253.11.113',
      '161.253.8.30',
      '128.164.160.66',
      '161.253.23.203',
      '128.164.232.25',
      '161.253.5.171',
      '161.253.46.172',
      '128.164.33.124',
      '161.253.21.241',
      '128.164.159.150',
      '161.253.10.36',
      '161.253.14.74',
      '128.164.81.224',
      '161.253.32.118',
      '161.253.20.89',
      '161.253.16.156',
      '128.164.184.212',
      '161.253.7.98',
      '64.233.185.19',
      '128.164.235.89',
      '161.253.22.207',
      '161.253.5.134',
      '161.253.18.60',
      '161.253.21.225',
      '161.253.20.252',
      '128.164.184.192',
      '161.253.21.131',
      '161.253.40.33',
      '161.253.9.25',
      '161.253.46.81',
      '161.253.14.60',
      '161.253.23.107',
      '128.164.240.153',
      '128.164.31.183',
      '161.253.23.31',
      '128.164.233.250',
      '161.253.15.176',
      '161.253.28.156',
      '128.164.241.197',
      '128.164.76.212',
      '161.253.53.21',
      '161.253.31.139',
      '161.253.35.160',
      '128.164.33.55',
      '128.164.124.168',
      '161.253.22.2',
      '161.253.46.170',
      '161.253.41.213',
      '161.253.8.142',
      '128.164.234.183',
      '128.164.234.54',
      '128.164.241.12',
      '161.253.20.153'
    ],
    groupByDestinationIp: [
      '72.14.205.17',
      '66.249.83.19',
      '66.249.83.83',
      '64.233.185.83',
      '64.233.185.19',
      '161.253.48.56'
    ],
    escalationStatus: 'NON_ESCALATED',
    createdFromRule: true
  },
  infoStatus: 'completed',
  viewMode: 'storyline',
  inspectorWidth: 400,
  selection: {
    type: '',
    ids: []
  },
  hideViz: true,
  tasks: [],
  tasksStatus: 'completed',
  isShowingTasksAndJournal: false,
  tasksJournalMode: 'journal',
  searchEntity: null,
  searchTimeFrameName: null,
  searchStatus: null,
  searchResults: null,
  defaultSearchTimeFrameName: 'LAST_TWENTY_FOUR_HOURS',
  defaultSearchEntityType: 'IP',
  addRelatedIndicatorsStatus: null,
  visibleEntityTypes: [
    'domain',
    'user',
    'file_name',
    'file_hash',
    'ip',
    'mac_address'
  ]
};

const storylineState = {
  id: 'INC-8858',
  storyline: [
  ],
  storylineStatus: 'completed',
  stopStorylineStream: null,
  storylineEvents: [
  ],
  storylineEventsBuffer: [],
  storylineEventsBufferMax: 50,
  storylineEventsStatus: 'completed'
};

const enrichmentState = {
  'http-packet': {
    c2: {
      referer: {
        score: 2.5174987194382777e-7,
        cardinality: 100,
        num_events: 250
      },
      newdomain: {
        score: 99.98379760902546,
        age: 14000,
        num_events: 341
      },
      command_control: {
        confidence: 45.283018867924525,
        'weighted_http-packet_c2_referer_score': 6.041996926651867e-8,
        aggregate: 2.5174987194382777e-7
      },
      normalized: {
        full_domain: 'mail.google.com',
        srcip_full_domain: '161.253.40.218_mail.google.com',
        domain: 'google.com',
        user_agent: 'Mozilla/5.0'
      },
      useragent: {
        score: 2.5174987194382777e-7,
        cardinality: 100,
        num_events: 1345
      },
      beaconing: {
        score: 0,
        period: 0
      },
      ua: {
        score: 2.5174987194382777e-7,
        cardinality: 100,
        num_events: 250
      },
      smooth: {
        score: 0
      }
    }
  }
};

export const generateRecon = (port) => {
  const state = Object.assign({}, reconState);
  const portValue = port ? port : 56005;
  const devices = _.mapValues(state.serviceData, (device) => {
    const firstConcentrator = '555d9a6fe4b0d37c827d402d';
    if (device.id === firstConcentrator) {
      return _.defaults({
        port: portValue
      }, device);
    }
    return device;
  });
  return {
    ...state,
    serviceData: { ...state.serviceData, ...devices }
  };
};

export const generateStoryline = ({ withEnrichment, withEventSourceId, withSslEventSource }) => {
  const state = Object.assign({}, storylineState);
  const storylineIds = [
    '5afb3edc2de080511717841a',
    '5afb3edc2de080511717842b',
    '5afb3edc2de080511717843c',
    '5afb3edc2de080511717844d',
    '5afb3edc2de080511717845e',
    '5afb3edc2de080511717846f',
    '5afb3edc2de080511717847g'
  ];
  const eventSourceIdValue = withEventSourceId === undefined ? true : withEventSourceId;
  const eventSourceId = eventSourceIdValue ? '150' : null;

  const withSslEventSourceValue = withSslEventSource === undefined ? true : withSslEventSource;
  const eventSource = withSslEventSourceValue ? '10.4.61.33:56005' : '10.4.61.33:50005';

  const enrichment = withEnrichment ? Object.assign({}, enrichmentState) : '';
  const storylineEvents = storylineIds.map((id, index) => {
    return {
      indicatorId: id,
      events: [
        {
          data: [
            {
              filename: '<none>',
              size: 1455,
              hash: ''
            }
          ],
          destination: {
            device: {
              compliance_rating: '',
              netbios_name: '',
              port: 80,
              mac_address: '00:13:c3:3b:c7:00',
              criticality: '',
              asset_type: '',
              ip_address: '66.249.83.83',
              facility: '',
              business_unit: '',
              geolocation: {
                country: 'United States',
                city: '',
                latitude: null,
                organization: 'Google',
                domain: 'google.com',
                longitude: null
              }
            },
            user: {
              email_address: '',
              ad_username: '',
              ad_domain: '',
              username: ''
            }
          },
          description: '',
          domain_src: '',
          event_source: index > 2 ? '' : eventSource,
          source: {
            device: {
              compliance_rating: '',
              netbios_name: '',
              port: 2045,
              mac_address: '00:13:c3:3b:be:00',
              criticality: '',
              asset_type: '',
              ip_address: '161.253.40.218',
              facility: '',
              business_unit: '',
              geolocation: {
                country: 'United States',
                city: '',
                latitude: null,
                organization: 'The George Washington University',
                domain: '',
                longitude: null
              }
            },
            user: {
              email_address: '',
              ad_username: '',
              ad_domain: '',
              username: ''
            }
          },
          type: 'Network',
          analysis_file: '',
          enrichment,
          user_src: '',
          analysis_service: 'http1.1 without accept header',
          file: '<none>',
          detected_by: '',
          host_src: '',
          from: '161.253.40.218:2045',
          timestamp: 1526414954000,
          event_source_id: eventSourceId,
          related_links: [
            {
              type: 'investigate_original_event',
              url: '/investigation/host/10.4.61.6:50005/navigate/event/AUTO/41623'
            },
            {
              type: 'investigate_destination_domain',
              url: '/investigation/10.4.61.6:50005/navigate/query/alias.host%3D\'mail.google.com\'%2Fdate%2F2018-05-15T19%3A59%3A14.000Z%2F2018-05-15T20%3A09%3A14.000Z'
            }
          ],
          domain_dst: 'google.com',
          user_dst: '',
          host_dst: '',
          size: 1455,
          domain: 'mail.google.com',
          to: '66.249.83.83:80',
          detector: {
            device_class: '',
            ip_address: '',
            product_name: ''
          },
          user: '',
          analysis_session: '',
          site_categorization: 'spectrum',
          username: '',
          indicatorId: id,
          id: `${id}:0`
        }
      ]
    };
  });
  const storyline = storylineIds.map((id) => {
    return {
      id,
      receivedTime: 1526415068562,
      status: 'GROUPED_IN_INCIDENT',
      errorMessage: null,
      originalHeaders: null,
      originalRawAlert: null,
      originalAlert: null,
      incidentId: 'INC-8858',
      partOfIncident: true,
      incidentCreated: 1526415248636,
      alert: {
        destination_country: [
          'United States'
        ],
        groupby_type: 'Network',
        user_summary: [
          ''
        ],
        groupby_domain: 'mail.google.com',
        source: 'Event Stream Analysis',
        type: [
          'Network'
        ],
        groupby_user_src: '',
        groupby_source_country: 'United States',
        groupby_destination_country: 'United States',
        groupby_analysis_session: '',
        groupby_analysis_file: '',
        signature_id: 'Suspected C&C',
        groupby_filename: '<none>',
        groupby_data_hash: '',
        groupby_domain_dst: 'google.com',
        groupby_destination_ip: '66.249.83.83',
        groupby_host_dst: '',
        groupby_source_ip: '161.253.40.218',
        groupby_source_username: '',
        groupby_detector_ip: '',
        timestamp: 1526415060399,
        severity: 80,
        related_links: [
          {
            type: 'investigate_session',
            url: '/investigation/10.4.61.6:50005/navigate/query/sessionid%3D41623'
          },
          {
            type: 'investigate_src_ip',
            url: '/investigation/10.4.61.6:50005/navigate/query/ip.src%3D161.253.40.218%2Fdate%2F2018-05-15T19%3A59%3A14.000Z%2F2018-05-15T20%3A19%3A14.000Z'
          },
          {
            type: 'investigate_dst_ip',
            url: '/investigation/10.4.61.6:50005/navigate/query/ip.dst%3D66.249.83.83%2Fdate%2F2018-05-15T19%3A59%3A14.000Z%2F2018-05-15T20%3A19%3A14.000Z'
          },
          {
            type: 'investigate_destination_domain',
            url: '/investigation/10.4.61.6:50005/navigate/query/alias.host%3D\'mail.google.com\'%2Fdate%2F2018-05-15T19%3A59%3A14.000Z%2F2018-05-15T20%3A19%3A14.000Z'
          }
        ],
        host_summary: '161.253.40.218:2045 to 66.249.83.83:80',
        groupby_username: '',
        risk_score: 80,
        groupby_destination_port: '80',
        groupby_c2domain: 'mail.google.com',
        groupby_user_dst: '',
        source_country: [
          'United States'
        ],
        groupby_domain_src: '',
        name: 'http-packet',
        numEvents: 1,
        groupby_host_src: '',
        groupby_analysis_service: 'http1.1 without accept header'
      },
      timestamp: 1526415060399,
      storylineId: 'INC-8858'
    };
  });
  return {
    ...state,
    storyline,
    storylineEvents
  };
};

export const generateIncident = ({ withSelection }) => {
  const state = Object.assign({}, incidentState);
  const defaultSelection = {
    type: 'event',
    ids: [
      '5afb3edc2de080511717842b:0'
    ]
  };
  const noSelection = {
    type: '',
    ids: []
  };
  const selection = withSelection ? Object.assign({}, defaultSelection) : Object.assign({}, noSelection);
  return {
    ...state,
    selection
  };
};
