import arrayFlattenBy from 'respond-shared/utils/array/flatten-by';
import arrayFilterByList from 'respond-shared/utils/array/filter-by-list';

export const alertSelectionId = '5b841c880a32bd5a68baeaf3';
export const eventSelectionId = '5b7f06c10a32bd5a68baea84:0';
export const reEventId = '5b841c880a32bd5a68baeaf3:0';
export const networkEventId = '5b757f480a32bd36c7609e96:0';
export const endpointEventId = '5b8554be0a32bd353ad3a167:0';
export const uebaEventId = '44732bcc-b9e8-4b2d-badb-e8747c98db46';

const storyLineEvents = {
  respond: {
    incident: {
      id: 'INC-108',
      selection: {
        type: '',
        ids: []
      }
    },
    storyline: {
      id: 'INC-108',
      storyline: [
        {
          alert: {
            destination_country: [],
            groupby_analysis_file: '',
            groupby_analysis_service: 'ssl over non-standard port',
            groupby_analysis_session: 'ratio medium transmitted',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: '',
            groupby_destination_ip: '10.4.61.44',
            groupby_destination_port: '5671',
            groupby_detector_ip: '',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_src: '',
            groupby_source_country: '',
            groupby_source_ip: '10.4.61.97',
            groupby_source_username: '',
            groupby_type: 'Network',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: '10.4.61.97:34443 to 10.4.61.44:5671',
            name: 'test',
            numEvents: 1,
            related_links: [
              {
                type: 'investigate_session',
                url: '/investigation/10.4.61.36:56005/navigate/query/sessionid%3D217947'
              },
              {
                type: 'investigate_src_ip',
                url: '/investigation/10.4.61.36:56005/navigate/query/ip.src%3D10.4.61.97%2Fdate%2F2018-08-16T13%3A31%3A28.000Z%2F2018-08-16T13%3A51%3A28.000Z'
              },
              {
                type: 'investigate_dst_ip',
                url: '/investigation/10.4.61.36:56005/navigate/query/ip.dst%3D10.4.61.44%2Fdate%2F2018-08-16T13%3A31%3A28.000Z%2F2018-08-16T13%3A51%3A28.000Z'
              }
            ],
            risk_score: 90,
            severity: 90,
            signature_id: null,
            source: 'Event Stream Analysis',
            source_country: [],
            timestamp: 1534426952000,
            type: [
              'Network'
            ],
            user_summary: [
              ''
            ]
          },
          errorMessage: null,
          id: '5b757f480a32bd36c7609e96',
          incidentCreated: 1535379508093,
          incidentId: 'INC-249',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1534426952476,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1334426952000,
          storylineId: 'INC-108'
        },
        {
          alert: {
            destination_country: [],
            groupby_analysis_file: '',
            groupby_analysis_service: 'ssl over non-standard port',
            groupby_analysis_session: 'ratio medium transmitted',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: '',
            groupby_destination_ip: '10.4.61.44',
            groupby_destination_port: '5671',
            groupby_detector_ip: '',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_src: '',
            groupby_source_country: '',
            groupby_source_ip: '10.4.61.97',
            groupby_source_username: '',
            groupby_type: 'Network',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: '10.4.61.97:36749 to 10.4.61.44:5671',
            name: 'test',
            numEvents: 1,
            related_links: [
              {
                type: 'investigate_session',
                url: '/investigation/10.4.61.36:56005/navigate/query/sessionid%3D217948'
              },
              {
                type: 'investigate_src_ip',
                url: '/investigation/10.4.61.36:56005/navigate/query/ip.src%3D10.4.61.97%2Fdate%2F2018-08-16T13%3A31%3A29.000Z%2F2018-08-16T13%3A51%3A29.000Z'
              },
              {
                type: 'investigate_dst_ip',
                url: '/investigation/10.4.61.36:56005/navigate/query/ip.dst%3D10.4.61.44%2Fdate%2F2018-08-16T13%3A31%3A29.000Z%2F2018-08-16T13%3A51%3A29.000Z'
              }
            ],
            risk_score: 90,
            severity: 90,
            signature_id: null,
            source: 'Event Stream Analysis',
            source_country: [],
            timestamp: 1534426952000,
            type: [
              'Network'
            ],
            user_summary: [
              ''
            ]
          },
          errorMessage: null,
          id: '5b757f480a32bd36c7609e97',
          incidentCreated: 1535379508093,
          incidentId: 'INC-249',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1534426952478,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1334426952000,
          storylineId: 'INC-108'
        },
        {
          alert: {
            destination_country: [],
            groupby_agent_id: '',
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: '',
            groupby_destination_ip: '',
            groupby_destination_port: '',
            groupby_detector_ip: '',
            groupby_device_type: 'winevent_snare',
            groupby_domain: '09:50:16',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_file_sha_256: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_name: '09:50:16',
            groupby_host_src: '',
            groupby_os: '',
            groupby_source_country: '',
            groupby_source_ip: '10.40.14.66',
            groupby_source_username: '',
            groupby_type: 'Log',
            groupby_user_dst: 'azkaislam',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: [
              '10.40.14.66'
            ],
            name: 'Logon',
            numEvents: 1,
            related_links: [
              {
                type: 'investigate_session',
                url: '/investigation/10.4.61.36:56005/navigate/query/sessionid%3D395871'
              },
              {
                type: 'investigate_src_ip',
                url: '/investigation/10.4.61.36:56005/navigate/query/ip.src%3D10.40.14.66%2Fdate%2F2018-09-01T02%3A19%3A00.000Z%2F2018-09-01T02%3A39%3A00.000Z'
              },
              {
                type: 'investigate_destination_domain',
                url: '/investigation/10.4.61.36:56005/navigate/query/alias.host%3D"09%3A50%3A16"%2Fdate%2F2018-09-01T02%3A19%3A00.000Z%2F2018-09-01T02%3A39%3A00.000Z'
              }
            ],
            risk_score: 90,
            severity: 90,
            signature_id: null,
            source: 'Event Stream Analysis',
            source_country: [],
            timestamp: 1535768957000,
            type: [
              'Log'
            ],
            user_summary: [
              'azkaislam'
            ]
          },
          errorMessage: null,
          id: '5b89f97d0a32bd26fdf4507d',
          incidentCreated: 1535379508093,
          incidentId: 'INC-249',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1535768957648,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1335768957000,
          storylineId: 'INC-108'
        },
        {
          id: '586ecfc0ecd25950034e1318',
          errorMessage: null,
          incidentCreated: null,
          incidentId: null,
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: false,
          receivedTime: 1532658252079,
          status: 'NORMALIZED',
          timestamp: 1532297340000,
          alert: {
            id: '1486f9ac-974d-4be6-8641-1b0826097854',
            entity_id: '1c86c083-d82d-47f4-8930-187473ddad13',
            classification: 'abnormal_ad_changes',
            classifier_id: '3af8801b-0979-4066-b906-6330eaca2337',
            classifier_score: 97.41587325223246,
            classifier_severity: 'HIGH',
            end_date: '2018-07-22T22:09:00.000+0000',
            entity_name: 'ad_qa_1_9',
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: '',
            groupby_destination_ip: '',
            groupby_destination_port: '',
            groupby_detector_ip: '',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_src: '',
            groupby_source_country: '',
            groupby_source_ip: '',
            groupby_source_username: '',
            groupby_type: 'User Entity',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            name: 'abnormal_object_change_operation',
            numEvents: 2,
            risk_score: 4,
            severity: 4,
            signature_id: 'UEBAIOC',
            source: 'User Entity Behavior Analytics',
            start_date: '2018-07-22T22:09:00.000+0000',
            timestamp: 1532297340000,
            type: [
              'User Entity'
            ]
          },
          storylineId: 'INC-108'
        },
        {
          alert: {
            destination_country: [],
            groupby_agent_id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: '',
            groupby_destination_ip: '',
            groupby_destination_port: '',
            groupby_detector_ip: '',
            groupby_device_type: 'nwendpoint',
            groupby_domain: 'INENMENONS4L2C',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_file_sha_256: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_name: '',
            groupby_host_src: '',
            groupby_os: '',
            groupby_source_country: '',
            groupby_source_ip: '',
            groupby_source_username: 'CORP\\menons4',
            groupby_type: 'Log',
            groupby_user_dst: '',
            groupby_user_src: 'CORP\\menons4',
            groupby_username: '',
            host_summary: [
              '-nwendpoint'
            ],
            name: 'Unsigned Open Process and Runs Command Shell',
            numEvents: 8,
            related_links: [
              {
                type: 'investigate_session',
                url: '/investigation/10.63.0.117:56005/navigate/query/sessionid%3D857775%7C%7Csessionid%3D857776%7C%7Csessionid%3D857777%7C%7Csessionid%3D857778%7C%7Csessionid%3D857779%7C%7Csessionid%3D857780%7C%7Csessionid%3D857782%7C%7Csessionid%3D857783'
              },
              {
                type: 'investigate_destination_domain',
                url: '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A50%3A12.000Z'
              }
            ],
            risk_score: 50,
            severity: 50,
            signature_id: 'Manual',
            source: 'NetWitness Investigate',
            source_country: [],
            timestamp: 1528431673932,
            type: [
              'Log'
            ],
            user_summary: [
              'CORP\\menons4'
            ]
          },
          errorMessage: null,
          id: '5b8554be0a32bd353ad3a167',
          incidentCreated: 1535569262762,
          incidentId: 'INC-94980',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1535464638419,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1528431673932,
          storylineId: 'INC-108'
        },
        {
          alert: {
            destination_country: [
              'United States'
            ],
            groupby_agent_id: '',
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: 'United States',
            groupby_destination_ip: '129.6.15.28',
            groupby_destination_port: '123',
            groupby_detector_ip: '',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: 'nist.gov',
            groupby_domain_src: '',
            groupby_file_sha_256: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_name: '',
            groupby_host_src: '',
            groupby_os: '',
            groupby_source_country: '',
            groupby_source_ip: '192.168.100.185',
            groupby_source_username: '',
            groupby_type: 'Network',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: '192.168.100.185:123 to 129.6.15.28:123',
            name: 'country_dst',
            numEvents: 3,
            risk_score: 70,
            severity: 70,
            signature_id: 'RULE_1_20140529211814',
            source: 'Reporting Engine',
            source_country: [],
            timestamp: 1407929290000,
            type: [
              'Network'
            ],
            user_summary: [
              ''
            ]
          },
          errorMessage: null,
          id: '5b841c880a32bd5a68baeaf3',
          incidentCreated: 1535384713034,
          incidentId: 'INC-94966',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1535384712550,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1407929290000,
          storylineId: 'INC-108'
        },
        {
          alert: {
            groupby_agent_id: '',
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: '',
            groupby_destination_country: '',
            groupby_destination_ip: '',
            groupby_destination_port: '',
            groupby_detector_ip: '',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_file_sha_256: '',
            groupby_filename: '',
            groupby_host_dst: '',
            groupby_host_name: '',
            groupby_host_src: '',
            groupby_os: '',
            groupby_source_country: '',
            groupby_source_ip: '',
            groupby_source_username: '',
            groupby_type: 'Web Threat Detection Incident',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: [],
            name: 'incident1',
            numEvents: 1,
            related_links: [
              {
                type: 'Open in WTD (external)'
              }
            ],
            risk_score: 10,
            severity: 10,
            signature_id: null,
            source: 'Web Threat Detection',
            timestamp: 1424914173385,
            type: [
              'Web Threat Detection Incident'
            ],
            user_summary: []
          },
          errorMessage: null,
          id: '5b7f08240a32bd5a68baea89',
          incidentCreated: 1535039446679,
          incidentId: 'INC-33',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1535051812504,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1424914173385,
          storylineId: 'INC-108'
        },
        {
          alert: {
            groupby_agent_id: '',
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3',
            groupby_destination_country: '',
            groupby_destination_ip: '',
            groupby_destination_port: '',
            groupby_detector_ip: '22.90.206.71',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_file_sha_256: '',
            groupby_filename: 'EhStorShell.dll',
            groupby_host_dst: '',
            groupby_host_name: '',
            groupby_host_src: '',
            groupby_os: '',
            groupby_source_country: '',
            groupby_source_ip: '',
            groupby_source_username: '',
            groupby_type: 'Instant IOC',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: [
              'it_laptop4.eng.matrix.com'
            ],
            name: 'ecat example',
            numEvents: 1,
            related_links: [
              {
                type: 'investigate_ecat',
                url: 'ecatui://26C5C21F-4DA8-3A00-437C-AB7444987430'
              }
            ],
            risk_score: 10,
            severity: 10,
            signature_id: 'ModuleIOC',
            source: 'ECAT',
            timestamp: 1353069839000,
            type: [
              'Instant IOC'
            ],
            user_summary: [
              ''
            ]
          },
          errorMessage: null,
          id: '5b7f06c10a32bd5a68baea84',
          incidentCreated: 1535397013408,
          incidentId: 'INC-94975',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1535051457068,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1353069839000,
          storylineId: 'INC-108'
        },
        {
          alert: {
            destination_country: [
              'Unavailable'
            ],
            groupby_analysis_file: '',
            groupby_analysis_service: '',
            groupby_analysis_session: '',
            groupby_c2domain: '',
            groupby_data_hash: '7d708f247cc6a7364b873c029bbdf459',
            groupby_destination_country: 'Unavailable',
            groupby_destination_ip: 'qaspectrum2.netwitness.local',
            groupby_destination_port: '',
            groupby_detector_ip: '10.25.51.142',
            groupby_device_type: '',
            groupby_domain: '',
            groupby_domain_dst: '',
            groupby_domain_src: '',
            groupby_filename: 'card.gif.exe',
            groupby_host_dst: '',
            groupby_host_src: '',
            groupby_source_country: '',
            groupby_source_ip: '10.25.51.142',
            groupby_source_username: '',
            groupby_type: 'Resubmit',
            groupby_user_dst: '',
            groupby_user_src: '',
            groupby_username: '',
            host_summary: [
              '10.25.51.142'
            ],
            name: 'Malware Found in Uploaded File(Miss secondary hit)',
            numEvents: 1,
            related_links: [
              {
                type: 'investigate_malware',
                url: '/investigation/undefined/malware/event/3328608'
              }
            ],
            risk_score: 90,
            severity: 90,
            signature_id: 'Suspicious Event',
            source: 'Malware Analysis',
            source_country: [],
            timestamp: 1407801720000,
            type: [
              'Resubmit'
            ],
            user_summary: []
          },
          errorMessage: null,
          id: '5b9bb59dbcde8d385474a70a',
          incidentCreated: 1536853112331,
          incidentId: 'INC-7',
          originalAlert: null,
          originalHeaders: null,
          originalRawAlert: null,
          partOfIncident: true,
          receivedTime: 1536931229901,
          status: 'GROUPED_IN_INCIDENT',
          timestamp: 1407801720000,
          storylineId: 'INC-108'
        }
      ],
      storylineStatus: 'completed',
      stopStorylineStream: null,
      storylineEvents: [
        {
          indicatorId: '5b757f480a32bd36c7609e96',
          events: [
            {
              analysis_file: '',
              analysis_service: 'ssl over non-standard port',
              analysis_session: 'ratio medium transmitted',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 4175
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '10.4.61.44',
                  mac_address: '00:50:56:33:18:15',
                  netbios_name: '',
                  port: 5671
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: ''
              },
              domain: '',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.4.61.33:56005',
              event_source_id: '150',
              file: '',
              from: '10.4.61.97:36749',
              host_dst: '',
              host_src: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.4.61.36:56005/navigate/event/AUTO/217948'
                }
              ],
              size: 4175,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '10.4.61.97',
                  mac_address: '00:50:56:33:18:18',
                  netbios_name: '',
                  port: 36749
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              timestamp: 1534426889000,
              to: '10.4.61.44:5671',
              type: 'Network',
              user: '',
              user_dst: '',
              user_src: '',
              username: '',
              indicatorId: '5b757f480a32bd36c7609e96',
              eventIndex: 0,
              id: networkEventId
            }
          ]
        },
        {
          indicatorId: '5b757f480a32bd36c7609e97',
          events: [
            {
              analysis_file: '',
              analysis_service: 'ssl over non-standard port',
              analysis_session: 'ratio medium transmitted',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 4175
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '10.4.61.44',
                  mac_address: '00:50:56:33:18:15',
                  netbios_name: '',
                  port: 5671
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: ''
              },
              domain: '',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.4.61.33:56005',
              event_source_id: '150',
              file: '',
              from: '10.4.61.97:36749',
              host_dst: '',
              host_src: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.4.61.36:56005/navigate/event/AUTO/217948'
                }
              ],
              size: 4175,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '10.4.61.97',
                  mac_address: '00:50:56:33:18:18',
                  netbios_name: '',
                  port: 36749
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              timestamp: 1534426889000,
              to: '10.4.61.44:5671',
              type: 'Network',
              user: '',
              user_dst: '',
              user_src: '',
              username: '',
              indicatorId: '5b757f480a32bd36c7609e97',
              eventIndex: 0,
              id: '5b757f480a32bd36c7609e97:0'
            }
          ]
        },
        {
          indicatorId: '5b89f97d0a32bd26fdf4507d',
          events: [
            {
              action: '',
              agent_id: '',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: 'Logon/Logoff',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 641
                }
              ],
              description: 'Successful',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'azkaislam'
                }
              },
              destination_parameter: '',
              detected_by: 'Windows Hosts-winevent_snare',
              detector: {
                device_class: 'Windows Hosts',
                ip_address: '',
                product_name: 'winevent_snare'
              },
              device_type: 'winevent_snare',
              domain: '09:50:16',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.4.61.28:56003',
              event_source_id: '154',
              file: '',
              file_SHA256: '',
              from: '10.40.14.66',
              host_dst: '',
              host_src: '',
              hostname: '09:50:16',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.4.61.36:56005/navigate/event/AUTO/395871'
                },
                {
                  type: 'investigate_destination_domain',
                  url: '/investigation/10.4.61.36:56005/navigate/query/alias.host%3D"09%3A50%3A16"%2Fdate%2F2018-09-01T02%3A19%3A00.000Z%2F2018-09-01T02%3A29%3A00.000Z'
                }
              ],
              size: 641,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '10.40.14.66',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              source_filename: '',
              source_hash: '',
              source_parameter: '',
              target_filename: '',
              target_hash: '',
              timestamp: 1535768940000,
              to: '',
              type: 'Log',
              user: 'azkaislam',
              user_account: '',
              user_dst: 'azkaislam',
              user_src: '',
              username: '',
              indicatorId: '5b89f97d0a32bd26fdf4507d',
              eventIndex: 0,
              id: '5b89f97d0a32bd26fdf4507d:0'
            }
          ]
        },
        {
          indicatorId: '586ecfc0ecd25950034e1318',
          events: [
            {
              createdDate: '2018-07-27T02:23:28.229+0000',
              eventTime: '2018-07-22T22:09:00.000+0000',
              features: {
                additionalInfo: {},
                dataSource: '4741',
                eventDate: {
                  epochSecond: 1532297340,
                  nano: 0
                },
                eventId: 'EV-828-ad_qa_1_9',
                id: '5b5a81388402c7548eacfacd',
                isUserAdmin: false,
                objectId: 'Test5',
                operationType: 'COMPUTER_ACCOUNT_CREATED',
                operationTypeCategories: [
                  'COMPUTER_MANAGEMENT',
                  'OBJECT_MANAGEMENT'
                ],
                result: 'SUCCESS',
                userDisplayName: 'ad_qa_1_9',
                userId: 'ad_qa_1_9',
                userName: 'ad_qa_1_9'
              },
              id: 'c05972b1-db65-4786-bf86-d4f1541aa161',
              indicatorId: '586ecfc0ecd25950034e1318',
              eventIndex: 0,
              schema: 'ACTIVE_DIRECTORY',
              scores: {
                operationType: 4
              },
              updatedBy: 'hourlyOutputProcessorRun2018-07-22T22:00:00Z',
              updatedDate: '2018-07-27T02:23:28.229+0000'
            },
            {
              createdDate: '2018-07-27T02:23:28.229+0000',
              eventTime: '2018-07-22T22:42:00.000+0000',
              features: {
                additionalInfo: {},
                dataSource: '4741',
                eventDate: {
                  epochSecond: 1532299320,
                  nano: 0
                },
                eventId: 'EV-861-ad_qa_1_9',
                id: '5b5a81388402c7548eacfaf0',
                isUserAdmin: false,
                objectId: 'Test3',
                operationType: 'COMPUTER_ACCOUNT_CREATED',
                operationTypeCategories: [
                  'COMPUTER_MANAGEMENT',
                  'OBJECT_MANAGEMENT'
                ],
                result: 'SUCCESS',
                userDisplayName: 'ad_qa_1_9',
                userId: 'ad_qa_1_9',
                userName: 'ad_qa_1_9'
              },
              id: uebaEventId,
              indicatorId: '586ecfc0ecd25950034e1318',
              eventIndex: 1,
              schema: 'ACTIVE_DIRECTORY',
              scores: {
                operationType: 4
              },
              updatedBy: 'hourlyOutputProcessorRun2018-07-22T22:00:00Z',
              updatedDate: '2018-07-27T02:23:28.229+0000'
            }
          ]
        },
        {
          indicatorId: '5b8554be0a32bd353ad3a167',
          events: [
            {
              action: 'createProcess',
              agent_id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: 'Process Event',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 41
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '-nwendpoint',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: 'nwendpoint'
              },
              device_type: 'nwendpoint',
              domain: 'INENMENONS4L2C',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.63.0.117:56005',
              event_source_id: '857775',
              file: '',
              file_SHA256: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              from: '',
              host_dst: '',
              host_src: '',
              hostname: 'INENMENONS4L2C',
              ip_dst: '',
              ip_src: '',
              launch_argument_dst: 'PowerShell.exe --run',
              launch_argument_src: 'dtf.exe  -dll:ioc.dll -testcase:353',
              operating_system: 'macOS',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857775'
                },
                {
                  type: 'investigate_destination_domain',
                  url: '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
                }
              ],
              size: 41,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'CORP\\menons4'
                }
              },
              source_filename: 'dtf.exe',
              source_path: '/foo/bar',
              source_hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
              target_filename: 'cmd.EXE',
              target_path: '/bar/baz',
              target_hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              timestamp: 1528429212000,
              to: '',
              type: 'Log',
              user: 'CORP\\menons4',
              user_account: 'foobar',
              user_dst: '',
              user_src: 'CORP\\menons4',
              username: '',
              indicatorId: '5b8554be0a32bd353ad3a167',
              eventIndex: 0,
              id: endpointEventId
            },
            {
              action: 'openProcess',
              agent_id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: 'Process Event',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 41
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '-nwendpoint',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: 'nwendpoint'
              },
              device_type: 'nwendpoint',
              domain: 'INENMENONS4L2C',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.63.0.117:56005',
              event_source_id: '857776',
              file: '',
              file_SHA256: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              from: '',
              host_dst: '',
              host_src: '',
              hostname: 'INENMENONS4L2C',
              ip_dst: '',
              ip_src: '',
              launch_argument_dst: 'cmd.EXE /C COPY /Y C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\dtf.exe C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\MSHTA.EXE',
              launch_argument_src: 'dtf.exe  -dll:ioc.dll -testcase:353',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857776'
                },
                {
                  type: 'investigate_destination_domain',
                  url: '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
                }
              ],
              size: 41,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'CORP\\menons4'
                }
              },
              source_filename: 'dtf.exe',
              source_hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
              source_path: '',
              target_filename: 'cmd.EXE',
              target_hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              target_path: '',
              timestamp: 1528429212000,
              to: '',
              type: 'Log',
              user: 'CORP\\menons4',
              user_account: '',
              user_dst: '',
              user_src: 'CORP\\menons4',
              username: '',
              indicatorId: '5b8554be0a32bd353ad3a167',
              eventIndex: 1,
              id: '5b8554be0a32bd353ad3a167:1'
            },
            {
              agent_id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
              analysis_session: '',
              category: 'Process Event',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 41
                }
              ],
              destination_parameter: 'MSHTA.EXE',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: 'nwendpoint'
              },
              source_user_account: 'CORP\\menons4',
              'targ…ain': 'INENMENONS4L2C',
              to: '',
              user: 'CORP\\menons4',
              user_account: '',
              username: '',
              indicatorId: '5b8554be0a32bd353ad3a167',
              eventIndex: 2,
              id: '5b8554be0a32bd353ad3a167:2'
            },
            {
              action: 'createProcess',
              agent_id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: 'Process Event',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 41
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '-nwendpoint',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: 'nwendpoint'
              },
              device_type: 'nwendpoint',
              domain: 'INENMENONS4L2C',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.63.0.117:56005',
              event_source_id: '857782',
              file: '',
              file_SHA256: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              from: '',
              host_dst: '',
              host_src: '',
              hostname: 'INENMENONS4L2C',
              ip_dst: '',
              ip_src: '',
              launch_argument_dst: 'cmd.exe  /C sc stop dtfsvc && sc delete dtfsvc',
              launch_argument_src: 'dtf.exe  -dll:ioc.dll -testcase:353',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857782'
                },
                {
                  type: 'investigate_destination_domain',
                  url: '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
                }
              ],
              size: 41,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'CORP\\menons4'
                }
              },
              source_filename: 'dtf.exe',
              source_hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
              source_path: '',
              target_filename: 'cmd.exe',
              target_hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              target_path: '',
              timestamp: 1528429212000,
              to: '',
              type: 'Log',
              user: 'CORP\\menons4',
              user_account: '',
              user_dst: '',
              user_src: 'CORP\\menons4',
              username: '',
              indicatorId: '5b8554be0a32bd353ad3a167',
              eventIndex: 3,
              id: '5b8554be0a32bd353ad3a167:3'
            },
            {
              action: 'openProcess',
              agent_id: 'C593263F-E2AB-9168-EFA4-C683E066A035',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: 'Process Event',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 41
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '-nwendpoint',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: 'nwendpoint'
              },
              device_type: 'nwendpoint',
              domain: 'INENMENONS4L2C',
              domain_dst: '',
              domain_src: '',
              enrichment: '',
              event_source: '10.63.0.117:56005',
              event_source_id: '857783',
              file: '',
              file_SHA256: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              from: '',
              host_dst: '',
              host_src: '',
              hostname: 'INENMENONS4L2C',
              ip_dst: '',
              ip_src: '',
              launch_argument_dst: 'cmd.exe  /C sc stop dtfsvc && sc delete dtfsvc',
              launch_argument_src: 'dtf.exe  -dll:ioc.dll -testcase:353',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.63.0.117:56005/navigate/event/AUTO/857783'
                },
                {
                  type: 'investigate_destination_domain',
                  url: '/investigation/10.63.0.117:56005/navigate/query/alias.host%3D"INENMENONS4L2C"%2Fdate%2F2018-06-08T03%3A30%3A12.000Z%2F2018-06-08T03%3A40%3A12.000Z'
                }
              ],
              size: 41,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'CORP\\menons4'
                }
              },
              source_filename: 'dtf.exe',
              source_hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6',
              source_path: '',
              target_filename: 'cmd.exe',
              target_hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9',
              target_path: '',
              timestamp: 1528429212000,
              to: '',
              type: 'Log',
              user: 'CORP\\menons4',
              user_account: '',
              user_dst: '',
              user_src: 'CORP\\menons4',
              username: '',
              indicatorId: '5b8554be0a32bd353ad3a167',
              eventIndex: 4,
              id: '5b8554be0a32bd353ad3a167:4'
            }
          ]
        },
        {
          indicatorId: '5b841c880a32bd5a68baeaf3',
          events: [
            {
              action: '',
              agent_id: '',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: '',
              data: [
                {
                  filename: 'foobarbaz.sh',
                  hash: '123987def',
                  size: 180
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: 'Gaithersburg',
                    country: 'United States',
                    domain: 'nist.gov',
                    latitude: 39,
                    longitude: -77,
                    organization: 'National Bureau of Standards'
                  },
                  ip_address: '129.6.15.28',
                  mac_address: '00:00:00:00:5E:00',
                  netbios_name: '',
                  port: 123
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'xor'
                }
              },
              destination_parameter: '',
              detected_by: '',
              detector: {
                device_class: '',
                ip_address: '127.0.0.1',
                product_name: ''
              },
              device_type: '',
              domain: 'zap',
              domain_dst: 'nist.gov',
              domain_src: '',
              enrichment: '',
              event_source: '10.25.51.157:50105',
              event_source_id: '47560522',
              file: '',
              file_SHA256: '',
              from: '192.168.100.185:123',
              host_dst: '',
              host_src: '',
              hostname: '',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.25.51.157:50105/navigate/event/AUTO/47560522'
                }
              ],
              size: 180,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '192.168.100.185',
                  mac_address: '00:00:46:8F:F4:20',
                  netbios_name: '',
                  port: 123
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: 'tbozo'
                }
              },
              source_filename: '',
              source_hash: '',
              source_parameter: '',
              source_user_account: '',
              target_filename: '',
              target_hash: '',
              timestamp: 0,
              to: '129.6.15.28:123',
              type: 'Network',
              user: '',
              user_account: '',
              user_dst: '',
              user_src: '',
              username: '',
              indicatorId: '5b841c880a32bd5a68baeaf3',
              id: reEventId
            },
            {
              action: '',
              agent_id: '',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: '',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 180
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: 'Gaithersburg',
                    country: 'United States',
                    domain: 'nist.gov',
                    latitude: 39,
                    longitude: -77,
                    organization: 'National Bureau of Standards'
                  },
                  ip_address: '129.6.15.28',
                  mac_address: '00:00:00:00:5E:00',
                  netbios_name: '',
                  port: 123
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              destination_parameter: '',
              detected_by: '',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: ''
              },
              device_type: '',
              domain: '',
              domain_dst: 'nist.gov',
              domain_src: '',
              enrichment: '',
              event_source: '10.25.51.157:50105',
              event_source_id: '47560522',
              file: '',
              file_SHA256: '',
              from: '192.168.100.185:123',
              host_dst: '',
              host_src: '',
              hostname: '',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.25.51.157:50105/navigate/event/AUTO/47560522'
                }
              ],
              size: 180,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '192.168.100.185',
                  mac_address: '00:00:46:8F:F4:20',
                  netbios_name: '',
                  port: 123
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              source_filename: '',
              source_hash: '',
              source_parameter: '',
              source_user_account: '',
              target_filename: '',
              target_hash: '',
              timestamp: 0,
              to: '129.6.15.28:123',
              type: 'Network',
              user: '',
              user_account: '',
              user_dst: '',
              user_src: '',
              username: '',
              indicatorId: '5b841c880a32bd5a68baeaf3',
              eventIndex: 1,
              id: '5b841c880a32bd5a68baeaf3:1'
            },
            {
              action: '',
              agent_id: '',
              analysis_file: '',
              analysis_service: '',
              analysis_session: '',
              category: '',
              data: [
                {
                  filename: '',
                  hash: '',
                  size: 180
                }
              ],
              description: '',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: 'Gaithersburg',
                    country: 'United States',
                    domain: 'nist.gov',
                    latitude: 39,
                    longitude: -77,
                    organization: 'National Bureau of Standards'
                  },
                  ip_address: '129.6.15.28',
                  mac_address: '00:00:00:00:5E:00',
                  netbios_name: '',
                  port: 123
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              destination_parameter: '',
              detected_by: '',
              detector: {
                device_class: '',
                ip_address: '',
                product_name: ''
              },
              device_type: '',
              domain: '',
              domain_dst: 'nist.gov',
              domain_src: '',
              enrichment: '',
              event_source: '10.25.51.157:50105',
              event_source_id: '47560522',
              file: '',
              file_SHA256: '',
              from: '192.168.100.185:123',
              host_dst: '',
              host_src: '',
              hostname: '',
              operating_system: '',
              related_links: [
                {
                  type: 'investigate_original_event',
                  url: '/investigation/host/10.25.51.157:50105/navigate/event/AUTO/47560522'
                }
              ],
              size: 180,
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '192.168.100.185',
                  mac_address: '00:00:46:8F:F4:20',
                  netbios_name: '',
                  port: 123
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              source_filename: '',
              source_hash: '',
              source_parameter: '',
              source_user_account: '',
              target_filename: '',
              target_hash: '',
              timestamp: 0,
              to: '129.6.15.28:123',
              type: 'Network',
              user: '',
              user_account: '',
              user_dst: '',
              user_src: '',
              username: '',
              indicatorId: '5b841c880a32bd5a68baeaf3',
              eventIndex: 2,
              id: '5b841c880a32bd5a68baeaf3:2'
            }
          ]
        },
        {
          indicatorId: '5b7f08240a32bd5a68baea89',
          events: [
            {
              description: 'Retail Wire Over 3000',
              details: 'Retail wire amount is 150,000',
              from: '10.1.1.198',
              id: 198,
              name: 'Retail Wire Over 3000',
              related_links: [
                {
                  type: 'View Original Event (in WTD)',
                  url: 'https://test-bhasker.silvertailsystems.com/#incidentDetails?incident=198'
                }
              ],
              rule: 'retail_wire_over_3000',
              rulecomment: 'Triggered when retail wire exceeds $3000',
              score: 0,
              source: {
                device: {
                  ip_address: '10.1.1.198'
                },
                user: {
                  username: 'John Doe1'
                }
              },
              tenant: 'tenant1',
              timestamp: 1434571084000,
              type: 'Web Threat Detection Incident',
              user: 'John Doe1',
              eventIndex: 0,
              indicatorId: '5b7f08240a32bd5a68baea89'
            }
          ]
        },
        {
          indicatorId: '5b7f06c10a32bd5a68baea84',
          events: [
            {
              data: [
                {
                  bit9_status: 'bad',
                  filename: 'AppIdPolicyEngineApi.dll',
                  hash: 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3',
                  module_signature: 'ABC Inc.',
                  opswat_result: 'OPSWAT result here',
                  size: '23562',
                  yara_result: 'N YARA rules matched'
                }
              ],
              description: 'ModuleIOC',
              detected_by: 'it_laptop1.eng.matrix.com',
              detector: {
                dns_hostname: 'it_laptop1.eng.matrix.com',
                ecat_agent_id: '26C5C21F-4DA8-3A00-437C-AB7444987430',
                ip_address: '100.3.36.242',
                mac_address: 'B8-4B-2F-08-6A-AD-5A-C7',
                os: 'Windows 7'
              },
              file: 'AppIdPolicyEngineApi.dll',
              score: '1-2-3-4',
              size: '23562',
              timestamp: 1371725338000,
              type: 'ecat event',
              user: '',
              indicatorId: '5b7f06c10a32bd5a68baea84',
              eventIndex: 0,
              id: '5b7f06c10a32bd5a68baea84:0'
            }
          ]
        },
        {
          indicatorId: '5b9bb59dbcde8d385474a70a',
          events: [
            {
              communityScore: 100,
              data: [
                {
                  av_aliases: {
                    AVG: 'IRC/BackDoor.Flood',
                    AegisLab: '',
                    Agnitum: '',
                    Ahnlab: '',
                    Antiy: '',
                    Avira: 'TR/Patched.Ren.Gen',
                    BitDefender: '',
                    ByteHero: '',
                    ClamWin: '',
                    Commtouch: 'IRC/Zapchast.AK',
                    ESET: '',
                    Emsisoft: '',
                    'F-prot': '',
                    'F-secure': '',
                    Filseclab: '',
                    Fortinet: 'W32/Inject.8A2F!tr',
                    GFI: 'Trojan.Win32.Generic!BT',
                    Hauri: '',
                    Ikarus: 'Trojan.IRC.Zapchast',
                    Jiangmin: '',
                    K7: '',
                    Kaspersky: '',
                    Kingsoft: '',
                    Lavasoft: 'Trojan.Script.39573',
                    'McAfee-Gateway': 'Artemis!7D708F247CC6',
                    Microsoft: '',
                    NANO: 'Trojan.Script.Zapchast.yexdu',
                    Norman: 'winpe/Script_Gen.GR',
                    QuickHeal: '',
                    SUPERAntiSpyware: '',
                    Sophos: '',
                    Symantec: '',
                    TotalDefense: '',
                    TrendMicro: 'Mal_Zap',
                    TrendMicroHouseCall: 'Mal_Zap',
                    VirIT: '',
                    VirusBlokAda: '',
                    'Zillya!': '',
                    Zoner: '',
                    nProtect: ''
                  },
                  av_hit: null,
                  communityScore: 100,
                  extension: 'exe',
                  filename: 'card.gif.exe',
                  hash: '7d708f247cc6a7364b873c029bbdf459',
                  mime_type: 'application/x-dosexec',
                  original_path: '/var/lib/rsamalware/spectrum/repository/files/7d/7d708f247cc6a7364b873c029bbdf459/card.gif.exe',
                  sandboxScore: 18,
                  size: 63302,
                  staticScore: 100
                }
              ],
              description: 'Malware Found in Uploaded File(Miss secondary hit)',
              destination: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: 'Unavailable',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: 'qaspectrum2.netwitness.local',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              detected_by: '10.25.51.142',
              detector: {
                ip_address: '10.25.51.142'
              },
              event_source: '',
              event_source_id: '0',
              file: [
                'card.gif.exe'
              ],
              networkScore: 0,
              related_links: [
                {
                  type: 'investigate_malware',
                  url: '/investigation/undefined/malware/event/3328608'
                }
              ],
              sandboxScore: 18,
              size: [
                63302
              ],
              source: {
                device: {
                  asset_type: '',
                  business_unit: '',
                  compliance_rating: '',
                  criticality: '',
                  facility: '',
                  geolocation: {
                    city: '',
                    country: '',
                    domain: '',
                    latitude: null,
                    longitude: null,
                    organization: ''
                  },
                  ip_address: '10.25.51.142',
                  mac_address: '',
                  netbios_name: '',
                  port: ''
                },
                user: {
                  ad_domain: '',
                  ad_username: '',
                  email_address: '',
                  username: ''
                }
              },
              staticScore: 100,
              timestamp: 1407801720000,
              type: 'Resubmit',
              indicatorId: '5b9bb59dbcde8d385474a70a',
              eventIndex: 0,
              id: '5b9bb59dbcde8d385474a70a:0'
            }
          ]
        }
      ],
      storylineEventsBuffer: [],
      storylineEventsBufferMax: 50,
      storylineEventsStatus: 'completed',
      expandedStorylineEventId: null
    }
  }
};

export const getAllAlerts = () => {
  return storyLineEvents.respond.storyline.storyline.map((indicator) => {
    const alert = indicator.alert || {};
    return {
      ...alert,
      indicatorId: indicator.id
    };
  });
};

export const getAllEvents = () => {
  return arrayFlattenBy(storyLineEvents.respond.storyline.storylineEvents, 'events');
};

export const getSelection = () => {
  return storyLineEvents.respond.incident.selection;
};

export const filterEventsBySelection = (selectionType) => {
  if (selectionType === 'alert') {
    return arrayFilterByList(getAllEvents(), 'indicatorId', [alertSelectionId]);
  }
  return arrayFilterByList(getAllEvents(), 'id', [eventSelectionId]);
};

export const getEventSelection = () => {
  return {
    id: 'INC-108',
    type: 'event',
    ids: [
      eventSelectionId
    ]
  };
};

export const getAlertSelection = () => {
  return {
    id: 'INC-108',
    type: 'storyPoint',
    ids: [
      alertSelectionId
    ]
  };
};

export { storyLineEvents };
