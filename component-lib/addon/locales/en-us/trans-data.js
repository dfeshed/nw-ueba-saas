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
      filterPlaceHolder: 'Type to filter the list'
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
    noMatches: 'No matches found',
    enterValue: 'Enter a single value',
    insertFilter: 'Insert new filter',
    query: 'Query with filters',
    open: 'Open in a new tab',
    delete: 'Delete selected filters',
    deleteFilter: 'Delete this filter',
    edit: 'Edit this filter',
    placeholder: 'Enter individual statements consisting of a Meta Key, Operator, and Value (optional)',
    querySelected: 'Query with selected filters',
    querySelectedNewTab: 'Query with selected filters in a new tab',
    expensive: 'Performing this operation might take more time.',
    queryEvents: 'Query Events',
    validationMessages: {
      time: 'You must enter a valid date.',
      text: 'Strings must be quoted with "',
      ipv4: 'You must enter an IPv4 address.',
      ipv6: 'You must enter an IPv6 address.',
      uint8: 'You must enter an 8 bit Integer.',
      uint16: 'You must enter a 16 bit Integer.',
      uint32: 'You must enter a 32 bit Integer.',
      uint64: 'You must enter a 64 bit Integer.',
      float32: 'You must enter a 32 bit Float.',
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
    extractWarning: '<span>You have navigated away before the downloaded files could be attached to the browser tray. Your download will be available <a href="{{url}}" target="_blank">here</a>.</span>',
    extractedFileReady: 'The file has been extracted. Please go to the job queue to download',
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
        mail: 'Email'
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
      pivotToProcessAnalysis: 'Process Analysis',
      pivotToProcessAnalysisTitle: 'Process relationship and event view of source process',
      pivotToHostOverview: 'Pivot to Host Overview',
      pivotToEndpointTitle: 'Applicable for hosts with 4.x Endpoint agents installed, please install the Netwitness Endpoint Thick Client.',
      downloadJson: 'Download JSON',
      downloadLog: 'Download Log',
      downloadXml: 'Download XML',
      headerShowing: 'Showing',
      singleMessageTruncated: ' (This message has been truncated)',
      isDownloading: 'Downloading...',
      rawEndpointHeader: 'Raw Endpoint',
      rawLogHeader: 'Raw Log',
      renderingMore: 'Showing more...',
      renderRemaining: 'Rendering Remaining {{remainingPercent}}%..',
      showRemaining: 'Show Remaining {{remainingPercent}}%'
    },
    packetView: {
      noHexData: 'No HEX data was generated during content reconstruction.',
      isDownloading: 'Downloading...',
      defaultDownloadPCAP: 'Download PCAP',
      downloadPCAP: 'Download PCAP',
      downloadPayload1: 'Download Request Payload',
      downloadPayload2: 'Download Response Payload',
      downloadPayload: 'Download All Payloads',
      payloadToggleLabel: 'Display Payloads Only',
      payloadToggleTitle: 'Removes packet headers & footers from display',
      stylizeBytesLabel: 'Shade Bytes',
      stylizeBytesTitle: 'Enable to help distinguish patterns within the data',
      commonFilePatternLabel: 'Common File Patterns',
      commonFilePatternTitle: 'Enable to highlight common file signature patterns',
      headerMeta: 'Header Meta',
      headerAttribute: 'Header Attribute',
      headerSignature: 'Interesting Bytes',
      headerDisplayLabel: '{{label}} = {{displayValue}}',
      renderingMore: 'Showing more...'
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
      expand: 'Expand View',
      shrink: 'Contract View',
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
  investigate: {
    controls: {
      toggle: 'Show/Hide Events Panel',
      togglePreferences: 'Toggle Investigate Preferences'
    },
    title: 'Investigate',
    loading: 'Loading',
    loadMore: 'Load More',
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
      isAtThreshold: 'The event count reached the query threshold.',
      columnGroups: {
        custom: 'Custom Column Groups',
        customTitle: 'Manage Custom Column Groups in Events View',
        default: 'Default Column Groups',
        searchPlaceholder: 'Type to filter column group'
      },
      download: {
        selected: 'Download',
        all: 'Download All',
        LOG: 'Logs as {{option}}',
        NETWORK: 'Network as {{option}}',
        META: 'Visible Meta as {{option}}',
        options: {
          LOG: 'Log',
          CSV: 'CSV',
          TSV: 'TSV',
          TEXT: 'Text',
          XML: 'XML',
          JSON: 'JSON',
          PCAP: 'PCAP',
          PAYLOAD: 'All Payloads',
          PAYLOAD1: 'Request Payload',
          PAYLOAD2: 'Response Payload'
        }
      },
      error: 'An unexpected error occurred when executing this query.',
      fatalSummaryError: 'The service is unavailable',
      shrink: 'Shrink Events Panel',
      expand: 'Expand Events Panel',
      close: 'Close Events Panel',
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
      summary: 'returned {{eventCount}} event(s) in {{elapsedTime}} second(s).',
      summaryNoTime: 'returned {{eventCount}} event(s) in <1 second.',
      offline: 'This service is offline.',
      allOnline: 'All services are online.',
      oneOffline: '1 service is offline.',
      someOffline: '{{offline}} services are offline.',
      queried: 'Service queried:',
      between: 'Time range:',
      progress: 'Progress:',
      complete: 'Complete',
      nestedElapsedTime: '({{time}}s)',
      nestedNoElapsedTime: '(<1s)',
      slowest: 'This is the slowest service in the query.',
      hasError: 'This query returned an error.',
      hasWarning: 'This query returned a warning.',
      openCloseLabel: 'Click to open or close the query console.',
      disabledLabel: 'The query console will become available once a query is initiated.',
      copy: 'Click to copy query filters:',
      disabledCopy: 'Click to copy will become available when filters are present.',
      warning: 'Warning',
      error: 'Error',
      percentCompleted: '{{progress}} percent completed'
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
    respondNotifications: 'Respond Notifications',
    incidentRulesTitle: 'Incident Rules',
    subscriptions: 'Subscriptions',
    customFeeds: 'Custom Feeds',
    incidentRules: {
      noManagePermissions: 'You do not have permissions to make edits to Incident Rules',
      confirm: 'Are you sure you want to do this?',
      assignee: {
        none: '(Unassigned)'
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
        reorderSuccess: 'You successfully changed the order of the rules',
        reorderFailure: 'There was a problem changing the order of the rules',
        cloneSuccess: 'You successfully cloned the selected rule',
        cloneFailure: 'There was a problem cloning the selected rule',
        createSuccess: 'You successfully created a new rule',
        createFailure: 'There was a problem creating the new rule',
        deleteSuccess: 'You successfully deleted the selected rule',
        deleteFailure: 'There was a problem deleting the selected rule',
        saveSuccess: 'The changes to the rule were successfully saved',
        saveFailure: 'There was a problem saving the changes to the rule',
        duplicateNameFailure: 'There is already another rule with the same name. Please modify the rule name so that it is unique.'
      },
      missingRequiredInfo: 'There is required information missing from the incident rule'
    },
    notifications: {
      settings: 'Respond Notification Settings',
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
        updateFailure: 'There was a problem updating the Respond notification settings'
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
        footer: 'Showing {{count}} of {{total}} | {{selected}} selected',
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
          edit: 'Edit Certificate Status',
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
      eventAnalysis: 'Event Analysis'
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
          createIncidentInstruction: 'An incident will be created from the selected {{alertCount}} alert(s). Please provide a name for the incident.',
          addAlertToIncidentSucceeded: 'You successfully added the selected alerts to {{incidentId}}.',
          addAlertToIncidentFailed: 'There was a problem adding the selected alerts to this incident',
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
        'Risk Scoring Server': 'Risk Scoring Server',
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
        graph: 'View: Graph',
        datasheet: 'View: Datasheet'
      },
      journalTasksRelated: 'Journal, Tasks, and Related',
      search: {
        tab: 'Related',
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
        fileHash: 'FILE HASH',
        tableFILENAME: 'FILE NAME',
        tableLAUNCH: 'LAUNCH ARGUMENT',
        tablePATH: 'PATH',
        tableHASH: 'HASH',
        sourceLabel: 'Source',
        targetLabel: 'Target'
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
    }
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
        'Risk Scoring Server': 'Risk Scoring Server',
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
      defaultPacketFormat: 'Default Packet Format',
      defaultMetaFormat: 'Default Meta Format',
      LOG: 'Download Log',
      CSV: 'Download CSV',
      TSV: 'Download TSV',
      TEXT: 'Download Text',
      XML: 'Download XML',
      JSON: 'Download JSON',
      PCAP: 'Download PCAP',
      PAYLOAD: 'Download All Payloads',
      PAYLOAD1: 'Download Request Payload',
      PAYLOAD2: 'Download Response Payload',
      reconView: {
        FILE: 'File Analysis',
        TEXT: 'Text Analysis',
        PACKET: 'Packet Analysis'
      },
      queryTimeFormat: 'Time format for query',
      DB: 'Database Time',
      WALL: 'Wall Clock Time',
      'DB-tooltip': 'Database time where events are stored',
      'WALL-tooltip': 'Current time with timezone set in user preferences',
      autoDownloadExtractedFiles: 'Download extracted files automatically',
      autoUpdateSummary: 'Update time window automatically',
      'autoUpdateSummary-tooltip': 'Enable this option to automatically update the time range window in the breadcrumb when the service is polled and sends fresh results. When the time range is updated, you can see the Submit Query button activated and click to get the fresh results. To keep the time range window in the breadcrumb synchronized with the current results, do not enable this option.'
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
      invalidPasswordString: 'Can contain alphanumeric or special characters, and a minimum of 3 characters.',
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
      DESTINATIONS_NOT_UNIQUE: 'Warning: Primary Destination and Secondary Destination are same.'
    },
    errorPage: {
      errorTitle: 'Endpoint Server is offline',
      errorDescription: 'Endpoint Server may not be configured properly , please check deployment guide for endpoint server provisioning steps and ensure all the steps are followed.'
    },
    packagerTitle: 'Packager',
    serviceName: 'Service Name*',
    server: 'Endpoint Server*',
    port: 'HTTPS Port*',
    certificateValidation: 'Server Validation',
    certificatePassword: 'Certificate Password*',
    none: 'None',
    fullChain: 'Full Chain',
    thumbprint: 'Certificate Thumbprint',
    reset: 'Reset',
    generateAgent: 'Generate Agent',
    generateLogConfig: 'Generate Log Configuration Only',
    loadExistingLogConfig: 'Load Existing Configuration...',
    description: 'Description',
    title: 'Packager',
    becon: 'Beacon',
    displayName: 'Display Name*',
    driverServiceName: 'Driver Service Name*',
    driverDisplayName: 'Driver Display Name*',
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
    windowsLogCollectionCongfig: 'Windows Log Collection Configuration',
    enableWindowsLogCollection: 'Configure Windows Log Collection',
    configurationName: 'Configuration Name*',
    statusLabel: 'Status',
    enabled: 'Enabled',
    disabled: 'Disabled',
    tooltip: 'Configuration information will be saved but logs will not be forwarded',
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
    channelFilter: 'Channel Filters',
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
    agentConfigNote: 'For a subsequent installation/upgrade, use the same service names.'
  },
  investigateFiles: {
    title: 'Files',
    deleteTitle: 'Confirm Delete',
    noManagePermissions: 'You do not have permissions to make edits to file(s) status',
    certificatePageTitle: 'Certificates',
    certificate: {
      editCertificateStatus: {
        actionNote: 'Applies to all files signed by this certificate.',
        commentLimitError: 'Comment is limited to 900 characters'
      },
      contextMenu: {
        actions: {
          editCertificateStatus: 'Edit Certificate Status'
        }
      }
    },
    button: {
      exportToCSV: 'Export to CSV',
      downloading: 'Downloading',
      save: 'Save',
      reset: 'Reset',
      cancel: 'Cancel',
      viewCertificates: 'View Certificates',
      backToFiles: 'Files',
      closeCertificateView: 'Close certificate view',
      filters: 'Filters',
      close: 'Close File Details'
    },
    message: {
      noResultsMessage: 'No matching files were found',
      noResultsFoundMessage: 'No results found'
    },
    errorPage: {
      serviceDown: 'Endpoint {{serviceType}}Server is offline',
      serviceDownDescription: 'The Endpoint {{serviceType}}Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
    },
    footer: 'Showing {{count}} of {{total}} {{label}} | {{selectedItems}} selected',
    fileStatus: {
      Blacklist: 'Blacklisted',
      Graylist: 'Graylisted',
      Whitelist: 'Whitelisted',
      KnownGood: 'Known Good',
      Neutral: 'Neutral'
    },
    riskScore: {
      label: 'Reset Risk Score',
      confirmMessage: 'Are you sure you want to reset the Risk Score for the selected file(s). All alerts related to this risk score will be deleted and the Risk Score will be reset to 0. Click Help for more information.',
      cancel: 'Cancel',
      reset: 'Reset',
      success: 'The Risk Score has been successfully reset.',
      error: ' The Risk Score cannot be reset.'
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
          successMessage: 'Query deleted successfully.',
          errorMessage: 'Failed to delete the saved search',
          confirmMessage: 'Are you sure you want to delete the selected query?'
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
      reputationStatus: 'Reputation Status',
      score: 'Risk Score',
      machineCount: 'Host Count',
      remediationAction: 'Remediation Action',
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
        status: 'Download Status',
        path: 'Download Path'
      },
      path: 'Path',
      entropy: 'Entropy',
      fileName: 'FileName',
      firstFileName: 'FileName',
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
      title: 'Edit File Status',
      blacklistCategory: 'Category',
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
        osNotToBlock: 'Files on Linux and Mac hosts cannot be blocked.'
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
      commentTitle: 'Comments*',
      saveBtn: 'Save',
      cancelBtn: 'Cancel',
      blacklistCertificate: 'Blacklist Certificate',
      blacklistAllFiles: 'Blacklist all files signed by this certificate',
      contexthubServerOffline: 'An error has occurred. The Contexthub server may be offline or inaccessible. '
    },
    tabs: {
      riskProperties: 'Risk Properties',
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
          reputationStatus: 'Reputation Status',
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
        hookedProcess: 'Process Context'
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
      reputationStatus: 'Reputation Status',
      score: 'Risk Score',
      fileStatus: 'File Status',
      downloaded: 'Downloaded',
      riskScore: 'Risk Score',
      hosts: 'Hosts',
      filePath: 'File Path',
      backToProcesses: 'Processes',
      processDetails: 'Process Details',
      analyzeProcess: 'Analyze Process',
      toolTip: {
        openProcessDetails: 'Open Process details',
        openFileDetails: 'Open File details',
        treeView: 'Switch to Tree view',
        listView: 'Switch to Process view'
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
      }
    },
    anomalies: {
      imageHooks: {
        type: 'Type',
        dllFileName: 'File Name',
        reputationStatus: 'Reputation Status',
        score: 'Risk Score',
        hookFileName: 'Hooked FileName',
        hookedProcess: 'Hooked Process',
        hookedSymbol: 'Hooked Symbol',
        signature: 'Signature',
        title: 'Image Hooks',
        downloaded: 'Downloaded',
        message: {
          noResultsMessage: 'No Image Hooks information was found'
        }
      },
      suspiciousThreads: {
        signature: 'Signature',
        tid: 'Thread ID',
        dllFileName: 'DLL Name',
        reputationStatus: 'Reputation Status',
        score: 'Risk Score',
        teb: 'Thread Environment Block',
        startAddress: 'Start Address',
        process: 'Process',
        downloaded: 'Downloaded',
        message: {
          noResultsMessage: 'No Suspicious Threads information was found'
        }
      },
      kernelHooks: {
        type: 'Type',
        driverFileName: 'Driver Name',
        hookedFileName: 'Hooked FileName',
        reputationStatus: 'Reputation Status',
        score: 'Risk Score',
        signature: 'Signature',
        title: 'Kernel Hooks',
        objectFunction: 'Object Function',
        downloaded: 'Downloaded',
        message: {
          noResultsMessage: 'No Kernel Hooks information was found'
        }
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
    tabs: {
      overview: 'Details',
      process: 'Processes',
      autoruns: 'Autoruns',
      files: 'Files',
      drivers: 'Drivers',
      systemInformation: 'System Information',
      services: 'Services',
      tasks: 'Tasks',
      hostFileEntries: 'Host File Entries',
      mountedPaths: 'Mounted Paths',
      networkShares: 'Network Shares',
      bashHistories: 'Bash History',
      libraries: 'Libraries',
      explore: 'Explore',
      securityProducts: 'Security Products',
      windowsPatches: 'Windows Patches',
      anomalies: 'Anomalies',
      hooks: 'Image Hooks',
      threads: 'Suspicious Threads',
      kernelHooks: 'Kernel Hooks',
      registryDiscrepancies: 'Registry Discrepancies',
      hostDetails: 'Host Details',
      policyDetails: 'Policy Details',
      riskDetails: 'Risk Details'
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
      error: {
        'context.service.timeout': 'Context Hub server is not running or is inaccessible. Check with your Administrator to resolve this issue.',
        'context.error': 'Context service is not reachable. Check your service connectivity.'
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
        export: 'Export to JSON',
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
        rightPanel: 'Show/Hide Detail Right Panel'
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
        }
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
        libraries: 'libraries'
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
      selected: 'selected ({{count}})',
      list: {
        noResultsMessage: 'No Results Found.',
        errorOffline: 'An error has occurred. The Endpoint Server may be offline or inaccessible.'
      },
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
          machineOsType: 'Operating System',
          machineName: 'Hostname',
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
            domainUserQualifiedName: 'User QualifiedName',
            domainUserId: 'User Domain UserID',
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
            type: 'SecurityProducts - Type',
            instance: 'SecurityProducts - Instance',
            displayName: 'SecurityProducts - DisplayName',
            companyName: 'SecurityProducts - CompanyName',
            version: 'SecurityProducts - Version',
            features: 'SecurityProducts - Features'
          },
          networkInterfaces: {
            name: 'NIC Name',
            macAddress: 'NIC MAC Address',
            networkId: 'NetworkInterface - NerworkID',
            ipv4: 'IPv4',
            ipv6: 'IPv6',
            gateway: 'NetworkInterface - Gateway',
            dns: 'NetworkInterface - DNS',
            promiscuous: 'NIC Promiscous'
          }
        },
        riskScore: {
          moduleScore: 'Module Score',
          highestScoringModules: 'Highest Scoring Module'
        },
        machineIdentity: {
          machineName: 'Hostname',
          agentMode: 'Agent Mode',
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
            blockingUpdateTime: 'Agent - Blocking Update Time'
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
            chassisType: 'Hardware - ChassisType',
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
        checkbox: 'Show properties with values only',
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
            domainUserId: 'Domain UserID',
            domainUserOu: 'Domain User OU',
            domainUserCanonicalOu: 'Domain User Canonical OU',
            host: 'Host',
            deviceName: 'DeviceName'
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
            blockingUpdateTime: 'Blocking Update Time'
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
          infoMessage: 'Some of the selected hosts are already being scanned, so a new scan will not be started for them.',
          ecatAgentMessage: 'Some of the selected hosts are 4.4 agents, this feature is not supported for them.',
          migratedHostMessage: 'Some of the selected hosts are not managed by the current server, so a scan will not start for them.',
          quickScan: {
            label: 'Quick Scan (Default)',
            description: 'Performs a quick scan of all executable modules loaded in memory. Takes approximately 10 minutes.'
          }
        },
        success: 'Scan initiated successfully',
        error: 'Scan initiate failed'
      },
      cancelScan: {
        modal: {
          title: 'Stop Scan for {{count}} host(s)',
          description: 'Are you sure, you want to stop scanning the selected host(s)?',
          error1: '*Select at least one host'
        },
        success: 'Stop scan initiated successfully',
        error: 'Stop Scan initiate failed'
      },
      deleteHosts: {
        modal: {
          title: 'Delete {{count}} host(s)',
          message: 'Delete the host if the host scan data is no longer required or the agent is uninstalled. ' +
            'All scan data associated with the host will be deleted. Do you want to continue? '
        },
        success: 'Host(s) are deleted successfully',
        error: 'Host(s) deletion failed'
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
      footer: 'Showing {{count}} of {{total}} {{label}}',
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
      iconTitle: 'Pivot to Event Analysis'
    },
    flash: {
      fileDownloadRequestSent: 'Files are set for download. Check the "Downloaded" column for status.'
    }
  },
  investigateShared: {
    endpoint: {
      message: {
        brokerViewMessage: 'Reached maximum results supported in a broker view.'
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
          emptyContext: 'No Alerts are available which contribute for the risk score calculation of this file.',
          noEvents: 'No Events are available for this file.'
        },
        errorPage: {
          serviceDown: 'Respond Server is offline',
          serviceDownDescription: 'The Respond Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
        }
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
      fileActions: {
        editFileStatus: 'Edit File Status',
        pivotToInvestigate: 'Analyze Events',
        watch: 'Watch',
        downloadToServer: 'Download to Server',
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
        registryEvents: 'Registry Events'
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
        pe: {
          architecture: 'Architecture',
          characteristics: 'Characteristics',
          compileTime: 'Compile Time',
          entryPoint: 'Entry Point',
          importedDlls: 'Imported DLLs',
          sectionNames: 'Section Names',
          subSystem: 'Subsystem',
          entryPointValid: 'Entry Point Valid',
          uncommonSectionFound: 'Uncommon Section Found',
          packerSectionFound: 'Packer Section Found'
        },
        elf: {
          neededLibraries: 'Needed Libraries',
          architecture: 'Architecture',
          entryPoint: 'Entry Point',
          sectionNames: 'Section Names',
          entryPointValid: 'Entry Point Valid',
          uncommonSectionFound: 'Uncommon Section Found',
          packerSectionFound: 'Packer Section Found',
          fileType: 'File Type'
        },
        macho: {
          architecture: 'Architecture',
          entryPoint: 'Entry Point',
          importedDlls: 'Imported DLLs',
          segmentNames: 'Segment Names',
          subSystem: 'Subsystem',
          entryPointValid: 'Entry Point Valid',
          uncommonSectionFound: 'Uncommon Section Found',
          packerSectionFound: 'Packer Section Found',
          uuid: 'UUID',
          fileType: 'File Type'
        },
        importedDlls: {
          parentTitle: 'Imported DLLs({{dllCount}}) and Functions({{functionCount}})',
          childTitle: '{{importedDllName}} - {{functionCount}} Functions'
        }
      }
    }
  },
  investigateProcessAnalysis: {
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
      close: 'Process Details Close'
    },
    tabs: {
      properties: 'Properties',
      events: 'Events ({{count}})'
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
        andOrConditionsPart2: 'of the following criteria are met:'
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
          confirm: 'You are about to permanently delete {{numItems}} policy(ies). Deletion will take immediate effect. Any groups currently applying these policies will no longer do so.',
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
      }
    },
    // policy: {
    //   name: 'Policy Name',
    //   namePlaceholder: 'Enter a unique policy name',
    //   description: 'Policy Description',
    //   descPlaceholder: 'Enter a description',
    //   availableSettings: 'Available Settings',
    //   selectedSettings: 'Selected Settings',
    //   scanSchedule: 'Scan Schedule',
    //   schedOrManScan: 'Scheduled or Manual Scan',
    //   effectiveDate: 'Effective Date',
    //   scanFrequency: 'Scan Frequency',
    //   startTime: 'Start Time',
    //   cpuMax: 'CPU Maximum',
    //   vmMaximum: 'Virtual Machine Maximum',
    //   captureFloatingCode: 'Capture Floating Code',
    //   downloadMbr: 'Download Master Boot Record',
    //   filterSignedHooks: 'Include Hooks With Signed Modules',
    //   advScanSettings: 'Advanced Scan Settings',
    //   requestScanOnRegistration: 'Auto Scan New Systems When Added',
    //   blockingEnabled: 'Blocking',
    //   agentSettings: 'Agent Settings',
    //   agentMode: 'Monitoring Mode',
    //   noMonitoring: 'No Monitoring',
    //   fullMonitoring: 'Full Monitoring',
    //   invasiveActions: 'Invasive Actions',
    //   radioOptionEnabled: 'Enabled',
    //   radioOptionDisabled: 'Disabled',
    //   endpointServerSettings: 'Endpoint Server',
    //   primaryAddress: 'Hostname',
    //   primaryHttpsPort: 'HTTPS Port',
    //   primaryUdpPort: 'UDP Port',
    //   scanStartDateInvalidMsg: 'The scan start date should not be empty',
    //   primaryAddressInvalidMsg: 'The endpoint server host name should not be empty',
    //   portInvalidMsg: 'The port should be between 1 and 65535',
    //   primaryHttpsBeaconInterval: 'HTTPS Beacon Interval',
    //   primaryHttpsBeaconIntervalInvalidMsg: 'The interval should be between 1 minute and 24 hours',
    //   primaryHttpsBeaconInterval_MINUTES: 'Minutes',
    //   primaryHttpsBeaconInterval_HOURS: 'Hours',
    //   primaryUdpBeaconInterval: 'UDP Beacon Interval',
    //   primaryUdpBeaconIntervalInvalidMsg: 'The interval should be between 5 seconds and 10 minutes',
    //   primaryUdpBeaconInterval_SECONDS: 'Seconds',
    //   primaryUdpBeaconInterval_MINUTES: 'Minutes',
    //   scheduleConfiguration: {
    //     scanType: {
    //       title: 'Scheduled or Manual Scan',
    //       options: {
    //         manual: 'Manual',
    //         scheduled: 'Scheduled'
    //       }
    //     },
    //     title: 'Scan Schedule',
    //     save: 'Save',
    //     enable: 'Enable',
    //     effectiveDate: 'Effective Date',
    //     startTime: 'Start Time',
    //     recurrenceInterval: {
    //       title: 'Scan Frequency',
    //       options: {
    //         daily: 'Days',
    //         weekly: 'Weeks',
    //         monthly: 'Months'
    //       },
    //       every: 'Scan every',
    //       on: 'on',
    //       intervalText: {
    //         DAYS: 'day(s)',
    //         WEEKS: 'week(s)',
    //         MONTHS: 'month(s)'
    //       },
    //       week: {
    //         MONDAY: 'M',
    //         TUESDAY: 'T',
    //         WEDNESDAY: 'W',
    //         THURSDAY: 'T',
    //         FRIDAY: 'F',
    //         SATURDAY: 'S',
    //         SUNDAY: 'S'
    //       }
    //     },
    //     runAtTime: 'Start Time',
    //     cpuThrottling: {
    //       title: 'Agent CPU Throttling',
    //       cpuMax: 'CPU Maximum ( % )',
    //       vmMax: 'Virtual Machine Maximum ( % ) '
    //     }
    //   },
    //   saveFailure: 'A problem occurred while trying to save the policy.',
    //   saveSuccess: 'The policy has been saved successfully.'
    // },
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
      nameRequired: 'Group name is required',
      nameExists: 'Group name already exists',
      nameExceedsMaxLength: 'Group name is limited to 256 characters',
      description: 'Group Description',
      descPlaceholder: 'Enter a description',
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
        sourceType: 'Source Type',
        selectedPolicy: 'Selected Policy',
        policyPlaceholder: 'Select a Policy',
        addSourceType: 'Add Another Source Type',
        policyStatus: 'Policy Status',
        policyName: 'POLICY NAME',
        appliedToGroup: 'APPLIED TO GROUP(S)',
        description: 'POLICY DESCRIPTION',
        available: 'Available Policies',
        selected: 'Selected Policy'
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
        nSources: 'Source Count'
      },
      chooseSource: 'Choose Source Type',
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
      // identify-policy-step
      identifyPolicy: 'Identify Policy',
      sourceType: 'Source Type',
      sourceTypePlaceholder: 'Choose a Source Type',
      edrSourceType: 'Agent Endpoint',
      fileLogSourceType: 'Agent Log Files',
      windowsLogSourceType: 'Agent Windows Logs',
      name: 'Policy Name',
      namePlaceholder: 'Enter a unique policy name',
      nameRequired: 'Policy name is required',
      nameExists: 'Policy name already exists',
      nameExceedsMaxLength: 'Policy name is limited to 256 characters',
      description: 'Policy Description',
      descPlaceholder: 'Enter a description',
      descriptionExceedsMaxLength: 'Policy description is limited to 8000 characters',
      // define-policy-step
      definePolicy: 'Define Policy',
      availableSettings: 'Available Settings',
      selectedSettings: 'Selected Settings',
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
        schedOrManScan: 'Run Scheduled Scan',
        scanTypeManual: 'Disabled',
        scanTypeScheduled: 'Enabled',
        effectiveDate: 'Effective Date',
        scanStartDateInvalidMsg: 'The scan start date should not be empty',
        scanFrequency: 'Scan Frequency',
        recurrenceInterval: {
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
        startTime: 'Start Time',
        cpuMax: 'CPU Maximum',
        vmMax: 'Virtual Machine Maximum',
        advScanSettings: 'Scan Settings',
        captureFloatingCode: 'Capture Floating Code',
        downloadMbr: 'Download Master Boot Record',
        filterSignedHooks: 'Include Hooks With Signed Modules',
        requestScanOnRegistration: 'Auto Scan New Systems When Added',
        radioOptionEnabled: 'Enabled',
        radioOptionDisabled: 'Disabled',
        invasiveActions: 'Response Action Settings',
        blockingEnabled: 'Blocking',
        endpointServerSettings: 'Endpoint Server Settings',
        primaryAddress: 'Hostname',
        primaryAddressInvalidMsg: 'The endpoint server host name should not be empty',
        primaryHttpsPort: 'HTTPS Port',
        primaryUdpPort: 'UDP Port',
        portInvalidMsg: 'The port should be between 1 and 65535',
        primaryHttpsBeaconInterval: 'HTTPS Beacon Interval',
        primaryHttpsBeaconIntervalInvalidMsg: 'The interval should be between 1 minute and 24 hours',
        primaryHttpsBeaconInterval_MINUTES: 'Minutes',
        primaryHttpsBeaconInterval_HOURS: 'Hours',
        primaryUdpBeaconInterval: 'UDP Beacon Interval',
        primaryUdpBeaconIntervalInvalidMsg: 'The interval should be between 5 seconds and 10 minutes',
        primaryUdpBeaconInterval_SECONDS: 'Seconds',
        primaryUdpBeaconInterval_MINUTES: 'Minutes',
        agentSettings: 'Agent Settings',
        agentMode: 'Monitoring Mode',
        noMonitoring: 'No Monitoring',
        fullMonitoring: 'Full Monitoring',
        advancedConfig: 'Advanced Configuration',
        customConfigSetting: 'Setting',
        customConfigInvalidMsg: 'The custom setting cannot be empty or greater than 4000 characters'

      },
      // windowsLog policy settings
      windowsLogPolicy: {
        windowsLogSettingsHeader: 'Windows Log Settings',
        radioOptionEnabled: 'Enabled',
        radioOptionDisabled: 'Disabled',
        enabled: 'Windows Log Collection', // 'Status',
        sendTestLog: 'Send Test Log',
        primaryDestination: 'Primary Log Decoder / Log collector*',
        secondaryDestination: 'Secondary Log Decoder / Log collector',
        windowsLogDestinationInvalidMsg: 'The log server host name should not be empty',
        protocol: 'Protocol',
        channelFiltersSettingsHeader: 'Channel Filter Settings'
      }
    },
    policyTypes: {
      edrPolicy: 'Agent Endpoint',
      fileLogPolicy: 'Agent Log Files',
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
        second: ' of the following criteria are met:'
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
      626: 'Group criteria attribute IPv6 address value is invalid'
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
      alerts: 'Alerts'
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
    users: {
      title: 'Users',
      addAllToWatchedList: 'Add All To WatchedList',
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
      all: 'All Alerts',
      alertName: 'Alert Name',
      entityName: 'Entity Name',
      score: 'Score',
      startTime: 'Start Time',
      indicatorCount: 'Indicator Count',
      startFrom: 'Alerts starts from ',
      feedback: 'Feedback',
      indicators: 'Indicators',
      alertNames: {
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
        name: 'Indicator Name',
        anomalyValue: 'Anomaly Value',
        numOfEvents: 'Event Count',
        startDate: 'Start Date',
        indicatorNames: {
          // Presidio File Anoamlies
          abnormal_file_action_operation_type: {
            name: 'Abnormal File Access Event',
            timeline: '{{username}} has accessed a file abnormally.',
            chartTitle: 'File Access Events Baselines'
          },
          abnormal_file_permision_change_operation_type: {
            name: 'Abnormal File Access Permission Change',
            timeline: 'Multiple Share Permissions have changed by {{username}}',
            chartTitle: 'File Access Permission Changes Baseline'
          },
          abnormal_event_day_time: {
            name: 'Abnormal File Access Time',
            timeline: '{{username}} has accessed a file at an abnormal time',
            chartTitle: 'File Access Time Baseline'
          },
          high_number_of_distinct_folders_opened_attempts: {
            name: 'Multiple Folder Open Events',
            timeline: '{{username}} has opened multiple folders',
            chartTitle: 'Folder Open Events (Last 30 Days)',
            desc: '{{username}} has opened multiple folders',
            axisYtitle: 'Sum of Folder Open Events (Hourly)'
          },
          high_number_of_successful_file_permission_change: {
            name: 'Multiple File Access Permission Changes',
            timeline: 'Multiple file share permissions have changed by {{username}}',
            chartTitle: 'Permission Changes (Last 30 Days)',
            desc: 'Multiple file share permissions have changed by {{username}}',
            axisYtitle: 'Sum of Permission Changes (Hourly)'
          },
          high_number_of_successful_file_action_operations: {
            name: 'Multiple File Access Events',
            timeline: '{{username}} has successfully accessed files',
            chartTitle: 'File Access Events (Last 30 Days)',
            axisYtitle: 'Sum of File Access Events (Hourly)'
          },
          high_number_of_failed_file_permission_change_attempts: {
            name: 'Multiple Failed File Access Permission Changes',
            timeline: '{{username}} has failed multiple attempts to change file access permissions',
            chartTitle: 'Failed Permission Changes (Last 30 Days)',
            desc: '{{username}} has failed multiple attempts to change file access permissions',
            axisYtitle: 'Sum of Failed Permission Changes (Hourly)'
          },
          high_number_of_failed_file_action_attempts: {
            name: 'Multiple Failed File Access Events',
            timeline: '{{username}} has failed multiple attempts to access a file',
            chartTitle: 'Failed File Access Events (Last 30 Days)',
            desc: '{{username}} has failed multiple attempts to access a file',
            axisYtitle: 'Sum of Failed File Access Events (Hourly)'
          },
          high_number_of_distinct_files_opened_attempts: {
            name: 'Multiple File Open Events',
            timeline: '{{username}} has opened multiple files',
            chartTitle: 'File Open Events (Last 30 Days)',
            desc: '{{username}} has opened multiple files',
            axisYtitle: 'Sum of File Open Events (Hourly)'
          },
          high_number_of_deletions: {
            name: 'Multiple File Delete Events',
            timeline: '{{username}} has deleted multiple files',
            chartTitle: 'File Delete Events (Last 30 Days)',
            desc: '{{username}} has deleted multiple files',
            axisYtitle: 'Sum of File Delete Events (Hourly)'
          },
          high_number_of_successful_file_rename_operations: {
            name: 'Multiple File Rename Events',
            timeline: '{{username}} has renamed multiple files',
            chartTitle: 'File Rename Events (Last 30 Days)',
            desc: '{{username}} has renamed multiple files',
            axisYtitle: 'Sum of File Rename Events (Hourly)'
          },
          high_number_of_file_move_operations: {
            name: 'Excessive Amount of Files Moved',
            timeline: '{{username}} has moved files',
            chartTitle: 'File Move Events (Last 30 Days)',
            desc: '{{username}} has moved files',
            axisYtitle: 'Sum of File Move Events (Hourly)'
          },
          high_number_of_file_move_operations_to_shared_drive: {
            name: 'Excessive Amount of Files Moved to Shared Drive',
            timeline: '{{username}} has moved files to a shared drive',
            chartTitle: 'File Move Events (Last 30 Days)',
            desc: '{{username}} has moved files to a shared drive',
            axisYtitle: 'Sum of File Move Events (Hourly)'
          },
          // Presidio Active Directory Anomalies
          abnormal_site: {
            name: 'Abnormal Site',
            timeline: '{{username}} has attempted to access an abnormal site',
            chartTitle: 'Active Directory Sites (Last 30 days)'
          },
          abnormal_group_membership_sensitive_operation: {
            name: 'Abnormal Group Changes',
            timeline: '{{username}} made an abnormal change to group memberships',
            chartTitle: 'Group Changes Baselines'
          },
          abnormal_active_directory_day_time_operation: {
            name: 'Abnormal Active Directory Change Time',
            timeline: '{{username}} executed Active Directory activity at an abnormal time',
            chartTitle: 'Active Directory Change Time Baseline'
          },
          admin_changed_his_own_password: {
            name: 'Admin changed his own password',
            timeline: 'Admin {{username}}\'s has changed a password',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_enabled: {
            name: 'User account enabled',
            timeline: '{{username}}\'s has enabled an account',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_disabled: {
            name: 'User account disabled',
            timeline: '{{username}}\'s  has disabled an account',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_unlocked: {
            name: 'User account unlocked',
            timeline: '{{username}}\'s has unlocked an account',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_type_changed: {
            name: 'User Account type changed',
            timeline: '{{username}}\'s has changed an account\'s type',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_account_locked: {
            name: 'User Account locked',
            timeline: '{{username}}\'s account has been locked out',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_password_never_expires_option_changed: {
            name: 'User Password Never Expires option changed',
            timeline: '{{username}}\'s has changed a password policy',
            chartTitle: 'Sensitive User Status Changes'
          },
          user_password_changed: {
            name: 'User password changed',
            timeline: '{{username}}\'s has changed a password',
            chartTitle: 'Sensitive User Status Changes'
          },
          high_number_of_distinct_sites: {
            name: 'Logon Attempts from Multiple Sites',
            timeline: '{{username}} has attempted to access from multiple sites',
            chartTitle: 'Active Directory Sites (Last 30 Days)',
            axisYtitle: 'Sum of AD Sites (Hourly)'
          },
          high_number_of_senesitive_group_membership_events: {
            name: 'Multiple Member Additions to Enterprise Critical Groups',
            timeline: '{{username}} has successfully executed multiple changes to sensitive groups',
            chartTitle: 'Sensitive Group Changes (Last 30 Days)',
            axisYtitle: 'Sum of Sensitive Group Changes (Hourly)'
          },
          high_number_of_group_membership_events: {
            name: 'Multiple Group Membership Changes',
            timeline: '{{username}} has successfully executed multiple changes to groups',
            chartTitle: 'Group Changes (Last 30 Days)',
            axisYtitle: 'Sum of Group Changes (Hourly)'
          },
          high_number_of_successful_object_change_operations: {
            name: 'Multiple Active Directory Object Changes',
            timeline: '{{username}} has successfully executed multiple Active Directory changes',
            chartTitle: 'Active Directory Changes (Last 30 Days)',
            axisYtitle: 'Sum of AD Changes (Hourly)'
          },
          high_number_of_successful_user_change_security_sensitive_operations: {
            name: 'Multiple User Account Changes',
            timeline: '{{username}} has successfully executed multiple sensitive Active Directory changes',
            chartTitle: 'Sensitive Active Directory Changes (Last\' 30 Days)',
            axisYtitle: 'Sum of Sensitive AD Changes (Hourly)'
          },
          high_number_of_failed_active_directory_events: {
            name: 'Multiple Failed Account Changes',
            timeline: '{{username}}  has failed to execute multiple Active Directory changes',
            chartTitle: 'Failed Active Directory Changes (Last 30 Days)',
            axisYtitle: 'Sum of Failed AD Changes (Hourly)'
          },
          // Presidio Authentication(logon) Anomalies
          abnormal_logon_day_time: {
            name: 'Abnormal Logon Time',
            timeline: '{{username}} has logged on in an abnormal time',
            chartTitle: 'Logon Time Baseline'
          },
          abnormal_destination_machine: {
            name: 'Abnormal Remote Host',
            timeline: '{{username}} has attempted to access remotely an abnormal host',
            chartTitle: 'Remote Hosts Baseline'
          },
          abnormal_source_machine: {
            name: 'Abnormal Host',
            timeline: '{{username}} has attempted to access an abnormal host',
            chartTitle: 'Hosts Baseline'
          },
          high_number_of_failed_authentications: {
            name: 'Multiple Failed Authentications',
            timeline: '{{username}} has failed multiple authentication attempts',
            chartTitle: 'Failed Logons (Last 30 Days)',
            axisYtitle: 'Sum of Failed Logons (Hourly)'
          },
          high_number_of_successful_authentications: {
            name: 'Multiple Successful Authentications',
            timeline: '{{username}} has logged on multiple times',
            chartTitle: 'Logons (Last 30 Days)',
            axisYtitle: 'Sum of Logons (Hourly)'
          },
          high_number_of_distinct_destination_domains: {
            name: 'Logged into Multiple Domains',
            timeline: '{{username}} has attempted to log on to multiple Domains',
            chartTitle: 'Domains (Last 30 Days)',
            axisYtitle: 'Sum of Domains (Hourly)'
          },
          high_number_of_distinct_src_computer_clusters: {
            name: 'Logon Attempts to Multiple Source Hosts',
            timeline: '{{username}} has attempted to log on from multiple hosts',
            chartTitle: 'Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          high_number_of_distinct_src_computers: {
            name: 'Logged onto Multiple Hosts',
            timeline: '{{username}} has attempted to log on from multiple hosts',
            chartTitle: 'Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          high_number_of_distinct_dst_computer_clusters: {
            name: 'Logged Into Multiple Hosts - Cluster',
            timeline: '{{username}} has attempted to log on to multiple hosts',
            chartTitle: 'Remote Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          high_number_of_distinct_dst_computers: {
            name: 'Logged Into Multiple Hosts',
            timeline: '{{username}} has attempted to log on to multiple hosts',
            chartTitle: 'Remote Hosts (Last 30 Days)',
            axisYtitle: 'Sum of Hosts (Hourly)'
          },
          // Registry anomalies
          abnormal_process_modified_a_service_key_registry: {
            name: 'Abnormal Process Modified a Service Key registry',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          // Process anomalies
          abnormal_process_injects_into_lsass: {
            name: 'Abnormal process injected into LSASS',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          abnormal_reconnaissance_tool_executed: {
            name: 'Abnormal reconnaissance tool executed',
            timeline: '',
            chartTitle: '',
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
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          abnormal_process_opened_by_scripting_tool: {
            name: 'Abnormal Process Opened by Scripting Tool',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          high_number_of_distinct_reconnaissance_tools_executed: {
            name: 'Multiple Distinct Reconnaissance Tools Executed',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          high_number_of_reconnaissance_tool_activities_executed: {
            name: 'Multiple Reconnaissance Tool Activities Executed',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
          },
          high_number_of_reconnaissance_tools_executed_process: {
            name: 'Process Executed Multiple Times by a Reconnaissance Tool',
            timeline: '',
            chartTitle: '',
            axisYtitle: ''
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
            timeline: '{{username}} has deleted multiple files',
            chartTitle: 'Number Of File Delete or Move Events (Last 30 Days)',
            axisYtitle: 'Delete / Move Events'
          },
          multiple_folder_open_events: {
            name: 'Multiple Folder Open Events',
            desc: 'A user has opened multiple folders',
            timeline: 'Opened {{value}} folders',
            chartTitle: 'Number Of Folder Open Events (Last 30 Days)',
            axisYtitle: 'Folder Open Events'
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
      LAST_SIX_MONTH: '6 Month ago'
    },
    startDate: 'Start Date',
    endDate: 'End Date',
    customDateErrorStartAfterEnd: 'The start date and time cannot be the same or later than the end date',
    accessError: 'You do not have the required permission to save the filter.'
  }
};
