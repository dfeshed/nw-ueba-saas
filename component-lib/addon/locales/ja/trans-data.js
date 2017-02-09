export default {
  languages: {
    en: 'ja_English',
    'en-us': 'ja_English',
    ja: 'ja_Japanese'
  },
  forms: {
    cancel: 'ja_Cancel',
    submit: 'ja_Submit',
    reset: 'ja_Reset',
    apply: 'ja_Apply',
    ok: 'ja_OK',
    delete: 'ja_Delete',
    save: 'ja_Save'
  },
  tables: {
    noResults: 'ja_No Results'
  },
  login: {
    username: 'ja_Username',
    password: 'ja_Password',
    login: 'ja_Login',
    logout: 'ja_Logout',
    lostPasswordLink: 'ja_Lost Password?',
    genericError: 'ja_Error: Please try again',
    unAuthorized: 'ja_Invalid credentials',
    badCredentials: 'ja_Invalid credentials',
    userLocked: 'ja_User account is locked',
    userDisabled: 'ja_User account is disabled',
    userExpired: 'ja_User account has expired',
    authServerNotFound: 'ja_There was an error while authenticating your credentials.',
    lostPassword: {
      title: 'ja_Lost Password Recovery',
      description: 'ja_Please submit your username.'
    },
    thankYou: {
      title: 'ja_Thank You!',
      description: 'ja_A password reset has been sent to the registered user\'s email account.',
      back: 'ja_Return to Login'
    }
  },
  userPreferences: {
    preferences: 'ja_User Preferences',
    personalize: 'ja_Personalize your experience',
    signOut: 'ja_Sign Out',
    version: 'ja_Version 11.0.0',
    username: 'ja_Username',
    email: 'ja_Email',
    language: 'ja_Language',
    timeZone: 'ja_Time Zone',
    dateFormat: {
      label: 'ja_Date Format',
      dayFirst: 'ja_DD/MM/YYYY',
      monthFirst: 'ja_MM/DD/YYYY',
      yearFirst: 'ja_YYYY/MM/DD'
    },
    timeFormat: {
      label: 'ja_Time Format',
      twelveHour: 'ja_12hr',
      twentyFourHour: 'ja_24hr'
    },
    defaultLandingPage: {
      label: 'ja_Default Landing Page',
      monitor: 'ja_Monitor',
      investigate: 'ja_Investigate',
      investigateClassic: 'ja_Investigate Classic',
      dashboard: 'ja_Monitor',
      live: 'ja_Configure',
      respond: 'ja_Respond',
      admin: 'ja_Admin'
    }
  },
  ipConnections: {
    ipCount: '(ja_{{count}} IPs)',
    clickToCopy: 'ja_Click to copy IP address',
    sourceIp: 'ja_Source IP',
    destinationIp: 'ja_Destination IP'
  },
  list: {
    all: '(ja_All)',
    items: 'ja_items',
    packets: 'ja_packets',
    packet: 'ja_packet',
    of: 'ja_of',
    sessions: 'ja_sessions'
  },
  updateLabel: {
    'one': 'ja_update',
    'other': 'ja_updates'
  },
  recon: {
    files: {
      fileName: 'ja_File Name',
      extension: 'ja_Extension',
      mimeType: 'ja_MIME Type',
      fileSize: 'ja_File Size',
      hashes: 'ja_Hashes',
      noFiles: 'ja_There are no files available for this event.',
      linkFile: 'ja_This file is in another session.<br>Click the file link to view the related session in a new tab.'
    },
    error: {
      generic: 'ja_An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'ja_This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.'
    },
    toggles: {
      header: 'ja_Show/Hide Header',
      request: 'ja_Show/Hide Request',
      response: 'ja_Show/Hide Response',
      topBottom: 'ja_Top/Bottom View',
      sideBySide: 'ja_Side by Side View',
      meta: 'ja_Show/Hide Meta',
      settings: 'ja_Settings',
      expand: 'ja_Expand/Contract View',
      close: 'ja_Close Reconstruction'
    }
  },
  memsize: {
    B: 'ja_bytes',
    KB: 'ja_KB',
    MB: 'ja_MB',
    GB: 'ja_GB',
    TB: 'ja_TB'
  },
  previousMonth: 'ja_Previous Month',
  nextMonth: 'ja_Next Month',
  months() {
    return [
      'ja_January',
      'ja_February',
      'ja_March',
      'ja_April',
      'ja_May',
      'ja_June',
      'ja_July',
      'ja_August',
      'ja_September',
      'ja_October',
      'ja_November',
      'ja_December'
    ];
  },
  monthsShort() {
    return [
      'ja_Jan',
      'ja_Feb',
      'ja_Mar',
      'ja_Apr',
      'ja_May',
      'ja_Jun',
      'ja_Jul',
      'ja_Aug',
      'ja_Sep',
      'ja_Oct',
      'ja_Nov',
      'ja_Dec'
    ];
  },
  weekdays() {
    return [
      'ja_Sunday',
      'ja_Monday',
      'ja_Tuesday',
      'ja_Wednesday',
      'ja_Thursday',
      'ja_Friday',
      'ja_Saturday'
    ];
  },
  weekdaysShort() {
    return [
      'ja_Sun',
      'ja_Mon',
      'ja_Tue',
      'ja_Wed',
      'ja_Thu',
      'ja_Fri',
      'ja_Sat'
    ];
  },
  weekdaysMin() {
    return [
      'ja_Sun',
      'ja_Mon',
      'ja_Tue',
      'ja_Wed',
      'ja_Thu',
      'ja_Fri',
      'ja_Sat'
    ];
  },
  midnight: 'ja_Midnight',
  noon: 'ja_Noon',
  investigate: {
    title: 'ja_Investigate',
    loading: 'ja_Loading',
    loadMore: 'ja_Load More',
    tryAgain: 'ja_Try Again',
    service: 'ja_Service',
    timeRange: 'ja_Time Range',
    filter: 'ja_Filter',
    size: {
      bytes: 'ja_bytes',
      KB: 'ja_KB',
      MB: 'ja_MB',
      GB: 'ja_GB',
      TB: 'ja_TB'
    },
    medium: {
      network: 'ja_Network',
      log: 'ja_Log',
      correlation: 'ja_Correlation'
    },
    empty: {
      title: 'ja_No events found.',
      description: 'ja_Your filter criteria did not match any records.'
    },
    error: {
      title: 'ja_Unable to load data.',
      description: 'ja_An unexpected error occurred when attempting to fetch the data records.'
    },
    meta: {
      title: 'ja_Meta',
      clickToOpen: 'ja_Click to open'
    },
    events: {
      title: 'ja_Events',
      error: 'ja_An unexpected error occurred when executing this query.'
    },
    services: {
      loading: 'ja_Loading list of available services',
      empty: {
        title: 'ja_Unable to find services.',
        description: 'ja_No Brokers, Concentrators, or other services were detected. This may be due to a configuration or connectivity issue.'
      },
      error: {
        title: 'ja_Unable to load services.',
        description: 'ja_Unexpected error loading the list of Brokers, Concentrators, and other services to investigate. This may be due to a configuration or connectivity issue.'
      }
    },
    customQuery: {
      title: 'ja_Enter a query.'
    }
  },
  live: {
    title: 'ja_Live Content',
    home: 'ja_Home',
    details: 'ja_Live content and general home for live goes here.',
    deployed: {
      title: 'ja_Deployed'
    },
    jobs: {
      title: 'ja_Jobs'
    },
    updates: {
      title: 'ja_Updates'
    },
    custom: {
      title: 'ja_Custom'
    },
    search: {
      title: 'ja_Find',
      details: 'ja_Live Search content goes here.',
      searchCriteria: 'ja_Search Criteria',
      matchingResources: 'ja_Matching Resources',
      resourceDetailsTitle: 'ja_Resource Details',
      advanced: 'ja_Advanced',
      dropdownPlaceholder: 'ja_All',
      deploy: 'ja_Deploy',
      export: 'ja_Export',
      import: 'ja_Import',
      deployModalHeader: 'ja_Deploy Selected Resources',
      exportModalHeader: 'ja_Export Selected Resources',
      showingResultsMessage: 'ja_Showing results',
      showingResultsMessageOf: 'ja_of',
      fields: {
        categories: 'ja_Categories',
        keywords: 'ja_Keywords',
        resourceType: 'ja_Resource Type',
        medium: 'ja_Medium',
        createdDate: 'ja_Created',
        updatedDate: 'ja_Updated',
        requiredMetaKeys: 'ja_Required Meta Keys',
        generatedMetaValues: 'ja_Generated Meta Values',
        select: '',
        remove: 'ja_Remove',
        subscribed: 'ja_Subscribed',
        resourceName: 'ja_Name',
        description: 'ja_Description',
        nwVersion: 'ja_NetWitness',
        version: 'ja_Version'
      }
    },
    manage: {
      title: 'ja_Configure',
      details: 'ja_Live Manage Resource content goes here.'
    },
    feeds: {
      title: 'ja_Feeds',
      details: 'ja_Live Feeds content goes here.'
    }
  },
  context: {
    title: 'ja_Context',
    header: {
      overview: 'ja_overview',
      iioc: 'ja_iioc',
      modules: 'ja_modules',
      users: 'ja_users',
      categoryTags: 'ja_category tags',
      incidents: 'ja_Incidents',
      alerts: 'ja_Alerts',
      files: 'ja_Files',
      lists: 'ja_Lists',
      feeds: 'ja_Feeds',
      liveConnect: 'ja_Live Connect',
      unsafe: 'ja_Unsafe'
    },
    toolbar: {
      investigate: 'ja_Investigate',
      endpoint: 'ja_NETWITNESS ENDPOINT',
      googleLookup: 'ja_Google Lookup',
      virusTotal: 'ja_VirusTotal Lookup',
      addToList: 'ja_Add to List'
    },
    hostSummary: {
      title: 'ja_Host Summary',
      riskScore: 'ja_Risk Score',
      modulesCount: 'ja_# of Modules',
      iioc0: 'ja_Iioc 0',
      iioc1: 'ja_Iioc 1',
      lastUpdated: 'ja_Last Updated',
      adminStatus: 'ja_Admin Status',
      lastLogin: 'ja_Last Login',
      macAddress: 'ja_Mac Address',
      operatingSystem: 'ja_Operating System',
      machineStatus: 'ja_Machine Status',
      ipAddress: 'ja_IPAddress'
    },
    modules: {
      title: 'ja_Top Suspicious Modules',
      iiocScore: 'ja_Iioc Score',
      moduleName: 'ja_Module Name',
      analyticsScore: 'ja_Analytics Score',
      machineCount: 'ja_Machine Count',
      signature: 'ja_Signature'
    },
    iiocs: {
      title: 'ja_Machine IIOC Levels',
      lastExecuted: 'js_LastExecuted',
      description: 'ja_Description',
      iOCLevel: 'ja_IOCLevel',
      header: ''
    },
    incident: {
      title: 'ja_Incidents',
      averageAlertRiskScore: 'ja_Risk Score',
      _id: 'ja_Id',
      name: 'ja_Name',
      created: 'ja_Created',
      status: 'ja_Status',
      assignee: 'ja_ASSIGNEE',
      alertCount: 'ja_Alerts',
      priority: 'ja_Priority',
      header: ''
    },
    alerts: {
      title: 'ja_Alerts',
      risk_score: 'ja_Risk Score',
      source: 'ja_Source',
      name: 'ja_Name',
      numEvents: 'ja_Events',
      severity: 'ja_Severity',
      created: 'ja_Created',
      id: 'ja_Incident ID',
      timestamp: 'ja_timestamp',
      header: ''
    },
    list: {
      title: 'ja_list',
      createdByUser: 'ja_Author',
      createdTimeStamp: 'ja_Created',
      lastModifiedTimeStamp: 'ja_Updated',
      dataSourceDescription: 'ja_Description',
      dataSourceName: 'ja_Name',
      data: 'ja_Data'
    },
    lc: {
      reviewStatus: 'ja_Review Status',
      status: 'ja_Status',
      modifiedDate: 'ja_Modified Date',
      reviewer: 'ja_Reviewer',
      riskConfirmation: 'ja_Risk Confirmation',
      safe: 'ja_Safe',
      unsafe: 'ja_Unsafe',
      unknown: 'ja_Unknown',
      confidenceStatus: 'ja_Confidence Status',
      high: 'ja_High',
      med: 'ja_Medium',
      low: 'ja_Low',
      riskTags: 'ja_Risk Indicator Tags',
      sectionOrInd: 'ja_Section / Industry',
      submit: 'ja_Submit',
      commActivity: 'ja_Community Activity',
      firstSeen: 'ja_First Seen',
      activitySS: 'ja_Activity Snapshot',
      communityTrend: 'ja_Trending Community Activity (Last 30 Days)',
      submitTrend: 'ja_Trending Submission Activity (Last 30 Days)',
      communityActivityDesc1: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span> ja_of the Community seen <span class="rsa-context-panel__liveconnect__entity">{{value}}</span>',
      communityActivityDesc2: 'ja_Of the <span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span> ja_seen, <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span> ja_of the Community submitted feedback',
      submittedActivityDesc1: 'ja_Of the <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span> ja_submitted feedback:',
      submittedActivityDesc2: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__high-risk">{{highrisk}}%</span> ja_marked High Risk',
      submittedActivityDesc3: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unsafe">{{unsafe}}%</span> ja_marked Unsafe',
      submittedActivityDesc4: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__suspicious">{{suspicious}}%</span> ja_marked Suspicious',
      submittedActivityDesc5: 'ja_(Not displayed in chart)',
      submittedActivityDesc6: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__safe">{{safe}}%</span> ja_marked Safe',
      submittedActivityDesc7: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unknown">{{unknown}}%</span> ja_marked Unknown',
      riskIndicators: 'ja_Risk Indicators',
      identity: 'ja_Identity',
      asn: 'ja_Autonomous System Number(ASN)',
      prefix: 'ja_Prefix',
      countryCode: 'ja_Country Code',
      countryName: 'ja_Country Name',
      registrant: 'ja_Organization',
      fileDate: 'ja_Date',
      fileName: 'ja_FILE NAME',
      fileSize: 'ja_FILE SIZE',
      md5: 'ja_MD5',
      compileTime: 'ja_COMPILE TIME',
      sh1: 'ja_SH1',
      mimeType: 'ja_MIME TYPE',
      sh256: 'ja_SH256',
      certificateInfo: 'ja_Certificate Information',
      certIssuer: 'ja_Certificate Issuer',
      certSubject: 'ja_Certificate Subject',
      certSerial: 'ja_Certificate Serial Number',
      certSigAlgo: 'ja_Signature Algorithm',
      certThumbprint: 'ja_Certificate Counter Signature',
      certNotValidBefore: 'ja_Certificate Not Valid Before',
      certNotValidAfter: 'ja_Certificate Not Valid After',
      whois: 'ja_WHOIS',
      whoisCreatedDate: 'ja_Created date',
      whoisUpdatedDate: 'ja_Updated date',
      whoisExpiredDate: 'ja_Expired date',
      whoisRegType: 'ja_Type',
      whoisRegName: 'ja_Name',
      whoisRegOrg: 'ja_Organization',
      whoisRegStreet: 'ja_Street',
      whoisRegCity: 'ja_city',
      whoisRegState: 'ja_State',
      whoisPostalCode: 'ja_Postal Code',
      whoisCountry: 'ja_Country',
      whoisPhone: 'ja_Phone',
      wohisFax: 'ja_Fax',
      whoisEmail: 'ja_Email',
      domain: 'ja_Domain',
      ipAddress: 'ja_IP Address',
      errorMsg: 'ja_Error occurred while fetching Live Connect data: ',
      riskAssessment: 'ja_Live Connect Risk Assessment',
      riskReason: 'ja_Risk Reasons',
      highRiskDesc: 'ja_Indicator seen as high risk and deserves focused attention',
      safeRiskDesc: 'ja_Research and analysis shows indicators to be trusted resources',
      unsafeRiskDesc: 'ja_Research and analysis shows resource to be untrusted',
      unknownRiskDesc: 'ja_Inconclusive results from any available information, research, and analysis',
      suspiciousRiskDesc: 'ja_Research and analysis indicates potentially threatening activity',
      riskFeedback: 'ja_Risk Assessment Feedback',
      noDataMsg: 'ja_No data from Live Connect'
    },
    error: {
      error: 'ja_Error processing stream call for context lookup.',
      dataSource: 'ja_Error processing stream call for data source.',
      noData: 'ja_No context data present for this DataSource.',
      'datasource.query.not.supported': 'ja_Context data is query not supported for given meta.',
      'transport.http.read.failed': 'ja_Context data is not available as the data source is down.',
      'transport.ad.read.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.init.failed': 'ja_Context data is not available as the connection to the data source is timed out.',
      'transport.not.found': 'ja_Context data is not available as the data source is down.',
      'transport.create.failed': 'ja_Context data is not available as the Database is down.',
      'transport.refresh.failed': 'ja_Context data is not available as the Database is down.',
      'transport.connect.failed': 'ja_Context data is not available as the Data Source is down.'
    },
    footer: {
      viewAll: 'ja_VIEW All'
    }
  }
};
