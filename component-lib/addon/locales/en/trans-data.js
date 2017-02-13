export default {
  pageTitle: '{{section}} - Netwitness Suite',
  languages: {
    en: 'English',
    'en-us': 'English',
    ja: 'Japanese'
  },
  forms: {
    cancel: 'Cancel',
    submit: 'Submit',
    reset: 'Reset',
    apply: 'Apply',
    ok: 'OK',
    delete: 'Delete',
    save: 'Save'
  },
  tables: {
    noResults: 'No Results'
  },
  login: {
    username: 'Username',
    password: 'Password',
    login: 'Login',
    logout: 'Logout',
    lostPasswordLink: 'Lost Password?',
    genericError: 'Error: Please try again',
    unAuthorized: 'Invalid credentials',
    badCredentials: 'Invalid credentials',
    userLocked: 'User account is locked',
    userDisabled: 'User account is disabled',
    userExpired: 'User account has expired',
    authServerNotFound: 'There was an error while authenticating your credentials.',
    lostPassword: {
      title: 'Lost Password Recovery',
      description: 'Please submit your username.'
    },
    thankYou: {
      title: 'Thank You!',
      description: 'A password reset has been sent to the registered user\'s email account.',
      back: 'Return to Login'
    }
  },
  userPreferences: {
    preferences: 'User Preferences',
    personalize: 'Personalize your experience',
    signOut: 'Sign Out',
    version: 'Version',
    username: 'Username',
    email: 'Email',
    language: 'Language',
    timeZone: 'Time Zone',
    dateFormat: {
      label: 'Date Format',
      dayFirst: 'DD/MM/YYYY',
      monthFirst: 'MM/DD/YYYY',
      yearFirst: 'YYYY/MM/DD'
    },
    timeFormat: {
      label: 'Time Format',
      twelveHour: '12hr',
      twentyFourHour: '24hr'
    },
    defaultLandingPage: {
      label: 'Default Landing Page',
      monitor: 'Monitor',
      investigate: 'Investigate',
      investigateClassic: 'Investigate Classic',
      dashboard: 'Monitor',
      live: 'Configure',
      respond: 'Respond',
      admin: 'Admin'
    }
  },
  ipConnections: {
    ipCount: '({{count}} IPs)',
    clickToCopy: 'Click to copy IP address',
    sourceIp: 'Source IP',
    destinationIp: 'Destination IP'
  },
  list: {
    all: '(All)',
    items: 'items',
    packets: 'packets',
    packet: 'packet',
    of: 'of',
    sessions: 'sessions'
  },
  updateLabel: {
    'one': 'update',
    'other': 'updates'
  },
  recon: {
    error: {
      generic: 'An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.'
    },
    files: {
      fileName: 'File Name',
      extension: 'Extension',
      mimeType: 'MIME Type',
      fileSize: 'File Size',
      hashes: 'Hashes',
      noFiles: 'There are no files available for this event.',
      linkFile: 'This file is in another session.<br>Click the file link to view the related session in a new tab.'
    },
    toggles: {
      header: 'Show/Hide Header',
      request: 'Show/Hide Request',
      response: 'Show/Hide Response',
      topBottom: 'Top/Bottom View',
      sideBySide: 'Side by Side View',
      meta: 'Show/Hide Meta',
      settings: 'Settings',
      expand: 'Expand/Contract View',
      close: 'Close Reconstruction'
    }
  },
  memsize: {
    B: 'bytes',
    KB: 'KB',
    MB: 'MB',
    GB: 'GB',
    TB: 'TB'
  },
  previousMonth: 'Previous Month',
  nextMonth: 'Next Month',
  months() {
    return [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December'
    ];
  },
  monthsShort() {
    return [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec'
    ];
  },
  weekdays() {
    return [
      'Sunday',
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday'
    ];
  },
  weekdaysShort() {
    return [
      'Sun',
      'Mon',
      'Tue',
      'Wed',
      'Thu',
      'Fri',
      'Sat'
    ];
  },
  weekdaysMin() {
    return [
      'Sun',
      'Mon',
      'Tue',
      'Wed',
      'Thu',
      'Fri',
      'Sat'
    ];
  },
  midnight: 'Midnight',
  noon: 'Noon',
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
        select: '',
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
  context: {
    title: 'Context for',
    lastUpdated: 'Last Updated:',
    timeWindow: 'Time Window:',
    header: {
      overview: 'overview',
      iioc: 'iioc',
      users: 'users',
      categoryTags: 'category tags',
      modules: 'modules',
      incidents: 'Incidents',
      alerts: 'Alerts',
      files: 'Files',
      lists: 'Lists',
      feeds: 'Feeds',
      liveConnect: 'Live Connect',
      unsafe: 'Unsafe'
    },
    toolbar: {
      investigate: 'Investigate',
      endpoint: 'NetWitness Endpoint',
      googleLookup: 'Google Lookup',
      virusTotal: 'VirusTotal Lookup',
      addToList: 'Add to List'
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
      machineStatus: 'Machine Status',
      ipAddress: 'IPAddress'
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
      lastExecuted: 'LastExecuted',
      description: 'Description',
      iOCLevel: 'IOCLevel',
      header: ''
    },
    incident: {
      title: 'Incidents',
      averageAlertRiskScore: 'Severity',
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
      risk_score: 'Severity',
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
      title: 'List',
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
      errorMsg: 'Error occurred while fetching Live Connect data: ',
      riskAssessment: 'Live Connect Risk Assessment',
      riskReason: 'Risk Reasons',
      highRiskDesc: 'Indicator seen as high risk and deserves focused attention',
      safeRiskDesc: 'Research and analysis shows indicators to be trusted resources',
      unsafeRiskDesc: 'Research and analysis shows resource to be untrusted',
      unknownRiskDesc: 'Inconclusive results from any available information, research, and analysis',
      suspiciousRiskDesc: 'Research and analysis indicates potentially threatening activity',
      riskFeedback: 'Risk Assessment Feedback',
      noDataMsg: 'No data from Live Connect'
    },
    error: {
      error: 'Error processing stream call for context lookup.',
      dataSource: 'Error processing stream call for data source.',
      noData: 'No context data present for this DataSource.',
      'datasource.query.not.supported': 'Context data is query not supported for given meta.',
      'transport.http.read.failed': 'Context data is not available as the data source is down.',
      'transport.ad.read.failed': 'Context data is not available as the data source is not reachable.',
      'transport.init.failed': 'Context data is not available as the connection to the data source is timed out.',
      'transport.not.found': 'Context data is not available as the data source is down.',
      'transport.create.failed': 'Context data is not available as the Data Source is down.',
      'transport.refresh.failed': 'Context data is not available as the Data Source is down.',
      'transport.connect.failed': 'Context data is not available as the Data Source is down.'
    },
    footer: {
      viewAll: 'VIEW All'
    }
  }
};
