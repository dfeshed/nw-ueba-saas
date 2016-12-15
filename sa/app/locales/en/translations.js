import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/en/translations';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, {
  application: {
    title: 'Netwitness Suite',
    version: '11.0.0',
    copyright: '&copy;2015 RSA Security LLC. All rights reserved.'
  },
  monitor: {
    title: 'Monitor',
    details: 'Monitor contents go here.'
  },
  respond: {
    title: 'Respond',
    details: 'Respond contents go here.',
    myQueue: 'Incident Queue',
    myFilteredQueue: 'Showing {{filteredCount}} of {{totalCount}}',
    allIncidents: 'All Incidents',
    incidents: 'Incidents',
    sort: 'Sort',
    filter: 'Filter',
    journal: {
      add: 'Add Comment',
      placeholder: 'Enter comment here'
    },
    incidentDetails: {
      title: 'Incident Details',
      instructions: 'Please select an Incident from the list on the left in order to view its information here.',
      summary: 'Summary',
      details: 'Details',
      journal: 'Incident Journal',
      by: 'by',
      remediationTasks: 'Remediation Tasks'
    }
  },
  investigate: {
    title: 'Investigate',
    loading: 'Loading',
    loadMore: 'Load More',
    tryAgain: 'Try Again',
    service: 'Service',
    timeRange: 'Time Range',
    filter: 'Filter',
    size: {
      bytes: 'bytes',
      KB: 'KB',
      MB: 'MB',
      GB: 'GB',
      TB: 'TB'
    },
    medium: {
      network: 'Network',
      log: 'Log',
      correlation: 'Correlation'
    },
    empty: {
      title: 'No events found.',
      description: 'Your filter criteria did not match any records.'
    },
    error: {
      title: 'Unable to load data.',
      description: 'An unexpected error occurred when attempting to fetch the data records.'
    },
    meta: {
      title: 'Meta',
      clickToOpen: 'Click to open'
    },
    events: {
      title: 'Events',
      error: 'An unexpected error occurred when executing this query.'
    },
    services: {
      loading: 'Loading list of available services',
      empty: {
        title: 'Unable to find services.',
        description: 'No Brokers, Concentrators, or other services were detected. This may be due to a configuration or connectivity issue.'
      },
      error: {
        title: 'Unable to load services.',
        description: 'Unexpected error loading the list of Brokers, Concentrators, and other services to investigate. This may be due to a configuration or connectivity issue.'
      }
    },
    customQuery: {
      title: 'Enter a query.'
    }
  },
  live: {
    title: 'Live Content',
    home: 'Home',
    details: 'Live content and general home for live goes here.',
    deployed: {
      title: 'Deployed'
    },
    jobs: {
      title: 'Jobs'
    },
    updates: {
      title: 'Updates'
    },
    custom: {
      title: 'Custom'
    },
    search: {
      title: 'Find',
      details: 'Live Search content goes here.',
      searchCriteria: 'Search Criteria',
      matchingResources: 'Matching Resources',
      resourceDetailsTitle: 'Resource Details',
      advanced: 'Advanced',
      dropdownPlaceholder: 'All',
      deploy: 'Deploy',
      export: 'Export',
      import: 'Import',
      deployModalHeader: 'Deploy Selected Resources',
      exportModalHeader: 'Export Selected Resources',
      showingResultsMessage: 'Showing results',
      showingResultsMessageOf: 'of',
      fields: {
        categories: 'Categories',
        keywords: 'Keywords',
        resourceType: 'Resource Type',
        medium: 'Medium',
        createdDate: 'Created',
        updatedDate: 'Updated',
        requiredMetaKeys: 'Required Meta Keys',
        generatedMetaValues: 'Generated Meta Values',
        select: 'Select',
        remove: 'Remove',
        subscribed: 'Subscribed',
        resourceName: 'Name',
        description: 'Description',
        nwVersion: 'NetWitness',
        version: 'Version'
      }
    },
    manage: {
      title: 'Configure',
      details: 'Live Manage Resource content goes here.'
    },
    feeds: {
      title: 'Feeds',
      details: 'Live Feeds content goes here.'
    }
  },
  config: {
    title: 'Configure',
    details: 'Config contents go here.'
  },
  admin: {
    title: 'Admin',
    details: 'Admin contents go here.'
  },
  context: {
    title: 'Context',
    header: {
      hosts: 'Hosts',
      incidents: 'Incidents',
      alerts: 'Alerts',
      files: 'Files',
      lists: 'Lists',
      feeds: 'Feeds',
      liveConnect: 'Live Connect',
      unsafe: 'Unsafe'
    },
    hostSummary: {
      title: 'Host Summary',
      riskScore: 'Risk Score',
      modulesCount: '# of Modules',
      iioc0: 'Iioc 0',
      iioc1: 'Iioc 1',
      lastUpdated: 'Last Updated',
      adminStatus: 'Admin Status',
      lastLogin: 'Last Login',
      macAddress: 'Mac Address',
      operatingSystem: 'Operating System',
      machineStatus: 'Machine Status'
    },
    modules: {
      title: 'Top Suspicious Modules',
      iiocScore: 'Iioc Score',
      moduleName: 'Module Name',
      analyticsScore: 'Analytics Score',
      machineCount: 'Machine Count',
      signature: 'Signature',
      header: '(IIOC Score > 500)'
    },
    iiocs: {
      title: 'Machine IIOC Levels',
      lastExecuted: 'LastExecuted',
      description: 'Description',
      iOCLevel: 'IOCLevel',
      header: ''
    },
    incident: {
      title: 'Incidents',
      averageAlertRiskScore: 'Risk Score',
      _id: 'Id',
      name: 'Name',
      created: 'Created',
      status: 'Status',
      assignee: 'ASSIGNEE',
      alertCount: 'Alerts',
      priority: 'Priority',
      header: ''
    },
    alerts: {
      title: 'Alerts',
      risk_score: 'Risk Score',
      source: 'Source',
      name: 'Name',
      numEvents: 'Events',
      severity: 'Severity',
      created: 'Created',
      id: 'Incident ID',
      timestamp: 'timestamp',
      header: ''
    },
    list: {
      title: 'list',
      createdByUser: 'Author',
      createdTimeStamp: 'Created',
      lastModifiedTimeStamp: 'Updated',
      dataSourceDescription: 'Description',
      dataSourceName: 'Name',
      data: 'Data'
    },
    lc: {
      reviewStatus: 'Review Status',
      status: 'Status',
      modifiedDate: 'Modified Date',
      reviewer: 'Reviewer',
      riskConfirmation: 'Risk Confirmation',
      safe: 'Safe',
      unsafe: 'Unsafe',
      unknown: 'Unknown',
      confidenceStatus: 'Confidence Status',
      high: 'High',
      med: 'Medium',
      low: 'Low',
      riskTags: 'Risk Indicator Tags',
      sectionOrInd: 'Section / Industry',
      submit: 'Submit',
      commActivity: 'Community Activity',
      firstSeen: 'First Seen',
      activitySS: 'Activity Snapshot',
      communityTrend: 'Trending Community Activity (Last 30 Days)',
      submitTrend: 'Trending Submission Activity (Last 30 Days)',
      communityActivityDesc1: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span> of the Community seen <span class="rsa-context-panel__liveconnect__entity">{{value}}</span>',
      communityActivityDesc2: 'Of the <span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span> seen, <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span> of the Community submitted feedback',
      submittedActivityDesc1: 'Of the <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span> submitted feedback:',
      submittedActivityDesc2: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__high-risk">{{highrisk}}%</span> marked High Risk',
      submittedActivityDesc3: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unsafe">{{unsafe}}%</span> marked Unsafe',
      submittedActivityDesc4: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__suspicious">{{suspicious}}%</span> marked Suspicious',
      submittedActivityDesc5: '(Not displayed in chart)',
      submittedActivityDesc6: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__safe">{{safe}}%</span> marked Safe',
      submittedActivityDesc7: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unknown">{{unknown}}%</span> marked Unknown',
      riskIndicators: 'Risk Indicators',
      identity: 'Identity',
      asn: 'Autonomous System Number(ASN)',
      prefix: 'Prefix',
      countryCode: 'Country Code',
      countryName: 'Country Name',
      registrant: 'Organization',
      fileDate: 'Date',
      fileName: 'FILE NAME',
      fileSize: 'FILE SIZE',
      md5: 'MD5',
      compileTime: 'COMPILE TIME',
      sh1: 'SH1',
      mimeType: 'MIME TYPE',
      sh256: 'SH256',
      certificateInfo: 'Certificate Information',
      certIssuer: 'Certificate Issuer',
      certSubject: 'Certificate Subject',
      certSerial: 'Certificate Serial Number',
      certSigAlgo: 'Signature Algorithm',
      certThumbprint: 'Certificate Counter Signature',
      certNotValidBefore: 'Certificate Not Valid Before',
      certNotValidAfter: 'Certificate Not Valid After',
      whois: 'WHOIS',
      whoisCreatedDate: 'Created date',
      whoisUpdatedDate: 'Updated date',
      whoisExpiredDate: 'Expired date',
      whoisRegType: 'Type',
      whoisRegName: 'Name',
      whoisRegOrg: 'Organization',
      whoisRegStreet: 'Street',
      whoisRegCity: 'city',
      whoisRegState: 'State',
      whoisPostalCode: 'Postal Code',
      whoisCountry: 'Country',
      whoisPhone: 'Phone',
      wohisFax: 'Fax',
      whoisEmail: 'Email',
      domain: 'Domain',
      ipAddress: 'IP Address',
      error: 'Error fetching Live Connect Data'
    }
  },
  about: {
    title: 'About',
    appName: 'App',
    appVersion: 'Version'
  },
  journal: {
    title: 'Notes',
    milestones: {
      RECONNAISSANCE: 'Reconnaissance',
      DELIVERY: 'Delivery',
      EXPLOITATION: 'Exploitation',
      INSTALLATION: 'Installation',
      COMMAND_AND_CONTROL: 'Command and control',
      ACTION_ON_OBJECTIVE: 'Action on objective',
      CONTAINMENT: 'Containment',
      ERADICATION: 'Eradication',
      CLOSURE: 'Closure'
    },
    new: {
      notePlaceholder: 'Add Note Here...',
      attachFile: 'Attach file',
      milestone: 'Investigation milestones',
      addNote: 'Add Note'
    },
    delete: {
      dialog: 'Are you sure you want to delete the journal entry?'
    },
    sort: {
      title: 'Show',
      myNotes: 'My Notes',
      allNotes: 'All Notes'
    }
  },
  incident: {
    sortFields: {
      sortByLabel: 'Sort By:',
      alertCount: 'Alerts',
      assigneeName: 'Assignee',
      dateCreated: 'Date Created',
      lastUpdated: 'Date Updated',
      id: 'Incident ID',
      priority: 'Priority',
      riskScore: 'Risk Score'
    },
    fields: {
      id: 'ID',
      name: 'Title',
      status: 'Status',
      assignee: 'Assignee',
      priority: 'Priority',
      description: 'Description',
      createdDate: 'Created',
      createdBy: 'Rule',
      lastUpdated: 'Updated',
      riskScore: 'Risk Score',
      averageAlertRiskScore: 'Avg. Risk',
      alertCount: 'Alerts',
      sources: 'Sources',
      categories: 'Categories',
      journal: 'Journal',
      events: 'Events',
      createdTimestamp: 'created ',
      updatedTimestamp: 'updated ',
      groupBySourceIp: 'Source IP',
      groupByDestinationIp: 'Dest IP'
    },
    list: {
      id: 'ID',
      name: 'Name',
      createdDate: 'Date created',
      status: 'Status',
      priority: 'Priority',
      riskScore: 'Risk Score',
      assignee: 'Assignee',
      alertCount: 'Alerts',
      sources: 'Source',
      filters: {
        reset: 'Reset Filters',
        sources: 'Select sources to filter by',
        assignees: 'Select assignees to filter by'
      }
    },
    queue: {
      myQueue: 'My Incidents',
      allIncidents: 'All Incidents'
    },
    details: {
      createdOn: 'created on',
      updatedOn: 'updated on',
      sourceIp: 'Source IP',
      destinationIp: 'Destination IP',
      actions: 'Incident Actions',
      closeIncident: 'Close Incident',
      escalateIncident: 'Escalate Incident',
      alertsGrid: {
        title: 'Alerts List ({{count}})',
        severity: 'Severity',
        dateCreated: 'Date Created',
        name: 'Name',
        events: 'Events',
        host: 'Host',
        domain: 'Domain',
        source: 'Source'
      },
      storyline: {
        header: 'Storyline',
        to: 'to',
        lookup: {
          ip2host: 'Host is related to source ip'
        },
        c2: {
          title: 'Detected C&C communication',
          titleScore: 'Domain risk score {{score}}',
          beaconingScore: 'The time intervals between communication events are very uniform',
          newDomain: 'Domain is relatively new to the environment',
          whoisAgeScore: 'Domain is relatively new based on the registration date {{age}} days ago',
          expiringDomain: 'The domain registration will expire relatively soon {{age}} days',
          rareDomain: 'The domain is rare in this environment',
          referrer: 'A high percentage of hosts connecting to the domain are not utilizing referrers',
          userAgent: 'A high percentage of hosts connecting to the domain are using a rare or no user agent'
        },
        ecat: {
          title: 'Risky process',
          riskScore: 'Module Risk Score {{score}}'
        },
        winauth: {
          title: 'Anomalous Windows authentication',
          titleScore: 'User risk score {{score}}',
          highServerScore: 'Abnormally high number of servers accessed today',
          newDeviceScore: 'Accessed an abnormally high number of new devices this week',
          newServerScore: 'Accessed an abnormally high number of new servers today',
          passTheHash: 'Potential "pass the hash" attack indicated by a new device followed by a new server',
          failedServerScore: 'Abnormally high number of servers with failed logins today',
          logonType: 'Accessed using a WIndows logon type they have rarely used in the past',
          aggregation: 'Accessed using a Windows logon type they have rarely used in the past'
        }
      },
      eventOverview: {
        title: 'event overview',
        content: 'content',
        service: 'service',
        type: 'type',
        domain: 'domain',
        networkEvent: 'network event',
        source: 'source',
        destination: 'destination',
        domainInfo: 'domain information',
        detector: 'detector',
        detectorDeviceClass: 'detector device class',
        detectorDeviceIpAddress: 'detector ip address',
        detectorProductName: 'detector product name',
        meta: 'event meta',
        devicePort: 'device port',
        deviceMacAddress: 'device mac address',
        geolocationInfo: 'geolocation information',
        user: 'user',
        country: 'country',
        city: 'city',
        organization: 'organization',
        domainRegistrar: 'domain registrar',
        registrantName: 'registrant name',
        notAvailable: 'Not Available',
        investigate: 'Investigate',
        badges: {
          beaconBehavior: {
            label: 'beacon behavior',
            description: 'A high score indicates that the communications between this source IP and this domain are highly regular and therefore suspected Command and Control.'
          },
          domainAge: {
            label: 'domain age',
            description: 'A high score indicates that this domain is relatively new based on the registration date found at the registrar.'
          },
          expiringDomain: {
            label: 'expiring Domain',
            description: 'A high score means that the likelihood the domain will expire soon is high.'
          },
          rareDomain: {
            label: 'rare domain',
            description: 'A high score indicates that relatively few source IPs have connected to this domain on this network in the last week.'
          },
          noReferrers: {
            label: 'no referers',
            description: 'A high score indicates that a relatively low percentage of the IPs connecting to this domain have used referers.'
          },
          rareUserAgent: {
            label: 'rare user agent',
            description: 'A high score indicates that the domain has a high percentage of IPs using a rare user agent.'
          }
        },
        relatedLinks: 'related links',
        relatedLinkOptions: {
          investigate_destination_domain: 'Investigate Destination Domain',
          investigate_original_event: 'Investigate Original Event',
          investigate_dst_ip: 'Investigate Destination IP Address',
          investigate_src_ip: 'Investigate Source IP Address',
          investigate_device_ip: 'Investigate Device IP Address',
          investigate_session: 'Investigate Session'
        }
      },
      overview: {
        title: 'Overview',
        about: 'About Incident',
        categoryTags: 'Category Tags',
        addCategoryTags: 'Click to add category tags'
      }
    },
    status: {
      0: 'New',
      1: 'Assigned',
      2: 'In Progress',
      3: 'Remediation Requested',
      4: 'Remediation Complete',
      5: 'Closed',
      6: 'False Positive'
    },
    priority: {
      0: 'Low',
      1: 'Medium',
      2: 'High',
      3: 'Critical'
    },
    assignee: {
      none: '(Unassigned)'
    },
    description: {
      none: 'None'
    },
    emptyNewMessage: 'No new incidents',
    emptyMessage: 'No incidents',
    emptyInProgressMessage: 'No incidents being worked on',
    bulkEdit: {
      statusSelectListLabel: 'Status',
      assigneeSelectListLabel: 'Assignee',
      prioritySelectListLabel: 'Priority',
      saveButton: 'Save',
      cancelButton: 'Cancel',
      modalOkButtonLabel: 'OK',
      successfulUpdateMessage: {
        'one': '{{totalFields}} record updated successfully',
        'other': '{{totalFields}} records updated successfully'
      },
      deleteConfirmation: 'Are you sure?',
      areYouSureConfirmation: {
        'one': 'Please confirm you want to delete this incident. Once this incident is deleted, it cannot be recovered.',
        'other': 'Please confirm you want to delete these incidents.  Once these incidents are deleted, they cannot be recovered.'
      },
      successfulDeleteMessage: {
        'one': '{{totalFields}} incident successfully deleted',
        'other': '{{totalFields}}  incidents successfully deleted'
      }
    }
  },
  sort: {
    ascending: 'Ascending',
    descending: 'Descending'
  },
  time: {
    abbrev: {
      hour: 'H',
      day: 'D',
      week: 'W',
      month: 'M'
    },
    lastHour: 'Last Hour',
    last24Hours: 'Last 24 Hours',
    last7Days: 'Last 7 Days',
    last30Days: 'Last 30 Days'
  }
});
