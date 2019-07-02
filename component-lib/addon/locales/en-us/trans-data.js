export default {
  appTitle: 'NetWitness Platform',
  pageTitle: '{{section}} - NetWitness Platform',
  empty: '',
  languages: {
    en: 'English',
    'en-us': 'English',
    ja: 'Japanese'
  },
  monitor: {
    title: 'Monitor'
  },
  config: {
    title: 'Configure'
  },
  errorPages: {
    errorLabel: 'Error Description',
    back: 'Back',
    support: 'Support',
    documentation: 'Documentation',
    viewOnlineDocs: 'View online documentation for details about the error.',
    notFound: {
      errorDescription: 'Oops! The requested link was not found.',
      subtitle: 'Page Not Found',
      title: '404'
    },
    error: {
      errorDescription: 'We\'ve encountered an internal error and cannot proceed. Here are some reasons why you might be seeing this error page:',
      bulletA: 'The Legacy NetWitness UI may still be starting up. Please wait a few minutes and try again.',
      bulletB: 'If after a few minutes you are still receiving this message, please check to make sure that the NetWitness Legacy UI is properly running. ',
      subtitle: 'Internal Server Error'
    },
    permissionDenied: {
      errorDescription: '403 Forbidden is an HTTP status code returned by a web server when a user requests a web page or media that the server does not allow them to. In other words, the server can be reached, but the server declined to allow access to the page.',
      subtitle: 'You cannot access the requested page.',
      title: 'Forbidden.'
    }
  },
  errorDictionaryMessages: {
    code: 'Error Code',
    investigateEvents: {
      'MISCONFIGURED_SERVICE_CERTIFICATE': 'No authorization server certificates configured. code: {{errorCode}} - {{type}}',
      'ACCESS_DENIED': 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions. code: {{errorCode}} - {{type}}',
      'SESSION_REMOVED': 'Data not loaded. It has rolled out of the database. code: {{errorCode}} - {{type}}',
      'SESSION_NOT_AVAILABLE': 'Remote content for the session is no longer available. code: {{errorCode}} - {{type}}',
      'PACKETS_NOT_FOUND': 'Data not loaded. It has rolled out of the database. code: {{errorCode}} - {{type}}',
      'DEVICE_OFFLINE': 'Data not loaded. The underlying service is offline. code: {{errorCode}} - {{type}}',
      'GENERIC': 'An unexpected error has occurred attempting to retrieve this data. If further details are available, they can be found in the console. code: {{errorCode}} - {{type}}'
    }
  },
  passwordPolicy: {
    passwordPolicyRequestError: 'There was an issue retrieving your password policy.',
    passwordPolicyMinChars: 'Must be at least {{passwordPolicyMinChars}} characters',
    passwordPolicyMinNumericChars: 'Must contain at least {{passwordPolicyMinNumericChars}} number(s) (0 through 9)',
    passwordPolicyMinUpperChars: 'Must have at least {{passwordPolicyMinUpperChars}} uppercase character(s)',
    passwordPolicyMinLowerChars: 'Must have at least {{passwordPolicyMinLowerChars}} lowercase character(s)',
    passwordPolicyMinNonLatinChars: 'Must contain at least {{passwordPolicyMinNonLatinChars}} Unicode alphabetic character(s) that are not uppercase or lowercase',
    passwordPolicyMinSpecialChars: 'Must contain at least {{passwordPolicyMinSpecialChars}} non-alphanumeric character(s): (~!@#$%^&*_-+=`|(){}[]:;"\'<>,.?/)',
    passwordPolicyCannotIncludeId: 'Your password cannot contain your username'
  },
  forms: {
    cancel: 'Cancel',
    submit: 'Submit',
    reset: 'Reset',
    apply: 'Apply',
    ok: 'OK',
    delete: 'Delete',
    save: 'Save',
    yes: 'Yes',
    no: 'No'
  },
  tables: {
    noResults: 'No Results',
    columnChooser: {
      filterPlaceHolder: 'Type to filter the list',
      noMatchingColumns: 'No matching columns'
    }
  },
  login: {
    username: 'Username',
    password: 'Password',
    login: 'Login',
    loggingIn: 'Logging In',
    logout: 'Logout',
    oldPassword: 'Old Password',
    newPassword: 'New Password',
    confirmPassword: 'Confirm Password',
    passwordMismatch: 'Password confirmation does not match.',
    passwordNoChange: 'New password cannot match your old password.',
    passwordChangeFailed: 'There was an issue while saving your password change. Please try again.',
    lostPasswordLink: 'Lost Password?',
    genericError: 'Authentication error. Please try again.',
    communicationError: 'The server could not be reached. Please contact your system admin.',
    userLocked: 'User account is locked',
    userDisabled: 'User account is disabled',
    userExpired: 'User account has expired',
    changePasswordLink: 'Change my password',
    changePasswordSoon: 'Please note that your password to the RSA NetWitness Server will expire in {{daysRemaining}} day(s). We encourage you to change the password before it expires. To change your password, click the preferences button on the top right of the application window.',
    changePasswordToday: 'Please note that your password to the RSA NetWitness Server will expire today. We encourage you to change the password before it expires. To change your password, click the preferences button on the top right of the application window.',
    lostPassword: {
      title: 'Lost Password Recovery',
      description: 'Please submit your username.'
    },
    thankYou: {
      title: 'Thank You!',
      description: 'A password reset has been sent to the registered user\'s email account.',
      back: 'Return to Login'
    },
    eula: {
      title: 'End User License Agreement',
      agree: 'Agree'
    },
    forcePassword: {
      warning: 'You will need to create a new password before you can log in.',
      changePassword: 'Change Password'
    }
  },
  dateTime: {
    start: 'Start Date/Time',
    end: 'End Date/Time',
    range: 'Range',
    years: 'years',
    months: 'months',
    days: 'days',
    hours: 'hours',
    minutes: 'minutes',
    seconds: 'seconds',
    duration: 'Calculated duration',
    isValid: 'The current date/time range is valid',
    yearIsEmpty: 'There is no year value',
    monthIsEmpty: 'There is no month value',
    dateIsEmpty: 'There is no day value',
    hourIsEmpty: 'There is no hour value',
    minuteIsEmpty: 'There is no minute value',
    secondIsEmpty: 'There is no seconds value',
    monthOutOfBounds: 'The month value is not valid',
    dateOutOfBounds: 'The day value is not valid',
    hourOutOfBounds: 'The hour value is not in the valid range (0-23)',
    hourOutOfBounds12Hour: 'The hour value is not in the valid range (1-12)',
    minuteOutOfBounds: 'The minute value is not in the valid range (0-59)',
    secondOutOfBounds: 'The seconds value is not in the valid range (0-59)',
    endBeforeStart: 'The end date/time occurs before the start date/time'
  },
  userPreferences: {
    preferences: 'User Preferences',
    personalize: 'Personalize your experience',
    signOut: 'Sign Out',
    version: 'Version',
    username: 'Username',
    email: 'Email',
    locale: {
      label: 'Language',
      error: 'There was an error attempting to save your Language selection. Please try again. If this issue persists, please contact your system admin.',
      fetchError: 'There was an error attempting to load your Language selection. Please try again. If this issue persists, please contact your system admin.'
    },
    timeZone: 'Time Zone',
    dateFormatError: 'There was an error attempting to save your Date Format selection. Please try again. If this issue persists, please contact your system admin.',
    landingPageError: 'There was an error attempting to save your Default Landing Page selection. Please try again. If this issue persists, please contact your system admin.',
    defaultInvestigatePageError: 'There was an error attempting to save your Default Investigate View selection. Please try again. If this issue persists, please contact your system admin.',
    timeFormatError: 'There was an error attempting to save your Time Format selection. Please try again. If this issue persists, please contact your system admin.',
    timezoneError: 'There was an error attempting to save your Time Zone selection. Please try again. If this issue persists, please contact your system admin.',
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
    theme: {
      title: 'Theme',
      dark: 'Dark',
      light: 'Light',
      error: 'There was an error attempting to save your Theme selection. Please try again. If this issue persists, please contact your system admin.'
    },
    defaultLandingPage: {
      label: 'Default Landing Page',
      monitor: 'Monitor',
      investigate: 'Investigate',
      investigateClassic: 'Investigate',
      dashboard: 'Monitor',
      live: 'Configure',
      respond: 'Respond',
      admin: 'Admin'
    },
    defaultInvestigatePage: {
      label: 'Default Investigate View',
      events: 'Events',
      eventAnalysis: 'Event Analysis',
      malware: 'Malware Analysis',
      navigate: 'Navigate',
      hosts: 'Hosts',
      files: 'Files',
      users: 'Users'
    }
  },
  queryBuilder: {
    cancelQuery: 'Cancel Query',
    delete: 'Delete selected filters',
    deleteFilter: 'Delete this filter',
    edit: 'Edit this filter',
    enterValue: 'Enter a single value',
    expensive: 'Performing this operation might take more time.',
    insertFilter: 'Insert new filter',
    noMatches: 'No matches found',
    open: 'Open in a new tab',
    placeholder: 'Enter individual statements consisting of a Meta Key, Operator, and Value (optional)',
    query: 'Query with filters',
    queryEvents: 'Query Events',
    querySelected: 'Query with selected filters',
    querySelectedNewTab: 'Query with selected filters in a new tab',
    metaNoMatch: 'All meta filtered out',
    operatorNoMatch: 'All operators filtered out',
    recentQueriesNoMatch: 'No recent queries',
    validationMessages: {
      time: 'You must enter a valid date.',
      text: 'Strings must be quoted with "',
      ipv4: 'You must enter an IPv4 address.',
      ipv6: 'You must enter an IPv6 address.',
      uint8: 'You must enter an 8-bit Integer.',
      uint16: 'You must enter a 16-bit Integer.',
      uint32: 'You must enter a 32-bit Integer.',
      uint64: 'You must enter a 64-bit Integer.',
      float32: 'You must enter a 32-bit Float.',
      mac: 'You must enter a MAC address.'
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
    titleBar: {
      titles: {
        endpoint: 'Endpoint Event Details',
        network: 'Network Event Details',
        log: 'Log Event Details'
      },
      views: {
        textView: 'Text View',
        text: 'Text Analysis',
        packet: 'Packet Analysis',
        file: 'File Analysis',
        web: 'Web',
        mail: 'Email Analysis'
      }
    },
    meta: {
      scroller: {
        of: 'of',
        results: 'results'
      }
    },
    textView: {
      compressToggleLabel: 'Display Compressed Payloads',
      compressToggleTitle: 'Display HTTP payloads as compressed or not',
      downloadCsv: 'Download CSV',
      downloadEndpointEvent: 'Download Endpoint',
      pivotToEndpoint: 'Pivot to Endpoint Thick Client',
      pivotToProcessAnalysis: 'Analyze Process',
      pivotToProcessAnalysisTitle: 'Process relationship and event view of source process',
      pivotToHostOverview: 'Pivot to Host Overview',
      pivotToEndpointTitle: 'Applicable for hosts with 4.x Endpoint agents installed, please install the Netwitness Endpoint Thick Client.',
      downloadJson: 'Download JSON',
      // downloadLog: 'Download Log',
      downloadText: 'Download Text',
      downloadXml: 'Download XML',
      headerShowing: 'Showing',
      singleMessageTruncated: ' (This message has been truncated)',
      isDownloading: 'Downloading...',
      rawEndpointHeader: 'Large meta values',
      rawLogHeader: 'Raw Log',
      renderingMore: 'Showing more...',
      renderRemaining: 'Rendering Remaining {{remainingPercent}}%..',
      showRemaining: 'Show Remaining {{remainingPercent}}%',
      endpointDetail: {
        performed: 'performed',
        on: 'on',
        presentIn: 'present in',
        madeConnectionTo: 'made a network connection to',
        resolvedTo: 'resolving to',
        from: 'from',
        runningAsService: 'is running as a service named',
        loadedInto: 'is loaded into',
        runningAsTask: 'is running as a task named',
        launchedBy: 'is launched by',
        hookedFunctionIn: 'hooked function in',
        hooked: 'hooked',
        colon: '::',
        loadedIn: 'loaded in',
        ran: 'ran',
        triggeredEventTypeIs: 'triggered event type is',
        machine: 'Scan Snapshot available in Host View',
        autorun: 'Set as Autorun',
        initd: 'is configured as Initd service',
        systemd: 'is configured as Systemd service',
        cron: 'is configured as cron job',
        daemon: 'is configured as Daemon'
      }
    },
    packetView: {
      commonFilePatternLabel: 'Common File Patterns',
      commonFilePatternTitle: 'Enable to highlight common file signature patterns',
      defaultDownloadPCAP: 'Download PCAP',
      downloadPCAP: 'Download PCAP',
      downloadPayload: 'Download Payloads',
      downloadPayload1: 'Download Request Payload',
      downloadPayload2: 'Download Response Payload',
      headerAttribute: 'Header Attribute',
      headerDisplayLabel: '{{label}} = {{displayValue}}',
      headerMeta: 'Header Meta',
      headerSignature: 'Interesting Bytes',
      isDownloading: 'Downloading...',
      noHexData: 'No HEX data was generated during content reconstruction.',
      noPayload: 'The event has no payload and the Display Payloads Only option is selected. To view the Packet Analysis for the event, disable the Display Payloads Only option.',
      payloadToggleLabel: 'Display Payloads Only',
      payloadToggleTitle: 'Removes packet headers & footers from display',
      renderingMore: 'Showing more...',
      stylizeBytesLabel: 'Shade Bytes',
      stylizeBytesTitle: 'Enable to help distinguish patterns within the data'
    },
    reconPager: {
      packetPagnationPageFirst: 'First',
      packetPagnationPagePrevious: 'Previous',
      packetPagnationPageNext: 'Next',
      packetPagnationPageLast: 'Last',
      packetsPerPageText: 'Packets Per Page'
    },
    fileView: {
      downloadFile: 'Download File',
      downloadFiles: 'Download Files ({{fileCount}})',
      isDownloading: 'Downloading...',
      downloadWarning: 'Warning: Files contain the original raw unsecured content. Use caution when opening or downloading files; they may contain malicious data.'
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
    error: {
      generic: 'An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.',
      noTextContentData: 'No text data was generated during content reconstruction. This could mean that the event data was corrupt or invalid. Check the other reconstruction views.',
      noTextContentDataWithCompressedPayloads: 'No text data was generated during content reconstruction. This could mean that the event data was corrupt or invalid. Try enabling the Display Compressed Payloads button or check the other reconstruction views.',
      permissionError: 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions.',
      noRawDataEndpoint: 'No text data was generated during content reconstruction. This could mean that the event data was corrupt/invalid, or that an administrator has disabled the transmission of raw endpoint events in the Endpoint server configuration. Check the other reconstruction views.'
    },
    fatalError: {
      115: 'Session is unavailable for viewing.',
      1000: 'Session is unavailable for viewing.',
      124: 'Invalid session ID: {{eventId}}',
      11: 'The session id is too large to be handled: {{eventId}}',
      3: 'The service is unavailable',
      permissions: 'You do not have the required permissions to view this content.'
    },
    toggles: {
      header: 'Show/Hide Header',
      request: 'Show/Hide Request',
      response: 'Show/Hide Response',
      topBottom: 'Top/Bottom View',
      sideBySide: 'Side by Side View',
      meta: 'Show/Hide Meta',
      expand: 'Expand Panel',
      shrink: 'Shrink Panel',
      close: 'Close Reconstruction'
    },
    eventHeader: {
      nwService: 'NW Service',
      sessionId: 'Session ID',
      type: 'Type',
      source: 'Source IP:PORT',
      destination: 'Destination IP:PORT',
      service: 'Service',
      firstPacketTime: 'First Packet Time',
      lastPacketTime: 'Last Packet Time',
      packetSize: 'Calculated Packet Size',
      payloadSize: 'Calculated Payload Size',
      packetCount: 'Calculated Packet Count',
      packetSizeTooltip: 'The calculated packet size in the summary header may be different than the packet size in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      payloadSizeTooltip: 'The calculated payload size in the summary header may be different than the payload size in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      packetCountTooltip: 'The calculated packet count in the summary header may be different than the packet count in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      deviceIp: 'Device IP',
      deviceType: 'Device Type',
      deviceClass: 'Device Class',
      eventCategory: 'Event Category',
      nweCategory: 'NWE Category',
      collectionTime: 'Collection Time',
      eventTime: 'Event Time',
      nweEventTime: 'Event Time',
      nweMachineName: 'Machine Name',
      nweMachineIp: 'Machine IP',
      nweMachineUsername: 'Machine Username',
      nweMachineIiocScore: 'Machine IIOC Score',
      nweEventSourceFilename: 'Event Source Filename',
      nweEventSourcePath: 'Event Source Path',
      nweEventDestinationFilename: 'Event Destination Filename',
      nweEventDestinationPath: 'Event Destination Path',
      nweFileFilename: 'Filename',
      nweFileIiocScore: 'File IIOC Score',
      nweProcessFilename: 'Process Filename',
      nweProcessParentFilename: 'Parent Filename',
      nweProcessPath: 'Process Path',
      nweDllFilename: 'DLL Filename',
      nweDllPath: 'DLL Path',
      nweDllProcessFilename: 'Process Filename',
      nweAutorunFilename: 'Autorun Filename',
      nweAutorunPath: 'Autorun Path',
      nweServiceDisplayName: 'Service Display Name',
      nweServiceFilename: 'Service Filename',
      nweServicePath: 'Service Path',
      nweTaskName: 'Task Name',
      nweTaskPath: 'Task Path',
      nweNetworkFilename: 'Network Filename',
      nweNetworkPath: 'Network Path',
      nweNetworkProcessFilename: 'Network Process Filename',
      nweNetworkProcessPath: 'Network Process Path',
      nweNetworkRemoteAddress: 'Network Remote Address'
    },
    contextmenu: {
      copy: 'Copy',
      externalLinks: 'External Lookup',
      livelookup: 'Live Lookup',
      endpointIoc: 'Endpoint Thick Client Lookup',
      applyDrill: 'Apply Drill in New Tab',
      applyNEDrill: 'Apply !EQUALS Drill in New Tab',
      refocus: 'Refocus Investigation in New Tab',
      hostslookup: 'Hosts Lookup',
      external: {
        google: 'Google',
        sansiphistory: 'SANS IP History',
        centralops: 'CentralOps Whois for IPs and Hostnames',
        robtexipsearch: 'Robtex IP Search',
        ipvoid: 'IPVoid',
        urlvoid: 'URLVoid',
        threatexpert: 'ThreatExpert Search'
      }
    }
  },
  memsize: {
    B: 'bytes',
    KB: 'KB',
    MB: 'MB',
    GB: 'GB',
    TB: 'TB'
  },
  midnight: 'Midnight',
  noon: 'Noon',
  fileExtract: {
    info: '<span>Preparing your download. The downloaded files will also be available in the <a href="{{url}}" target="_blank">job queue</a> for later retrieval.</span>',
    ready: '<span>The file has been extracted. Please go to the <a href="{{url}}" target="_blank">job queue</a> to download.</span>',
    error: {
      generic: 'An unexpected error has occurred attempting to download file(s). If further details are available, they can be found in the console.'
    }
  },
  investigate: {
    controls: {
      toggle: 'Show/Hide Events List Panel',
      togglePreferences: 'Toggle Investigate Preferences'
    },
    sortTooltips: {
      disableSort: 'Sorting is disabled because your environment requires an update to support sorting.',
      notIndexedAtValue: 'This column is not sortable because it is not indexed by value.',
      notSingleton: 'This column is not sortable because it has multiple values.',
      notValid: 'This column is not sortable because it has multiple unindexed values.',
      composed: 'This column is not sortable because it is composed of multiple fields.'
    },
    title: 'Investigate',
    loading: 'Loading',
    reExecutingQuery: 'Query is being re-executed to fetch new data.',
    loadMore: 'Load More',
    allLoaded: 'All results for this query have been loaded',
    notAllLoaded: 'Reached the {{maxEvents}} event limit. Consider refining your query.',
    partialResults: 'Retrieved {{actualEventCount}} of {{totalCount}} events prior to query cancellation.',
    partialResultsWithError: 'Retrieved {{actualEventCount}} of {{totalCount}} events prior to query error.',
    allResultsLoaded: 'All results loaded',
    textSearchLimitedResults: 'Results may be limited by a text filter, which matches only indexed meta keys.',
    showNextXEvents: 'Show Next {{count}} Events',
    tryAgain: 'Try Again',
    service: 'Service',
    timeRange: 'Time Range',
    filter: 'Filter',
    guided: {
      linkTitle: 'Guided Mode',
      textLabel: 'Guided Mode',
      deletePill: 'Delete this filter'
    },
    freeForm: {
      linkTitle: 'Free-Form Mode',
      placeholder: 'Enter multiple complex statements consisting of a Meta Key, Operator, and Value (optional)',
      textLabel: 'Free-Form Mode'
    },
    size: {
      bytes: 'bytes',
      KB: 'KB',
      MB: 'MB',
      GB: 'GB',
      TB: 'TB'
    },
    medium: {
      endpoint: 'Endpoint',
      network: 'Network',
      log: 'Log',
      correlation: 'Correlation',
      undefined: 'Unknown'
    },
    empty: {
      title: 'No events found.',
      description: 'Your filter criteria did not match any records.',
      canceled: 'Query canceled before any results were returned.',
      error: 'Query errored before any results were returned.',
      canceledWithPartial: 'Query errored before all results were returned.',
      errorWithPartial: 'Query errored before all results were returned.'
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
      selectAll: 'Select all events',
      selectAllDisabled: 'Select all is disabled while events load, and when there are no events to select',
      eventCount: '{{current}}/{{total}} event matches',
      search: 'Find text in table',
      searchDisabled: 'Find is disabled while events load.',
      searchDisabledWithSummary: 'Find is disabled when the summary column is included.',
      searchMin: '2 character minimum to find text.',
      noMatches: 'No matches were found.',
      searchPlaceholder: 'Enter text to be found.',
      titleThreshold: '{{age}} {{count}} Events ({{sort}})',
      title: '{{count}} Events ({{sort}})',
      oldest: 'oldest',
      newest: 'newest',
      abbr: {
        Ascending: 'Asc',
        asc: 'Asc',
        Descending: 'Desc',
        desc: 'Desc'
      },
      titleThresholdTooltip: 'Reached the {{count}} - event limit configured in the Admin > System view > Investigation settings. Displaying {{actualEventCount}} of the {{age}} events from the time window. Consider refining your query to narrow the results.',
      isAtThreshold: 'The event count reached the query threshold.',
      tableGroupLabel: 'Events {{startNextGroup}} - {{endNextGroup}}',
      columnGroups: {
        custom: 'Custom Column Groups',
        customTitle: 'Manage Custom Column Groups in Events List',
        default: 'Default Column Groups',
        searchPlaceholder: 'Type to filter column group'
      },
      download: {
        selected: 'Download',
        all: 'Download All',
        isDownloading: 'Downloading...',
        groups: {
          default: 'Default Options',
          other: 'Other Options'
        },
        LOG: 'Logs as {{option}}',
        NETWORK: 'Network as {{option}}',
        META: 'Visible Meta as {{option}}',
        options: {
          // LOG: 'Text',
          CSV: 'CSV',
          TSV: 'TSV',
          TEXT: 'Text',
          XML: 'XML',
          JSON: 'JSON',
          PCAP: 'PCAP',
          PAYLOAD: 'Payloads',
          PAYLOAD1: 'Request Payload',
          PAYLOAD2: 'Response Payload'
        }
      },
      error: 'An unexpected error occurred when executing this query.',
      fatalSummaryError: 'The service is unavailable',
      deviceDownRequery: 'Service went offline in the middle of the query. Please re-query.',
      shrink: 'Shrink Events List Panel',
      expand: 'Expand Events List Panel',
      close: 'Close Events List Panel',
      scrollMessage: 'Scroll down to view the selected event highlighted in blue',
      eventTips: {
        noResults: 'No results yet. Please select a service, time range, and submit a query',
        head: {
          headerOne: 'GUIDED MODE QUERY FILTER EXAMPLES',
          textOne: {
            one: 'Find outbound HTTP events with a user agent of some version of Mozilla',
            two: 'Find failed login Windows events',
            three: 'Find endpoint events with tasks having filenames ending with exe'
          },
          headerTwo: 'FREE-FORM MODE QUERY FILTER EXAMPLES',
          textTwo: {
            one: {
              description: 'Find events with an 8 to 11 character username similar to atreeman-72',
              example: 'user.all length 8-11 && (user.all regex \'^a[a-z]{2}ee[a-z]{3}-[0-9]{2}\')'
            },
            two: {
              description: 'Find events that are either HTTP network events or related to aix or ciscoasa logs',
              example: 'service=80 || (device.type = \'aix\',\'ciscoasa\')'
            },
            three: {
              description: 'Find all outbound events not going to Canada or the United States',
              example: 'direction = \'outbound\' AND not(country.dst = \'united states\' || country.dst = \'canada\')'
            }
          }
        },
        section: {
          mouse: {
            header: 'MOUSE INTERACTIONS',
            textOne: 'Click before, after, or between filters to insert another filter.',
            textTwo: 'Click a filter and right-click to show the action menu.',
            textThree: 'Double-click a filter to open it for editing.',
            textFour: 'Click multiple filters and press <span class="highlight">Delete</span> to remove selected filters.',
            textFive: 'Click the browser <span class="highlight">Back</span> button to go back to the previous state.'
          },
          keyboard: {
            header: 'KEYBOARD INTERACTIONS',
            textOne: 'Begin typing a meta key name or description in the query builder.',
            textTwo: 'Use <span class="highlight">up</span> and <span class="highlight">down arrows</span> in the drop-down menus, and press <span class="highlight">Enter</span> to select.',
            textThree: 'Press <span class="highlight">Enter</span> or click <span class="highlight">Query Events</span> to execute query.',
            textFour: 'Press <span class="highlight">left</span> or <span class="highlight">right arrow</span> to move through the query to add more filters or press <span class="highlight">Enter</span> to edit existing ones.',
            textFive: 'Press <span class="highlight">Shift + left</span> or <span class="highlight">right arrow</span> to select multiple filters to delete by pressing <span class="highlight">Backspace</span> or <span class="highlight">Delete</span.'
          }
        }
      },
      logs: {
        wait: 'Loading log...',
        rejected: 'No log data.'
      }
    },
    queryStats: {
      error: 'Error',
      isMixedMode: 'For accurate service status details, all services queried must be on 11.3.',
      summary: 'found ({{queryTimeElapsed}}s) and retrieved ({{streamingTimeElapsed}}s) {{age}} {{eventCount}} event(s).',
      summaryNoStreaming: 'found ({{queryTimeElapsed}}s) {{age}} {{eventCount}} event(s).',
      summaryNoEvents: 'found ({{queryTimeElapsed}}s) 0 event(s).',
      offline: 'This service is offline.',
      allOnline: 'All services are online.',
      oneOffline: '1 service is offline.',
      someOffline: '{{offline}} services are offline.',
      queried: 'Service Queried:',
      between: 'Time:',
      metaFilter: 'Meta Filter:',
      textFilter: 'Text Filter',
      textFilterWarning: 'A text filter matches only indexed meta keys, possibly limiting results loaded in the Events panel.',
      progress: 'Progress:',
      complete: 'Complete',
      retrieving: 'Retrieving',
      executing: 'Executing',
      canceled: 'User Canceled',
      nestedElapsedTime: '({{time}}s)',
      nestedNoElapsedTime: '(<1s)',
      slowest: 'This is the slowest service in the query.',
      hasOffline: 'This query includes offline services.',
      hasError: 'This query returned an error.',
      hasWarning: 'This query returned a warning.',
      openCloseLabel: 'Click to open or close the query console.',
      disabledLabel: 'The query console will become available once a query is initiated.',
      copy: 'Click to copy query filters:',
      disabledCopy: 'Click to copy will become available when filters are present.',
      warning: 'Warning',
      percentCompleted: 'Query {{progress}}% completed'
    },
    generic: {
      loading: 'Loading data...',
      rejected: 'No data was loaded.',
      rolledOut: 'Raw data not loaded. It has rolled out of the database.'
    },
    services: {
      loading: 'Loading Services',
      noData: 'The selected service does not have any data',
      coreServiceNotUpdated: 'Event Analysis requires all core services to be NetWitness {{minVersion}} or greater. Connecting versions prior to {{minVersion}} with an {{version}} NetWitness server results in limited functionality (see "Investigate in Mixed Mode" in the Physical Host Upgrade Guide).',
      empty: {
        title: 'Unable to find services.',
        description: 'No Brokers, Concentrators, or other services were detected. This may be due to a configuration or connectivity issue.'
      },
      error: {
        label: 'Services Unavailable',
        description: 'Unexpected error loading the list of Brokers, Concentrators, and other services to investigate. This may be due to a configuration or connectivity issue.'
      }
    },
    summary: {
      loading: 'Loading Summary'
    },
    customQuery: {
      title: 'Enter a query.'
    }
  },
  configure: {
    logsParser: {
      pageTitle: 'Log Parsers',
      wait: 'Loading Log Parsers...',
      logParserRules: 'Log Parser Rules',
      logParsers: 'Log Parsers',
      rules: 'Rules',
      tokens: 'Tokens',
      mapping: 'Meta',
      valueMatching: 'Matching',
      type: 'Type',
      pattern: 'Pattern',
      values: 'Values',
      sampleLogs: 'Sample Log Messages (Test up to 60K characters of log messages)',
      sampleLogsError: 'There was an error highlighting the sample logs below.',
      tooManyLogMessages: 'Sample log limit exceeded. Only 60K characters can be processed.',
      invalidRegEx: 'Invalid regular expression.',
      hasMissingCapturesError: 'The regular expression has fewer capture groups than the configured meta captures.',
      confirmDiscardChanges: 'Confirm Discard Changes',
      switchParserUnsavedChanges: 'Your parser has unsaved changes. Are you sure you want to discard these changes and switch ' +
        'to another parser?',
      noLoadedParsersInfo: 'No Log Parser rules or content found.  This feature requires at least one Log Decoder running version 11.2 or higher.',
      error: {
        generic: 'An unexpected error has occurred attempting to retrieve this data.',
        contentOffLine: 'There was a problem loading the Content Log Parser Rules. The Content Server may be offline or inaccessible.'
      },
      tokensList: {
        noTokensError: 'At least one token required.'
      },
      captures: {
        FULL_CAPTURE: 'Full Capture',
        FIRST_CAPTURE: 'First Capture',
        SECOND_CAPTURE: 'Second Capture',
        THIRD_CAPTURE: 'Third Capture',
        noCapturesError: 'At least one meta capture selection is required.'
      },
      modals: {
        ok: 'OK',
        cancel: 'Cancel',
        addRule: {
          title: 'Add New Rule',
          ruleName: 'Rule Name',
          pleaseEnterRuleName: 'Please Enter Rule Name',
          matchesExistingRuleName: 'Rule name can not match an existing rule within the same Log Parser.',
          incorrectRuleName: 'Choose a rule name that contains 1-64 alphanumeric characters.',
          ok: 'Add New Rule'
        },
        deleteRule: {
          title: 'Delete Rule',
          confirm: 'Delete rule \'{{ruleName}}\' from this log parser?',
          ok: 'Delete Rule',
          info: 'If the rule has been previously saved, it will not be permanently deleted until the next save.'
        },
        deleteParser: {
          title: 'Delete Parser',
          confirm: 'Are you sure you want to delete Parser \'{{parserName}}\'?',
          ok: 'Delete Parser',
          success: 'Successfully deleted parser \'{{parserName}}\' from all Log Decoders',
          failure: 'Failed to delete log parser \'{{parserName}}\' from all Log Decoders. Some may not be currently accessible. Please see the logs for details.',
          outOfBox: 'This parser is out of the Box and cannot be deleted'
        },
        deployLogParser: {
          title: 'Deploy Parser',
          confirm: 'Deploy rules for log parser \'{{logParser}}\' to all Log Decoders?',
          success: 'Successfully deployed rules to all Log Decoders for log parser \'{{logParser}}\'',
          partialSuccess: 'Failed deploying rules to some Log Decoders for log parser \'{{logParser}}\'',
          failure: 'Failed deploying rules to Log Decoders for log parser \'{{logParser}}\'',
          apiError: 'Communication error while deploying rules for log parser \'{{logParser}}\'',
          ok: 'Deploy Parser'
        }
      },
      buttons: {
        addRule: 'Add Rule',
        delete: 'Delete',
        deploy: 'Deploy',
        save: 'Save',
        discardChanges: 'Discard Changes',
        pleaseEnterNewToken: 'Please Enter New Token'
      },
      addParser: {
        label: 'Add Parser',
        addNew: 'Add Parser',
        instruction: 'Use dynamic parser rules to create a new dynamic log parser or add to an existing log parser.',
        selectParser: 'Select Log Parser',
        deviceDisplayName: 'Device Display Name',
        deviceDisplayNamePlaceholder: 'Enter a display name for this log parser',
        deviceType: 'Device Type',
        deviceTypePlaceholder: 'Enter a device type',
        deviceClass: 'Device Class',
        cloneFrom: 'Clone Dynamic Parser Rules From',
        new: 'New',
        addParserSuccessful: 'A new parser has been successfully added.',
        addParserFailed: 'A problem occurred while trying to add this new parser.',
        displayNameExistsError: 'This display name already exists for another log parser.',
        nameExistsError: 'Choose a log parser name that contains 3-30 lowercase, alphanumeric characters. Underscores are also permitted. The name must start with a character, and must not match an already existing log parser name.'
      }
    },
    title: 'Configure',
    liveContent: 'Live Content',
    esaRules: 'ESA Rules',
    respondNotifications: 'Incident Notifications',
    incidentRulesTitle: 'Incident Rules',
    subscriptions: 'Subscriptions',
    customFeeds: 'Custom Feeds',
    incidentRules: {
      noManagePermissions: 'You do not have permissions to make edits to Incident Rules',
      confirm: 'Are you sure you want to do this?',
      assignee: {
        none: '(Unassigned)'
      },
      riskScoring: {
        header: 'Endpoint Risk Scoring Settings',
        labels: {
          enabled: 'Enabled',
          disabled: 'Disabled',
          fileThresholdEnabled: 'Create Alerts and Incidents For Files',
          fileThreshold: 'File Risk Score Threshold',
          fileTimeWindow: 'File Incident Time Window',
          fileTimeWindowUnit: 'File Time Window Unit (days or hours)',
          hostThresholdEnabled: 'Create Alerts and Incidents For Hosts',
          hostThreshold: 'Host Risk Score Threshold',
          hostTimeWindow: 'Host Incident Time Window',
          hostTimeWindowUnit: 'Host Time Window Unit (days or hours)'
        },
        validations: {
          threshold: 'Threshold must be a whole number between 0 and 100',
          timeWindow: 'Time Window must be a whole number between 1 and 24',
          timeWindowUnit: 'Time Window must be in hours or days'
        },
        timeUnits: {
          d: 'Day(s)',
          h: 'Hour(s)'
        },
        actionMessages: {
          fetchFailure: 'There was a problem loading the Endpoint Risk Scoring settings. The Respond Server may be offline or inaccessible.',
          updateSuccess: 'You successfully updated the Endpoint Risk Scoring settings',
          updateFailure: 'There was a problem updating the Endpoint Risk Scoring settings'
        }
      },
      priority: {
        LOW: 'Low',
        MEDIUM: 'Medium',
        HIGH: 'High',
        CRITICAL: 'Critical'
      },
      action: 'Action',
      actionMessage: 'Choose the action taken if the rule matches an alert',
      error: 'There was a problem loading incident rules. The Respond Server may be offline or inaccessible.',
      noResults: 'No incident rules were found',
      createRule: 'Create Rule',
      deleteRule: 'Delete',
      cloneRule: 'Clone',
      export: 'Export',
      import: 'Import',
      enable: 'Enable',
      disable: 'Disable',
      select: 'Select',
      order: 'Order',
      enabled: 'Enabled',
      name: 'Name',
      namePlaceholder: 'Provide a unique name for the rule',
      ruleNameRequired: 'You must provide a rule name',
      description: 'Description',
      descriptionPlaceholder: 'Provide a description of the rule',
      lastMatched: 'Last Matched',
      alertsMatchedCount: 'Matched Alerts',
      incidentsCreatedCount: 'Incidents',
      matchConditions: 'Match Conditions',
      queryMode: 'Query Mode',
      queryModes: {
        RULE_BUILDER: 'Rule Builder',
        ADVANCED: 'Advanced'
      },
      queryBuilderQuery: 'Query Builder',
      advancedQuery: 'Advanced',
      advancedQueryRequired: 'Advanced Query cannot be empty',
      groupingOptions: 'Grouping Options',
      groupBy: 'Group By',
      groupByPlaceholder: 'Choose a group-by field (required)',
      groupByError: 'A minimum of one group-by field is required, and a maximum of two is allowed',
      timeWindow: 'Time Window',
      incidentOptions: 'Incident Options',
      incidentTitle: 'Title',
      incidentTitleRequired: 'You must provide a title for Incidents created from this rule',
      incidentTitlePlaceholder: 'Enter a title for the incident created by this rule',
      incidentTitleHelp: 'The Title template is used to create the Incident title. For ex- If rule has name Rule-01 and groupBy field is Severity, groupBy value is 50 and template is ${ruleName} for ${groupByValue1}, then incident will be created with name Rule-01 for 50.',
      incidentSummary: 'Summary',
      incidentSummaryPlaceholder: 'Enter a summary for the incident created by this rule',
      incidentCategories: 'Categories',
      incidentCategoriesPlaceholder: 'Choose a category (optional)',
      incidentAssignee: 'Assignee',
      incidentAssigneePlaceholder: 'Choose an assignee (optional)',
      incidentPriority: 'Priority',
      incidentPriorityInstruction: 'Use the following to set the priority for the incident',
      incidentPriorityAverage: 'Average of Risk Score across all of the Alerts',
      incidentPriorityHighestScore: 'Highest Risk Score available across all of the Alerts',
      incidentPriorityAlertCount: 'Number of Alerts in the time window',
      priorityScoreError: 'The priority score ranges are invalid',
      confirmQueryChange: 'Confirm Query Change',
      confirmAdvancedQueryMessage: 'Switching from Query Builder mode to Advanced mode will reset your match criteria.',
      confirmQueryBuilderMessage: 'Switching from Advanced mode to Query Builder mode will reset your match criteria.',
      groupAction: 'Group into an Incident',
      suppressAction: 'Suppress the Alert',
      timeUnits: {
        DAY: 'Days',
        HOUR: 'Hours',
        MINUTE: 'Minutes'
      },
      ruleBuilder: {
        addConditionGroup: 'Add Group',
        removeConditionGroup: 'Remove Group',
        addCondition: 'Add Condition',
        field: 'Field',
        operator: 'Operator',
        operators: {
          '=': 'is equal to',
          '!=': 'is not equal to',
          'begins': 'begins with',
          'ends': 'ends with',
          'contains': 'contains',
          'regex': 'matches regex',
          'in': 'in',
          'nin': 'not in',
          '>': 'is greater than',
          '>=': 'is equal or greater than',
          '<': 'is less than',
          '<=': 'is equal or less than'
        },
        groupOperators: {
          and: 'All of these',
          or: 'Any of these',
          nor: 'None of these'
        },
        value: 'Value',
        hasGroupsWithoutConditions: 'All groups must have at least one condition',
        hasMissingConditionInfo: 'At least one condition is missing a field, operator, or value'
      },
      actionMessages: {
        deleteRuleConfirmation: 'Are you sure you want to delete this rule? Once applied, this deletion cannot be reversed.',
        enableRulesConfirmation: 'Are you sure you want to enable the {{count}} selected rule(s)?',
        disableRulesConfirmation: 'Are you sure you want to disable the {{count}} selected rule(s)?',
        reorderSuccess: 'You successfully changed the order of the rules',
        reorderFailure: 'There was a problem changing the order of the rules',
        cloneSuccess: 'You successfully cloned the selected rule',
        cloneFailure: 'There was a problem cloning the selected rule',
        createSuccess: 'You successfully created a new rule',
        createFailure: 'There was a problem creating the new rule',
        deleteSuccess: 'You successfully deleted the selected rule',
        deleteFailure: 'There was a problem deleting the selected rule',
        enableSuccess: 'You successfully enabled the selected rules',
        enableFailure: 'There was a problem enabling the selected rules. Refresh the page to get the latest state',
        disableSuccess: 'You successfully disabled the selected rules',
        disableFailure: 'There was a problem disabling the selected rules. Refresh the page to get the latest state',
        saveSuccess: 'The changes to the rule were successfully saved',
        saveFailure: 'There was a problem saving the changes to the rule',
        exportSuccess: 'You successfully exported the selected rules',
        exportFailure: 'There was a problem exporting the selected rules',
        importSuccess: 'You successfully imported the rules from the selected file',
        importFailure: 'There was a problem importing the rules from the selected file',
        duplicateNameFailure: 'There is already another rule with the same name. Please modify the rule name so that it is unique.'
      },
      missingRequiredInfo: 'There is required information missing from the incident rule'
    },
    notifications: {
      settings: 'Incident Email Notification Settings',
      emailServer: 'Email Server',
      socEmailAddresses: 'SOC Manager Email Addresses',
      noSocEmails: 'There are no SOC Manager emails configured',
      emailAddressPlaceholder: 'Enter an email address to add',
      addEmail: 'Add',
      notificationTypes: 'Notification Types',
      type: 'Type',
      sendToAssignee: 'Send to Assignee',
      sendToSOCManagers: 'Send to SOC Managers',
      types: {
        'incident-created': 'Incident Created',
        'incident-state-changed': 'Incident Updated'
      },
      hasUnsavedChanges: 'You have unsaved changes. Click Apply to save.',
      emailServerSettings: 'Email Server Settings',
      noManagePermissions: 'You do not have permissions to make edits to Respond Notifications',
      actionMessages: {
        fetchFailure: 'There was a problem loading the Respond notification settings. The Respond Server may be offline or inaccessible.',
        updateSuccess: 'You successfully updated the Respond notification settings',
        updateFailure: 'There was a problem updating the Respond notification settings',
        updateEmailServerFailure: 'These settings cannot be edited because the corresponding e-mail server cannot be found. Access the Servers tab from the "E-mail Servers Settings" link and add the necessary e-mail server.'
      }
    },
    endpoint: {
      certificates: {
        columns: {
          friendlyName: 'Friendly Name',
          subject: 'Subject',
          subjectKey: 'Subject Key',
          serial: 'Serial',
          issuer: 'Issuer',
          authorityKey: 'Authority Key',
          thumbprint: 'Thumb Print',
          notValidBeforeUtcDate: 'Not Valid Before UtcDate',
          notValidAfterUtcDate: 'Not Valid After UtcDate',
          certificateStatus: 'Status'
        },
        noResultsMessage: 'No certificates were found.',
        footer: 'Showing {{count}} out of {{total}} {{label}} | {{selected}} selected',
        filter: {
          invalidFilterInput: 'Invalid filter input',
          invalidFilterInputLength: 'Filter input longer than 256 characters',
          invalidCharacters: 'Can contain alphanumeric or special characters.',
          invalidCharsAlphabetOnly: 'Numbers and special characters are not allowed',
          invalidCharsAlphaNumericOnly: 'Special characters are not allowed',
          isRootMicrosoft: 'Is Root Microsoft',
          signature: 'Signature'
        },
        status: {
          edit: 'Change Certificate Status',
          certificateStatus: 'Certificate Status',
          success: 'Certificate status updated successfully',
          error: 'Certificate status update failed',
          statusOptions: {
            neutral: 'Neutral',
            blacklisted: 'Blacklisted',
            whitelisted: 'Whitelisted'
          }
        }
      }
    }
  },
  respond: {
    title: 'Respond',
    common: {
      yes: 'Yes',
      no: 'No',
      true: 'Yes',
      false: 'No'
    },
    none: 'None',
    select: 'Select',
    close: 'Close',
    empty: '(empty)',
    ueba: {
      label: 'User Entity Behavior Analytics',
      error: 'User Entity Behavior Analytics is unavailable at this time. Please try again later.'
    },
    investigation: {
      eventAnalysis: 'Event Analysis',
      selectedEventsThresoldTooltip: 'A maximum of 1000 events will be added to incident at once'
    },
    errorPage: {
      serviceDown: 'Respond Server is offline',
      serviceDownDescription: 'The Respond Server is not running or is inaccessible. Check with your administrator to resolve this issue.',
      fetchError: 'An error has occurred. The Respond Server may be offline or inaccessible.'
    },
    confirmation: {
      deleteTitle: 'Confirm Delete',
      deleteConfirmation: 'Are you sure you want to delete {{count}} record(s)? Once applied, this deletion cannot be reversed.'
    },
    timeframeOptions: {
      LAST_5_MINUTES: 'Last 5 Minutes',
      LAST_10_MINUTES: 'Last 10 Minutes',
      LAST_15_MINUTES: 'Last 15 Minutes',
      LAST_30_MINUTES: 'Last 30 Minutes',
      LAST_HOUR: 'Last Hour',
      LAST_3_HOURS: 'Last 3 Hours',
      LAST_6_HOURS: 'Last 6 Hours',
      LAST_TWELVE_HOURS: 'Last 12 Hours',
      LAST_TWENTY_FOUR_HOURS: 'Last 24 Hours',
      LAST_FORTY_EIGHT_HOURS: 'Last 2 Days',
      LAST_5_DAYS: 'Last 5 Days',
      LAST_7_DAYS: 'Last 7 Days',
      LAST_14_DAYS: 'Last 14 Days',
      LAST_30_DAYS: 'Last 30 Days',
      ALL_TIME: 'All Data'
    },
    entities: {
      incidents: 'Incidents',
      remediationTasks: 'Tasks',
      alerts: 'Alerts',
      actionMessages: {
        updateSuccess: 'Your change was successful',
        updateFailure: 'There was a problem updating the field for this record',
        createSuccess: 'You successfully added a new record',
        createFailure: 'There was a problem creating this record',
        deleteSuccess: 'You successfully deleted this record',
        deleteFailure: 'There was a problem deleting this record',
        saveSuccess: 'Your changes were successfully saved',
        saveFailure: 'There was a problem saving this record'
      },
      alert: 'Alert'
    },
    remediationTasks: {
      loading: 'Loading Tasks',
      addNewTask: 'Add New Task',
      noTasks: 'There are no tasks for {{incidentId}}',
      openFor: 'Opened',
      newTaskFor: 'New Task for',
      delete: 'Delete Task',
      noAccess: 'You do not have permissions to view tasks',
      actions: {
        actionMessages: {
          deleteWarning: 'Deleting a Task from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
            'to delete the task from any other applicable systems.'
        }
      },
      filters: {
        taskId: 'Task ID',
        idFilterPlaceholder: 'e.g., REM-123',
        idFilterError: 'The ID must match the format: REM-###'
      },
      list: {
        priority: 'Priority',
        select: 'Select',
        id: 'ID',
        name: 'Name',
        createdDate: 'Created',
        status: 'Status',
        assignee: 'Assignee',
        noResultsMessage: 'No matching Tasks were found',
        incidentId: 'Incident ID',
        targetQueue: 'Target Queue',
        remediationType: 'Type',
        escalated: 'Escalated',
        lastUpdated: 'Last Updated',
        description: 'Description',
        createdBy: 'Created By'
      },
      type: {
        QUARANTINE_HOST: 'Quarantine Host',
        QUARANTINE_NETORK_DEVICE: 'Quarantine Network Device',
        BLOCK_IP_PORT: 'Block IP/Port',
        BLOCK_EXTERNAL_ACCESS_TO_DMZ: 'Block External Access To DMZ',
        BLOCK_VPN_ACCESS: 'Block VPN Access',
        REIMAGE_HOST: 'Reimage Host',
        UPDATE_FIREWALL_POLICY: 'Update Firewall Policy',
        UPDATE_IDS_IPS_POLICY: 'Update IDS/IPS Policy',
        UPDATE_WEB_PROXY_POLICY: 'Update Web Proxy Policy',
        UPDATE_ACCESS_POLICY: 'Update Access Policy',
        UPDATE_VPN_POLICY: 'Update VPN Policy',
        CUSTOM: 'Custom',
        MITIGATE_RISK: 'Mitigate Risk',
        MITIGATE_COMPLIANCE_VIOLATION: 'Mitigate Compliance Violation',
        MITIGATE_VULNERABILITY_THREAT: 'Mitigate Vulnerability/Threat',
        UPDATE_CORPORATE_BUSINESS_POLICY: 'Update Corporate/Business Policy',
        NOTIFY_BC_DR_TEAM: 'Notify BC/DR Team',
        UPDATE_RULES: 'Update Rule(s)',
        UPDATE_FEEDS: 'Update Feed(s)'
      },
      targetQueue: {
        OPERATIONS: 'Operations',
        GRC: 'GRC',
        CONTENT_IMPROVEMENT: 'Content Improvement'
      },
      noDescription: 'There is no description for this task'
    },
    incidents: {
      incidentName: 'Incident Name',
      actions: {
        addEntryLabel: 'Add Entry',
        confirmUpdateTitle: 'Confirm update',
        changeAssignee: 'Change Assignee',
        changePriority: 'Change Priority',
        changeStatus: 'Change Status',
        addJournalEntry: 'Add Journal Entry',
        actionMessages: {
          deleteWarning: 'Warning: You are about to delete one or more incidents which may have tasks and which may have been escalated. ' +
            'Deleting an incident from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
            'to delete the incident and its tasks from any other applicable systems.',
          addJournalEntrySuccess: 'You added a journal entry to incident {{incidentId}}',
          addJournalEntryFailure: 'There was a problem adding a journal entry to incident {{incidentId}}',
          incidentCreated: 'You successfully created the incident {{incidentId}} from the selected alerts.',
          incidentCreationFailed: 'There was a problem creating an incident from the selected alerts',
          incidentCreatedFromEvents: 'You successfully created the incident {{incidentId}} from the selected events.',
          incidentCreationFromEventsFailed: 'There was a problem creating an incident from the selected events.',
          createIncidentInstruction: 'An incident will be created from the selected {{alertCount}} alert(s). Please provide a name for the incident.',
          createIncidentFromEventsInstruction: 'An incident will be created from the selected {{evenCount}} event(s). Please provide a name for the alert & the incident.',
          addAlertToIncidentSucceeded: 'You successfully added the selected {{entity}} to {{incidentId}}.',
          addAlertToIncidentFailed: 'There was a problem adding the selected {{entity}} to this incident',
          confirmSendToArcherTitle: 'Confirm Send to Archer',
          confirmSendToArcher: 'Are you sure you want to send this incident to Archer? This action is not reversible.',
          sendToArcherSuccess: 'Incident {{incidentId}} has been sent to Archer. The new Archer Incident ID is {{archerIncidentId}}',
          sendToArcherConnectionFailed: 'Could not connect to Archer. Archer may be unavailable or the credentials may be incorrect.',
          sendToArcherMetadataLoadFailed: 'Could not send incident ({{incidentId}}) to Archer. There was a problem loading Archer field definitions.',
          sendToArcherValidationFailed: 'Could not send incident ({{incidentId}}) to Archer because of a validation failure. There is required information missing or there are values that are too long.',
          sendToArcherFailed: 'There was a problem sending this incident ({{incidentId}}) to Archer. Note: This feature only supports Archer version 6.4 or later.'
        },
        deselectAll: 'Deselect all'
      },
      filters: {
        incidentId: 'Incident ID',
        idFilterPlaceholder: 'e.g., INC-123',
        idFilterError: 'The ID must match the format: INC-###',
        showOnlyUnassigned: 'Show only unassigned incidents'
      },
      label: 'Incidents',
      list: {
        select: 'Select',
        id: 'ID',
        name: 'Name',
        createdDate: 'Created',
        status: 'Status',
        priority: 'Priority',
        score: 'Risk Score',
        assignee: 'Assignee',
        alertCount: 'Alerts',
        sources: 'Source',
        noResultsMessage: 'No matching Incidents were found'
      },
      footer: 'Showing {{count}} out of {{total}} incidents'
    },
    alerts: {
      createIncident: 'Create Incident',
      addToIncident: 'Add to Incident',
      alertSummary: 'Alert Summary',
      defaultAlertSummaryText: 'Manual alert for All Data',
      severity: 'Severity',
      incidentSearch: {
        searchInputLabel: 'Search Open Incidents',
        searchInputPlaceholder: 'Search by incident ID (e.g., INC-123) or incident name',
        noResults: 'No open Incidents found',
        noQuery: 'Use the search box above to search for open incidents by name or ID. Your search must contain at least (3) characters.',
        error: 'There was a problem searching for incidents'
      },
      actions: {
        actionMessages: {
          deleteWarning: 'Warning: You are about to delete one or more alerts that may be associated with incidents. ' +
            'Be aware that any associated incidents will be updated or deleted accordingly.',
          deleteWarningTitle: 'Deleting an alert will:',
          removeFromIncidentWarning: 'Remove it from any incidents it is part of',
          deleteIncidentWarning: 'Delete the incident if all the alerts in that incident are deleted',
          resetAlertNameFiltersWarning: 'Reset the Alert Names filter if all the alerts of that name are deleted'
        }
      },
      list: {
        receivedTime: 'Created',
        severity: 'Severity',
        numEvents: '# Events',
        id: 'ID',
        name: 'Name',
        status: 'Status',
        source: 'Source',
        incidentId: 'Incident ID',
        partOfIncident: 'Part of Incident',
        type: 'Type',
        hostSummary: 'Host Summary',
        userSummary: 'User Summary'
      },
      notAssociatedWithIncident: '(None)',
      originalAlert: 'Raw Alert',
      originalAlertLoading: 'Loading raw alert',
      originalAlertError: 'There was a problem loading the raw alert',
      alertNames: 'Alert Names'
    },
    alert: {
      status: {
        GROUPED_IN_INCIDENT: 'Grouped In Incident',
        NORMALIZED: 'Normalized'
      },
      type: {
        Correlation: 'Correlation',
        Log: 'Log',
        Network: 'Network',
        'Instant IOC': 'Instant IOC',
        'Web Threat Detection Incident': 'Web Threat Detection Incident',
        'File Share': 'File Share',
        'Manual Upload': 'Manual Upload',
        'On Demand': 'On Demand',
        Resubmit: 'Resubmit',
        Unknown: 'Unknown'
      },
      source: {
        'Risk Scoring': 'Risk Scoring',
        'ESA Correlation': 'ESA Correlation',
        'ECAT': 'Endpoint',
        'Event Stream Analysis': 'Event Stream Analysis',
        'Event Streaming Analytics': 'Event Stream Analysis',
        'Security Analytics Investigator': 'Security Analytics Investigator',
        'Web Threat Detection': 'Web Threat Detection',
        'Malware Analysis': 'Malware Analysis',
        'Reporting Engine': 'Reporting Engine',
        'NetWitness Investigate': 'NetWitness Investigate',
        'User Entity Behavior Analytics': 'User Entity Behavior Analytics'
      },
      backToAlerts: 'Back To Alerts'
    },
    incident: {
      sendToArcher: 'Send to Archer',
      sentToArcher: 'Sent to Archer',
      created: 'Created',
      status: 'Status',
      priority: 'Priority',
      riskScore: 'Risk Score',
      assignee: 'Assignee',
      alertCount: 'Indicator(s)',
      eventCount: 'Event(s)',
      catalystCount: 'Catalysts',
      sealed: 'Sealed',
      sealsAt: 'Seals At',
      sources: 'Sources',
      categories: 'Categories',
      chooseCategory: 'Choose a category (optional)',
      chooseAssignee: 'Choose an assignee (optional)',
      backToIncidents: 'Back To Incidents',
      overview: 'Overview',
      indicators: 'Indicators',
      indicatorsCutoff: 'Showing {{limit}} of {{expected}} indicators',
      events: 'Events',
      loadingEvents: 'Loading events...',
      view: {
        graph: 'Shows the relationship between incident entities such as IP address, user, host, domain, file name, or file hash in an interactive graph.',
        eventslist: 'Lists incident events and displays overview information such as source and destination. Click the top of an event to view the event details.'
      },
      journalTasksRelated: 'Journal, Tasks, and Related',
      search: {
        tab: 'Find Related',
        title: 'Related Indicators',
        subtext: 'Enter a value below and click the Find button to look for other indicators related to that value.',
        partOfThisIncident: 'Part Of This Incident',
        types: {
          IP: 'IP',
          MAC_ADDRESS: 'MAC',
          HOST: 'Host',
          DOMAIN: 'Domain',
          FILE_NAME: 'Filename',
          FILE_HASH: 'Hash',
          USER: 'User',
          label: 'Find'
        },
        text: {
          label: 'Value',
          placeholders: {
            IP: 'Enter an IP address',
            MAC_ADDRESS: 'Enter a MAC address',
            HOST: 'Enter a hostname',
            DOMAIN: 'Enter a domain name',
            FILE_NAME: 'Enter a file name',
            FILE_HASH: 'Enter a file hash',
            USER: 'Enter a user name'
          }
        },
        timeframe: {
          label: 'When'
        },
        devices: {
          source: 'Source',
          destination: 'Destination',
          detector: 'Detector',
          domain: 'Domain',
          label: 'Look in'
        },
        results: {
          title: 'Indicators for',
          openInNewWindow: 'Open in new window'
        },
        actions: {
          search: 'Find',
          cancel: 'Cancel',
          addToIncident: 'Add To Incident',
          addingAlert: 'Adding Alert to Incident',
          unableToAddAlert: 'Unable to add Alert to Incident.',
          pleaseTryAgain: 'Please try again.',
          closedIncident: 'You cannot add an indicator to a closed incident'
        }
      }
    },
    storyline: {
      loading: 'Loading incident storyline',
      error: 'Unable to load incident storyline',
      catalystIndicator: 'Catalyst Indicator',
      relatedIndicator: 'Related Indicator',
      source: 'Source',
      partOfIncident: 'Part of Incident',
      relatedBy: 'Related to Catalyst by',
      event: 'event',
      events: 'events'
    },
    details: {
      loading: 'Loading incident details',
      error: 'Unable to load incident details'
    },
    journal: {
      newEntry: 'New Journal Entry',
      title: 'Journal',
      close: 'Close',
      milestone: 'Milestone',
      loading: 'Loading journal entries',
      noEntries: 'There are no journal entries for {{incidentId}}',
      delete: 'Delete Entry',
      deleteConfirmation: 'Are you sure you want to delete this journal entry? This action cannot be reversed.',
      noAccess: 'You do not have permissions to view journal entries'
    },
    milestones: {
      title: 'Milestones',
      RECONNAISSANCE: 'Reconnaissance',
      DELIVERY: 'Delivery',
      EXPLOITATION: 'Exploitation',
      INSTALLATION: 'Installation',
      COMMAND_AND_CONTROL: 'Command and Control',
      ACTION_ON_OBJECTIVE: 'Action on Objective',
      CONTAINMENT: 'Containment',
      ERADICATION: 'Eradication',
      CLOSURE: 'Closure'
    },
    eventDetails: {
      caption: 'Event Source and Target',
      title: 'Event Details',
      events: 'events',
      in: 'in',
      indicators: 'indicators',
      type: {
        'Instant IOC': 'Instant IOC',
        'Log': 'Log',
        'Network': 'Network',
        'Endpoint': 'Endpoint',
        'Correlation': 'Correlation',
        'Web Threat Detection': 'Web Threat Detection',
        'Web Threat Detection Incident': 'Web Threat Detection Incident',
        'Unknown': 'Event',
        'File Share': 'File Share',
        'Manual Upload': 'Manual Upload',
        'On Demand': 'On Demand',
        Resubmit: 'Resubmit'
      },
      backToTable: 'Back To Table',
      labels: {
        timestamp: 'Timestamp',
        type: 'Type',
        description: 'Description',
        source: 'Source',
        destination: 'Target',
        domain: 'Domain/Host',
        detector: 'Detector',
        device: 'Device',
        ip_address: 'IP Address',
        mac_address: 'MAC Address',
        dns_hostname: 'Host',
        dns_domain: 'Domain',
        netbios_name: 'NetBIOS Name',
        asset_type: 'Asset Type',
        business_unit: 'Business Unit',
        facility: 'Facility',
        criticality: 'Criticality',
        compliance_rating: 'Compliance_rating',
        malicious: 'Malicious',
        site_categorization: 'Site Categorization',
        geolocation: 'Geolocation',
        city: 'City',
        country: 'Country',
        longitude: 'Longitude',
        latitude: 'Latitude',
        organization: 'Organization',
        device_class: 'Device Class',
        product_name: 'Product Name',
        port: 'Port',
        user: 'User',
        username: 'Username',
        ad_username: 'Active Directory Username',
        ad_domain: 'Active Directory Domain',
        email_address: 'Email Address',
        os: 'Operating System',
        size: 'Size',
        data: 'Data',
        filename: 'Filename',
        hash: 'Hash',
        av_hit: 'AV Hit',
        extension: 'Extension',
        mime_type: 'MIME Type',
        original_path: 'Original Path',
        av_aliases: 'AV Aliases',
        networkScore: 'Network Score',
        communityScore: 'Community Score',
        staticScore: 'Static Score',
        sandboxScore: 'Sandbox Score',
        opswat_result: 'OPSWAT Result',
        yara_result: 'YARA Result',
        bit9_status: 'Bit9 Status',
        module_signature: 'Module Signature',
        related_links: 'Related Links',
        url: 'URL',
        ecat_agent_id: 'NWE Agent ID',
        ldap_ou: 'LDAP OU',
        last_scanned: 'Last Scanned',
        enrichment: 'Enrichment',
        enrichmentSections: {
          domain_registration: 'Domain Registration',
          command_control_risk: 'Command and Control',
          beaconing_behavior: 'Beaconing',
          domain_age: 'Domain Age',
          expiring_domain: 'Expiring Domain',
          rare_domain: 'Rare Domain',
          no_referers: 'Referers',
          rare_user_agent: 'Rare User Agent'
        },
        registrar_name: 'Domain Registrar',
        registrant_organization: 'Registrant Organization',
        registrant_name: 'Registrant Name',
        registrant_email: 'Registrant Email',
        registrant_telephone: 'Registrant Telephone',
        registrant_street1: 'Registrant Street Address',
        registrant_postal_code: 'Registrant Postal Code',
        registrant_city: 'Registrant City',
        registrant_state: 'Registrant State',
        registrant_country: 'Registrant Country',
        whois_created_dateNetWitness: 'Registration Date',
        whois_updated_dateNetWitness: 'Updated Date',
        whois_expires_dateNetWitness: 'Expiration Date',
        whois_age_scoreNetWitness: 'Domain Registration Age Score',
        whois_validity_scoreNetWitness: 'Expiring Domain Score',
        whois_estimated_domain_age_daysNetWitness: 'Domain Registration Age (in days)',
        whois_estimated_domain_validity_daysNetWitness: 'Time To Expiration (in days)',
        command_control_aggregate: 'Command and Control Risk Score',
        command_control_confidence: 'Confidence',
        weighted_c2_referer_score: 'Contribution of Rare Domain Score (This Network)',
        weighted_c2_referer_ratio_score: 'Contribution of No Domain Referer Score',
        weighted_c2_ua_ratio_score: 'Contribution of Rare User Agent Score',
        weighted_c2_whois_age_score: 'Contribution of Domain Registration Age Score',
        weighted_c2_whois_validity_score: 'Contribution of Expiring Domain Score',
        smooth_score: 'Score',
        beaconing_period: 'Period',
        newdomain_score: 'Domain Age Score (This Network)',
        newdomain_age: 'Domain Age (This Network)',
        referer_score: 'Rare Score',
        referer_cardinality: 'Rare Cardinality',
        referer_num_events: 'Rare Events',
        referer_ratio: 'Rare Ratio',
        referer_ratio_score: 'Rare Ratio Score',
        referer_cond_cardinality: 'Rare Conditional Cardinality',
        ua_num_events: 'Occurences in the last week',
        ua_ratio: 'Percentage of IPs With Rare User Agent',
        ua_ratio_score: 'Rare User Agent Score',
        ua_cond_cardinality: 'IPs with Rare User Agent',
        event_source: 'Event Source',
        event_source_id: 'Event Source ID',
        agent_id: 'Agent ID',
        device_type: 'Device Type',
        category: 'Category',
        source_user_account: 'Source User Account',
        domain_src: 'Source Domain',
        domain_dst: 'Target Domain',
        host_src: 'Source Host',
        host_dst: 'Target Host'
      },
      periodValue: {
        hours: 'hour(s)',
        minutes: 'minute(s)',
        seconds: 'second(s)'
      }
    },
    eventsList: {
      title: {
        'one': 'event',
        'other': 'events'
      },
      na: 'N/A',
      eventOfTotal: 'Event {{eventIndex}} of {{numEvents}}',
      seeAllEvents: 'See All Events',
      generic: {
        time: 'EVENT TIME',
        type: 'EVENT TYPE',
        detectorIP: 'DETECTOR IP',
        fileName: 'FILE NAME',
        fileHash: 'FILE HASH',
        tableIP: 'IP',
        tablePORT: 'PORT',
        tableHOST: 'HOST',
        tableMAC: 'MAC',
        tableUSER: 'USER',
        sourceLabel: 'Source',
        targetLabel: 'Target'
      },
      endpoint: {
        time: 'EVENT TIME',
        type: 'EVENT TYPE',
        category: 'CATEGORY',
        action: 'ACTION',
        hostname: 'HOSTNAME',
        userAccount: 'USER ACCOUNT',
        operatingSystem: 'OPERATING SYSTEM',
        fileName: 'FILE NAME',
        hash: 'HASH',
        fileHash: 'FILE HASH',
        tableFILENAME: 'FILE NAME',
        tableLAUNCH: 'LAUNCH ARGUMENT',
        tablePATH: 'PATH',
        tableHASH: 'HASH',
        sourceLabel: 'Source',
        targetLabel: 'Target'
      },
      ueba: {
        time: 'EVENT TIME',
        type: 'EVENT TYPE',
        category: 'CATEGORY',
        username: 'USERNAME',
        operationType: 'OPERATION TYPE',
        eventCode: 'EVENT CODE',
        result: 'RESULT',
        dataSource: 'DATA SOURCE',
        process: {
          sourceLabel: 'SOURCE',
          destinationLabel: 'DESTINATION',
          tableFileName: 'FILE NAME',
          tableChecksum: 'CHECKSUM',
          tableDirectory: 'DIRECTORY',
          tableUsername: 'USERNAME',
          tableCategories: 'CATEGORIES'
        }
      }
    },
    eventsTable: {
      time: 'Time',
      type: 'Type',
      sourceDomain: 'Source Domain',
      destinationDomain: 'Destination Domain',
      sourceHost: 'Source Host',
      destinationHost: 'Destination Host',
      sourceIP: 'Source IP',
      destinationIP: 'Destination IP',
      detectorIP: 'Detector IP',
      sourcePort: 'Source Port',
      destinationPort: 'Destination Port',
      sourceMAC: 'Source MAC',
      destinationMAC: 'Destination MAC',
      sourceUser: 'Source User',
      destinationUser: 'Destination User',
      fileName: 'File Name',
      fileHash: 'File Hash',
      indicator: 'Indicator'
    },
    entity: {
      legend: {
        user: 'user(s)',
        host: 'host(s)',
        ip: 'IP(s)',
        domain: 'domain(s)',
        mac_address: 'MAC(s)',
        file_name: 'file(s)',
        file_hash: 'hash(es)',
        selection: {
          storyPoint: 'in {{count}} selected indicator(s)',
          event: 'in {{count}} selected event(s)'
        },
        selectionNotShown: 'The selected nodes could not be shown due to size limits.',
        hasExceededNodeLimit: 'Showing only first {{limit}} nodes.',
        showAll: 'Show all data'
      }
    },
    enrichment: {
      uniformTimeIntervals: 'The time intervals between communication events are very uniform.',
      newDomainToEnvironment: 'Domain is relatively new to the environment.',
      rareDomainInEnvironment: 'The domain is rare in this environment.',
      newDomainRegistration: 'Domain is relatively new based on the registration date:',
      domainRegistrationExpires: 'The domain registration will expire relatively soon:',
      rareUserAgent: 'A high percentage of hosts connecting to the domain are using a rare or no user agent.',
      noReferers: 'A high percentage of hosts connecting to the domain are not utilizing referrers.',
      highNumberServersAccessed: 'Abnormally high number of servers accessed today.',
      highNumberNewServersAccessed: 'Accessed an abnormally high number of new servers today.',
      highNumberNewDevicesAccessed: 'Accessed an abnormally high number of new devices this week.',
      highNumberFailedLogins: 'Abnormally high number of servers with failed logins today.',
      passTheHash: 'Potential "pass the hash" attack indicated by a new device followed by a new server.',
      rareLogonType: 'Accessed using a Windows logon type they have rarely used in the past.',
      authFromRareDevice: 'Authenticated from a rare device.',
      authFromRareLocation: 'Accessed from a rare location.',
      authFromRareServiceProvider: 'Accessed using a rare service provider.',
      authFromNewServiceProvider: 'Accessed using a new service provider.',
      highNumberVPNFailedLogins: 'High number of VPN login failures.',
      daysAgo: '{{days}} day(s) ago',
      days: '{{days}} day(s)',
      domainIsWhitelisted: 'Domain is whitelisted.',
      domainIsNotWhitelisted: 'Domain is not whitelisted.'
    },
    sources: {
      'C2-Packet': 'User Entity Behavioral Analytics',
      'C2-Log': 'User Entity Behavioral Analytics',
      'UBA-WinAuth': 'User Entity Behavioral Analytics',
      UbaCisco: 'User Entity Behavioral Analytics',
      ESA: 'Event Stream Analytics',
      'Event-Stream-Analysis': 'Event Stream Analytics',
      RE: 'Reporting Engine',
      'Reporting-Engine': 'Reporting Engine',
      ModuleIOC: 'Endpoint',
      ECAT: 'Endpoint',
      generic: 'NetWitness'
    },
    status: {
      NEW: 'New',
      ASSIGNED: 'Assigned',
      IN_PROGRESS: 'In Progress',
      REMEDIATION_REQUESTED: 'Task Requested',
      REMEDIATION_COMPLETE: 'Task Complete',
      CLOSED: 'Closed',
      CLOSED_FALSE_POSITIVE: 'Closed - False Positive',
      REMEDIATED: 'Remediated',
      RISK_ACCEPTED: 'Risk Accepted',
      NOT_APPLICABLE: 'Not Applicable'
    },
    priority: {
      LOW: 'Low',
      MEDIUM: 'Medium',
      HIGH: 'High',
      CRITICAL: 'Critical'
    },
    assignee: {
      none: '(Unassigned)'
    },
    mixedModeMessage: 'Event Analysis is not available from the Respond view because it requires all event sources to be on RSA NetWitness Platform version {{minVersion}} or greater.'
  },
  context: {
    noData: 'No matching context is available',
    noResults: '(No Results)',
    notConfigured: '(Not Configured)',
    title: 'Context for',
    lastUpdated: 'Last Updated:',
    timeWindow: 'Time Window: ',
    iiocScore: 'Iioc Score',
    IP: 'IP',
    USER: 'User',
    MAC_ADDRESS: 'Mac Address',
    HOST: 'Host',
    FILE_NAME: 'File Name',
    FILE_HASH: 'File Hash',
    DOMAIN: 'Domain',
    noValues: 'Context Sources with no values: ',
    dsNotConfigured: 'Context Sources not configured: ',
    marketingText: ' is not a currently configured data source in Context Hub. Contact your Administrator to enable this feature. Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more sources on-demand. For more information , click Help.',
    lcMarketingText: 'Live Connect collects, analyzes, and assesses the Threat Intelligence data such as IP Addresses, Domains and File Hashes collected from various sources. Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more sources on-demand. To view the data, enable Threat Insights in ADMIN > System > Live Services or contact your Administrator. For more information, click Help.',
    fileReputationMarketingText: 'File Reputation displays reputation of a file hash that indicates the level of threat a file poses. This information is leveraged by analysts during investigation of files to increase detection and response to malicious and suspicious file behavior. Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more sources on-demand. This feature is not enabled by default. To enable File Reputation, go to ADMIN > System > Live Services or contact your Administrator. For more information, click Help.',
    timeUnit: {
      allData: 'ALL DATA',
      HOUR: 'HOUR',
      HOURS: 'HOURS',
      MINUTE: 'MINUTE',
      MINUTES: 'MINUTES',
      DAY: 'DAY',
      DAYS: 'DAYS',
      MONTH: 'MONTH',
      MONTHS: 'MONTHS',
      YEAR: 'YEAR',
      YEARS: 'YEARS',
      WEEK: 'WEEK',
      WEEKS: 'WEEKS'
    },
    marketingDSType: {
      Users: 'Active Directory',
      Alerts: 'Respond(Alerts)',
      Incidents: 'Respond(Incidents)',
      Machines: 'Endpoint (Machines)',
      Modules: 'Endpoint (Modules)',
      IOC: 'Endpoint (IOC)',
      Archer: 'Archer',
      LIST: 'List',
      FileReputationServer: 'File Reputation'
    },
    header: {
      title: {
        archer: 'Archer',
        users: 'Active Directory',
        alerts: 'Alerts',
        incidents: 'Incidents',
        lIST: 'Lists',
        endpoint: 'NetWitness Endpoint',
        liveConnectIp: 'Live Connect',
        liveConnectFile: 'Live Connect',
        liveConnectDomain: 'Live Connect',
        fileReputationServer: 'File Reputation'
      },
      archer: 'Archer',
      overview: 'overview',
      iioc: 'IIOC',
      users: 'Users',
      categoryTags: 'category tags',
      modules: 'Modules',
      incidents: 'Incidents',
      alerts: 'Alerts',
      files: 'Files',
      lists: 'Lists',
      feeds: 'Feeds',
      endpoint: 'Endpoint',
      liveConnect: 'Live Connect',
      unsafe: 'Unsafe',
      fileReputationServer: 'File Reputation',
      closeButton: {
        title: 'Close'
      },
      help: {
        title: 'Help'
      }
    },
    toolbar: {
      investigate: 'Investigate',
      endpoint: 'NetWitness Endpoint',
      googleLookup: 'Google Lookup',
      virusTotal: 'VirusTotal Lookup',
      addToList: 'Add to List'
    },
    hostSummary: {
      title: 'Endpoint',
      riskScore: 'Risk Score',
      modulesCount: '# of Modules',
      iioc0: 'Iioc 0',
      iioc1: 'Iioc 1',
      lastUpdated: 'Last Updated',
      adminStatus: 'Admin Status',
      lastLogin: 'Last Login',
      macAddress: 'MAC Address',
      operatingSystem: 'Operating System',
      machineStatus: 'Machine Status',
      ipAddress: 'IPAddress',
      endpoint: 'Applicable for hosts with 4.X Endpoint agents installed, please install Netwitness Endpoint Thick Client.'
    },
    addToList: {
      title: 'Add/Remove from List',
      create: 'Create New List',
      metaValue: 'Meta Value',
      newList: 'Create New List',
      tabAll: 'All',
      tabSelected: 'Selected',
      tabUnselected: 'Unselected',
      cancel: 'Cancel',
      save: 'Save',
      name: 'List Name',
      listTitle: 'List',
      descriptionTitle: 'Description',
      filter: 'Filter Results',
      listName: 'Enter List Name',
      headerMessage: 'Click on Save to update the list(s). Refresh the page to view the updates.'
    },
    ADdata: {
      title: 'User Information',
      employeeID: 'employee ID',
      department: 'department',
      location: 'location',
      manager: 'manager',
      groups: 'groups',
      company: 'company',
      email: 'Email',
      phone: 'phone',
      jobTitle: 'job Title',
      lastLogon: 'last Logon',
      lastLogonTimeStamp: 'last Logon TimeStamp',
      adUserID: 'ad User ID',
      distinguishedName: 'Distinguished Name',
      displayName: 'Display Name'
    },
    archer: {
      title: 'Archer',
      criticalityRating: 'Criticality Rating',
      riskRating: 'Risk Rating',
      deviceName: 'Device Name',
      hostName: 'Hostname',
      deviceId: 'Device Id',
      deviceType: 'Device Type',
      deviceOwner: 'Device Owner',
      deviceOwnerTitle: 'Device Owner Title',
      businessUnit: 'Business Unit',
      facility: 'Facility',
      ipAddress: 'Internal IP Address',
      businessProcesses: 'Business Processes',
      macAddress: 'MAC Address'
    },
    reputation: {
      title: 'Reputation',
      status: 'Reputation Status',
      scannerMatch: 'Scanner Match',
      platform: 'Classification Platform',
      type: 'Classification Type',
      familyName: 'Classification Family'
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
      title: 'Machine IOC Levels',
      lastExecuted: 'LastExecuted',
      description: 'Description',
      iOCLevel: 'IOC Level',
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
      risk_score: 'Severity',
      source: 'Source',
      name: 'Name',
      numEvents: '# Events',
      severity: 'Severity',
      created: 'Created',
      id: 'Incident ID',
      timestamp: 'timestamp',
      header: '',
      sourceName: {
        'Risk Scoring': 'Risk Scoring',
        'ESA Correlation': 'ESA Correlation',
        ECAT: 'Endpoint',
        'Event Stream Analysis': 'Event Stream Analysis',
        'Event Streaming Analytics': 'Event Stream Analysis',
        'Security Analytics Investigator': 'Security Analytics Investigator',
        'Web Threat Detection': 'Web Threat Detection',
        'Malware Analysis': 'Malware Analysis',
        'Reporting Engine': 'Reporting Engine',
        'NetWitness Investigate': 'NetWitness Investigate',
        'User Entity Behavior Analytics': 'User Entity Behavior Analytics'
      }
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
      notReviewed: 'NOT REVIEWED',
      noFeedback1: 'No Feedback Analysis Yet',
      noFeedback2: ' - Be an active member of the Live Connect Threat Community, provide your risk assessment',
      blankField: '-',
      modifiedDate: 'Modified Date',
      reviewer: 'Reviewer',
      riskConfirmation: 'Risk Confirmation',
      safe: 'Safe',
      unsafe: 'Unsafe',
      unknown: 'Unknown',
      suspicious: 'Suspicious',
      highRisk: 'High Risk',
      high: 'High',
      med: 'Medium',
      low: 'Low',
      riskTags: 'Risk Indicator Tags',
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
      organization: 'Organization',
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
      whoisFax: 'Fax',
      whoisEmail: 'Email',
      domain: 'Domain',
      ipAddress: 'IP Address',
      errorMsg: 'Could not fetch data from Live Connect: {{error}}',
      riskAssessment: 'Live Connect Risk Assessment',
      riskReason: 'Risk Reasons',
      highRiskDesc: 'Indicator seen as high risk and deserves focused attention',
      safeRiskDesc: 'Research and analysis shows indicators to be trusted resources',
      unsafeRiskDesc: 'Research and analysis shows resource to be untrusted',
      unknownRiskDesc: 'Inconclusive results from any available information, research, and analysis',
      suspiciousRiskDesc: 'Research and analysis indicates potentially threatening activity',
      riskFeedback: 'Risk Assessment Feedback',
      relatedFiles: 'Related Files ',
      risk: 'LC RISK RATING',
      importHashFunction: 'API FUNCTION IMPORT HASH',
      compiledTime: 'COMPILE DATE',
      relatedDomains: 'Related Domains ',
      relatedIps: 'Related IPs ',
      country: 'Country',
      registeredDate: 'Registered Date',
      expiredDate: 'Expired Date',
      email: 'Registrant email',
      asnShort: 'ASN',
      confidenceLevel: 'Confidence Level',
      select: 'Select...',
      feedbackSubmitted: 'Feedback has been submitted to Live Connect server.',
      feedbackSubmissionFailed: 'Could not submit feedback to Live Connect server.',
      feedbackFormInvalid: 'Select the \'Risk Confirmation\' and \'Confidence Level\'.',
      noTrendingCommunityActivity: 'There is no new community activity in the past 30 days',
      noTrendingSubmissionActivity: 'There are no new submissions in the past 30 days',
      skillLevel: 'Analyst Skill Level',
      skillLevelPrefix: 'Tier {{level}}',
      noRelatedData: 'There are no Related {{entity}} for this entity.',
      ips: 'IPs',
      files: 'Files',
      domains: 'Domains'
    },
    error: {
      error: 'An unexpected error occurred when attempting to fetch the data.',
      noDataSource: 'No data source configured/enabled.',
      dataSourcesFailed: 'Unable to fetch data from the configured data sources.',
      dataSource: 'An unexpected error occurred when attempting to fetch the data.',
      noData: 'No context data is available for this DataSource.',
      listDuplicateName: 'List name already exists!',
      listValidName: 'Enter valid list name (Max length is 255 characters).',
      'mongo.error': 'Unexpected database error occurred.',
      'total.entries.exceed.max': 'List size exceeds the limit 100000.',
      'context.error': 'Context  service is not reachable. Check your service connectivity.',
      'datasource.disk.usage.high': 'Low disk space. Delete unwanted data to free up space.',
      'context.service.timeout': 'Context Hub service is not reachable. Check your service connectivity.',
      'get.mongo.connect.failed': 'Database is not reachable. Retry after sometime.',
      'datasource.query.not.supported': 'Context data lookup is not supported for this meta.',
      'transport.http.read.failed': 'Context data is not available as the data source is not reachable.',
      'transport.ad.read.failed': 'Context data is not available as the data source is not reachable.',
      'transport.init.failed': 'Data source connection timed out.',
      'transport.not.found': 'Context data is not available as the data source is not reachable.',
      'transport.create.failed': 'Context data is not available as the data source is not reachable.',
      'transport.refresh.failed': 'Context data is not available as the data source is not reachable.',
      'transport.connect.failed': 'Context data is not available as the data source is not reachable.',
      'live.connect.private.ip.unsupported': 'Only public IP addresses are supported by Live Connect.',
      'transport.http.error': 'Context lookup failed for this datasource since it returned an error.',
      'transport.validation.error': 'The data format is not supported for the data source.',
      'transport.http.auth.failed': 'Could not fetch context from this data source - Authorization failed.',
      'service.unavailable': 'Maximum queries permissible in a day for RSA Live File Reputation Service has exceeded. Retry after 00:00 UTC tomorrow. Click Help for more details.',
      'transport.data.read.failed': 'Context Lookup has timed out. No results found.',
      archer: {
        noData: 'Data in RSA Archer is not available.',
        notConfigured: 'Configure Archer as a data source, add or enable to pivot successfully.',
        notReachable: 'Check the data source connectivity to Archer.',
        invalidAttributes: ' field(s) does not exists in Archer.'
      }
    },
    footer: {
      viewAll: 'VIEW All',
      title: {
        incidents: 'Incident(s)',
        alerts: 'Alert(s)',
        lIST: 'List(s)',
        users: 'User(s)',
        endpoint: 'Host',
        archer: 'Asset',
        fileReputationServer: 'File Reputation'
      },
      resultCount: '(First {{count}} Results)'
    },
    tooltip: {
      contextHighlights: 'Context Highlights',
      viewOverview: 'View Context',
      actions: 'Actions',
      navigate: 'Pivot to Investigate > Navigate',
      addToList: 'Add/Remove from List',
      virusTotal: 'Virus Total Lookup',
      googleLookup: 'Google Lookup',
      ecat: 'Pivot to Endpoint Thick Client',
      events: 'Pivot to Events',
      archer: 'Pivot to Archer',
      endpoint: 'Pivot to Investigate > Hosts/Files',
      contextUnavailable: 'No context data available at this time.',
      archerToolTip: 'Add or enable Archer or Data is not available.',
      dataSourceNames: {
        Incidents: 'Incidents',
        Alerts: 'Alerts',
        LIST: 'Lists',
        Users: 'Users',
        IOC: 'IOCs',
        Machines: 'Endpoint',
        Modules: 'Modules',
        'LiveConnect-Ip': 'LiveConnect',
        'LiveConnect-File': 'LiveConnect',
        'LiveConnect-Domain': 'LiveConnect',
        criticality: 'Criticality',
        riskRating: 'Asset Risk',
        FileReputationServer: 'Reputation'
      }
    }
  },
  preferences: {
    'investigate-events': {
      panelTitle: 'Event Preferences',
      triggerTip: 'Open/Hide Event Preferences',
      defaultEventView: 'Default Event Analysis View',
      defaultLogFormat: 'Default Log Format',
      defaultPacketFormat: 'Default Network Format',
      defaultMetaFormat: 'Default Meta Format',
      // LOG: 'Download Text',
      CSV: 'Download CSV',
      TSV: 'Download TSV',
      TEXT: 'Download Text',
      XML: 'Download XML',
      JSON: 'Download JSON',
      PCAP: 'Download PCAP',
      PAYLOAD: 'Download Payloads',
      PAYLOAD1: 'Download Request Payload',
      PAYLOAD2: 'Download Response Payload',
      reconView: {
        FILE: 'File Analysis',
        TEXT: 'Text Analysis',
        PACKET: 'Packet Analysis',
        MAIL: 'Email Analysis'
      },
      queryTimeFormat: 'Time format for query',
      DB: 'Database Time',
      WALL: 'Current Time',
      'DB-tooltip': 'Default selection. When selected, the end time is the last time data was stored in the service being queried.',
      'WALL-tooltip': 'When selected, the end time is the current time of day, with the preferred time zone, no matter when the most recent data was stored.',
      autoDownloadExtractedFiles: 'Download extracted files automatically',
      'autoDownloadExtractedFiles-tooltip': 'If selected, the browser downloads files to the download folder and if a file type is mapped to a default program, automatically open it.',
      autoUpdateSummary: 'Update time window automatically',
      'autoUpdateSummary-tooltip': 'Enables continuous update of the relative time window for the query. The time window updates when new data is stored (database time) or when a minute elapses (current time). In both cases the query icon activates, allowing you to refresh stale results.',
      eventTimeSortOrder: 'Event Sort Order',
      'eventTimeSortOrder-tooltip': 'Sort events in descending or ascending order based on collection time. If results exceed the events limit, not all events can be loaded. The portion of returned events (oldest or newest) that is loaded will match the sort order preference (Descending or Ascending respectively).',
      Descending: 'Descending',
      Ascending: 'Ascending',
      sortOrderDefaultText: '(default)'
    },
    'endpoint-preferences': {
      visibleColumns: 'Visible Columns',
      sortField: 'Sort Field',
      sortOrder: 'Sort Order',
      filter: 'Filter'
    }
  },
  packager: {
    errorMessages: {
      invalidServer: 'Please enter valid IP address or hostname',
      invalidPort: 'Please enter valid port number',
      invalidName: 'Please enter a valid name without special characters',
      passwordEmptyMessage: 'Please enter certificate password',
      invalidPasswordString: 'Password must contain at least 3 characters.',
      NAME_EMPTY: 'Warning: The configuration name is empty.',
      SERVERS_EMPTY: 'Warning: No servers are found.',
      EVENT_ID_INVALID: 'Warning: Event ID is invalid.',
      CHANNEL_EMPTY: 'Warning: Channel is empty.',
      FILTER_EMPTY: 'Warning: Filter is empty.',
      FILTER_INVALID: 'Warning: Filter is invalid.',
      INVALID_HOST: 'Warning: Host is invalid.',
      CONFIG_NAME_INVALID: 'Warning: The config name is invalid.',
      INVALID_PROTOCOL: 'Warning: The protocol is invalid.',
      CHANNEL_NAME_INVALID: 'Warning: The channel name is invalid.',
      EMPTY_CHANNELS: 'Warning: The channel name is empty.',
      CHANNEL_FILTER_INVALID: 'Warning: The channel filter is invalid.',
      DESTINATIONS_NOT_UNIQUE: 'Warning: Primary Destination and Secondary Destination are same.',
      packagerNotCreated: 'Agent Packager could not be created, please check whether the selected server is online'
    },
    errorPage: {
      errorTitle: 'Endpoint Server is offline',
      errorDescription: 'Endpoint Server may not be configured properly. See the Host Installation Guide for instructions on Endpoint Log Hybrid installation.'
    },
    packagerTitle: 'Agent Packager',
    serverListSearch: 'Type to filter options',
    serviceName: 'Service Name<sup>*</sup>',
    serverSettings: 'Endpoint Server Settings',
    server: 'Endpoint Server',
    serverEdit: 'Server Alias (Optional)',
    port: 'HTTPS Port<sup>*</sup>',
    certificateValidation: 'Server Validation',
    certificatePassword: 'Certificate Password<sup>*</sup>',
    none: 'None',
    fullChain: 'Full Chain',
    thumbprint: 'Certificate Thumbprint',
    reset: 'Cancel',
    generateAgent: 'Generate Agent Packager',
    generateLogConfig: 'Generate Log Configuration Only',
    loadExistingLogConfig: 'Load Existing Configuration...',
    description: 'Description',
    title: 'Agent Packager',
    becon: 'Beacon',
    displayName: 'Display Name<sup>*</sup>',
    driverServiceName: 'Driver Service Name<sup>*</sup>',
    driverDisplayName: 'Driver Display Name<sup>*</sup>',
    driverDescription: 'Driver Description',
    monitoringMode: 'Enable Monitoring',
    forceOverwriteWarningMessage: 'Force overwrite option is applicable only for windows agent',
    upload: {
      success: 'Configuration file loaded successfully.',
      failure: 'Unable to upload Configuration file.'
    },
    error: {
      generic: 'An unexpected error has occurred attempting to retrieve this data.'
    },
    autoUninstall: 'Auto Uninstall',
    forceOverwrite: 'Force Overwrite',
    overwriteInfo: 'Overwrites the installed Windows agent regardless of the version.',
    windowsLogCollectionCongfig: 'Windows Log Collection Configuration',
    enableWindowsLogCollection: 'Configure Windows Log Collection',
    configurationName: 'Configuration Name<sup>*</sup>',
    statusLabel: 'Status',
    enabled: 'Enabled',
    disabled: 'Disabled',
    tooltip: 'Configuration information will be saved but logs will not be forwarded.',
    primaryLogDecoder: 'Primary Log Decoder/Log collector*',
    secondaryLogDecoder: 'Secondary Log Decoder/Log Collector',
    protocol: 'Protocol',
    channels: 'Channel filters',
    eventId: 'event id to include/exclude (?)',
    heartbeatLogs: 'Send Heartbeat logs',
    heartbeatFrequency: 'Heartbeat Frequency',
    testLog: 'Send Test Log',
    placeholder: 'Make a selection',
    searchPlaceholder: 'Enter the filter option',
    emptyName: 'Configuration name is empty',
    channelFilter: 'Channel Filters🛈',
    specialCharacter: 'Configuration name contains special character.',
    fullAgent: 'Full Agent',
    channel: {
      add: 'Add a new channel',
      name: 'CHANNEL NAME *',
      filter: 'FILTER *',
      event: 'EVENT ID *',
      empty: ''
    },
    agentConfiguration: 'AGENT CONFIGURATION',
    service: 'SERVICE',
    driver: 'DRIVER',
    agentConfigNote: 'For a subsequent installation/upgrade, use the same service names.',
    helpText: {
      title: 'Quick Help',
      downloadInstaller: 'Download Installer',
      downloadInstallerContent: 'Packager is a zip file that contains executables and configuration files for Linux, Mac, and Windows operating systems.',
      whatNext: 'What next?',
      whatNextContentStep1: '1. Specify the Endpoint server, password, agent configuration settings, and generate the agent packager.',
      whatNextContentStep2: '2. Copy the AgentPackager.zip file to a Windows machine and generate agent installers. When prompted for a password, use the same password that you used to generate the agent packager.',
      whatNextContentStep3: '3. Deploy the agent on the host you want to monitor.',
      whatNextContentStep4: 'After the agent is installed and it successfully communicates with the Endpoint server, it appears in the Hosts view.',
      moreInfo: 'For more information, see the NetWitness Endpoint Agent Installation Guide.'
    }
  },
  endpointRAR: {
    title: 'Relay Server',
    notPermitted: 'Unable to load page due to insufficient permissions.',
    rarInstaller: {
      title: 'Download Installer',
      instruction: 'Specify a password that will be used during the Relay Server installation.',
      password: 'Password<sup>*</sup>',
      downloadButton: 'Download'
    },
    rarConfig: {
      enabled: 'Enable Relay Server',
      enabledMessage: 'Agents outside the corporate network can connect to the relay server only if the relay server configuration is enabled.',
      infoMessage: 'ESH is a hostname which can be resolved only within the corporate network.',
      title: 'Configure',
      hostName: 'Enterprise Specific hostname (ESH)<sup>*</sup>',
      endpointServer: 'RELAY SERVER',
      server: 'Server<sup>*</sup>',
      port: 'Port<sup>*</sup>',
      beaconInterval: 'HTTP BEACON INTERVAL<sup>*</sup>',
      mins: 'mins',
      saveConfig: 'Save configuration',
      resetConfig: 'Cancel',
      testConfiguration: 'Test connection',
      successMessage: 'Configuration saved successfully.',
      failureMessage: 'Unable to save configuration.',
      failureMessageForStatus: 'Unable to save the status.',
      dbFailureMessage: 'Unable to save configuration due to database connection timeout.',
      dbFailureMessageForStatus: 'Unable to save the status due to database connection timeout.',
      permissionDenied: 'Unable to save configuration due to insufficient permissions.',
      permissionDeniedForEnable: 'Unable to save status due to insufficient permissions.',
      permissionDeniedForDownload: 'Unable to download due to insufficient permissions.',
      testConfigSuccess: 'Test connection successful.',
      testConfigFailure: 'Test connection failed.',
      disableRar: 'Relay Sever configuration is disabled.',
      enableRar: 'Relay Sever configuration is enabled.',
      dbFailureMessageForLoad: 'Unable to load the page due to database connection timeout. Retry after sometime.',
      failureMessageForLoad: 'Unable to load the page. Retry after sometime.',
      installerFailure: 'Unable to download the installer. Retry after sometime.'
    },
    errorMessages: {
      passwordEmptyMessage: 'Enter the relay server installation password.',
      invalidPasswordString: 'Password must contain at least 3 characters.',
      invalidPort: 'Enter valid port number.',
      serverEmptyMessage: 'Enter an IP address or a hostname.',
      invalidServer: 'Enter valid IP address or hostname.',
      invalidBeaconInterval: 'Interval ranges from 60-1440 minutes.',
      invalidHostName: 'Enter a valid hostname.'
    },
    errorPage: {
      errorTitle: 'Endpoint Server is offline',
      errorDescription: 'Endpoint Server may not be configured properly. See the Host Installation Guide for instructions on Endpoint Log Hybrid installation.'
    },
    helpText: {
      title: 'Quick Help',
      quickHelpText: 'Relay Server allows hosts that are outside the corporate network to connect to the network and provide visibility into activities occurring on these hosts. When the agent is not able to communicate to the corporate network, it sends data to the configured Relay Server. The Endpoint server pulls this data from the Relay Server.',
      whatNext: 'What next?',
      whatNextContentStep1: '1. Specify the password and download the Relay Server installer. The same installer can be used for multiple Relay server installation.',
      whatNextContentStep2: '2. Install the Relay Server on a CentOS 7 appliance (for example, a server in cloud/DMZ).',
      whatNextContentStep3: '3. Configure the Relay Server and ESH details.',
      moreHelp: 'For more information, see NetWitness Endpoint Configuration Guide.'
    }
  },
  investigateFiles: {
    title: 'Files',
    deleteTitle: 'Confirm Delete',
    noManagePermissions: 'You do not have permissions to make edits to file(s) status',
    certificatePageTitle: 'Certificates',
    showNextRecords: 'Show next {{count}} {{label}}',
    certificate: {
      toolTipCertificateViewDisabled: 'Select a maximum of {{count}} files to view.',
      unsigned: {
        toolTipCertificateViewDisabled: 'No certificates available for the selected files.'
      },
      editCertificateStatus: {
        actionNote: 'Selected status is applied to all files signed by this certificate.',
        commentLimitError: 'Comment is limited to 900 characters'
      },
      contextMenu: {
        actions: {
          editCertificateStatus: 'Change Certificate Status'
        }
      }
    },
    button: {
      exportToCSV: 'Export to CSV',
      brokerExportToCSV: 'Export to CSV is not supported for Endpoint Broker',
      downloading: 'Downloading',
      save: 'Save',
      reset: 'Reset',
      cancel: 'Cancel',
      viewCertificates: 'View Certificates',
      backToFiles: 'Files',
      closeCertificateView: 'Close certificate view',
      filters: 'Filters',
      close: 'Close File Details',
      filePropertyPanel: 'Show/Hide File Property Panel'
    },
    message: {
      noResultsMessage: 'No matching files were found',
      noResultsFoundMessage: 'No results found'
    },
    errorPage: {
      serviceDown: 'Endpoint {{serviceType}}Server is offline',
      serviceDownDescription: 'The Endpoint {{serviceType}}Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
    },
    footer: 'Showing {{count}} out of {{total}} {{label}} | {{selectedItems}} selected',
    fileStatus: {
      Blacklist: 'Blacklisted',
      Graylist: 'Graylisted',
      Whitelist: 'Whitelisted',
      KnownGood: 'Known Good',
      Neutral: 'Neutral'
    },
    remediationAction: {
      Unblock: '--',
      Block: 'Blocked'
    },
    riskScore: {
      label: 'Reset Risk Score',
      confirmMessage: 'Are you sure you want to reset the Risk Score for the selected file(s). All alerts related to this risk score will be deleted and the Risk Score will be reset to 0.',
      cancel: 'Cancel',
      reset: 'Reset',
      success: 'The Risk Score has been successfully reset.',
      warning: 'Risk score of some selected files has been reset successfully.',
      error: ' The Risk Score cannot be reset.',
      limitInfo: 'Risk Score of only top 100 files can be reset at a time.'
    },
    filter: {
      allFiles: 'All',
      filter: 'Filters',
      filters: 'Saved Filters',
      newFilter: 'New Filter',
      windows: 'WINDOWS',
      mac: 'MAC',
      linux: 'LINUX',
      favouriteFilters: 'Favorite Filters',
      addMore: 'Add Filter',
      invalidFilterInput: 'Invalid filter input',
      invalidFilterInputLength: 'Filter input longer than 256 characters',
      invalidCharacters: 'Can contain alphanumeric or special characters.',
      invalidCharsAlphabetOnly: 'Numbers and special characters are not allowed',
      invalidCharsAlphaNumericOnly: 'Special characters are not allowed',
      osType: 'Operating System',
      fileHash: 'File Hash',
      fileHashInfo: 'Enter SHA256, SHA1 or MD5',
      restrictionType: {
        moreThan: 'Greater than',
        lessThan: 'Less than',
        between: 'Between',
        equals: 'Equals',
        contains: 'Contains'
      },
      customFilters: {
        save: {
          description: 'Choose a name that contains 1-256 alphanumeric characters, underscores, and hyphen. <br>The name must not match an already existing filter name.',
          errorHeader: 'Unable to save filter',
          header: 'Save Filter',
          errorMessage: 'The search cannot be saved. ',
          emptyMessage: 'Name field is empty.',
          nameExistsMessage: 'A saved search with the same name.',
          success: 'Filter saved successfully.',
          filterFieldEmptyMessage: 'One or more of the newly added filter fields are empty. Please add the filters or remove the fields to save.',
          invalidInput: 'Name can contain alphanumeric, underscore, or hyphen.'
        },
        accessError: 'You do not have the required permission to save the filter.',
        error: 'Name already exists. Provide a unique name for the filter.',
        delete: {
          successMessage: 'Saved filter deleted successfully.',
          errorMessage: 'Failed to delete the saved search',
          confirmMessage: 'Are you sure you want to delete the saved filter?'
        }
      },
      signature: {
        unsigned: 'Unsigned',
        valid: 'Valid Signature',
        invalid: 'Invalid Signature',
        catalog: 'Catalog',
        signer: {
          microsoft: 'Signed By Microsoft',
          apple: 'Signed By Apple'
        }
      },
      fileType: {
        pe: 'Windows',
        linux: 'Linux',
        macho: 'Mac',
        scripts: 'Scripts',
        unknown: 'Unknown'
      },
      reputationStatus: {
        Invalid: 'Invalid',
        Unknown: 'Unknown',
        Known: 'Known',
        Suspicious: 'Suspicious',
        Malicious: 'Malicious',
        Known_Good: 'Known Good'
      },
      fileDownloadStatus: {
        Downloaded: 'Downloaded',
        Error: 'Error',
        NotDownloaded: 'Not Downloaded'
      }
    },
    fields: {
      panelTitle: 'Files Preferences',
      triggerTip: 'Open/Hide Files Preferences',
      id: 'ID',
      companyName: 'Company Name',
      checksumMd5: 'MD5',
      checksumSha1: 'SHA1',
      checksumSha256: 'SHA256',
      machineOsType: 'Operating System',
      reputationStatus: 'Reputation',
      score: 'Risk Score',
      machineCount: 'Active On',
      remediationAction: 'Remediation',
      elf: {
        classType: 'ELF.Class Type',
        data: 'ELF.Data',
        entryPoint: 'ELF.Entry Point',
        features: 'ELF.Features',
        type: 'ELF.Type',
        sectionNames: 'ELF.Section Names',
        importedLibraries: 'ELF.Imported Libraries'
      },
      pe: {
        timeStamp: 'PE.Timestamp',
        imageSize: 'PE.Image Size',
        numberOfExportedFunctions: 'PE.Exported Functions',
        numberOfNamesExported: 'PE.Exported Names',
        numberOfExecuteWriteSections: 'PE.Execute Write Sections',
        features: 'PE.Features',
        sectionNames: 'PE.Section Names',
        importedLibraries: 'PE.Imported Libraries',
        resources: {
          originalFileName: 'PE.Resources.Filename',
          company: 'PE.Resources.Company',
          description: 'PE.Resources.Description',
          version: 'PE.Resources.Version'
        }
      },
      macho: {
        uuid: 'MachO.Uuid',
        identifier: 'MachO.Identifier',
        minOsxVersion: 'MachO.Osx Version',
        features: 'MachO.Features',
        flags: 'MachO.Flags',
        numberOfLoadCommands: 'MachO.Loaded Commands',
        version: 'MachO.Version',
        sectionNames: 'MachO.Section Names',
        importedLibraries: 'MachO.Imported Libraries'
      },
      signature: {
        timeStamp: 'Signature.Timestamp',
        thumbprint: 'Signature.Thumbprint',
        features: 'Signature',
        signer: 'Signer'
      },
      owner: {
        userName: 'Owner',
        groupName: 'Owner Group'
      },
      rpm: {
        packageName: 'Package'
      },
      downloadInfo: {
        fileName: 'Download Filename',
        status: 'Downloaded',
        path: 'Download Path'
      },
      path: 'Path',
      entropy: 'Entropy',
      fileName: 'FileName',
      firstFileName: 'File Name',
      firstSeenTime: 'First Seen Time',
      timeCreated: 'Created',
      format: 'Format',
      sectionNames: 'Section Names',
      importedLibraries: 'Imported Libraries',
      size: 'Size',
      fileStatus: 'File Status',
      downloaded: 'Downloaded'
    },
    sort: {
      fileNameDescending: 'Filename (descending)',
      fileNameAscending: 'Filename (ascending)',
      sizeAscending: 'Size (ascending)',
      sizeDescending: 'Size (descending)',
      formatAscending: 'Format (ascending)',
      formatDescending: 'Format (descending)',
      signatureAscending: 'Signature (ascending)',
      signatureDescending: 'Signature (descending)',
      fileStatusAscending: 'File Status (ascending)',
      fileStatusDescending: 'File Status (descending)'
    },
    editFileStatus: {
      successMessage: 'File status updated successfully',
      title: 'Change File Status',
      blacklistCategory: 'Category',
      blacklistCategoryPlaceholder: 'Select',
      fileStatusTitle: 'Status',
      fileStatusOptions: {
        neutral: 'Neutral',
        whitelist: 'Whitelist',
        graylist: 'Graylist',
        blacklist: 'Blacklist',
        knowngood: 'Known Good'
      },
      fileCategoryTitle: 'File Category',
      remediationActionTitle: 'Remediation',
      remediationActionNote: 'Applies to hosts where blocking is enabled and terminates all <br> processes associated with the selected file hashes.',
      remediationActionAlert: {
        isSigned: 'Files signed by RSA and Microsoft cannot be blocked.',
        sizeExceeds: 'Files exceeding 100 MB cannot be blocked.',
        osNotToBlock: 'Files on Linux and Mac hosts cannot be blocked.',
        isFloatingOrMemoryDll: 'Memory Dlls and Floating code cannot be blocked.'
      },

      commentLimitError: 'Comment is limited to 900 characters',

      whiteListAlert: {
        warningMessage: 'File cannot be whitelisted as it can be used for malicious purposes.',
        info: 'Some selected files cannot be whitelisted as they can be used for malicious purposes.',
        limitInfo: 'Status of only top 100 files can be edited at a time.'
      },
      remediationActionOptions: {
        blockFile: 'Block',
        blockQuarantineFile: 'Block and Quarantine'
      },
      certificateStatusTitle: 'Certificate Status',
      commentTitle: 'Comments ',
      placeholder: 'Enter comments',
      saveBtn: 'Save',
      cancelBtn: 'Cancel',
      blacklistCertificate: 'Blacklist Certificate',
      blacklistAllFiles: 'Blacklist all files signed by this certificate',
      contexthubServerOffline: 'An error has occurred. The Contexthub server may be offline or inaccessible. '
    },
    tabs: {
      riskDetails: 'Risk Details',
      fileDetails: 'File Details',
      hosts: 'HOSTS',
      overview: 'Details',
      analysis: 'Analysis'
    }
  },
  investigateHosts: {
    title: 'Investigate',
    loading: 'Loading',
    loadMore: 'Load More',
    deleteTitle: 'Confirm Delete',
    noSnapshotMessage: 'No scan history were found.',
    common: {
      save: 'Save',
      enable: 'Enable',
      saveSuccess: 'Saved successfully',
      emptyMessage: 'No matching results'
    },
    errorPage: {
      serviceDown: 'Endpoint {{serviceType}}Server is offline',
      serviceDownDescription: 'The Endpoint {{serviceType}}Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
    },
    property: {
      file: {
        features: 'Features',
        fileId: 'FileId',
        id: 'ID',
        serviceName: 'ServiceName',
        state: 'State',
        description: 'Description',
        type: 'Type',
        displayName: 'Display Name',
        win32ErrorCode: 'win32ErrorCode',
        creatorUser: 'Creator User',
        executeUser: 'Execute User',
        companyName: 'Company Name',
        registryPath: 'Registry Path',
        launchArguments: 'Launch Arguments',
        triggerString: 'Trigger String',
        user: 'User',
        name: 'Name',
        status: 'Status',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Operating System',
        timeCreated: 'Created',
        timeModified: 'Modified',
        timeAccessed: 'Accessed',
        createTime: 'Process Created',
        pid: 'PID',
        eprocess: 'EPROCESS',
        path: 'Full Path',
        processName: 'Process Name',
        processTime: 'Process Creation Time',
        ethread: 'ETHREAD',
        tid: 'Thread ID',
        teb: 'Thread Environment Block',
        startAddress: 'Start Address',
        sameDirectoryFileCounts: {
          nonExe: '# Non-Executables',
          exe: '# Executables',
          subFolder: '# Folder',
          exeSameCompany: '#Same company Executables'
        },
        elf: {
          classType: 'Class Type',
          data: 'Data',
          entryPoint: 'Entry Point',
          features: 'Features',
          type: 'Type',
          sectionNames: 'Section Names',
          importedLibraries: 'Imported Libraries'
        },
        pe: {
          timeStamp: 'Timestamp',
          imageSize: 'Image Size',
          numberOfExportedFunctions: 'Exported Functions',
          numberOfNamesExported: 'Exported Names',
          numberOfExecuteWriteSections: 'Execute Write Sections',
          features: 'Features',
          sectionNames: 'Section Names',
          importedLibraries: 'Imported Libraries',
          resources: {
            originalFileName: 'Filename',
            company: 'Company',
            description: 'Description',
            version: 'Version'
          }
        },
        macho: {
          uuid: 'Uuid',
          identifier: 'Identifier',
          minOsxVersion: 'Osx Version',
          features: 'Features',
          flags: 'Flags',
          numberOfLoadCommands: 'Loaded Commands',
          version: 'Version',
          sectionNames: 'Section Names',
          importedLibraries: 'Imported Libraries'
        },
        signature: {
          timeStamp: 'Timestamp',
          thumbprint: 'Thumbprint',
          features: 'Features',
          signer: 'Signer'
        },
        process: {
          title: 'Process',
          processName: 'Process Name',
          eprocess: 'EPROCESS',
          integrityLevel: 'Integrity',
          parentPath: 'Parent Path',
          threadCount: 'Thread Count',
          owner: 'Owner',
          sessionId: 'Session ID',
          createUtcTime: 'Created',
          imageBase: 'Image Base',
          imageSize: 'Image Size'
        },
        entropy: 'Entropy',
        firstFileName: 'FileName',
        fileName: 'FileName',
        format: 'Format',
        sectionNames: 'Section Names',
        importedLibraries: 'Imported Libraries',
        size: 'Size',
        imageBase: 'Image Base',
        imageSize: 'Image Size',
        loaded: 'Loaded',
        fileProperties: {
          entropy: 'Entropy',
          size: 'Size',
          format: 'Format',
          reputationStatus: 'Reputation',
          score: 'Risk Score'
        },
        inlinePatch: {
          currentAsm: 'Current Assembly',
          originalAsm: 'Original Assembly'
        },
        hookLocation: {
          address: 'Hooked Address',
          symbolOffset: 'Symbol Offset',
          section: 'Section',
          fileName: 'Hooked FileName',
          symbol: 'Symbol',
          checksumSha256: 'ChecksumSha256',
          objectName: 'Object Name',
          path: 'Path',
          imageBase: 'Image Base',
          imageSize: 'Image Size',
          objectFunction: 'Object Function'
        },
        jumpTo: 'Jump To Address',
        jumpCount: 'Jump Count',
        hookedProcess: 'Process Context',
        reputationStatus: 'Reputation'
      }
    },
    process: {
      title: 'Processes',
      processName: 'Process Name',
      properties: 'Details',
      pid: 'PID',
      parentId: 'PPID',
      owner: 'Owner',
      hostCount: 'Host Count',
      creationTime: 'Creation Time',
      hashlookup: 'Hash Lookup',
      signature: 'Signature',
      path: 'Path',
      launchArguments: 'Launch Arguments',
      reputationStatus: 'Reputation',
      score: 'Risk Score',
      fileStatus: 'File Status',
      downloaded: 'Downloaded',
      riskScore: 'Risk Score',
      localRiskScore: 'Local Risk Score',
      globalRiskScore: 'Global Risk Score',
      hosts: 'Hosts',
      filePath: 'File Path',
      backToProcesses: 'Processes',
      processDetails: 'Process Details',
      analyzeProcess: 'Analyze Process',
      analyzeProcessDisabledTooltip: 'Select a single file to analyze.',
      machineCount: 'Active On',
      toolTip: {
        openProcessDetails: 'Open Process details.',
        openFileDetails: 'Open File details.',
        treeView: 'Switch to Tree view.',
        listView: 'Switch to Process view.'
      },
      viewType: {
        listView: 'List view',
        treeView: 'Tree view',
        treeViewTooltip: 'Sorting in not possible in tree view.'
      },
      message: {
        noResultsMessage: 'No process information was found.'
      },
      dll: {
        dllName: 'DLL Name',
        filePath: 'File Path',
        title: 'Loaded Libraries ({{count}})',
        message: {
          noResultsMessage: 'No loaded library information was found'
        },
        note: {
          windows: ' Displays libraries that are not signed by Microsoft.',
          mac: ' Displays libraries that are not signed by Apple.'
        }
      },
      suspiciousThreads: {
        signature: 'Signature',
        tid: 'Thread ID',
        dllFileName: 'DLL Name',
        teb: 'Thread Environment Block',
        startAddress: 'Start Address',
        title: 'Suspicious Threads ({{count}})',
        message: {
          noResultsMessage: 'No Suspicious Thread information was found.'
        }
      },
      imageHooks: {
        signature: 'Signature',
        type: 'Type',
        dllFileName: 'DLL Name',
        hookFileName: 'Hooked FileName',
        symbol: 'Symbol',
        title: 'Image Hooks ({{count}})',
        message: {
          noResultsMessage: 'No Image Hooks information was found.'
        }
      },
      processData: {
        signature: 'Signature',
        signer: 'Signer',
        reputationStatus: 'Reputation',
        fileStatus: 'File Status',
        directoryDst: 'File Path',
        paramDst: 'Launch Arguments',
        userAll: 'Username',
        copyLaunchArguments: 'Copy Launch Arguments'
      }
    },
    anomalies: {
      imageHooks: {
        type: 'Type',
        dllFileName: 'File Name',
        reputationStatus: 'Reputation',
        localRiskScore: 'Local Risk Score',
        globalRiskScore: 'Global Risk Score',
        hookFileName: 'Hooked FileName',
        hookedProcess: 'Hooked Process',
        hookedSymbol: 'Hooked Symbol',
        signature: 'Signature',
        title: 'Image Hooks',
        downloaded: 'Downloaded',
        message: {
          noResultsMessage: 'No Image Hooks information was found'
        },
        machineCount: 'Active On'
      },
      suspiciousThreads: {
        signature: 'Signature',
        tid: 'Thread ID',
        dllFileName: 'DLL Name',
        reputationStatus: 'Reputation',
        localRiskScore: 'Local Risk Score',
        globalRiskScore: 'Global Risk Score',
        teb: 'Thread Environment Block',
        startAddress: 'Start Address',
        process: 'Process',
        downloaded: 'Downloaded',
        message: {
          noResultsMessage: 'No Suspicious Threads information was found'
        },
        machineCount: 'Active On'
      },
      kernelHooks: {
        type: 'Type',
        driverFileName: 'Driver Name',
        hookedFileName: 'Hooked FileName',
        reputationStatus: 'Reputation',
        localRiskScore: 'Local Risk Score',
        globalRiskScore: 'Global Risk Score',
        signature: 'Signature',
        title: 'Kernel Hooks',
        objectFunction: 'Object Function',
        downloaded: 'Downloaded',
        message: {
          noResultsMessage: 'No Kernel Hooks information was found'
        },
        machineCount: 'Active On'
      },
      registryDiscrepancies: {
        hive: 'Hive',
        reason: 'Reason',
        registryPath: 'Registry Path',
        rawType: 'Raw Type',
        rawData: 'Raw Data',
        apiType: 'Api Type',
        apiData: 'Api Data',
        message: {
          noResultsMessage: 'No Registry Discrepancy information was found'
        }
      }
    },
    downloads: {
      filters: {
        title: 'Filters',
        fileType: 'File Type',
        mft: 'MFT',
        files: 'File',
        memoryDump: 'Memory Dump',
        fileName: 'File Name',
        checksum: 'checksumSha256',
        downloadedTime: 'Downloaded Time'
      },
      help: {
        header: 'Select the drive or folder to view content.'
      },
      backToDownloads: 'Downloads',
      backToDownloadsTitle: 'Back to downloads tab',
      errorMessages: {
        invalidFilterInputLength: 'Filter input longer than 256 characters',
        invalidCharsAlphaNumericOnly: 'Special characters are not allowed',
        noResultsMessage: 'No downloads found'
      },
      tableHeader: {
        filename: 'File name',
        fileType: 'Type',
        downloaded: 'Downloaded',
        fileSize: 'Size',
        downloadedTime: 'Downloaded Time',
        agentId: 'Id of the agent',
        hash: 'SHA256'
      },
      buttons: {
        saveLocalCopy: 'Save local copy',
        deleteFiles: 'Delete file'
      },
      deleteDownloadedFiles: {
        title: 'Downloads',
        modal: {
          title: 'Delete downloaded files',
          message: 'Do you want to delete the downloaded files from the server?'
        },
        success: 'Selected downloaded files have been deleted from the server'
      },
      showNextRecords: 'Show next {{count}} {{label}}',
      footer: 'Showing {{count}} out of {{total}} {{label}} | {{selectedItems}} selected'
    },
    tabs: {
      overview: 'Details',
      process: 'Processes',
      PROCESS: 'Processes',
      autoruns: 'Autoruns',
      files: 'Files',
      FILE: 'Files',
      drivers: 'Drivers',
      DRIVER: 'Drivers',
      systemInformation: 'System Info',
      services: 'Services',
      tasks: 'Tasks',
      hostFileEntries: 'Host File Entries',
      mountedPaths: 'Mounted Paths',
      networkShares: 'Network Shares',
      bashHistories: 'Bash History',
      libraries: 'Libraries',
      LIBRARY: 'Libraries',
      explore: 'Explore',
      securityProducts: 'Security Products',
      windowsPatches: 'Windows Patches',
      securityConfiguration: 'Security Configuration',
      anomalies: 'Anomalies',
      hooks: 'Image Hooks',
      threads: 'Suspicious Threads',
      kernelHooks: 'Kernel Hooks',
      registryDiscrepancies: 'Registry Discrepancies',
      hostDetails: 'Host Details',
      policyDetails: 'Policy Details',
      riskDetails: 'Risk Details',
      localRiskDetails: 'Local Risk Details',
      fileDetails: 'File Details',
      downloads: 'Downloads'
    },
    context: {
      score: 'Score',
      source: 'Source',
      timestamp: 'Created',
      risk_score: 'Severity',
      name: 'Name',
      numEvents: '# Events',
      incident: 'Incident ID',
      _id: 'ID',
      priority: 'Priority',
      status: 'Status',
      alertCount: 'Alerts',
      averageAlertRiskScore: 'Risk Score',
      created: 'Created',
      assignee: 'Assignee',
      details: 'Details',
      error: {
        'context.service.timeout': 'Context Hub server is not running or is inaccessible. Check with your Administrator to resolve this issue.',
        'context.error': 'Context service is not reachable. Check your service connectivity.',
        'transport.data.read.failed': 'Context Lookup has timed out. No results found.'
      },
      button: {
        close: 'Close Panel'
      },
      filterMessage: 'Filtered {{filterCount}} out of {{totalCount}}'
    },
    systemInformation: {
      ipAddress: 'IP Address',
      dnsName: 'DNS Name',
      fileSystem: 'File System',
      path: 'Path',
      remotePath: 'Remote Path',
      options: 'Options',
      name: 'Name',
      description: 'Description',
      permissions: 'Permissions',
      type: 'Type',
      maxUses: 'Max Users',
      currentUses: 'Current Users',
      userName: 'User Name',
      command: 'Command',
      commandNote: 'Note: Most recent commands are on the top',
      filterUser: 'Type to filter user',
      filterBy: 'Filter By User',
      patches: 'Patches',
      securityProducts: {
        type: 'Type',
        instance: 'Instance',
        displayName: 'Display Name',
        companyName: 'Company Name',
        version: 'Version',
        features: 'Features'
      }
    },
    hosts: {
      title: 'Hosts',
      search: 'Filter',
      button: {
        addMore: 'Add Filter',
        loadMore: 'Load More',
        exportCSV: 'Export to CSV',
        brokerExportCSV: 'Export to CSV is not supported for Endpoint Broker',
        export: 'Export Host details',
        exportTooltip: 'Exports all scan data categories for the host.',
        downloading: 'Downloading',
        initiateScan: 'Start Scan',
        cancelScan: 'Stop Scan',
        delete: 'Delete',
        cancel: 'Cancel',
        save: 'Save',
        saveAs: 'Save As...',
        clear: 'Clear',
        search: 'Search',
        ok: 'Ok',
        moreActions: 'More Actions',
        explore: 'Explore',
        gearIcon: 'Click here to manage columns',
        overview: 'Show/Hide Overview Panel',
        settings: 'Settings',
        meta: 'Show/Hide Meta',
        close: 'Close Host Details',
        shrink: 'Contract View',
        update: 'Update',
        reset: 'Reset',
        rightPanel: 'Show/Hide Detail Right Panel',
        resetRiskScore: 'Reset Risk Score'
      },
      autoruns: {
        services: {
          initd: 'INIT.D',
          systemd: 'SYSTEM.D'
        }
      },
      ranas: {
        ranas: 'Ran as',
        categories: {
          Process: 'Process',
          Libraries: 'Library',
          Autorun: 'Autorun',
          Service: 'Service',
          Task: 'Task',
          Driver: 'Driver',
          Thread: 'Thread',
          imageHooks: 'Image Hooks',
          kernelHooks: 'Kernel Hooks'
        }
      },
      explore: {
        input: {
          placeholder: 'Search by filename, path or hash'
        },
        noResultsFound: 'No Results Found.',
        fileName: 'Filename: ',
        path: 'Path: ',
        hash: 'Hash: ',
        search: {
          minimumtext: {
            required: 'For filename or path, enter minimum 3 characters. For hash, enter entire SHA-256 hash string'
          }
        },
        truncatedNote: 'Maximum of 100 results are displayed for a snapshot'
      },
      footerLabel: {
        autoruns: 'autoruns',
        services: 'services',
        tasks: 'tasks',
        imageHooks: 'image hooks',
        threads: 'suspicious threads',
        kernelHooks: 'kernel hooks',
        registryDiscrepancies: 'Registry Discrepancies',
        hostFiles: 'files',
        drivers: 'drivers',
        libraries: 'libraries',
        strings: 'Showing {{count}} of {{total}} strings'
      },
      summary: {
        snapshotTime: 'Snapshot Time',
        overview: {
          typeToFilterOptions: 'Enter the filter option',
          noSnapShots: 'No snapshots available'
        },
        body: {
          ipAddresses: 'IP Addresses ({{count}})',
          securityConfig: 'Security Configuration',
          loggedUsers: 'Logged-in Users ({{count}})',
          user: {
            administrator: 'Administrator',
            sessionId: 'Session ID',
            sessionType: 'Session Type',
            groups: 'Groups',
            host: 'Host',
            deviceName: 'Device Name'
          }
        },
        securityConfig: {
          arrangeBy: 'ARRANGE BY',
          alphabetical: 'Alphabetical',
          status: 'Status'
        }
      },
      downloadMFT: {
        success: 'MFT is set for download. Check the "Downloads" column for status.',
        tooltip: 'MFT can be downloaded only for Windows OS and only a single host can be selected at a time.'
      },
      selected: 'selected ({{count}})',
      insightAgentTooltip: 'For insights agent mode, Risk Score feature is not supported. Please upgrade to advanced agent mode.',
      list: {
        noResultsMessage: 'No Results Found.',
        errorOffline: 'An error has occurred. The Endpoint Server may be offline or inaccessible.'
      },
      rarIconTitle: 'Roaming Agent',
      filters: {
        systemFilter: 'This search is system defined and cannot be edited.',
        since: 'Since',
        customDateRange: 'Custom Date Range',
        customStartDate: 'Start Date',
        customEndDate: 'End Date',
        customDate: 'Custom Date',
        operator: 'Operator',
        searchPlaceHolder: 'Enter the filter option',
        mutlipleValuesNote: 'Note: To search multiple values, use || as a separator',
        invalidFilterInput: 'Invalid filter input',
        invalidFilterInputLength: 'Filter input longer than 256 characters',
        invalidIP: 'Please enter a valid IP address',
        invalidAgentID: 'Please enter a valid Agent ID',
        invalidAgentVersion: 'Please enter a valid Agent Version',
        invalidMacAddress: 'Please enter a valid MAC address',
        invalidOsDescription: 'Alphabet, numbers and .,-,() are allowed',
        invalidCountryCode: 'Can only contain alphabet and _ .',
        invalidCharacters: 'Can contain alphanumeric or special characters.',
        invalidCharsAlphabetOnly: 'Numbers and special characters are not allowed',
        invalidCharsAlphaNumericOnly: 'Special characters are not allowed',
        inTimeRange: 'In',
        notInTimeRange: 'Not In',
        startDateAfterEndDate: 'Start date needs to be before End date.',
        agentStatus: {
          lastSeenTime: 'Agent Not Seen Since'
        },
        showOnlyManageAgents: 'Show Only Managed Agents',
        showOnlyRARAgents: 'Show Only Roaming Agents',
        securityConfig: {
          allowAccessDataSourceDomain: 'Allow Access DataSource Domain',
          allowDisplayMixedContent: 'Allow Display Mixed Content',
          antiVirusDisabled: 'Anti-virus Disabled',
          'badCertificateWarningDisabled': 'Bad Certificate Warning Disabled',
          'cookiesCleanupDisabled': 'Cookies Cleanup Disabled',
          'crossSiteScriptFilterDisabled': 'Cross Site Script Filter Disabled',
          'firewallDisabled': 'Firewall Disabled',
          'fileVaultDisabled': 'File-Vault Disabled',
          'gatekeeperDisabled': 'Gatekeeper Disabled',
          'ieDepDisabled': 'IE-Dep Disabled',
          'ieEnhancedSecurityDisabled': 'IE Enhanced Security Disabled',
          'intranetZoneNotificationDisabled': 'Intranet Zone Notification Disabled',
          'kextSigningDisabled': 'Kext Signing Disabled',
          'luaDisabled': 'LUA Disabled',
          'windowsUpdateDisabled': 'Windows Update Disabled',
          'registryToolsDisabled': 'Registry Tools Disabled',
          'safariFraudWebsiteWarningDisabled': 'Safari Fraud Website Warning Disabled',
          'smartScreenFilterDisabled': 'Smart Screen Filter Disabled',
          'sudoersNoPasswordPrompt': 'Sudoers No Password Prompt',
          'systemIntegrityProtectionDisabled': 'System Integrity Protection Disabled',
          'systemRestoreDisabled': 'System Restore Disabled',
          'taskManagerDisabled': 'Task Manager Disabled',
          'uacDisabled': 'UAC Disabled',
          'warningPostRedirectionDisabled': 'Warning Post Redirection Disabled',
          'warningOnZoneCrossingDisabled': 'Warning On Zone Crossing Disabled'
        },
        agentMode: {
          insights: 'Insights',
          advanced: 'Advanced'
        }
      },
      restrictionTypeOptions: {
        EQUALS: 'Equals',
        CONTAINS: 'Contains',
        GT: '>',
        LT: '<',
        GTE: '>=',
        LTE: '<=',
        NOT_EQ: '!=',
        LESS_THAN: 'Less than',
        GREATER_THAN: 'Greater than',
        BETWEEN: 'Between',
        LAST_5_MINUTES: 'Last 5 Minutes',
        LAST_10_MINUTES: 'Last 10 Minutes',
        LAST_15_MINUTES: 'Last 15 Minutes',
        LAST_30_MINUTES: 'Last 30 Minutes',
        LAST_HOUR: 'Last 1 Hour',
        LAST_3_HOURS: 'Last 3 Hours',
        LAST_6_HOURS: 'Last 6 Hours',
        LAST_TWELVE_HOURS: 'Last 12 Hours',
        LAST_TWENTY_FOUR_HOURS: 'Last 24 Hours',
        LAST_FORTY_EIGHT_HOURS: 'Last 2 Days',
        LAST_5_DAYS: 'Last 5 Days',
        LAST_7_DAYS: 'Last 7 Days',
        LAST_14_DAYS: 'Last 14 Days',
        LAST_30_DAYS: 'Last 30 Days',
        LAST_HOUR_AGO: '1 Hour ago',
        LAST_TWENTY_FOUR_HOURS_AGO: '24 Hours ago',
        LAST_5_DAYS_AGO: '5 Days ago',
        ALL_TIME: 'All Data'
      },
      footer: '{{count}} of {{total}} hosts',
      column: {
        panelTitle: 'Hosts Preferences',
        triggerTip: 'Open/Hide Hosts Preferences',
        id: 'Agent ID',
        score: 'Risk Score',
        analysisData: {
          iocs: 'IOC Alerts'
        },
        agentStatus: {
          scanStatus: 'Agent Scan Status',
          lastSeenTime: 'Agent Last Seen'
        },
        groupPolicy: {
          groups: {
            name: 'Agent Groups'
          },
          policyStatus: 'Policy Status',
          serverName: 'Server'
        },
        machine: {
          id: 'Agent ID',
          agentVersion: 'Agent Version',
          scanStartTime: 'Last Scan Time',
          scanRequestTime: 'Scan Request Time',
          scanType: 'scanType',
          scanTrigger: 'Scan Trigger',
          securityConfigurations: 'Security Configurations',
          hostFileEntries: {
            ip: 'Host File IP',
            hosts: 'Host Entires'
          },
          users: {
            name: 'Username',
            sessionId: 'User Session ID',
            sessionType: 'User Session Type',
            isAdministrator: 'User Is Admin',
            groups: 'User Groups',
            domainUserQualifiedName: 'User Qualified Name',
            domainUserId: 'User Domain User ID',
            domainUserOu: 'User Domain User OU',
            domainUserCanonicalOu: 'User Domain User Canonical OU',
            host: 'User Host',
            deviceName: 'User DeviceName'
          },
          errors: {
            time: 'Error - Time',
            fileID: 'Error - FileID',
            line: 'Error - Line',
            number: 'Error - Number',
            value: 'Error - Value',
            param1: 'Error - Param1',
            param2: 'Error - Param2',
            param3: 'Error - Param3',
            info: 'Error - Info',
            level: 'Error - Level',
            type: 'Error - Type'
          },
          networkShares: {
            path: 'NetworkShare - Path',
            name: 'NetworkShare - Name',
            description: 'NetworkShare - Description',
            type: 'NetworkShare - Type',
            permissions: 'NetworkShare - Permissions',
            maxUses: 'NetworkShare - MaxUses',
            currentUses: 'NetworkShare - CurrentUses'
          },
          mountedPaths: {
            path: 'MountedPaths - Path',
            fileSystem: 'MountedPaths - FileSystem',
            options: 'MountedPaths - Options',
            remotePath: 'MountedPaths - RemotePath'
          },
          securityProducts: {
            type: 'Security Products - Type',
            instance: 'Security Products - Instance',
            displayName: 'Security Products - Display Name',
            companyName: 'Security Products - Company Name',
            version: 'Security Products - Version',
            features: 'Security Products - Features'
          }
        },
        riskScore: {
          moduleScore: 'Module Score',
          highestScoringModules: 'Highest Scoring Module'
        },
        machineIdentity: {
          machineName: 'Hostname',
          agentMode: 'Agent Mode',
          agentVersion: 'Agent Version',
          machineOsType: 'Operating System',
          networkInterfaces: {
            name: 'NIC Name',
            macAddress: 'NIC MAC Address',
            networkId: 'NetworkInterface - NerworkID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'NetworkInterface - Gateway',
            dns: 'NetworkInterface - DNS',
            promiscuous: 'NIC Promiscous'
          },
          agent: {
            exeCompileTime: 'Agent - User Mode Compile Time',
            sysCompileTime: 'Agent - Driver Compile Time',
            packageTime: 'Agent - Package Time',
            installTime: 'Agent - Install Time',
            serviceStartTime: 'Agent - Service Start Time',
            serviceProcessId: 'Agent - Service Process ID',
            serviceStatus: 'Agent - Service Status',
            driverStatus: 'Agent - Driver Status',
            blockingEnabled: 'Agent - Blocking Enabled',
            blockingUpdateTime: 'Agent - Blocking Update Time',
            driverErrorCode: 'Agent- Driver Error Code'
          },
          operatingSystem: {
            description: 'OS - Description',
            buildNumber: 'OS - Build Number',
            servicePack: 'OS - Service Pack',
            directory: 'OS - Directory',
            kernelId: 'OS - Kernel Id',
            kernelName: 'OS - Kernel Name',
            kernelRelease: 'OS - Kernel Release',
            kernelVersion: 'OS - Kernel Version',
            distribution: 'OS - Distribution',
            domainComputerId: 'OS - Domain ComputerID',
            domainComputerOu: 'OS - Domain Computer OU',
            domainComputerCanonicalOu: 'OS - Domain Computer Canonical OU',
            domainOrWorkgroup: 'OS - DomainOrWorkgroup',
            domainRole: 'OS - DomainRole',
            lastBootTime: 'OS - Last BootTime'
          },
          hardware: {
            processorArchitecture: 'Hardware - Processor Architecture',
            processorArchitectureBits: 'Hardware - Processor Architecture Bits',
            processorCount: 'Hardware - Processor Count',
            processorName: 'Hardware - Processor Name',
            totalPhysicalMemory: 'Hardware - Total Physical Memory',
            chassisType: 'Hardware - Chassis Type',
            manufacturer: 'Hardware - Manufacturer',
            model: 'Hardware - Model',
            serial: 'Hardware - Serial',
            bios: 'Hardware - Bios'
          },
          locale: {
            defaultLanguage: 'Locale - Default Language',
            isoCountryCode: 'Locale - Country Code',
            timeZone: 'Locale - Time Zone'
          },
          knownFolder: {
            appData: 'Folder - AppData',
            commonAdminTools: 'Folder - Common Admin Tools',
            commonAppData: 'Folder - Common App Data',
            commonDestop: 'Folder - Common Desktop',
            commonDocuments: 'Folder - Common Documents',
            commonProgramFiles: 'Folder - Common Program Files',
            commonProgramFilesX86: 'Folder - Common Program Files (x86)',
            commonPrograms: 'Folder - Common Programs',
            commonStartMenu: 'Folder - Common Start Menu',
            commonStartup: 'Folder - Common Startup',
            desktop: 'Folder - Desktop',
            localAppData: 'Folder - LocalAppData',
            myDocuments: 'Folder - My Documents',
            programFiles: 'Folder - Program Files',
            programFilesX86: 'Folder - Program Files (x86)',
            programs: 'Folder - Programs',
            startMenu: 'Folder - Start Menu',
            startup: 'Folder - Startup',
            system: 'Folder - System',
            systemX86: 'Folder - System (x86)',
            windows: 'Folder - Windows'
          }
        },
        markedForDeletion: 'Marked For Deletion'
      },

      properties: {
        title: 'Host Properties',
        filter: 'Type to filter list',
        checkbox: 'Show details with values only',
        groups: 'Groups',
        scheduleConfig: {
          enabled: 'Enabled',
          scanInterval: 'Recurrence',
          runAtTime: 'Start Time',
          scheduleStartDate: 'Start Date',
          scanOptions: {
            cpuMax: 'CPU Max',
            cpuMaxVm: 'CPU Max VM'
          }
        },
        machine: {
          securityConfigurations: 'Security Configurations',
          hostFileEntries: {
            title: 'Host File Entries',
            ip: 'Host File IP',
            hosts: 'Host Entires'
          },
          users: {
            title: 'User',
            name: 'Name',
            sessionId: 'Session ID',
            sessionType: 'Session Type',
            isAdministrator: 'Is Admin',
            administrator: 'Is Admin',
            groups: 'Groups',
            domainUserQualifiedName: 'QualifiedName',
            domainUserId: 'Domain User ID',
            domainUserOu: 'Domain User OU',
            domainUserCanonicalOu: 'Domain User Canonical OU',
            host: 'Host',
            deviceName: 'DeviceName'
          }
        },
        machineIdentity: {
          agent: {
            agentId: 'Agent ID',
            agentMode: 'Agent Mode',
            agentVersion: 'Agent Version',
            title: 'Agent',
            exeCompileTime: 'User Mode Compile Time',
            sysCompileTime: 'Driver Compile Time',
            packageTime: 'Package Time',
            installTime: 'Install Time',
            serviceStartTime: 'Service Start Time',
            serviceProcessId: 'Service Process ID',
            serviceStatus: 'Service Status',
            driverStatus: 'Driver Status',
            blockingEnabled: 'Blocking Enabled',
            blockingUpdateTime: 'Blocking Update Time',
            driverErrorCode: 'Driver Error Code'
          },
          networkInterfaces: {
            title: 'Network Interfaces',
            name: 'Name',
            macAddress: 'MAC Address',
            networkId: 'NerworkID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'Gateway',
            dns: 'DNS',
            promiscuous: 'Promiscous'
          },
          operatingSystem: {
            title: 'Operating System',
            description: 'Description',
            buildNumber: 'Build Number',
            servicePack: 'Service Pack',
            directory: 'Directory',
            kernelId: 'Kernel Id',
            kernelName: 'Kernel Name',
            kernelRelease: 'Kernel Release',
            kernelVersion: 'Kernel Version',
            distribution: 'Distribution',
            domainComputerId: 'Domain ComputerID',
            domainComputerOu: 'Domain Computer OU',
            domainComputerCanonicalOu: 'Domain Computer Canonical OU',
            domainOrWorkgroup: 'DomainOrWorkgroup',
            domainRole: 'DomainRole',
            lastBootTime: 'Last BootTime'
          },
          hardware: {
            title: 'Hardware',
            processorArchitecture: 'Processor Architecture',
            processorArchitectureBits: 'Processor Architecture Bits',
            processorCount: 'Processor Count',
            processorName: 'Processor Name',
            totalPhysicalMemory: 'Total Physical Memory',
            chassisType: 'ChassisType',
            manufacturer: 'Manufacturer',
            model: 'Model',
            serial: 'Serial',
            bios: 'Bios'
          },
          locale: {
            title: 'Locale',
            defaultLanguage: 'Default Language',
            isoCountryCode: 'Country Code',
            timeZone: 'Time Zone'
          }
        },
        message: {
          policyReadPermission: 'Permission required to view policy'
        }
      },
      propertyPanelTitles: {
        autoruns: {
          autorun: 'Autorun Properties',
          services: 'Service Properties',
          tasks: 'Task Properties'
        },
        anomalies: {
          hooks: 'Image Hook Properties',
          threads: 'Suspicious Thread Properties',
          kernelHooks: 'Kernel Hook Properties'
        },
        files: 'File Properties',
        drivers: 'Driver Properties',
        libraries: 'Library Properties'
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
      },
      customFilter: {
        save: {
          description: 'Provide a name to the search. This name will appear in the search list.',
          name: 'Name *',
          errorHeader: 'Unable to save search',
          header: 'Save search',
          errorMessage: 'The search cannot be saved. ',
          emptyMessage: 'Name field is empty.',
          nameExistsMessage: 'A saved search with the same name.',
          success: 'Filter saved successfully.',
          filterFieldEmptyMessage: 'One or more of the newly added filter fields are empty. Please add the filters or remove the fields to save.',
          invalidInput: 'Name can contain alphanumeric, underscore, or hyphen.'
        },
        update: {
          success: 'Filter updated successfully.'
        },
        error: 'A problem occurred while trying to create/update filter.'
      },
      initiateScan: {
        modal: {
          title: 'Start Scan for {{count}} host(s)',
          modalTitle: 'Start Scan for {{name}}',
          description: 'Select the type of scan for the selected host(s).',
          error1: '*Select at least one host',
          error2: 'Maximum 100 hosts allowed to start scan',
          infoMessage: 'Some of the selected hosts are already being scanned and a fresh scan will not be started for them.',
          ecatAgentMessage: 'Some of the selected hosts are 4.4 agents, this feature is not supported for them.',
          migratedHostMessage: 'Some of the selected hosts are not managed by the current server, so a scan will not start for them.',
          quickScan: {
            label: 'Quick Scan (Default)',
            description: 'Performs a quick scan of all executable files loaded in memory. Takes approximately 10 minutes.'
          }
        },
        success: 'Scan initiated successfully',
        error: 'Scan initiate failed',
        failure: 'Scan failed. Contact your system administrator.'
      },
      cancelScan: {
        modal: {
          title: 'Stop Scan for {{count}} host(s)',
          description: 'Are you sure you want to stop the scan?',
          error1: '*Select at least one host'
        },
        success: 'Stop scan initiated successfully',
        error: 'Stop scan initiate failed'
      },
      deleteHosts: {
        modal: {
          title: 'Delete {{count}} hosts',
          message: 'Delete the host if the host scan data is no longer required or the agent is uninstalled. ' +
            'All scan data associated with the host will be deleted. Do you want to continue? '
        },
        success: 'Hosts deleted successfully',
        failure: 'Host is already deleted.',
        error: 'Host(s) deletion failed'
      },
      resetHosts: {
        modal: {
          title: 'Reset Risk Score',
          message: 'Are you sure you want to reset the Risk Score for the selected host(s). All alerts related to this risk score will be deleted and the Risk Score will be reset to 0.'
        },
        success: 'The Risk Score has been successfully reset.',
        error: ' The Risk Score cannot be reset.',
        warning: 'Risk score of some selected hosts has been reset successfully.',
        limitInfo: 'Risk Score of only top 100 hosts can be reset at a time.',
        cancel: 'Cancel',
        reset: 'Reset'
      },
      moreActions: {
        openIn: 'Pivot to Endpoint Thick Client',
        openInErrorMessage: 'Select at least one host',
        notAnEcatAgent: 'Select only the 4.4 agent(s)',
        cancelScan: 'Stop Scan'
      }
    },
    savedQueries: {
      headerContent: 'Select a saved query from the list to run it.You can also edit the name of the saved query by clicking the pencil icon next to the name,and set it as default by clicking the star icon.',
      deleteBtn: 'Delete selected',
      runBtn: 'Run selected',
      yesBtn: 'Yes',
      noBtn: 'No',
      delete: {
        successMessage: 'Query deleted successfully.',
        confirmMessage: 'Are you sure you want to delete the selected query?'
      },
      edit: {
        successMessage: 'Query name updated successfully',
        errorMessage: 'Query name updation failed',
        nameExistsMessage: 'Query name already exists'
      }
    },
    files: {
      footer: 'Showing {{count}} out of {{total}} {{label}}',
      footerSelected: '| {{selectedItems}} selected',
      filter: {
        filters: 'Saved Filters',
        newFilter: 'New Filter',
        windows: 'WINDOWS',
        mac: 'MAC',
        linux: 'LINUX',
        favouriteFilters: 'Favorite Filters',
        restrictionType: {
          moreThan: 'Greater than',
          lessThan: 'Less than',
          between: 'Between',
          equals: 'Equals',
          contains: 'Contains'
        },
        save: 'Save',
        reset: 'Reset',
        customFilters: {
          save: {
            description: 'Provide a name to the search. This name will appear in the search list.',
            name: 'Name *',
            errorHeader: 'Unable to save search',
            header: 'Save search',
            errorMessage: 'The search cannot be saved. ',
            emptyMessage: 'Name field is empty.',
            nameExistsMessage: 'A saved search with the same name.',
            success: 'Filter saved successfully.',
            filterFieldEmptyMessage: 'Filter fields are empty',
            invalidInput: 'Name can contain alphanumeric, underscore, or hyphen.'
          }
        },
        button: {
          cancel: 'Cancel',
          save: 'Save'
        }
      },
      fields: {
        id: 'ID',
        firstSeenTime: 'First Seen Time',
        companyName: 'Company Name',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Operating System',
        elf: {
          classType: 'ELF.Class Type',
          data: 'ELF.Data',
          entryPoint: 'ELF.Entry Point',
          features: 'ELF.Features',
          type: 'ELF.Type',
          sectionNames: 'ELF.Section Names',
          importedLibraries: 'ELF.Imported Libraries'
        },
        pe: {
          timeStamp: 'PE.Timestamp',
          imageSize: 'PE.Image Size',
          numberOfExportedFunctions: 'PE.Exported Functions',
          numberOfNamesExported: 'PE.Exported Names',
          numberOfExecuteWriteSections: 'PE.Execute Write Sections',
          features: 'PE.Features',
          sectionNames: 'PE.Section Names',
          importedLibraries: 'PE.Imported Libraries',
          resources: {
            originalFileName: 'PE.Resources.Filename',
            company: 'PE.Resources.Company',
            description: 'PE.Resources.Description',
            version: 'PE.Resources.Version'
          }
        },
        macho: {
          uuid: 'MachO.Uuid',
          identifier: 'MachO.Identifier',
          minOsxVersion: 'MachO.Osx Version',
          features: 'MachO.Features',
          flags: 'MachO.Flags',
          numberOfLoadCommands: 'MachO.Loaded Commands',
          version: 'MachO.Version',
          sectionNames: 'MachO.Section Names',
          importedLibraries: 'MachO.Imported Libraries'
        },
        signature: {
          timeStamp: 'Signature.Timestamp',
          thumbprint: 'Signature.Thumbprint',
          features: 'Signature',
          signer: 'Signer'
        },
        owner: {
          userName: 'Owner',
          groupName: 'Owner Group'
        },
        rpm: {
          packageName: 'Package'
        },
        path: 'Path',
        entropy: 'Entropy',
        fileName: 'FileName',
        firstFileName: 'FileName',
        timeCreated: 'Created',
        format: 'Format',
        sectionNames: 'Section Names',
        importedLibraries: 'Imported Libraries',
        size: 'Size'
      }
    },
    pivotToInvestigate: {
      title: 'Select Service',
      buttonText: 'Navigate',
      buttonText2: 'Event Analysis',
      iconTitle: 'Analyze Events'
    },
    flash: {
      fileDownloadRequestSent: 'Files are set for download. Check the "Downloaded" column for status.'
    }
  },
  investigateShared: {
    eventMetaTitle: 'Event Meta',
    machineCount: {
      singular: '{{count}} host',
      plural: '{{count}} hosts'
    },
    endpoint: {
      message: {
        brokerViewMessage: 'Reached maximum results supported in an endpoint broker view.'
      },
      renderingGraph: 'Rendering graph...',
      errorTitle: 'Query Error',
      fileDetailsPanel: {
        statusHistory: 'Status History ({{count}})',
        properties: 'Properties',
        comment: 'COMMENTS'
      },
      riskProperties: {
        alerts: {
          heading: 'Alerts Severity',
          events: '{{count}} event(s)',
          critical: 'CRITICAL',
          high: 'HIGH',
          medium: 'MEDIUM',
          low: 'LOW',
          all: 'ALL',
          files: '{{count}} file(s)',
          users: '{{count}} user(s)'
        },
        error: {
          'mongo.connection.failed': 'Database is not reachable. Retry after sometime.',
          emptyContext: 'Risk Score is 0. Reasons could be whitelisting/reset of the file or no alerts.',
          noEvents: 'No Events are available for this file.',
          insightAgentRiskError: 'Risk score details are not available for agents in insight mode. Please upgrade to advanced mode.<br> For more information about this host, see other tabs.</br>',
          accessDeniedError: 'Require permissions to view risk score details. Contact the Administrator.'
        },
        errorPage: {
          serviceDown: 'Respond Server is offline',
          serviceDownDescription: 'The Respond Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
        },
        infoMessage: '  Events rolled over due to retention policies or manual deletion will not be available.'
      },
      riskPanel: {
        timeUnit: {
          allData: 'ALL DATA',
          HOUR: 'HOUR',
          HOURS: 'HOURS',
          MINUTE: 'MINUTE',
          MINUTES: 'MINUTES',
          DAY: 'DAY',
          DAYS: 'DAYS',
          MONTH: 'MONTH',
          MONTHS: 'MONTHS',
          YEAR: 'YEAR',
          YEARS: 'YEARS',
          WEEK: 'WEEK',
          WEEKS: 'WEEKS'
        },
        header: {
          lastUpdated: 'Last Updated:',
          timeWindow: 'Time Window: '
        },
        footer: {
          title: {
            ALERT: 'Alert(s)',
            INCIDENT: 'Incident(s)'
          },
          resultCount: '(First {{count}} Results)'
        }
      },
      hostActions: {
        startScan: 'Start Scan',
        stopScan: 'Stop Scan',
        delete: 'Delete'
      },
      fileActions: {
        editFileStatus: 'Change File Status',
        pivotToInvestigate: 'Analyze Events',
        pivotToInvestigateDisableToolTip: 'Select a single {{label}} to analyze.',
        watch: 'Watch',
        downloadToServer: 'Download File to Server',
        more: 'More',
        moreActions: 'More',
        saveLocalCopy: 'Save a Local Copy',
        analyzeFile: 'Analyze File',
        virusTotalLookup: 'VirusTotal Lookup',
        googleLookup: 'Google Lookup',
        fileName: 'File Name',
        md5: 'MD5',
        sha1: 'SHA1',
        sha256: 'SHA256',
        resetRiskScore: 'Reset Risk Score',
        networkEvents: 'Network Events',
        fileEvents: 'File Events',
        processEvents: 'Process Events',
        registryEvents: 'Registry Events',
        consoleEvents: 'Console Events',
        viewCertificate: 'View Certificates',
        analyzeUser: 'Analyze User',
        downloadMFT: 'Request MFT download',
        tooltips: {
          downloadToServer: '',
          saveLocalCopy: 'Download the file to server to save a local copy.',
          analyzeFile: 'Download the file to server to analyze.',
          changeFileStatusDisabled: 'Select {{label}} to change the status',
          moreActionDisabled: 'Select {{label}} to enable More actions',
          files: {
            downloadToServer: 'One file can be downloaded at a time.'
          }
        }
      },
      savedFilters: 'Saved Filters',
      filter: {
        select: 'Select',
        noSavedFilters: 'No saved filters'
      },
      fileAnalysis: {
        title: 'File Analysis',
        stringsView: 'Strings View',
        textView: 'Text View',
        checksumMd5: 'checksumMd5',
        checksumSha1: 'checksumSha1',
        checksumSha256: 'checksumSha256',
        size: 'Size',
        downloadedFileName: 'Downloaded FileName',
        downloadedPath: 'Downloaded Path',
        entropy: 'Entropy',
        format: 'Format',
        searchPlaceHolder: 'Enter Value',
        filter: 'Filter Strings',
        pe: {
          architecture: 'Architecture',
          characteristics: 'Characteristics',
          compileTime: 'Compile Time',
          entryPoint: 'Entry Point',
          importedDlls: 'Imported DLLs',
          sectionNames: 'Section Names {{count}}',
          subSystem: 'Subsystem',
          entryPointValid: 'Entry Point Valid',
          uncommonSectionFound: 'Uncommon Section Found',
          packerSectionFound: 'Packer Section Found'
        },
        elf: {
          neededLibraries: 'Needed Libraries',
          architecture: 'Architecture',
          entryPoint: 'Entry Point',
          sectionNames: 'Section Names {{count}}',
          entryPointValid: 'Entry Point Valid',
          uncommonSectionFound: 'Uncommon Section Found',
          packerSectionFound: 'Packer Section Found',
          fileType: 'File Type'
        },
        macho: {
          architecture: 'Architecture',
          entryPoint: 'Entry Point',
          importedDlls: 'Imported DyLib',
          segmentNames: 'Segment Names {{count}}',
          subSystem: 'Subsystem',
          entryPointValid: 'Entry Point Valid',
          uncommonSectionFound: 'Uncommon Section Found',
          packerSectionFound: 'Packer Section Found',
          uuid: 'UUID',
          fileType: 'File Type'
        },
        importedDlls: {
          parentTitle: 'Imported DLLs ({{dllCount}}) and Functions ({{functionCount}})',
          childTitle: '{{importedDllName}} - Functions ({{functionCount}})'
        },
        importedDyLib: {
          parentTitle: 'Imported Dylibs ({{dllCount}}) and Functions ({{functionCount}})',
          childTitle: '{{importedDllName}} - Functions ({{functionCount}})'
        }
      }
    }
  },
  investigateProcessAnalysis: {
    nodeList: {
      processName: 'Process Name',
      riskScore: 'Risk Score',
      hostCount: 'Active On',
      launchArguments: 'Launch Arguments'
    },
    property: {
      title: 'File Properties',
      file: {
        features: 'Features',
        fileId: 'FileId',
        id: 'ID',
        serviceName: 'ServiceName',
        state: 'State',
        description: 'Description',
        type: 'Type',
        displayName: 'Display Name',
        win32ErrorCode: 'win32ErrorCode',
        creatorUser: 'Creator User',
        executeUser: 'Execute User',
        companyName: 'Company Name',
        registryPath: 'Registry Path',
        launchArguments: 'Launch Arguments',
        triggerString: 'Trigger String',
        user: 'User',
        name: 'Name',
        status: 'Status',
        checksumMd5: 'MD5',
        checksumSha1: 'SHA1',
        checksumSha256: 'SHA256',
        machineOsType: 'Operating System',
        timeCreated: 'Created',
        timeModified: 'Modified',
        timeAccessed: 'Accessed',
        createTime: 'Process Created',
        pid: 'PID',
        eprocess: 'EPROCESS',
        path: 'Full Path',
        sameDirectoryFileCounts: {
          nonExe: '# Non-Executables',
          exe: '# Executables',
          subFolder: '# Folder',
          exeSameCompany: '#Same company Executables'
        },
        elf: {
          classType: 'Class Type',
          data: 'Data',
          entryPoint: 'Entry Point',
          features: 'Features',
          type: 'Type',
          sectionNames: 'Section Names',
          importedLibraries: 'Imported Libraries'
        },
        pe: {
          timeStamp: 'Timestamp',
          imageSize: 'Image Size',
          numberOfExportedFunctions: 'Exported Functions',
          numberOfNamesExported: 'Exported Names',
          numberOfExecuteWriteSections: 'Execute Write Sections',
          features: 'Features',
          sectionNames: 'Section Names',
          importedLibraries: 'Imported Libraries',
          resources: {
            originalFileName: 'Filename',
            company: 'Company',
            description: 'Description',
            version: 'Version'
          }
        },
        macho: {
          uuid: 'Uuid',
          identifier: 'Identifier',
          minOsxVersion: 'Osx Version',
          features: 'Features',
          flags: 'Flags',
          numberOfLoadCommands: 'Loaded Commands',
          version: 'Version',
          sectionNames: 'Section Names',
          importedLibraries: 'Imported Libraries'
        },
        signature: {
          timeStamp: 'Timestamp',
          thumbprint: 'Thumbprint',
          features: 'Features',
          signer: 'Signer'
        },
        process: {
          title: 'Process',
          processName: 'Process Name',
          eprocess: 'EPROCESS',
          integrityLevel: 'Integrity',
          parentPath: 'Parent Path',
          threadCount: 'Thread Count',
          owner: 'Owner',
          sessionId: 'Session ID',
          createUtcTime: 'Created',
          imageBase: 'Image Base',
          imageSize: 'Image Size'
        },
        entropy: 'Entropy',
        firstFileName: 'FileName',
        fileName: 'FileName',
        format: 'Format',
        sectionNames: 'Section Names',
        importedLibraries: 'Imported Libraries',
        size: 'Size',
        imageBase: 'Image Base',
        imageSize: 'Image Size',
        loaded: 'Loaded',
        fileProperties: {
          entropy: 'Entropy',
          size: 'Size',
          format: 'Format'
        }
      }
    },
    processExecutionDetails: {
      title: 'Process Execution Details',
      userAll: 'User Name',
      eventTime: 'Event Time',
      directoryDst: 'File Location',
      checksum: 'Checksum',
      paramDst: 'Launch Arguments',
      sessionId: 'Session Id',
      processName: 'Process Name'
    },
    buttons: {
      expandDetails: 'Process Details Expand/Collapse',
      close: 'Process Details Close',
      viewAll: 'View All',
      viewSelected: 'View Selected',
      cancel: 'Cancel',
      processSelected: '{{processCount}} Process selected'
    },
    tabs: {
      properties: 'Properties',
      events: 'Events List ({{count}})',
      all: 'All',
      alerts: 'Alerts ({{count}})',
      hosts: 'Hosts ({{count}})'
    },
    eventsTable: {
      header: 'Events'
    },
    filter: {
      title: 'Filters',
      filterReset: 'Reset Filter'
    },
    error: 'No relationship views for the process {{processName}} as there are no "create process" events. Try changing the time range or refer to the Events tab to view timeline of all events for the process'
  },
  // Begin context menu
  contextmenu: {
    actions: {
      applyDrillInNewTabLabel: 'Apply Drill in New Tab',
      applyNotEqualsInNewTabLabel: 'Apply !EQUALS Drill in New Tab',
      applyNotEqualsDrillLabel: 'Apply !EQUALS Drill',
      getOpenInNewTab: 'Open in New Tab',
      visualizeGeoMapLabel: 'Geo-map Locations in New Tab',
      getLookupInLive: 'Live Lookup',
      changeSelectedToOpenLabel: 'Change Selected to Open',
      changeSelectedToClosedLabel: 'Change Selected to Closed',
      changeSelectedToAutoLabel: 'Change Selected to Auto',
      changeSelectedToHiddenLabel: 'Change Selected to Hidden',
      refocusInvestigationInNewTabLabel: 'Refocus Investigation in New Tab',
      scan: 'Scan for Malware',
      getHashLookup: 'Hash Lookup',
      getECATIoc: 'Endpoint Thick Client Lookup',
      getGoogle: 'Google',
      getRobtex: 'Robtex',
      sansIPHistoryLabel: 'SANS IP History',
      googleMalwareDiagnosticsLabel: 'Google Malware Diagnostic for IPs and Hostnames',
      mcAfeeHostnameSiteAdvisorLabel: 'McAfee SiteAdvisor for Hostnames',
      getBFKPassiveDNSCollection: 'BFK Passive DNS Collection',
      getCentralOpsWhoisForIPsAndHostnames: 'CentralOps Whois for IPs and Hostnames',
      getMalwaredomainlistSearch: 'Malwaredomainlist.com Search',
      getRobtexIPSearch: 'Robtex IP Search',
      getThreatExpertSearch: 'ThreatExpert Search',
      getUrlVoidSearch: 'UrlVoid Search',
      viewInSuspiciousDomainReport: 'Suspicious Domain Report',
      viewInHostProfileReport: 'Host Profile Report',
      viewInVPNSessionReport: 'Suspicious VPN Session Report',
      contextServiceDefaultAction: 'Context Lookup',
      eventReconTitle: 'Event Reconstruction',
      viewInSuspiciousDNSActivityReport: 'Suspicious DNS Activity Report',
      getAddToListLabel: 'Add/Remove from List(s)',
      getCopy: 'Copy',
      viewDetailsNewRecon: 'Event Analysis',
      applyContainsDrillLabel: 'Apply Contains Drill',
      applyContainsInNewTabLabel: 'Apply Contains Drill in New Tab',
      applyRefocusLabel: 'Apply EQUALS',
      applyRefocusNotEqualsLabel: 'Apply !EQUALS',
      applyRefocusContainsLabel: 'Apply Contains',
      applyRefocusSessionSplitsLabel: 'Find Session Fragments',
      applyRefocusInNewTabLabel: 'Refocus EQUALS Drill in New Tab',
      applyRefocusNotEqualsInNewTabLabel: 'Refocus !EQUALS Drill in New Tab',
      applyRefocusContainsInNewTabLabel: 'Refocus Contains Drill in New Tab',
      applyRefocusSessionSplitsInNewTabLabel: 'Find Session Fragments in New Tab',
      getHostsLookup: 'Hosts Lookup'
    },
    groups: {
      investigationGroup: 'Investigation',
      externalLookupGroup: 'External Lookup',
      contextGroupDataScience: 'Data Science',
      refocusGroup: 'Refocus',
      refocusNewTabGroup: 'Refocus New Tab'
    }
  },
  // begin admin
  admin: {
    title: 'Admin',
    navbar: {
      appliances: 'Hosts',
      services: 'Services',
      eventSources: 'Event Sources',
      unifiedSources: 'Endpoint Sources', // USM
      healthWellness: 'Health & Wellness',
      system: 'System',
      security: 'Security'
    }
  },
  // end admin
  // begin admin-source-management (a.k.a USM)
  adminUsm: {
    title: 'Endpoint Sources', // USM
    button: {
      addNew: 'Add New',
      createNew: 'Create New',
      delete: 'Delete',
      edit: 'Edit',
      publish: 'Publish',
      applyGroups: 'Apply To Groups',
      save: 'Save and Publish',
      saveAndPublish: 'Save and Publish',
      cancel: 'Cancel',
      editRanking: 'Edit Ranking'
    },
    groups: {
      title: 'Groups',
      list: {
        select: 'Select',
        name: 'Group Name',
        publishStatus: 'Publication Status',
        assignedPolicies: 'Policy(ies) Applied',
        sourceType: 'Source Types Applied',
        description: 'Group Description',
        sourceCount: 'Source Count',
        lastUpdated: 'Last Updated',
        noResultsMessage: 'No Groups found.',
        sourceCountUnpublishedEditedGroupTooltip: 'Count represents group as previously published.',
        sourceCountPublishedNoEndpointTooltip: 'No Endpoint server found in the environment.',
        sourceCountPublishedNewGroupTooltip: 'No count available yet.',
        sourceCountUnpublishedNewGroupTooltip: 'No count available for unpublished groups.',
        noAssignedPolicies: 'N/A'
      },
      filter: {
        sourceType: 'Source Type',
        groupType: 'Group Type',
        appliedPolices: 'Applied Policies'
      },
      detail: {
        definition: 'Definition',
        sourceCount: 'Source Count',
        andOrConditionsPart1: 'Sources included if',
        andOrConditionsPart2: 'of the following conditions are met:'
      },
      modals: {
        deleteGroups: {
          title: 'Delete Group(s)',
          confirm: 'You are about to permanently delete {{numItems}} group(s). Deletion will take immediate effect. If any policies are linked to these groups the sources in these groups will no longer apply these policies.',
          ok: 'Delete Group(s)',
          success: 'Successfully deleted selected Group(s)',
          failure: 'There was an unexpected problem deleting the Group(s)'
        },
        publishGroups: {
          title: 'Publish Group(s)',
          confirm: 'You are about to publish the {{numItems}} selected Unpublished Group(s)?',
          ok: 'Publish Group(s)',
          success: 'Successfully published selected Group(s)',
          failure: 'There was an unexpected problem publishing the Group(s)'
        }
      },
      tooltips: {
        edit: 'Select a group to edit it, including applying policies. Only one group can be edited at a time.',
        editRanking: 'Edit ranking to change how policies are applied when a source falls into multiple groups.',
        delete: 'Select one or more groups to delete. If a deleted group applied a policy, the policy will no longer be applied to the sources that were in this group.',
        publish: 'Select one or more unpublished groups to publish. Sources will receive changes when they next connect.'
      }
    },
    policies: {
      title: 'Policies',
      list: {
        select: 'Select',
        name: 'Policy Name',
        publishStatus: 'Publication Status',
        sourceType: 'Source Type',
        associatedGroups: 'Applied to Group(s)',
        description: 'Policy Description',
        noResultsMessage: 'No Policies found.',
        defaultPolicyAssociatedGroup: 'Base Policy',
        noAssociatedGroups: 'None'
      },
      filter: {
        sourceType: 'Source Type',
        appliedGroups: 'Applied To Group(s)',
        policyParameters: 'Policy Parameters'
      },
      detail: {
        history: 'History',
        createdOn: 'Created On',
        createdBy: 'Created By',
        lastUpdatedOn: 'Last Updated On',
        lastUpdatedBy: 'Last Updated By',
        lastPublishedOn: 'Last Published On',
        windowsLogSettings: 'Windows Log Settings',
        channelFilterSettings: 'Channel Filter Settings',
        windowsLogPolicyEnabled: 'Status',
        primaryDestination: 'Primary Destination',
        secondaryDestination: 'Secondary Destination',
        defaultPrimaryAddress: 'As Per Packager',
        defaultEdrPolicy: 'Default EDR Policy',
        none: 'None',
        defaultPrimaryAddressTooltip: 'The agent will communicate to the endpoint server defined in the agent packager.',
        protocol: 'Protocol',
        sendTestLog: 'Send Test Log',
        eventId: 'Event Id',
        channel: 'Channel',
        filterType: 'Filter Type',
        systemCreated: 'System Created',
        recurrenceEvery: 'Every',
        enabled: 'Enabled',
        disabled: 'Disabled',
        fileSettings: 'File Settings',
        filePolicyEnabled: 'Status',
        sourceSettings: 'Source Settings ({{fileType}})'
      },
      modals: {
        deletePolicies: {
          title: 'Delete Policy(ies)',
          confirm: 'You are about to permanently delete {{numItems}} policy(ies). Deletion will take immediate effect. Any groups currently applying these policies will no longer do so. Default policies are automatically excluded from deletion.',
          ok: 'Delete Policy(ies)',
          success: 'Successfully deleted selected Policy(ies)',
          failure: 'There was an unexpected problem deleting selected Policy(ies)'
        },
        publishPolicies: {
          title: 'Publish Policy(ies)',
          confirm: 'You are about to publish the {{numItems}} selected Unpublished Policy(ies)?',
          ok: 'Publish Policy(ies)',
          success: 'Successfully published selected Policy(ies)',
          failure: 'There was an unexpected problem publishing the Policy(ies)'
        }
      },
      tooltips: {
        edit: 'Select a policy to edit it. Only one policy can be edited at a time.',
        delete: 'Select one or more policies to delete.',
        publish: 'Select one or more unpublished policies to publish. Sources will receive changes when they next connect.'
      }
    },
    sources: {
      title: 'Sources',
      list: {
        select: 'Select',
        name: 'Source Name',
        publishStatus: 'Publication Status',
        sourceType: 'Source Type',
        associatedGroups: 'Group(s)',
        description: 'Source Description',
        noResultsMessage: 'No Sources found.',
        defaultPolicyAssociatedGroup: 'Base Policy',
        noAssociatedGroups: 'None'
      },
      filter: {
        sourceType: 'Source Type',
        appliedGroups: 'Applied To Group(s)',
        policyParameters: 'Policy Parameters'
      },
      detail: {
        history: 'History',
        createdOn: 'Created On',
        createdBy: 'Created By',
        lastUpdatedOn: 'Last Updated On',
        lastUpdatedBy: 'Last Updated By',
        lastPublishedOn: 'Last Published On',
        windowsLogSettings: 'Windows Log Settings',
        channelFilterSettings: 'Channel Filter Settings',
        windowsLogPolicyEnabled: 'Status',
        primaryDestination: 'Primary Destination',
        secondaryDestination: 'Secondary Destination',
        defaultPrimaryAddress: 'As Per Packager',
        defaultEdrPolicy: 'Default EDR Policy',
        none: 'None',
        defaultPrimaryAddressTooltip: 'The agent will communicate to the endpoint server defined in the agent packager.',
        protocol: 'Protocol',
        sendTestLog: 'Send Test Log',
        eventId: 'Event Id',
        channel: 'Channel',
        filterType: 'Filter Type',
        systemCreated: 'System Created',
        recurrenceEvery: 'Every',
        enabled: 'Enabled',
        disabled: 'Disabled'
      },
      modals: {
        deletePolicies: {
          title: 'Delete Policy(ies)',
          confirm: 'You are about to permanently delete {{numItems}} policy(ies). Deletion will take immediate effect. Any groups currently applying these policies will no longer do so. Default policies are automatically excluded from deletion.',
          ok: 'Delete Policy(ies)',
          success: 'Successfully deleted selected Policy(ies)',
          failure: 'There was an unexpected problem deleting selected Policy(ies)'
        },
        publishPolicies: {
          title: 'Publish Policy(ies)',
          confirm: 'You are about to publish the {{numItems}} selected Unpublished Policy(ies)?',
          ok: 'Publish Policy(ies)',
          success: 'Successfully published selected Policy(ies)',
          failure: 'There was an unexpected problem publishing the Policy(ies)'
        }
      },
      tooltips: {
        edit: 'Select a policy to edit it. Only one policy can be edited at a time.',
        delete: 'Select one or more policies to delete.',
        publish: 'Select one or more unpublished policies to publish. Sources will receive changes when they next connect.'
      }
    },
    sourceTypes: {
      edrSource: 'Endpoint',
      windowsLogSource: 'Log Collection'
    },
    groupWizard: {
      // identify-group-step
      identifyGroup: 'Identify Group',
      sourceType: 'Source Type',
      sourceTypePlaceholder: 'Choose a Source Type',
      edrSourceType: 'Agent Endpoint',
      fileLogSourceType: 'Agent Log Files',
      windowsLogSourceType: 'Agent Windows Logs',
      name: 'Group Name',
      namePlaceholder: 'Enter a unique group name',
      nameTooltip: 'Group name must be unique and 256 characters or less.',
      nameRequired: 'Group name is required',
      nameExists: 'Group name already exists',
      nameExceedsMaxLength: 'Group name is limited to 256 characters',
      description: 'Group Description',
      descPlaceholder: 'Enter a description',
      descTooltip: 'Use the optional group description to add more detail about the group. The description can not exceed 8000 characters.',
      descriptionExceedsMaxLength: 'Group description is limited to 8000 characters',
      // define-group-step
      defineGroup: 'Define Group',
      // apply-policy-step
      applyPolicy: {
        stepTitle: 'Apply Policy(ies)',
        helpTitle: {
          label1: 'A group does not need to apply a policy, however, for a policy to be active, it must be applied to a group.',
          label2: 'If this step is skipped, policies can still be applied to this group later.',
          label3: 'Skip this step if this group will not apply a policy or if the policy you wish to apply has not been created yet.',
          label4: 'New policies can be created in the the Policies Tab.'
        },
        preview: 'Simulated Source Settings',
        setting: 'SETTING',
        governingPolicy: 'GOVERNING POLICY - GROUP',
        sourceType: 'Source Type',
        sourceTypeTooltip: 'Choose a source type to apply policies for.',
        selectedPolicy: 'Selected Policy',
        policyPlaceholder: 'Select a Policy',
        addSourceType: 'Add Another Source Type',
        policyStatus: 'Policy Status',
        policyName: 'POLICY NAME',
        appliedToGroup: 'APPLIED TO GROUP(S)',
        description: 'POLICY DESCRIPTION',
        available: 'Available Policies',
        availableTooltip: 'Available Policies shows all the published policies for the selected source type. If you do not see a ' +
          'policy you are expecting to see, you may need to return to the Policies tab and publish it. Click on the + icon to select the policy.',
        selected: 'Selected Policy',
        selectedTooltip: 'The Selected Policy will be applied by this group. A group can only apply one policy per source type. ' +
          'If sources in this group fall into multiple groups, this policy may not be applied to those sources depending on the group ranking. ' +
          'Use Edit Ranking on the Groups tab to ensure this policy will be applied as intended.'
      },
      // review-group-step
      reviewGroup: 'Review',
      // common titlebar
      newGroup: 'New Group',
      groupRanking: 'RANKING GROUPS',
      // common toolbar
      previous: 'Previous',
      next: 'Next',
      publishGroup: 'Publish Now',
      publishRanking: 'Publish Ranking',
      resetRanking: 'Reset Ranking',
      setTopRanking: 'Set Top Ranking',
      saveAndClose: 'Save and Close',
      cancel: 'Cancel',
      addCondition: 'Add Condition',
      maxTenCriteria: 'This group contains the maximum number of conditions',
      modals: {
        discardChanges: {
          title: 'Discard Changes',
          confirm: 'Discard the unsaved changes?',
          discardChanges: 'Discard Changes',
          continueEditing: 'Continue Editing'
        }
      },
      errorPage: {
        loadErrorTitle: 'Group could not be loaded',
        loadErrorDescription: 'There was a problem loading the requested group. The server may be offline or inaccessible, or the group may not exist.'
      },
      actionMessages: {
        prevFailure: 'Please fix all the validation errors before navigating back to the previous step.',
        prevEmptyFailure: 'At least one condition is required before navigating back to the previous step.',
        nextFailure: 'Please fix all the validation errors before continuing to next step.',
        nextEmptyFailure: 'At least one condition is required before continuing to next step.',
        saveValidationFailure: 'Please fix all the validation errors before Saving the Group.',
        saveEmptyFailure: 'At least one condition is required before Saving the Group.',
        saveNoChangeFailure: 'No changes detected from previous saved version of the Group.',
        saveFailure: 'A problem occurred while trying to save the group: {{errorType}}',
        saveSuccess: 'The group has been saved successfully in an unpublished state.',
        savePublishValidationFailure: 'Please fix all the validation errors before Publishing the Group.',
        savePublishEmptyFailure: 'At least one condition is required before Publishing the Group.',
        savePublishNoChangeFailure: 'No changes detected from previous published version of the Group.',
        savePublishFailure: 'A problem occurred while trying to save and publish the group: {{errorType}}',
        savePublishSuccess: 'The group has been saved and published successfully.'
      }
    },
    groupRankingWizard: {
      column: {
        rank: 'Rank',
        groupName: 'Group Name',
        policyApplied: 'Policy Applied',
        nSources: 'Source Count',
        preview: 'Simulate'
      },
      chooseSource: 'Choose Source Type',
      sourceTooltip: '\
A source takes its settings from the\n\
policies applied by the groups it belongs to,\n\
starting with rank one and working its way down,\n\
until it has a complete policy.\n\
\n\
If a source is a member of multiple groups with\n\
policies that set the same settings, it will\n\
follow the highest ranked.\n\
\n\
If the group policies do not create a full\n\
policy, the source will take the remaining\n\
settings from the default policy. Drag and drop\n\
the rows to change the order.\n\
\n\
Keyboard Navigation On Selected Group:\n\
\n\
Group Selection Focus Up One Step        UpArrow\n\
Group Selection Focus Down One Step    DownArrow\n\
Simulate On                           RightArrow\n\
Simulate Off                           LeftArrow\n\
Set Group Ranking Up One Step      Shift+UpArrow\n\
Set Group Ranking Down One Step  Shift+DownArrow\n\
Set Group To Top Ranking       Shift+Alt+UpArrow\n\
Set Group To Bottom Ranking  Shift+Alt+DownArrow\n',
      previewTooltip: '\
The preview shows how the policy settings will be\n\
applied to a hypothetical source.\n\
\n\
Use the preview toggles in the table to choose\n\
what groups the previewed source belongs to.\n\
\n\
Selecting a row will highlight the settings that \n\
belong to that policy and any potential conflicts.\n\
Change the order of the row to see how this\n\
effects the previewed source.',
      editRanking: 'Edit Ranking',
      chooseSourceTopText: 'Ranking is established per source type.  Select a source type to continue.',
      error: {
        generic: 'An unexpected error has occurred attempting to retrieve this data'
      },
      rankingSavedSuccessful: 'Group ranking has been published successfully.',
      rankingSavedFailed: 'A problem occurred while trying to publish group ranking.'
    },
    policyWizard: {
      // it is used in investigate hosts to display policy stettings
      policyStatus: 'Policy Status',
      evaluatedTime: 'Evaluated Time',
      errorDescription: 'Error Description',
      general: 'GENERAL',
      agentEndpoint: 'AGENT ENDPOINT',
      agentWindowsLogs: 'AGENT WINDOWS LOGS',
      agentFileLogs: 'AGENT FILE LOGS',
      // identify-policy-step
      identifyPolicy: 'Identify Policy',
      sourceType: 'Source Type',
      sourceTypeTooltip: 'Choose a source type to create a policy for.',
      sourceTypePlaceholder: 'Choose a Source Type',
      edrSourceType: 'Agent Endpoint',
      fileLogSourceType: 'Agent Log Files',
      windowsLogSourceType: 'Agent Windows Logs',
      name: 'Policy Name',
      nameTooltip: 'Policy name must be unique and 256 characters or less.',
      namePlaceholder: 'Enter a unique policy name',
      edrPolicyName: 'EDR Policy Name',
      windowsLogPolicyName: 'Windows Log Policy Name',
      dropdownPlaceholder: 'Type in or Pick from list',
      nameRequired: 'Policy name is required',
      nameExists: 'Policy name already exists',
      nameExceedsMaxLength: 'Policy name is limited to 256 characters',
      description: 'Policy Description',
      descTooltip: 'Use the optional policy description to add more detail about the policy. The description can not exceed 8000 characters.',
      descPlaceholder: 'Enter a description',
      descriptionExceedsMaxLength: 'Policy description is limited to 8000 characters',
      // define-policy-step
      definePolicy: 'Define Policy',
      definePolicyConnection: 'Define Connection Settings',
      definePolicySources: 'Define File Policy Settings',
      availableSettings: 'Available Settings',
      availableTooltip: 'Available Settings are the settings that can be used in this policy type. Select the settings you wish to include ' +
        'using the + icon. A policy does not need to include all settings as a source takes its settings from the policies applied by all ' +
        'the groups it belongs to according to rank, then the default policy.',
      selectedSettings: 'Selected Settings',
      selectedTooltip: 'Selected Settings are the settings that your policy will contain. A policy only needs to contain a minimum of one setting. ' +
        'A source will take the remaining settings from any other groups it belongs to according to rank, then the default policy. Use the X ' +
        'icon to remove a setting from the policy.',
      // apply-to-group-step
      applyToGroup: 'Apply to Group',
      // review-policy-step
      reviewPolicy: 'Review',
      // common titlebar
      newPolicy: 'New Policy',
      // common toolbar
      previous: 'Previous',
      next: 'Next',
      publishPolicy: 'Publish Policy',
      saveAndClose: 'Save and Close',
      cancel: 'Cancel',
      modals: {
        discardChanges: {
          title: 'Discard Changes',
          confirm: 'Discard the unsaved changes?',
          discardChanges: 'Discard Changes',
          continueEditing: 'Continue Editing'
        }
      },
      errorPage: {
        loadErrorTitle: 'Policy could not be loaded',
        loadErrorDescription: 'There was a problem loading the requested policy. The server may be offline or inaccessible, or the policy may not exist.'
      },
      actionMessages: {
        prevFailure: 'Please fix all the validation errors before navigating back to the previous step.',
        prevEmptyFailure: 'At least one condition is required before navigating back to the previous step.',
        nextFailure: 'Please fix all the validation errors before continuing to next step.',
        nextEmptyFailure: 'At least one condition is required before continuing to next step.',
        saveValidationFailure: 'Please fix all the validation errors before Saving the Policy.',
        saveEmptyFailure: 'At least one condition is required before Saving the Policy.',
        saveNoChangeFailure: 'No changes detected from previous saved version of the Policy.',
        saveFailure: 'A problem occurred while trying to save the policy: {{errorType}}',
        saveSuccess: 'The policy has been saved successfully in an unpublished state.',
        savePublishValidationFailure: 'Please fix all the validation errors before Publishing the Policy.',
        savePublishEmptyFailure: 'At least one condition is required before Publishing the Policy.',
        savePublishNoChangeFailure: 'No changes detected from previous published version of the Policy.',
        savePublishFailure: 'A problem occurred while trying to save and publish the policy: {{errorType}}',
        savePublishSuccess: 'The policy has been saved and published successfully.'
      },
      // edr policy settings
      edrPolicy: {
        scanSchedule: 'Scan Schedule',
        scanType: 'Run Scheduled Scan',
        scanTypeTooltip: 'Run a scheduled scan if you want to receive regular snapshots from a host. Scan snapshots can provide detailed information regarding processes, files etc loaded in memory on a host but must be repeated periodically to stay current. Regardless of whether or not a host runs a scheduled scan, a manual scan can always be run from the Hosts tab of Investigate.',
        scanTypeManual: 'Disabled',
        scanTypeScheduled: 'Enabled',
        scanStartDate: 'Effective Date',
        scanStartDateTooltip: 'The effective date is the earliest date that the policy will take effect. If you do not want this policy to take effect as soon as it is applied to a group and published, set an effective date that is in the future.',
        scanStartDateInvalidMsg: 'The scan start date should not be empty',
        recurrenceInterval: 'Scan Frequency',
        recurrenceIntervalTooltip: 'Scan frequency determines how often the scheduled scan runs on a host. Every network is different and the frequency should balance the needs of your analysts for current data, analyst availability to review that data, and how your systems will deal with the load of the generated data.',
        recurrenceIntervalOptions: {
          options: {
            daily: 'Days',
            weekly: 'Weeks',
            monthly: 'Months'
          },
          every: 'Scan every',
          on: 'on',
          intervalText: {
            DAYS: 'day(s)',
            WEEKS: 'week(s)',
            MONTHS: 'month(s)'
          },
          week: {
            MONDAY: 'M',
            TUESDAY: 'T',
            WEDNESDAY: 'W',
            THURSDAY: 'T',
            FRIDAY: 'F',
            SATURDAY: 'S',
            SUNDAY: 'S'
          }
        },
        scanStartTime: 'Start Time',
        scanStartTimeTooltip: 'Start time is the time that the scheduled scan will start to run on a host. This is the local machine time meaning that scans across a global network will not run all at once.',
        cpuMax: 'CPU Maximum',
        cpuMaxTooltip: 'CPU maximum will limit the amount of CPU the agent can use to run scheduled scans on physical machines. By default this is set at 25% to balance speed against possible performance impact. Increasing the CPU maximum will increase the speed of scan snapshot retrieval but could lead to excessive CPU usage.',
        cpuMaxVm: 'Virtual Machine Maximum',
        cpuMaxVmTooltip: 'Virtual machine maximum will limit the amount of CPU the agent can use to run scheduled scans on virtual machines. By default this is set at 10%, which is lower than the physical CPU maximum in case a host is over-provisioned. Increasing the virtual machine maximum will increase the speed of scan snapshot retrieval but could lead to excessive CPU usage.',
        advScanSettings: 'Scan Settings',
        captureFloatingCode: 'Capture Floating Code',
        scanMbr: 'Scan Master Boot Record',
        scanMbrTooltip: 'Enable this option to include Master Boot Record (MBR) details in scans. This can help to identify when an operating system boot sequence has been compromised. However, not all modifications to the MBR are malicious as they could be to provide encryption or enforce licensing of certain legitimate software.',
        filterSignedHooks: 'Include Hooks With Signed Modules',
        requestScanOnRegistration: 'Auto Scan New Hosts When Added',
        requestScanOnRegistrationTooltip: 'Automatic scan will be queued for the newly added hosts. If this option is disabled, no snapshot data will show in the Hosts tab of Investigate until a manual or scheduled scan is run on these hosts. Existing hosts will not be affected. Enabling this option on a new deployment when this policy is applied to a large number of hosts may result in a large number of simultaneous scans that cause performance degradation.',
        radioOptionEnabled: 'Enabled',
        radioOptionDisabled: 'Disabled',
        invasiveActions: 'Response Action Settings',
        blockingEnabled: 'Blocking',
        blockingEnabledTooltip: 'Blocking allows an analyst to prevent the execution of a malicious file on any host running an Advanced mode agent. File blocking will not be enforced if it is disabled by policy, which might be desirable to ensure that there are no performance side effects on systems where CPU or IO performance is critical.',
        endpointServerSettings: 'Endpoint Server Settings',
        primaryAddress: 'Endpoint Server',
        primaryAddressTooltip: 'The primary addresses of all your Endpoint servers will be automatically populated by NetWitness and will be available for selection from the dropdown. The Endpoint server once added to policy cannot be left blank. When the specified endpoint server is not available, agents will eventually fall back to the packaged address.',
        primaryAddressInvalidMsg: 'The endpoint server host name should not be empty',
        primaryAlias: 'Endpoint Server Forwarder (Optional)',
        primaryAliasTooltip: 'The optional Endpoint Server Forwarder allows you to enter an alternative hostname or IP address on which the server can be reached in the case that agents need to go through a NAT or similar in order to reach the Endpoint Server. If specified forwarder is not available, agent will eventually fall back to the packaged address.',
        primaryAliasInvalid: 'The endpoint server forwarder is invalid',
        primaryHttpsPort: 'HTTPS Port',
        primaryHttpsPortTooltip: 'The standard HTTPS Port - 443 - is populated by default. If you need to change this port, ensure that it matches the server configuration. Entering the wrong port will mean the agents can no longer communicate with the Endpoint server and the system will be non-functional.',
        primaryUdpPort: 'UDP Port',
        primaryUdpPortTooltip: 'The standard UDP Port - 444 - is populated by default. If you need to change this port, ensure that it matches the server configuration. Entering the wrong port will result in loss of functionality and effect performance.',
        portInvalidMsg: 'The port should be between 1 and 65535',
        primaryHttpsBeaconInterval: 'HTTPS Beacon Interval',
        primaryHttpsBeaconIntervalTooltip: 'HTTPS beacon interval determines how often an agent will check in over HTTPS with the Endpoint server. The default method of beaconing is UDP, which is used as a method of keep-alive to know if a host is online and to allow agents to answer faster than the fallback HTTPS beacon time, which is set to 15 minutes by default. If, for example, UDP is blocked in your environment and you want the agent to be more responsive and resources are not a concern, you can lower the interval. If resources are a concern and responsiveness of the agent is not, you can increase the interval.',
        primaryHttpsBeaconIntervalInvalidMsg: 'The interval should be between 1 minute and 24 hours',
        primaryHttpsBeaconInterval_MINUTES: 'Minutes',
        primaryHttpsBeaconInterval_HOURS: 'Hours',
        primaryUdpBeaconInterval: 'UDP Beacon Interval',
        primaryUdpBeaconIntervalTooltip: 'UDP beacon interval determines how often an agent will check in with the Endpoint server. The default UDP beacon interval is 30 seconds. If you want the agent to be more responsive and resources are not a concern, you can lower the interval. If resources are a concern and responsiveness of the agent is not, you can increase the interval.',
        primaryUdpBeaconIntervalInvalidMsg: 'The interval should be between 5 seconds and 10 minutes',
        primaryUdpBeaconInterval_SECONDS: 'Seconds',
        primaryUdpBeaconInterval_MINUTES: 'Minutes',
        agentSettings: 'Agent Mode',
        agentMode: 'Monitoring Mode',
        agentModeTooltip: 'Monitoring mode allows you to specify whether an agent should operate in Insights or Advanced mode. Insight agents have reduced functionality but do not count against your license. Advanced agents have full functionality but will count against your license. ',
        insights: 'Insights',
        advanced: 'Advanced',
        advancedConfig: 'Advanced Configuration',
        customConfig: 'Advanced Setting',
        customConfigTooltip: 'It is strongly recommended not to use this setting unless advised to by RSA support staff.',
        customConfigInvalidMsg: 'The custom setting cannot be empty or greater than 4000 characters',
        rarPolicy: {
          server: 'Server',
          port: 'Port',
          beaconInterval: 'HTTP Beacon Interval'
        }
      },
      // windowsLog policy settings
      windowsLogPolicy: {
        windowsLogSettingsHeader: 'Windows Log Settings',
        radioOptionEnabled: 'Enabled',
        radioOptionDisabled: 'Disabled',
        enabled: 'Windows Log Collection', // 'Status',
        enabledTooltip: 'If enabled, logs from the Windows system will be collected and forwarded to the NetWitness Platform as they are generated.', // 'Status',
        sendTestLog: 'Send Test Log',
        sendTestLogTooltip: 'If enabled, a sample log will be sent to the configured server when the policy is loaded to test connectivity. This allows the configuration to be tested before standard logs are available. ',
        primaryDestination: 'Primary Log Decoder / Log Collector',
        primaryDestinationTooltip: 'The primary Log Decoder or Log Collector to which the collected windows logs will be forwarded to.',
        secondaryDestination: 'Secondary Log Decoder / Log Collector',
        secondaryDestinationTooltip: 'If the primary Log Decoder or Log Collector is not reachable, collected windows logs will be forwarded to this server. Please note, NetWitness cannot detect failures when UDP is the protocol used.',
        windowsLogDestinationInvalidMsg: 'The log server host name should not be empty',
        protocol: 'Protocol',
        protocolTooltip: 'Select whether SSL, TCP, or UDP transport protocol is used to forward the collected windows logs to the NetWitness Platform servers. SSL is recommended but note that is is the heaviest option. TCP is reliable but unprotected, which may be acceptable within a corporate network. Finally, UDP is unsecured and delivery is not guaranteed.',
        channelFilters: 'Channel Filters',
        channelFiltersTooltip: 'Configure which Windows log events to collect by selecting a channel, ' +
         'filter condition, and the relevant event IDs. Common channels such as \'Security\' or \'System\' ' +
         'can be selected from the dropdown, whereas custom channels can be added by typing in the ' +
         'channel name field. By default all events are collected from a selected channel. To collect a ' +
         'subset of events from that channel replace \'ALL\' with the relevant Event ID(s). Leave the ' +
         'filter set to \'INCLUDE\' if only events with the listed Event IDs should be collected or ' +
         'change it to \'EXCLUDE\' to collect all events except for these events.',

        channel: {
          add: 'Add Channel Filter',
          name: 'CHANNEL NAME',
          filter: 'FILTER',
          event: 'EVENT ID',
          empty: ''
        },
        invalidEventId: 'Event ID is invalid',
        invalidChannelFilter: 'Field cannot be empty',
        channelFiltersSettingsHeader: 'Channel Filter Settings',
        applicationInclude: 'Application Include',
        name: 'Policy Name'
      },
      // filePolicy settings
      filePolicy: {
        fileSettingsHeader: 'File Settings',
        filePolicySettingsHeader: 'File Policy Settings',
        helpTitle: 'Select an application log file type from the list of supported file types to configure its log collection settings',
        radioOptionEnabled: 'Enabled',
        radioOptionDisabled: 'Disabled',
        radioOptionEnable: 'Enable',
        radioOptionDisable: 'Disable',
        enabled: 'File Collection', // 'Status',
        enabledTooltip: 'If enabled, logs from the Windows system will be collected and forwarded to the NetWitness Platform as they are generated.', // 'Status',
        sendTestLog: 'Send Test Log',
        sendTestLogTooltip: 'If enabled, a sample log will be sent to the configured server when the policy is loaded to test connectivity. This allows the configuration to be tested before standard logs are available. ',
        primaryDestination: 'Primary Log Decoder / Log Collector',
        primaryDestinationTooltip: 'The primary Log Decoder or Log Collector to which the collected windows logs will be forwarded to.',
        secondaryDestination: 'Secondary Log Decoder / Log Collector',
        secondaryDestinationTooltip: 'If the primary Log Decoder or Log Collector is not reachable, collected windows logs will be forwarded to this server. Please note, NetWitness cannot detect failures when UDP is the protocol used.',
        fileDestinationInvalidMsg: 'The log server host name should not be empty',
        protocol: 'Protocol',
        protocolTooltip: 'Select whether SSL, TCP, or UDP transport protocol is used to forward the collected windows logs to the NetWitness Platform servers. SSL is recommended but note that is is the heaviest option. TCP is reliable but unprotected, which may be acceptable within a corporate network. Finally, UDP is unsecured and delivery is not guaranteed.',
        logFileType: 'Log File Type',
        enableOnAgent: 'Log File Collection on Agent',
        dataCollection: 'Data Collection',
        collectNew: 'Collect new data only',
        collectAll: 'Collect historical and new data',
        fileEncoding: 'File Encoding',
        sourceName: 'Source Name',
        exclusionFilters: 'Exclusion Filters',
        paths: 'Paths',
        logFilePath: 'Log File Path',
        addPath: 'Add Path',
        fileSourceType: {
          add: 'Add Selected File Type',
          placeholder: 'Pick a Log File Type'
        }
      }
    },
    policyTypes: {
      edrPolicy: 'Agent Endpoint',
      filePolicy: 'Agent Log Files',
      windowsLogPolicy: 'Agent Windows Logs'
    },
    publishStatus: {
      unpublished: 'Unpublished',
      unpublishedEdits: 'Unpublished Edits',
      published: 'Published'
    },
    groupCriteria: {
      and: 'and',
      attribute: {
        osType: 'OS Type',
        osDescription: 'OS description',
        hostname: 'Host Name',
        ipv4: 'IPv4',
        ipv6: 'IPv6',
        agentMode: 'Agent Mode'
      },
      operator: {
        IN: 'in',
        EQUAL: 'is equal to',
        CONTAINS: 'contains',
        STARTS_WITH: 'starts with',
        ENDS_WITH: 'ends with',
        NOT_IN: 'not in',
        BETWEEN: 'between',
        NOT_BETWEEN: 'not between'
      },
      andOrOperator: {
        AND: 'all',
        OR: 'any'
      },
      andOrOperatorText: {
        first: 'Include source if ',
        second: ' of the following conditions are met:'
      },
      inputValidations: {
        none: '',
        notEmpty: 'Value can not be empty',
        maxLength255: 'Value can not be empty and must be less than 256 characters',
        validHostname: 'Invalid host name',
        validHostnameContains: 'Invalid host name characters for contains',
        validHostnameStartsWith: 'Invalid host name characters for starts with',
        validHostnameEndsWith: 'Invalid host name characters for ends with',
        validHostnameList: 'One or more of the the hostnames are invalid',
        validIPv4: 'Invalid IPv4 address',
        validIPv4List: 'One or more of the the IPv4 addresses are invalid',
        validIPv6: 'Invalid IPv6 address',
        validIPv6List: 'One or more of the the IPv6 addresses are invalid',
        validPolicyAssigned: 'Policy selection is required, or clear by selecting the \'X\''
      },
      tooltips: {
        osType_notEmpty: 'Select one or more of the operating system types.',
        osDescription_EQUAL: 'Enter OS Description value that is must be less than 256 characters. Only exact matches will be included.',
        osDescription_CONTAINS: 'Enter OS Description value that is must be less than 256 characters. Matches will be included if the value is found anywhere in the OS description.',
        osDescription_STARTS_WITH: 'Enter OS Description value that is must be less than 256 characters. Matches will be included if the value begins with the OS description.',
        osDescription_ENDS_WITH: 'Enter OS Description value that is must be less than 256 characters. Matches will be included if the value ends with the OS description.',
        hostname_EQUAL: 'Enter one or more valid hostnames. Valid hostnames contain alphanumeric, ., or _ characters. First and last characters must be alphanumeric.',
        hostname_CONTAINS: 'Enter a single valid hostname string containing alphanumeric, ., or _ characters. Matches included if the value is found anywhere in host name.',
        hostname_STARTS_WITH: 'Enter a single valid hostname string. Valid starts with substring contain alphanumeric, ., or _ characters. First character must be alphanumeric. Matches included if the value begins with the host name.',
        hostname_ENDS_WITH: 'Enter a single valid hostname ending substring. Valid ends with substring contain alphanumeric, ., or _ characters. First character must be alphanumeric. Matches included if the value ends with the host name',
        hostname_validHostnameList: 'Enter one or more valid hostnames.  Valid hostnames contain alphanumeric, ., or _ characters. First and last character must be alphanumeric. Multiple values can be separated by spaces, tabs, commas, or line feeds.',
        ipv4_validIPv4_BETWEEN: 'Enter a single valid IPv4 address value in each field to specify a range of IP addresses (including start and end address) that will be included in the group.',
        ipv4_validIPv4_NOT_BETWEEN: 'Enter a single valid IPv4 address value in each field to specify a range of IP addresses (including start and end address) that will be excluded from the group.',
        ipv4_validIPv4List: 'Enter one or more valid IPv4 addresses. Multiple values can be separated by spaces, tabs, commas, or line feeds.',
        ipv6_validIPv6_BETWEEN: 'Enter a single valid IPv6 address value in each field to specify a range of IP addresses (including start and end address) that will be included in the group.',
        ipv6_validIPv6_NOT_BETWEEN: 'Enter a single valid IPv6 address value in each field to specify a range of IP addresses (including start and end address) that will be excluded from the group.',
        ipv6_validIPv6List: 'Enter one or more valid IPv6 addresses. Multiple values can be separated by spaces, tabs, commas, or line feeds.'
      },
      placeholders: {
        beginning_IP: 'Beginning IP address',
        end_IP: 'End IP address',
        osType_notEmpty: 'Select one or more operating systems',
        osDescription_EQUAL: 'Enter valid OS Description',
        osDescription_CONTAINS: 'Enter OS Description substring',
        osDescription_STARTS_WITH: 'Enter starting substring',
        osDescription_ENDS_WITH: 'Enter ending substring',
        hostname_EQUAL: 'Enter valid hostnames',
        hostname_CONTAINS: 'Enter hostname substring',
        hostname_STARTS_WITH: 'Enter starting substring',
        hostname_ENDS_WITH: 'Enter ending substring',
        hostname_validHostnameList: 'Enter one or more valid hostnames',
        ipv4_validIPv4List: 'Enter one or more valid IPv4 addresses. Multiple values can be separated by spaces, tabs, commas, or line feeds.',
        ipv6_validIPv6List: 'Enter one or more valid IPv6 addresses. Multiple values can be separated by spaces, tabs, commas, or line feeds.'
      }
    },
    errorCodeResponse: {
      default: 'Unknown response error code',
      601: 'Policy with same name already exists',
      602: 'Group with same name already exists',
      603: 'Unable to delete a default policy type',
      604: 'Group criteria is invalid',
      605: 'Group criteria is empty',
      606: 'Group criteria conjunction operator is invalid',
      607: 'Group criteria condition is empty',
      608: 'Group criteria condition is invalid',
      609: 'Group criteria attribute name is empty',
      610: 'Group criteria attribute name is too long',
      611: 'Group criteria attribute operator is invalid',
      612: 'Group criteria attribute value is empty',
      613: 'Group criteria attribute value is unknown',
      614: 'Group criteria attribute description value is empty',
      615: 'Group criteria attribute description value is too long',
      616: 'Group criteria attribute hostname value is empty',
      617: 'Group criteria attribute hostname value is invalid',
      618: 'Group criteria attribute hostname value is too long',
      619: 'Group criteria attribute IPv4 address value is empty',
      620: 'Group criteria attribute IPv4 address range value is missing',
      621: 'Group criteria attribute IPv4 address range value is invalid',
      622: 'Group criteria attribute IPv4 address value is invalid',
      623: 'Group criteria attribute IPv6 address value is empty',
      624: 'Group criteria attribute IPv6 address range value is missing',
      625: 'Group criteria attribute IPv6 address range value is invalid',
      626: 'Group criteria attribute IPv6 address value is invalid',
      629: 'Invalid advanced configuration: Must be valid JSON'
    }
  },
  // end admin-source-management (a.k.a USM)
  license: {
    banner: {
      dismiss: 'Dismiss',
      licensePage: 'License Details',
      servicesPage: 'Services',
      unlicensed: 'One or more services are not licensed. For more information, see ',
      expired: 'One or more licenses have expired. For more information, see ',
      'usage-exceeded': 'You have exceeded license usage limits. For more information, see ',
      'near-expiry': 'One or more licenses are expiring. For more information, see ',
      'near-usage-limit': 'You are nearing license usage limits. For more information, see ',
      serverDown: 'The License Server is offline for more than 4 days. Please start the License Server from '
    }
  },
  rsaWizard: {
    tests: {
      testStep1Label: 'Test Step 1',
      testStep2Label: 'Test Step 2',
      testStep3Label: 'Test Step 3'
    }
  },
  rsaExplorer: {
    timeframeOptions: {
      LAST_5_MINUTES: 'Last 5 Minutes',
      LAST_10_MINUTES: 'Last 10 Minutes',
      LAST_15_MINUTES: 'Last 15 Minutes',
      LAST_30_MINUTES: 'Last 30 Minutes',
      LAST_HOUR: 'Last Hour',
      LAST_3_HOURS: 'Last 3 Hours',
      LAST_6_HOURS: 'Last 6 Hours',
      LAST_TWELVE_HOURS: 'Last 12 Hours',
      LAST_TWENTY_FOUR_HOURS: 'Last 24 Hours',
      LAST_FORTY_EIGHT_HOURS: 'Last 2 Days',
      LAST_5_DAYS: 'Last 5 Days',
      LAST_7_DAYS: 'Last 7 Days',
      LAST_14_DAYS: 'Last 14 Days',
      LAST_30_DAYS: 'Last 30 Days',
      ALL_TIME: 'All Data'
    },
    flash: {
      updateSuccess: 'Your change was successful',
      updateFailure: 'There was a problem updating the field for this record',
      createSuccess: 'You successfully added a new record',
      createFailure: 'There was a problem creating this record',
      deleteSuccess: 'You successfully deleted this record',
      deleteFailure: 'There was a problem deleting this record',
      saveSuccess: 'Your changes were successfully saved',
      saveFailure: 'There was a problem saving this record'
    },
    noResults: 'No results were found. Try expanding your time range or adjusting existing filters to include more results.',
    confirmation: {
      updateTitle: 'Confirm Update',
      deleteTitle: 'Confirm Delete',
      bulkUpdateConfrimation: 'You are about to make the following changes to more than one item',
      deleteConfirmation: 'Are you sure you want to delete {{count}} record(s)? Once applied, this deletion cannot be reversed.',
      field: 'Field',
      value: 'Value',
      recordCountAffected: 'Number of items'
    },
    filters: {
      label: 'Filters',
      timeRange: 'Time Range',
      reset: 'Reset Filters',
      customDateRange: 'Custom Date Range',
      customStartDate: 'Start Date',
      customEndDate: 'End Date',
      customDateErrorStartAfterEnd: 'The start date and time cannot be the same or later than the end date'
    },
    inspector: {
      overview: 'Overview'
    },
    footer: 'Showing {{count}} out of {{total}} items',
    selectionCount: '{{selectionCount}} selected',
    fetchError: 'An error has occurred. The server may be offline or inaccessible.'
  },
  rsaTooltip: {
    note: 'Note: ',
    labelMessage: 'Only the initial 255 characters of the value are indexed as part of this meta, and will be included in any indexed search.'
  },
  entityDetails: {
    entity: {
      removeWatch: 'Stop Watching',
      addWatch: 'Watch Profile'
    }
  },
  investigateUsers: {
    export: 'Export',
    sortBy: 'Sort By',
    sortOptions: {
      name: 'Name',
      score: 'Risk Score',
      alertsCount: 'Alerts'
    },
    resetFilters: 'Reset Filters',
    addToFavorites: 'Add To Favorites',
    filterName: 'Filter Name :',
    save: 'Save',
    cancel: 'Cancel',
    feedback: {
      none: 'None',
      rejected: 'Rejected'
    },
    severity: {
      Critical: 'Critical',
      High: 'High',
      Medium: 'Medium',
      Low: 'Low',
      // Need to use same key
      critical: 'Critical',
      high: 'High',
      medium: 'Medium',
      low: 'Low'
    },
    overview: {
      title: 'Overview',
      topAlerts: 'Top Alerts'
    },
    errorMessages: {
      topAlertsError: 'Unable to fetch top alerts.',
      noAlerts: 'No alerts present.',
      alertListError: 'Unable to fetch alerts',
      alertsForTimeLineError: 'Unable to fetch alerts for timeline.',
      unableToGetExistAnomalyTypesForAlert: 'Unable to fetch anomaly types.',
      unableToGetAdminUserCount: 'Unable to get admin user count.',
      unableToGetRiskyUserCount: 'Unable to get risky user count.',
      unableToGetWatchedUserCount: 'Unable to get watched user count.',
      unableToGetSeverityDetails: 'Unable to get severity details.',
      unableToGetExistAnomalyTypes: 'Unable to get anomaly types.',
      unableToGetExistAlertTypes: 'Unable to get alert type.',
      unableToSaveAsFavorite: 'Unable to save filter as favorite',
      unableToFollowUsers: 'Unable to follow users',
      unableToDeleteFavorite: 'Unable to delete saved filter.',
      topUsersError: 'Unable to fetch top risky users.',
      usersError: 'Unable to fectch users.',
      noUserData: 'No user data present.',
      unableToFindUsers: 'Unable to find Users'
    },
    users: {
      title: 'Users',
      addAllToWatchedList: 'Add All To WatchList',
      removeAllFromWatchedList: 'Remove All From WatchedList',
      highRisk: 'High Risk Users',
      all: 'All Users',
      risky: 'Risky Users',
      watched: 'Watched',
      admin: 'Admin',
      filters: {
        favorites: 'Favorites',
        title: 'Filters',
        allTypes: 'All Types',
        indicators: 'Indicators',
        riskyUsers: 'Risky Users',
        watchlistUsers: 'Watchlist Users',
        adminUsers: 'Admin Users'
      }
    },
    alerts: {
      title: 'Alerts',
      noAlerts: 'No Alerts',
      notARisk: 'Not a Risk',
      risk: 'Risk',
      all: 'All Alerts',
      alertName: 'Alert Name',
      alertOverview: 'Alert Overview',
      entityName: 'Entity Name',
      score: 'Score',
      startTime: 'Start Time',
      alertFlow: 'Alert Flow',
      indicatorCount: 'Indicator Count',
      startFrom: 'Alerts starts from ',
      contributionInScore: 'Contribution in Score',
      sources: 'Sources',
      severity: 'Severity',
      date: 'Date',
      feedback: 'Feedback',
      indicators: 'Indicators',
      error: 'Some problem while fetching entity alerts. Please retry after some time.',
      errorDetails: 'Some problem while fetching alert details. Please retry after some time.',
      alertNames: {
        credential_dumping: {
          name: 'Credential Dumping',
          desc: 'Credential dumping is the process of obtaining account login and password information, normally in the form of a hash or a clear text password, from the operating system and software. Credentials can then be used to perform Lateral Movement and access restricted information.',
          related: 'Related Threats: Common credential dumpers such as Mimikatz access the LSA Subsystem Service (LSASS) process by opening the process, locating the LSA secrets key, and decrypting the sections in memory where credential details are stored. Credential dumpers may also use methods for reflective Process Injection to reduce potential indicators of malicious activity.'
        },
        discovery_reconnaissance: {
          name: 'Discovery & Reconnaissance',
          desc: 'Discovery consists of techniques that allow the adversary to gain knowledge about the system and internal network. When Attackers gain access to a new system, they must orient themselves to what they now have control of and what benefits operating from that system give to their current objective or overall goals during the intrusion. The operating system provides many native tools that aid in this post-compromise information-gathering phase.',
          related: 'Related Threats: Example commands that can acquire this information are net user, net group , and net localgroup using the Net utility or through use of dsquery. If Attackers attempt to identify the primary user, currently logged in user, or set of users that commonly uses a system, System Owner/User Discovery may apply.'
        },
        powershell_scripting: {
          name: 'PowerShell & Scripting',
          desc: 'PowerShell is a powerful interactive command-line interface and scripting environment included in the Windows operating system. Attackers can use PowerShell to perform a number of actions, including discovery of information and execution of code. Examples include the Start-Process cmdlet which can be used to run an executable and the Invoke-Command cmdlet which runs a command locally or on a remote computer.',
          related: 'Related Threats: PowerShell is a powerful interactive command-line interface and scripting environment included in the Windows operating system. Attackers can use PowerShell to perform a number of actions, including discovery of information and execution of code. Examples include the Start-Process cmdlet which can be used to run an executable and the Invoke-Command cmdlet which runs a command locally or on a remote computer. PowerShell may also be used to download and run executables from the Internet, which can be executed from disk or in memory without touching disk. Administrator permissions are required to use PowerShell to connect to remote systems.'
        },
        registry_run_keys: {
          name: 'Registry Run Keys & Start Folder',
          desc: 'Adding an entry to the "run keys" in the Registry or startup folder will cause the program referenced to be executed when a user logs in. The program will be executed under the context of the user and will have the account\'s associated permissions level. Attackers can use these configuration locations to execute malware, such as remote access tools, to maintain persistence through system reboots. Attackers may also use Masquerading to make the Registry entries look as if they are associated with legitimate programs.',
          related: 'Related Threats: Adding an entry to the "run keys" in the Registry or startup folder will cause the program referenced to be executed when a user logs in. These programs will be executed under the context of the user and will have the account\'s associated permissions level.'
        },
        process_injection: {
          name: 'Process Injection',
          desc: 'Process injection is a method of executing arbitrary code in the address space of a separate live process. Running code in the context of another process may allow access to the process\'s memory, system/network resources, and possibly elevated privileges. Execution via process injection may also evade detection from security products since the execution is masked under a legitimate process.',
          related: 'Related Threats: Dynamic-link library (DLL) injection involves writing the path to a malicious DLL inside a process then invoking execution by creating a remote thread. Portable executable injection involves writing malicious code directly into the process (without a file on disk) then invoking execution with either additional code or by creating a remote thread. Thread execution hijacking involves injecting malicious code or the path to a DLL into a thread of a process. Similar to Process Hollowing, the thread must first be suspended. Asynchronous Procedure Call (APC) injection involves attaching malicious code to the APC Queue of a process\'s thread. Thread Local Storage (TLS) callback injection involves manipulating pointers inside a portable executable (PE) to redirect a process to malicious code before reaching the code\'s legitimate entry point.'
        },
        mass_changes_to_critical_enterprise_groups: {
          name: 'Mass Changes to Critical Enterprise Groups',
          desc: 'An abnormal number of changes have been made to critical enterprise groups. These groups often manage and control high-value IT assets. If these assets were compromised, attackers could escalate privileges and exploit them to establish persistent control over the domain. Investigate which elements have been changed, and decide if the changes were legitimate or possibly the result of risky or malicious behavior. This activity is usually associated with the <b>Multiple Member Additions to Enterprise Critical Groups</b> indicator.',
          related: 'Related Threats: Account Manipulation - Account manipulation may aid attackers in maintaining access to credentials and certain permission levels within an environment. Manipulation could consist of modifying permissions, adding or changing permission groups, modifying account settings, or modifying how authentication is performed.'
        },
        mass_changes_to_groups: {
          name: 'Mass Changes to Groups',
          desc: 'An abnormal number of changes have been made to groups. Investigate which elements have been changed, and decide if the changes were legitimate or possibly the result of risky or malicious behavior. This activity is usually associated with the <b>Multiple Group Membership Changes</b> indicator.',
          related: 'Related Threats: Account Manipulation - Account manipulation may aid attackers in maintaining access to credentials and certain permission levels within an environment. Manipulation could consist of modifying permissions, adding or changing permission groups, modifying account settings, or modifying how authentication is performed.'
        },
        elevated_privileges_granted: {
          name: 'Elevated Privileges Granted (Rule)',
          desc: 'Attackers often use regular user accounts, granting them elevated privileges, to exploit the network. Investigate the user that received the elevated privileges, and decide if these changes were legitimate or possibly the result of risky or malicious behavior.',
          related: 'Related Threats: Account Manipulation - Account manipulation may aid attackers in maintaining access to credentials and certain permission levels within an environment. Manipulation could consist of modifying permissions, adding or changing permission groups, modifying account settings, or modifying how authentication is performed.'
        },
        brute_force_authentication: {
          name: 'Brute Force Authentication',
          desc: 'In traditional password cracking attempts, the attacker will try to obtain a password through guesswork or by employing other low-tech methods to gain initial access. The attacker risks getting caught or being locked out by explicitly attempting to authenticate; but with some prior knowledge of the victim\'s password history, may be able to successfully authenticate. Look for additional abnormal indications that the account owner is not the one attempting to access this account. This activity is usually associated with the Multiple Failed Authentications indicator.',
          related: 'Related Threats: Brute Force - Attackers may attempt to brute force logons without knowledge of passwords or hashes during an operation either with zero knowledge or by attempting a list of known or possible passwords. A related technique uses one password, or a small list of passwords, that matches the complexity policy of the domain and may be a commonly used password. Logons are attempted with that password and many different accounts on a network to avoid account lockouts that would normally occur when brute forcing a single account with many passwords.'
        },
        multiple_logons_by_user: {
          name: 'Multiple Logons by User',
          desc: 'All authentication activity, malicious or not, appears as normal logons. Therefore, administrators should monitor unexpected "authorized" activity. The key is that attackers use these stolen credentials for unauthorized access, which may provide an opportunity for detection. When an account is being used for unusual activities, e.g. authenticating an unusual amount of times, then the account may have been compromised. This activity can be associated with the Multiple Successful Authentications indicator.',
          related: 'Related Threats: Valid Account Abuse -  Attackers may steal the credentials of a specific user or service account using Credential Access techniques. Compromised credentials may be used to bypass access controls within the network and may even be used for persistent access to remote systems. These credentials may also grant an attacker increased privilege to specific systems or access to restricted areas of the network.'
        },
        user_login_to_abnormal_remote_host: {
          name: 'User Logon to Abnormal Remote Host',
          desc: 'Attackers will often need to reacquire credentials and perform other sensitive activities, like using remote access. Tracing the access chain backwards may lead to the discovery of other hosts involved in possibly risky activity. If an attacker\'s presence is limited to a single compromised host or to many compromised hosts, that activity can be associated with the Abnormal Remote Host and Abnormal Host indicators.',
          related: 'Related Threats: Remote Desktop Protocol - Attackers may connect to a remote system over RDP/RDS to expand access if the service is enabled and allows access to accounts with known credentials. Attackers will likely use Credential Access techniques to acquire credentials to use with RDP.'
        },
        user_login_to_abnormal_host: {
          name: 'User Logon to Abnormal Host',
          desc: 'Attackers will often need to reacquire credentials and perform other sensitive functions, like using remote access. Tracing the access chain backwards may lead to the discovery of other hosts involved in possibly risky activity. If an attacker\'s presence is limited to a single compromised host or to many compromised hosts, that activity can be associated with the Abnormal Remote Host and Abnormal Host indicators.',
          related: 'Related Threats: Valid Account Abuse - Attackers may steal the credentials of a specific user or service account using Credential Access techniques. Compromised credentials may be used to bypass access controls within the network and may even be used for persistent access to remote systems. These credentials may also grant an attacker increased privilege to specific systems or access to restricted areas of the network.'
        },
        abnormal_site_access: {
          name: 'Abnormal Site Access',
          desc: 'Domain controllers store credential password hashes for all accounts on the domain, so they are high-value targets for attackers. Domain controllers that aren\'t stringently updated and secured are susceptible to attack and compromise, which could leave the domain and forest vulnerable. User privileges on multiple domains could indicate that a parent domain has been compromised. Determine if user access to/from multiple domains is legitimate or is an indication of a potential compromise. This activity is usually associated with the Abnormal Site and Logon Attempts from Multiple Sites indicators.',
          related: 'Related Threats: Valid Account Abuse -  Attackers may steal the credentials of a specific user or service account using Credential Access techniques. Compromised credentials may be used to bypass access controls within the network and may even be used for persistent access to remote systems. These credentials may also grant an attacker increased privilege to specific systems or access to restricted areas of the network.'
        },
        data_exfiltration: {
          name: 'Data Exfiltration',
          desc: 'Data exfiltration is the unauthorized copying, transfer, or retrieval of data from a computer or server. Data exfiltration is a malicious activity performed through various techniques, typically by cybercriminals over the Internet or other network. This activity can be associated with the <b>Excessive Number of File Delete Events, Excessive Number of Files Moved.<b>',
          related: 'Related Threats: Scheduled Transfers - Data exfiltration may be performed only at certain times of day or at certain intervals. This could be done to blend traffic patterns with normal activity or availability.'
        },
        mass_file_rename: {
          name: 'Mass File Rename',
          desc: 'Mass file renaming, triggered by a specific system or local service account could indicate the system may have been infected by Ransomware. Investigating the source user and related file types and folder names can quickly resolve if in fact ransomware has spread across the file system. This activity can be associated with the Multiple File Rename Events indicator.',
          related: 'Related Threats: Ransomware is a type of malware that encrypts desktop and system files, making them inaccessible. Some ransomware, e.g. "Locky", encrypt and rename files as part of their initial execution. Use this indication of mass-file-renaming to determine if your file system has been infected with Ransomware.'
        },
        snooping_user: {
          name: 'Snooping User',
          desc: 'Snooping is unauthorized access to another person\'s or company\'s data. Snooping can be as simple as the casual observance of an e-mail on another\'s computer, or watching what someone else is typing. More sophisticated snooping uses software programs to remotely monitor activity on a computer or network device. This activity can be associated with the Multiple File Access Events, Multiple Failed File Access Events, Multiple File Open Events, and Multiple Folder Open Events indicators.',
          related: 'Related Threats: File System Discovery - Attackers may enumerate files and directories or may search in specific locations of a host or network share for certain information within a file system.'
        },
        user_logins_to_multiple_ad_sites: {
          name: 'User Logons to Multiple Domains',
          desc: 'Domain controllers store credential password hashes for all accounts on the domain, so they are high-value targets for attackers. Domain controllers that aren\'t stringently updated and secured are susceptible to attack and compromise, which could leave the domain and forest vulnerable. User privileges on multiple domains could indicate that a parent domain has been compromised. Determine if user access to/from multiple domains is legitimate or is an indication of a potential compromise. This activity is usually associated with the Logged into Multiple Domains indicator.',
          related: 'Related Threats: Valid Account Abuse -  Attackers may steal the credentials of a specific user or service account using Credential Access techniques. Compromised credentials may be used to bypass access controls within the network and may even be used for persistent access to remote systems. These credentials may also grant an attacker increased privilege to specific systems or access to restricted areas of the network.'
        },
        user_logged_into_multiple_hosts: {
          name: 'User Logons to Multiple Hosts',
          desc: 'Attackers typically need to reacquire credentials periodically. This is because their keychain of stolen credentials will naturally degrade over time, due to password changes and resets. Therefore, attackers frequently maintain a foothold in the compromised organization by installing backdoors and maintaining credentials from many hosts in the environment. This activity can be associated with the Logged onto Multiple Hosts indicator. Valid Account Abuse -  Attackers may steal the credentials of a specific user or service account using Credential Access techniques. Compromised credentials may be used to bypass access controls within the network and may even be used for persistent access to remote systems. These credentials may also grant an attacker increased privilege to specific systems or access to restricted areas of the network.',
          related: 'Related Threats: Valid Account Abuse -  Attackers may steal the credentials of a specific user or service account using Credential Access techniques. Compromised credentials may be used to bypass access controls within the network and may even be used for persistent access to remote systems. These credentials may also grant an attacker increased privilege to specific systems or access to restricted areas of the network.'
        },
        admin_password_change: {
          name: 'Admin Password Change (Rule)',
          desc: 'Shared long-term secrets, e.g. privileged account passwords, are frequently used to access anything from print servers to domain controllers. To contain attackers, that seek to leverage these accounts, pay close attention to password changes by admins, and ensure they have been made by trusted parties and have no additional abnormal behavior associated with them. This activity can be associated with the <b>Admin Password Change</b> indicator.',
          related: 'Related Threats: Account Manipulation - Account manipulation may aid attackers in maintaining access to credentials and certain permission levels within an environment. Manipulation could consist of modifying permissions, adding or changing permission groups, modifying account settings, or modifying how authentication is performed.'
        },
        mass_permission_changes: {
          name: 'Mass Permission Changes',
          desc: 'Some credential theft techniques, e.g. Pass-the-Hash, use an iterative, two-stage process. First, an attacker obtains elevated read/write permission to privileged areas of volatile memory and file systems, which are typically accessible only to system-level processes on at least one computer. Second, the attacker attempts to increase access to other hosts on the network. Investigate if abnormal permission changes have taken place on the file systems to ensure that they were not compromised by an attacker. This activity can be associated with the Multiple File Access Permission Changes, Multiple Failed File Access Permission Changes, and Abnormal File Access Permission Change indicators.',
          related: 'Related Threats: File System Permissions Weakness -  Attackers may replace a legitimate service executable with their own executable to gain persistence and/or privilege escalation to the account context the service is set to execute under (local/domain account, SYSTEM, LocalService, or NetworkService).'
        },
        abnormal_ad_changes: {
          name: 'Abnormal AD Changes',
          desc: 'If an attacker gains highly privileged access to an Active Directory domain or domain controller, that access can be leveraged to access, control, or even destroy the entire forest. If a single domain controller is compromised and an attacker modifies the AD database, those modifications replicate to every other domain controller in the domain, and depending on the partition in which the modifications are made, the forest as well. Investigate abnormal changes conducted by admins and non-admins in AD to determine if they represent a possible true compromise to the domain. This activity can be associated with the Abnormal Active Directory Change, Multiple Account Management Changes, Multiple User Account Management Changes, and Multiple Failed Account Management Changes indicators.',
          related: 'Related Threats: Malicious Account Creation - Attackers with a sufficient access may create a domain account that can be used for persistence that does not require persistent remote access tools to be deployed on the system; Account Manipulation - Account manipulation may aid attackers in maintaining access to credentials and certain permission levels within an environment. Manipulation could consist of modifying permissions, adding or changing permission groups, modifying account settings, or modifying how authentication is performed; Account Discovery - Attackers may attempt to get a listing of local system or domain accounts.; Permission Groups Discovery - Attackers may attempt to find local system or domain-level groups and permissions settings.'
        },
        sensitive_user_status_changes: {
          name: 'Sensitive User Status Changes (Rule)',
          desc: 'A domain or enterprise administrator account has the default ability to exercise control over all resources in a domain, regardless of whether it operates with malicious or benign intent. This control includes the ability to create and change accounts; read, write, or delete data; install or alter applications; and erase operating systems. Some of these activities trigger organically as part of the account\'s natural life cycle. Investigate these security sensitive user account changes, and determine if it has been compromised.',
          related: 'Related Threats: Account Manipulation - Account manipulation may aid attackers in maintaining access to credentials and certain permission levels within an environment. Manipulation could consist of modifying permissions, adding or changing permission groups, modifying account settings, or modifying how authentication is performed.'
        },
        abnormal_file_access: {
          name: 'Abnormal File Access',
          desc: 'Monitor for abnormal file access to prevent improper access to confidential files and theft of sensitive data. By selectively monitoring file views, modifications and deletions, you can detect possibly unauthorized changes to sensitive files, whether caused by an attack or a change management error. This activity can be associated with the <b>Abnormal File Access Indicator</b>.',
          related: 'Related Threats: Remote File Access - Files may be moved or copied from one system to another to stage attacker tools or other files over the course of an operation. Attackers may also move files laterally between internal victim systems to support Lateral Movement with remote execution using authenticated connections with Windows Admin Shares or Remote Desktop Protocol.'
        },
        non_standard_hours: {
          name: 'Non-Standard Hours',
          desc: 'All authentication activity, malicious or not, appears as normal logons. Therefore, administrators should monitor unexpected authorized activity. The key is that attackers use these stolen credentials for unauthorized access, which may provide an opportunity for detection. When an account is being used for unusual activities, e.g. authenticating an unusual number of times, then the account may have been compromised. Use the indication of an abnormal activity time to determine if the account has been taken over by an external actor. This activity can be associated with the Abnormal File Access Time, Abnormal Active Directory Change Time, and Abnormal Logon Time indicators.',
          related: 'Related Threats: Logon Scripts - Windows allows logon scripts to be run whenever a specific user or group of users log into a system. If Attackers can access these scripts, they may insert additional code into the logon script to execute their tools when a user logs in. This code can allow them to maintain persistence on a single system, if it is a local script, or to move laterally within a network, if the script is stored on a central server and pushed to many systems. Depending on the access configuration of the logon scripts, either local credentials or an administrator account may be necessary.'
        }
      },
      indicator: {
        title: 'Indicator',
        name: 'Indicator Name',
        anomalyValue: 'Anomaly Value',
        numOfEvents: 'Event Count',
        dataSource: 'Data Source',
        contributionToAlert: 'Contribution to Alert',
        startDate: 'Start Date',
        eventsError: 'Some problem while fetching events for this indicator. Please retry after some time.',
        graphError: 'Some problem while fetching historical data for this indicator. Please retry after some time.',
        indicatorNames: {
          // Presidio File Anoamlies
          abnormal_file_action_operation_type: {
            name: 'Abnormal File Access Event',
            timeline: '{{entityName}} has accessed a file abnormally.',
            chartTitle: 'File Access Events Baselines'
          },
          abnormal_file_permision_change_operation_type: {
            name: 'Abnormal File Access Permission Change',
            timeline: 'Multiple Share Permissions have changed by {{entityName}}',
            chartTitle: 'File Access Permission Changes Baseline'
          },
          abnormal_file_day_time: {
            name: 'Abnormal File Access Time',
            timeline: '{{entityName}} has accessed a file at an abnormal time}',
            chartTitle: 'File Access Time Baseline'
          },
          abnormal_event_day_time: {
            name: 'Abnormal File Access Time',
            desc: '{{entityName}} has accessed a file at an abnormal time',
            timeline: '{{entityName}} has accessed a file at an abnormal time',
            chartTitle: 'File Access Time Baseline'
          },
          high_number_of_distinct_folders_opened_attempts: {
            name: 'Multiple Folder Open Events',
            timeline: '{{entityName}} has opened multiple folders',
            chartTitle: 'Folder Open Events (Last 30 Days)',
            desc: '{{entityName}} has opened multiple folders',
            axisYtitle: 'Sum of Folder Open Events (Hourly)'
          },
          high_number_of_successful_file_permission_change: {
            name: 'Multiple File Access Permission Changes',
            timeline: 'Multiple file share permissions have changed by {{entityName}}',
            chartTitle: 'Permission Changes (Last 30 Days)',
            desc: 'Multiple file share permissions have changed by {{entityName}}',
            axisYtitle: 'Sum of Permission Changes (Hourly)'
          },
          high_number_of_successful_file_action_operations: {
            name: 'Multiple File Access Events',
            timeline: '{{entityName}} has successfully accessed files',
            chartTitle: 'File Access Events (Last 30 Days)',
            axisYtitle: 'Sum of File Access Events (Hourly)'
          },
          high_number_of_failed_file_permission_change_attempts: {
            name: 'Multiple Failed File Access Permission Changes',
            timeline: '{{entityName}} has failed multiple attempts to change file access permissions',
            chartTitle: 'Failed Permission Changes (Last 30 Days)',
            desc: '{{entityName}} has failed multiple attempts to change file access permissions',
            axisYtitle: 'Sum of Failed Permission Changes (Hourly)'
          },
          high_number_of_failed_file_action_attempts: {
            name: 'Multiple Failed File Access Events',
            timeline: '{{entityName}} has failed multiple attempts to access a file',
            chartTitle: 'Failed File Access Events (Last 30 Days)',
            desc: '{{entityName}} has failed multiple attempts to access a file',
            axisYtitle: 'Sum of Failed File Access Events (Hourly)'
          },
          high_number_of_distinct_files_opened_attempts: {
            name: 'Multiple File Open Events',
            timeline: '{{entityName}} has opened multiple files',
            chartTitle: 'File Open Events (Last 30 Days)',
            desc: '{{entityName}} has opened multiple files',
            axisYtitle: 'Sum of File Open Events (Hourly)'
          },
          high_number_of_deletions: {
            name: 'Multiple File Delete Events',
            timeline: '{{entityName}} has deleted multiple files',
            chartTitle: 'File Delete Events (Last 30 Days)',
            desc: '{{entityName}} has deleted multiple files',
            axisYtitle: 'Sum of File Delete Events (Hourly)'
          },
          high_number_of_successful_file_rename_operations: {
            name: 'Multiple File Rename Events',
            timeline: '{{entityName}} has renamed multiple files',
            chartTitle: 'File Rename Events (Last 30 Days)',
            desc: '{{entityName}} has renamed multiple files',
            axisYtitle: 'Sum of File Rename Events (Hourly)'
          },
          high_number_of_file_move_operations: {
            name: 'Excessive Amount of Files Moved',
            timeline: '{{entityName}} has moved files',
            chartTitle: 'File Move Events (Last 30 Days)',
            desc: '{{entityName}} has moved files',
            axisYtitle: 'Sum of File Move Events (Hourly)'
          },
          high_number_of_file_move_operations_to_shared_drive: {
            name: 'Excessive Amount of Files Moved to Shared Drive',
            timeline: '{{entityName}} has moved files to a shared drive',
            chartTitle: 'File Move Events (Last 30 Days)',
            desc: '{{entityName}} has moved files to a shared drive',
            axisYtitle: 'Sum of File Move Events (Hourly)'
          },
          // Presidio Active Directory Anomalies
          abnormal_site: {
            name: 'Abnormal Site',
            timeline: '{{entityName}} has attempted to access an abnormal site',
            chartTitle: 'Active Directory Sites (Last 30 days)'
          },
          abnormal_group_membership_sensitive_operation: {
            name: 'Abnormal Group Changes',
            timeline: '{{entityName}} made an abnormal change to group memberships',
            chartTitle: 'Group Changes Baselines'
          },
          abnormal_active_directory_day_time_operation: {
            name: 'Abnormal Active Directory Change Time',
            timeline: '{{entityName}} executed Active Directory activity at an abnormal time',
            chartTitle: 'Active Directory Change Time Baseline'
          },
          admin_changed_his_own_password: {
            name: 'Admin changed his own password',
            timeline: 'Admin {{entityName}}\'s has changed a password',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_enabled: {
            name: 'User account enabled',
            timeline: '{{entityName}}\'s has enabled an account',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_disabled: {
            name: 'User account disabled',
            timeline: '{{entityName}}\'s  has disabled an account',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_unlocked: {
            name: 'User account unlocked',
            timeline: '{{entityName}}\'s has unlocked an account',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_type_changed: {
            name: 'User Account type changed',
            timeline: '{{entityName}}\'s has changed an account\'s type',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_locked: {
            name: 'User Account locked',
            timeline: '{{entityName}}\'s account has been locked out',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_password_never_expires_option_changed: {
            name: 'User Password Never Expires option changed',
            timeline: '{{entityName}}\'s has changed a password policy',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_password_changed: {
            name: 'User Password Changed',
            timeline: '{{entityName}}\'s has changed a password',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_password_reset: {
            name: 'User Password Reset',
            timeline: '{{entityName}}\'s has reset a password',
            chartTitle: 'Sensitive User Status Changes'
          },
          high_number_of_distinct_sites: {
            name: 'Logon Attempts from Multiple Sites',
            timeline: '{{entityName}} has attempted to access from multiple sites',
            chartTitle: 'Active Directory Sites (Last 30 Days)',
            axisYtitle: 'Sum of AD Sites (Hourly)'
          },
          high_number_of_senesitive_group_membership_events: {
            name: 'Multiple Member Additions to Enterprise Critical Groups',
            timeline: '{{entityName}} has successfully executed multiple changes to sensitive groups',
            chartTitle: 'Sensitive Group Changes (Last 30 Days)',
            axisYtitle: 'Sum of Sensitive Group Changes (Hourly)'
          },
          high_number_of_group_membership_events: {
            name: 'Multiple Group Membership Changes',
            timeline: '{{entityName}} has successfully executed multiple changes to groups',
            chartTitle: 'Group Changes (Last 30 Days)',
            axisYtitle: 'Sum of Group Changes (Hourly)'
          },
          high_number_of_successful_object_change_operations: {
            name: 'Multiple Active Directory Object Changes',
            timeline: '{{entityName}} has successfully executed multiple Active Directory changes',
            chartTitle: 'Active Directory Changes (Last 30 Days)',
            axisYtitle: 'Sum of AD Changes (Hourly)'
          },
          high_number_of_successful_user_change_security_sensitive_operations: {
            name: 'Multiple User Account Changes',
            timeline: '{{entityName}} has successfully executed multiple sensitive Active Directory changes',
            chartTitle: 'Sensitive Active Directory Changes (Last\' 30 Days)',
            axisYtitle: 'Sum of Sensitive AD Changes (Hourly)'
          },
          high_number_of_failed_active_directory_events: {
            name: 'Multiple Failed Account Changes',
            timeline: '{{entityName}}  has failed to execute multiple Active Directory changes',
            chartTitle: 'Failed Active Directory Changes (Last 30 Days)',
            axisYtitle: 'Sum of Failed AD Changes (Hourly)'
          },
          // Presidio Authentication(logon) Anomalies
          abnormal_logon_day_time: {
            name: 'Abnormal Logon Time',
            timeline: '{{entityName}} has logged on in an abnormal time',
            chartTitle: 'Logon Time Baseline'
          },
          abnormal_destination_machine: {
            name: 'Abnormal Remote Host',
            timeline: '{{entityName}} has attempted to access remotely an abnormal host',
            chartTitle: 'Remote Hosts Baseline'
          },
          abnormal_object_change_operation: {
            name: 'Abnormal Active Directory Object Change',
            timeline: '{{entityName}} made an abnormal change to an AD attribute',
            chartTitle: 'Active Directory Changes Baselines'
          },
          abnormal_source_machine: {
            name: 'Abnormal Host',
            timeline: '{{entityName}} has attempted to access an abnormal host',
            chartTitle: 'Hosts Baseline'
          },
          high_number_of_failed_authentications: {
            name: 'Multiple Failed Authentications',
            timeline: '{{entityName}} has failed multiple authentication attempts',
            chartTitle: 'Failed Logons (Last 30 Days)',
            axisYtitle: 'Sum of Failed Logons (Hourly)'
          },
          high_number_of_successful_authentications: {
            name: 'Multiple Successful Authentications',
            timeline: '{{entityName}} has logged on multiple times',
            chartTitle: 'Logons (Last 30 Days)',
            axisYtitle: 'Sum of Logons (Hourly)'
          },
          high_number_of_distinct_destination_domains: {
            name: 'Logged into Multiple Domains',
            timeline: '{{entityName}} has attempted to log on to multiple Domains',
            chartTitle: 'Domains (Last 30 Days)',
            axisYtitle: 'Sum of Domains (Hourly)'
          },
          high_number_of_distinct_src_computer_clusters: {
            name: 'Logon Attempts to Multiple Source Hosts',
            timeline: '{{entityName}} has attempted to log on from multiple hosts',
            chartTitle: 'Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          high_number_of_distinct_src_computers: {
            name: 'Logged onto Multiple Hosts',
            timeline: '{{entityName}} has attempted to log on from multiple hosts',
            chartTitle: 'Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          high_number_of_distinct_dst_computer_clusters: {
            name: 'Logged Into Multiple Hosts - Cluster',
            timeline: '{{entityName}} has attempted to log on to multiple hosts',
            chartTitle: 'Remote Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          high_number_of_distinct_dst_computers: {
            name: 'Logged Into Multiple Hosts',
            timeline: '{{entityName}} has attempted to log on to multiple hosts',
            chartTitle: 'Remote Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          // Registry anomalies
          abnormal_process_modified_registry_key_group: {
            name: 'Abnormal Process Modified a Registry Group',
            timeline: '{{value}} has modified {{context.registryKeyGroup}}',
            chartTitle: 'Anomaly for Registry Group'
          },
          abnormal_registry_day_time: {
            name: 'Abnormal Registry Access Time',
            timeline: '{{value}} has accessed a registry at an abnormal time',
            chartTitle: 'Registry Access Time Baseline'
          },
          abnormal_process_modified_a_service_key_registry: {
            name: 'Abnormal Process Modified a Service Key registry',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          // Process anomalies
          abnormal_process_injects_into_lsass: {
            name: 'Abnormal process injected into LSASS',
            timeline: '{{value}} has created a remote thread in the LSASS process',
            chartTitle: 'LSASS Process Injection',
            axisYtitle: ''
          },
          abnormal_reconnaissance_tool_executed: {
            name: 'Abnormal reconnaissance tool executed',
            timeline: '{{value}} has been created',
            chartTitle: 'Reconnaissance Tools',
            axisYtitle: ''
          },
          abnormal_process_executed_a_scripting_tool: {
            name: 'Abnormal Process Executed a Scripting Tool',
            timeline: '{{value}} has created {{contexts.dstProcessFileName}}',
            chartTitle: 'Processes Executing Scripting',
            axisYtitle: ''
          },
          abnormal_process_injects_into_windows_process: {
            name: 'Abnormal process injects into windows process',
            timeline: '{{value}} has created a remote thread in {{contexts.dstProcessFileName}} process',
            chartTitle: 'Processes injected to Windows Processes',
            axisYtitle: ''
          },
          uncommon_process_injects_into_windows_process: {
            name: 'Abnormal Process Injects into Windows Process',
            timeline: '{{value}} was injected into a known windows process',
            chartTitle: 'Processes injected to Windows Processes',
            axisYtitle: ''
          },
          abnormal_process_executeed_a_scripting_tool: {
            name: 'Abnormal process executed a Scripting Tool',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          abnormal_application_triggered_by_scripting_tool: {
            name: 'Abnormal Application Triggered by Scripting Tool',
            timeline: '{{value}} was created by {{contexts.srcProcessFileName}}',
            chartTitle: 'Applications triggered by Scripting',
            axisYtitle: ''
          },
          abnormal_process_opened_by_scripting_tool: {
            name: 'Abnormal Process Opened by Scripting Tool',
            timeline: '{{value}} was opened by {{contexts.srcProcessFileName}}',
            chartTitle: 'Processes Opened by Scripting',
            axisYtitle: ''
          },
          high_number_of_distinct_reconnaissance_tools_executed: {
            name: 'Multiple Distinct Reconnaissance Tools Executed',
            timeline: '{{value}} reconnaissance tools were executed in this hour',
            chartTitle: 'Distinct Reconnaissance Tools (Last 30 Days)',
            axisYtitle: 'Count of Tools'
          },
          high_number_of_reconnaissance_tool_activities_executed: {
            name: 'Multiple Reconnaissance Tool Activities Executed',
            timeline: '{{value}} reconnaissance tool activities were executed in this hour',
            chartTitle: 'Reconnaissance Tool Executions (Last 30 Days)',
            axisYtitle: 'Count of Executions'
          },
          high_number_of_reconnaissance_tools_executed_process: {
            name: 'Process Executed Multiple Times by a Reconnaissance Tool',
            timeline: '{{value}} was executed abnormally multiple times by a reconnaissance tool',
            chartTitle: 'Process Executions by Reconnaissance Tool (Last 30 Days)',
            axisYtitle: 'Count of Process Executions'
          },
          abnormal_process_day_time: {
            name: 'Abnormal Process Execution Time',
            timeline: '{{value}} has accessed a process at an abnormal time',
            chartTitle: 'Process Access Time Baseline'
          },
          user_abnormal_application_triggered_by_scripting_tool: {
            name: 'Abnormal User Application Triggered by Scripting Tool',
            timeline: '{{value}} triggered by scripting tool',
            chartTitle: 'Processes Triggered by Scripting'
          },
          user_abnormal_process_opened_by_scripting_tool: {
            name: 'Abnormal User Application Opened by Scripting Tool',
            timeline: '{{value}} opened by scripting tool',
            chartTitle: 'Processes Opened by Scripting'
          },
          user_abnormal_process_executed_a_scripting_tool: {
            name: 'Abnormal User Application Executed by Scripting Tool',
            timeline: '{{value}} executed by scripting tool.',
            chartTitle: 'Processes Executed by Scripting'
          },
          // logon anomalies
          multiple_failed_authentications: {
            name: 'Multiple Failed Authentications',
            timeline: '{{value}} failed authentications attempts',
            chartTitle: 'Number of Failed Authentications (Last 30 Days)',
            axisYtitle: 'Failed Authentications'
          },
          logged_onto_multiple_domains: {
            name: 'Logged onto Multiple Domains',
            desc: 'A user has attempted to log on to multiple domains.',
            timeline: 'accessed  {{value}} domains',
            chartTitle: 'Number of Domains (Last 30 Days)',
            axisYtitle: 'Domains'
          },
          account_management_change_anomaly: {
            name: 'Account Management Change Anomaly',
            desc: 'An abnormal change to an Active Directory attribute activity has been made.',
            timeline: '{{value}}',
            chartTitle: '',
            axisYtitle: ''
          },
          multiple_account_management_changes: {
            name: 'Multiple Account Management Changes',
            desc: 'A user has sucussfuly executed multiple Active Directory changes',
            timeline: 'Changed {{value}}> AD objects',
            chartTitle: 'Number Of Account Management Events (Last 30 Days)',
            axisYtitle: 'Account Management Events'
          },
          multiple_privileged_group_membership_changes: {
            name: 'Multiple privileged Group Membership Changes',
            desc: 'A user has sucussfuly executed multiple changes to groups',
            timeline: 'Changed {{value}} Group Memberships',
            chartTitle: 'Number Of Group Update Events (Last 30 Days)',
            axisYtitle: 'Number Of Group membership update Events'
          },
          quest_ca_ad_event_time: {
            name: 'Abnormal Active Directory Change Time',
            desc: 'A user executed Active Directory activity at an abnormal time',
            timeline: 'Changed AD objects at {{value}}'
          },
          quest_ca_auth_event_time: {
            name: 'Abnormal Logon Time',
            desc: 'A user executed Active Directory activity at an abnormal time',
            timeline: 'Changed AD objects at {{value}}'
          },
          multiple_failed_file_access_events: {
            name: 'Multiple Failed File Access Events',
            desc: 'A user has failed multiple attempts to access a file',
            timeline: 'Failed to access {{value}} files',
            chartTitle: 'Number Of Files Access Events (Last 30 Days)',
            axisYtitle: 'File Access Events'
          },
          multiple_file_delete_events: {
            name: 'Multiple File Delete Events',
            desc: 'A user has accessed a high number of files',
            timeline: '{{entityName}} has deleted multiple files',
            chartTitle: 'Number Of File Delete or Move Events (Last 30 Days)',
            axisYtitle: 'Delete / Move Events'
          },
          multiple_folder_open_events: {
            name: 'Multiple Folder Open Events',
            desc: 'A user has opened multiple folders',
            timeline: 'Opened {{value}} folders',
            chartTitle: 'Number Of Folder Open Events (Last 30 Days)',
            axisYtitle: 'Folder Open Events'
          },
          normalized_src_machine: {
            name: 'normalized_src_machine.name',
            desc: 'normalized_src_machine.desc',
            timeline: 'normalized_src_machine.timeline'
          }
        }
      },
      filters: {
        title: 'Filters',
        severity: 'Severity',
        feedback: 'Feedback',
        indicators: 'Indicators',
        dateRange: 'Date Range'
      }
    }
  },
  dataFilters: {
    save: 'Save',
    saveAs: 'Save as...',
    reset: 'Reset',
    timeframeOptions: {
      LAST_FIVE_MINUTES: '5 Minutes ago',
      LAST_TEN_MINUTES: '10 Minutes ago',
      LAST_FIFTEEN_MINUTES: '15 Minutes ago',
      LAST_THIRTY_MINUTES: '30 Minutes ago',
      LAST_ONE_HOUR: '1 Hours ago',
      LAST_THREE_HOURS: '3 Hours ago',
      LAST_SIX_HOURS: '6 Hours ago',
      LAST_TWELVE_HOURS: '12 Hours ago',
      LAST_TWENTY_FOUR_HOURS: '24 Hours ago',
      LAST_TWO_DAYS: '2 Days ago',
      LAST_SEVEN_DAYS: '7 Days ago',
      LAST_TWO_WEEKS: '2 Weeks ago',
      LAST_ONE_MONTH: '1 Month ago',
      LAST_THREE_MONTH: '3 Month ago',
      LAST_SIX_MONTH: '6 Month ago',
      IN_LAST_ONE_HOUR: 'Last 1 Hour',
      IN_LAST_THREE_HOURS: 'Last 3 Hours',
      IN_LAST_SIX_HOURS: 'Last 6 Hours',
      IN_LAST_TWELVE_HOURS: 'Last 12 Hours',
      IN_LAST_TWENTY_FOUR_HOURS: 'Last 24 Hours',
      IN_LAST_TWO_DAYS: 'Last 2 Days',
      IN_LAST_SEVEN_DAYS: 'Last 7 Days',
      IN_LAST_TWO_WEEKS: 'Last 2 Weeks',
      IN_LAST_ONE_MONTH: 'Last 1 Month',
      IN_LAST_THREE_MONTH: 'Last 3 Months',
      IN_LAST_SIX_MONTH: 'Last 6 Month'

    },
    startDate: 'Start Date',
    endDate: 'End Date',
    customDateErrorStartAfterEnd: 'The start date and time cannot be the same or later than the end date',
    accessError: 'You do not have the required permission to save the filter.'
  }
};
