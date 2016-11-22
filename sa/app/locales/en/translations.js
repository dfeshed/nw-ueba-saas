import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/en/translations';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, {
  application: {
    title: 'NetWitness',
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
      title: 'Meta'
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
      signature: 'Signature'
    },
    iiocs: {
      title: 'Machine IIOC Levels',
      iiocLevel0: 'Iioc Level 0',
      iiocLevel1: 'Iioc Level 1',
      iiocLevel2: 'Iioc Level 2',
      iiocLevel3: 'Iioc Level 3'
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
      priority: 'Priority'
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
      timestamp: 'timestamp'
    },
    list: {
      title: 'list',
      createdByUser: 'Author',
      createdTimeStamp: 'Created',
      lastModifiedTimeStamp: 'Updated',
      dataSourceDescription: 'Description',
      dataSourceName: 'Name'
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
      whoisEmail: 'Email'
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
      assigneeFirstLastName: 'Assignee',
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
        to: 'to'
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
      unsavedChangesModalHeader: 'Unsaved Changes',
      unsavedChangesModalMessage: 'You have unsaved changes. Please save incident changes before filtering.'
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
