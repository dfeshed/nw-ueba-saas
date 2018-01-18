export default {
  appTitle: 'ja_NetWitness Suite',
  pageTitle: 'ja_{{section}} - NetWitness Suite',
  empty: '',
  languages: {
    en: 'ja_English',
    'en-us': 'ja_English',
    ja: 'ja_Japanese'
  },
  passwordPolicy: {
    passwordPolicyRequestError: 'ja_There was an issue retrieving your password policy.',
    passwordPolicyMinChars: 'ja_Must be at least {{passwordPolicyMinChars}} characters',
    passwordPolicyMinNumericChars: 'ja_Must contain at least {{passwordPolicyMinNumericChars}} number(s) (0 through 9)',
    passwordPolicyMinUpperChars: 'ja_Must have at least {{passwordPolicyMinUpperChars}} uppercase character(s)',
    passwordPolicyMinLowerChars: 'ja_Must have at least {{passwordPolicyMinLowerChars}} lowercase character(s)',
    passwordPolicyMinNonLatinChars: 'ja_Must contain at least {{passwordPolicyMinNonLatinChars}} Unicode alphabetic character(s) that are not uppercase or lowercase',
    passwordPolicyMinSpecialChars: 'ja_Must contain at least {{passwordPolicyMinSpecialChars}} non-alphanumeric character(s): (~!@#$%^&*_-+=`|(){}[]:;"\'<>,.?/)',
    passwordPolicyCannotIncludeId: 'ja_Your password cannot contain your username'
  },
  forms: {
    cancel: 'ja_Cancel',
    submit: 'ja_Submit',
    reset: 'ja_Reset',
    apply: 'ja_Apply',
    ok: 'ja_OK',
    delete: 'ja_Delete',
    save: 'ja_Save',
    yes: 'ja_Yes',
    no: 'ja_No'
  },
  tables: {
    noResults: 'ja_No Results',
    columnChooser: {
      filterPlaceHolder: 'ja_Type to filter the list'
    }
  },
  login: {
    username: 'ja_Username',
    password: 'ja_Password',
    login: 'ja_Login',
    loggingIn: 'ja_Logging In',
    logout: 'ja_Logout',
    oldPassword: 'ja_Old Password',
    newPassword: 'ja_New Password',
    confirmPassword: 'ja_Confirm Password',
    lostPasswordLink: 'ja_Lost Password?',
    passwordMismatch: 'ja_Password confirmation does not match.',
    passwordNoChange: 'ja_New password cannot match your old password.',
    passwordChangeFailed: 'ja_There was an issue while saving your password change. Please try again.',
    genericError: 'ja_Authentication error. Please try again.',
    communicationError: 'ja_The server could not be reached. Please contact your system admin.',
    userLocked: 'ja_User account is locked',
    userDisabled: 'ja_User account is disabled',
    userExpired: 'ja_User account has expired',
    changePasswordLink: 'ja_Change my password',
    changePasswordSoon: 'ja_Please note that your password to the RSA NetWitness Server will expire in {{daysRemaining}} day(s). We encourage you to change the password before it expires. To change your password, click the preferences button on the top right of the application window.',
    changePasswordToday: 'ja_Please note that your password to the RSA NetWitness Server will expire today. We encourage you to change the password before it expires. To change your password, click the preferences button on the top right of the application window.',
    lostPassword: {
      title: 'ja_Lost Password Recovery',
      description: 'ja_Please submit your username.'
    },
    thankYou: {
      title: 'ja_Thank You!',
      description: 'ja_A password reset has been sent to the registered user\'s email account.',
      back: 'ja_Return to Login'
    },
    eula: {
      title: 'ja_End User License Agreement',
      agree: 'ja_Agree'
    },
    forcePassword: {
      warning: 'ja_You will need to create a new password before you can log in.',
      changePassword: 'ja_Change Password'
    }
  },
  userPreferences: {
    preferences: 'ja_User Preferences',
    personalize: 'ja_Personalize your experience',
    signOut: 'ja_Sign Out',
    version: 'ja_Version',
    username: 'ja_Username',
    email: 'ja_Email',
    language: 'ja_Language',
    timeZone: 'ja_Time Zone',
    dateFormatError: 'ja_There was an error attempting to save your Date Format selection. Please try again. If this issue persists, please contact your system admin.',
    landingPageError: 'ja_There was an error attempting to save your Default Landing Page selection. Please try again. If this issue persists, please contact your system admin.',
    defaultInvestigatePageError: 'ja_There was an error attempting to save your Default Investigate View selection. Please try again. If this issue persists, please contact your system admin.',
    timeFormatError: 'ja_There was an error attempting to save your Time Format selection. Please try again. If this issue persists, please contact your system admin.',
    timezoneError: 'ja_There was an error attempting to save your Time Zone selection. Please try again. If this issue persists, please contact your system admin.',
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
    theme: {
      title: 'ja_Theme',
      dark: 'ja_Dark',
      light: 'ja_Light',
      error: 'ja_There was an error attempting to save your Theme selection. Please try again. If this issue persists, please contact your system admin.'
    },
    defaultLandingPage: {
      label: 'ja_Default Landing Page',
      monitor: 'ja_Monitor',
      investigate: 'ja_Investigate',
      investigateClassic: 'ja_Investigate',
      dashboard: 'ja_Monitor',
      live: 'ja_Configure',
      respond: 'ja_Respond',
      admin: 'ja_Admin'
    },
    defaultInvestigatePage: {
      label: 'ja_Default Investigate View',
      events: 'ja_Events',
      eventAnalysis: 'ja_Event Analysis',
      malware: 'ja_Malware Analysis',
      navigate: 'ja_Navigate',
      hosts: 'ja_Hosts',
      files: 'ja_Files'
    }
  },
  queryBuilder: {
    enterValue: 'ja_Enter a single value',
    noMatches: 'ja_No matches found',
    insertFilter: 'ja_Insert new filter',
    query: 'ja_Query with filters',
    open: 'ja_Open in a new tab',
    delete: 'ja_Delete selected filters',
    deleteFilter: 'ja_Delete this filter',
    edit: 'ja_Edit this filter',
    placeholder: 'ja_Enter a Meta Key and Value (optional)',
    querySelected: 'ja_Query with selected filters',
    querySelectedNewTab: 'ja_Query with selected filters in a new tab',
    expensive: 'ja_Performing this operation might take more time.',
    validationMessages: {
      time: 'ja_You must enter a valid date.',
      text: 'ja_Strings must be quoted with "',
      ipv4: 'ja_You must enter an IPv4 address.',
      ipv6: 'ja_You must enter an IPv6 address.',
      uint8: 'ja_You must enter an 8 bit Integer.',
      uint16: 'ja_You must enter a 16 bit Integer.',
      uint32: 'ja_You must enter a 32 bit Integer.',
      float32: 'ja_You must enter a 32 bit Float.'
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
    extractWarning: '<span>ja_You have navigated away before the downloaded files could be attached to the browser tray. Your download will be available <a href="{{url}}" target="_blank">here</a>.</span>',
    extractedFileReady: 'ja_The file has been extracted. Please go to the job queue to download',
    titleBar: {
      titles: {
        endpoint: 'ja_Endpoint Event Details',
        network: 'ja_Network Event Details',
        log: 'ja_Log Event Details'
      },
      views: {
        text: 'ja_Text Analysis',
        packet: 'ja_Packet Analysis',
        file: 'ja_File Analysis',
        web: 'ja_Web',
        mail: 'ja_Email'
      }
    },
    meta: {
      scroller: {
        of: 'ja_of',
        results: 'ja_results'
      }
    },
    textView: {
      compressToggleLabel: 'ja_Display Compressed Payloads',
      compressToggleTitle: 'ja_Display HTTP payloads as compressed or not',
      downloadCsv: 'ja_Download CSV',
      downloadEndpointEvent: 'ja_Download Endpoint Event',
      pivotToEndpoint: 'ja_Pivot to Endpoint',
      pivotToEndpointTitle: 'ja_To Pivot to Endpoint, please install the NetWitness Endpoint client for Windows.',
      downloadJson: 'ja_Download JSON',
      downloadLog: 'ja_Download Log',
      downloadXml: 'ja_Download XML',
      headerShowing: 'ja_Showing',
      isDownloading: 'ja_Downloading...',
      maxPacketsReached: 'ja_Rendered <span class="darker">{{maxPacketCount}} (Max)</span> of <span class="darker">{{packetTotal}}</span> packets',
      maxPacketsReachedTooltip: 'ja_The limit of {{maxPacketCount}} packets to render a single event has been reached; no additional packets will be rendered for this event. The packet threshold ensures the best rendering experience.',
      rawEndpointHeader: 'ja_Raw Endpoint',
      rawLogHeader: 'ja_Raw Log',
      renderingMore: 'ja_Showing more...',
      renderRemaining: 'ja_Rendering Remaining {{remainingPercent}}%..',
      showRemaining: 'ja_Show Remaining {{remainingPercent}}%'
    },
    packetView: {
      noHexData: 'ja_No HEX data was generated during content reconstruction.',
      isDownloading: 'ja_Downloading...',
      defaultDownloadPCAP: 'ja_Download PCAP',
      downloadPCAP: 'ja_Download PCAP',
      downloadPayload1: 'ja_Download Request Payload',
      downloadPayload2: 'ja_Download Response Payload',
      downloadPayload: 'ja_Download All Payloads',
      payloadToggleLabel: 'ja_Display Payloads Only',
      payloadToggleTitle: 'ja_Removes packet headers & footers from display',
      stylizeBytesLabel: 'ja_Shade Bytes',
      stylizeBytesTitle: 'ja_Enable to help distinguish patterns within the data',
      commonFilePatternLabel: 'ja_Common File Patterns',
      commonFilePatternTitle: 'ja_Enable to highlight common file signature patterns',
      headerMeta: 'ja_Header Meta',
      headerAttribute: 'ja_Header Attribute',
      headerSignature: 'ja_Interesting Bytes',
      headerDisplayLabel: 'ja_{{label}} = {{displayValue}}',
      renderingMore: 'ja_Showing more...'
    },
    reconPager: {
      packetPagnationPageFirst: 'ja_First',
      packetPagnationPagePrevious: 'ja_Previous',
      packetPagnationPageNext: 'ja_Next',
      packetPagnationPageLast: 'ja_Last',
      packetsPerPageText: 'ja_Packets Per Page'
    },
    fileView: {
      downloadFile: 'ja_Download File',
      downloadFiles: 'ja_Download Files ({{fileCount}})',
      isDownloading: 'ja_Downloading...',
      downloadWarning: 'ja_Warning: Files contain the original raw unsecured content. Use caution when opening or downloading files; they may contain malicious data.'
    },
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
      missingRecon: 'ja_This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.',
      noTextContentData: 'ja_No text data was generated during content reconstruction. This could mean that the event data was corrupt or invalid. Check the other reconstruction views.',
      permissionError: 'ja_Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions.'
    },
    fatalError: {
      115: 'ja_Session is unavailable for viewing.',
      124: 'ja_Invalid session ID: {{eventId}}',
      11: 'ja_The session id is too large to be handled: {{eventId}}',
      permissions: 'ja_You do not have the required permissions to view this content.'
    },
    toggles: {
      header: 'ja_Show/Hide Header',
      request: 'ja_Show/Hide Request',
      response: 'ja_Show/Hide Response',
      topBottom: 'ja_Top/Bottom View',
      sideBySide: 'ja_Side by Side View',
      meta: 'ja_Show/Hide Meta',
      expand: 'ja_Expand View',
      shrink: 'ja_Contract View',
      close: 'ja_Close Reconstruction'
    },
    eventHeader: {
      nwService: 'ja_NW Service',
      sessionId: 'ja_Session ID',
      type: 'ja_Type',
      source: 'ja_Source IP:PORT',
      destination: 'ja_Destination IP:PORT',
      service: 'ja_Service',
      firstPacketTime: 'ja_First Packet Time',
      lastPacketTime: 'ja_Last Packet Time',
      packetSize: 'ja_Calculated Packet Size',
      payloadSize: 'ja_Calculated Payload Size',
      packetCount: 'ja_Calculated Packet Count',
      packetSizeTooltip: 'ja_The calculated packet size in the summary header may be different than the packet size in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      payloadSizeTooltip: 'ja_The calculated payload size in the summary header may be different than the payload size in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      packetCountTooltip: 'ja_The calculated packet count in the summary header may be different than the packet count in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      deviceIp: 'ja_Device IP',
      deviceType: 'ja_Device Type',
      deviceClass: 'ja_Device Class',
      eventCategory: 'ja_Event Category',
      nweCategory: 'ja_NWE Category',
      collectionTime: 'ja_Collection Time',
      eventTime: 'ja_Event Time',
      nweEventTime: 'ja_Event Time',
      nweMachineName: 'ja_Machine Name',
      nweMachineIp: 'ja_Machine IP',
      nweMachineUsername: 'ja_Machine Username',
      nweMachineIiocScore: 'ja_Machine IIOC Score',
      nweEventSourceFilename: 'ja_Event Source Filename',
      nweEventSourcePath: 'ja_Event Source Path',
      nweEventDestinationFilename: 'ja_Event Destination Filename',
      nweEventDestinationPath: 'ja_Event Destination Path',
      nweFileFilename: 'ja_Filename',
      nweFileIiocScore: 'ja_File IIOC Score',
      nweProcessFilename: 'ja_Process Filename',
      nweProcessParentFilename: 'ja_Parent Filename',
      nweProcessPath: 'ja_Process Path',
      nweDllFilename: 'ja_DLL Filename',
      nweDllPath: 'ja_DLL Path',
      nweDllProcessFilename: 'ja_Process Filename',
      nweAutorunFilename: 'ja_Autorun Filename',
      nweAutorunPath: 'ja_Autorun Path',
      nweServiceDisplayName: 'ja_Service Display Name',
      nweServiceFilename: 'ja_Service Filename',
      nweServicePath: 'ja_Service Path',
      nweTaskName: 'ja_Task Name',
      nweTaskPath: 'ja_Task Path',
      nweNetworkFilename: 'ja_Network Filename',
      nweNetworkPath: 'ja_Network Path',
      nweNetworkProcessFilename: 'ja_Network Process Filename',
      nweNetworkProcessPath: 'ja_Network Process Path',
      nweNetworkRemoteAddress: 'ja_Network Remote Address'
    },
    contextmenu: {
      copy: 'ja_Copy',
      externalLinks: 'ja_External',
      livelookup: 'ja_Live Lookup',
      endpointIoc: 'ja_Endpoint IOC Lookup',
      applyDrill: 'ja_Apply Drill in New Tab',
      applyNEDrill: 'ja_Apply !EQUALS Drill in New Tab',
      refocus: 'ja_Refocus Investigation in New Tab',
      hostslookup: 'ja_Hosts Lookup',
      external: {
        google: 'ja_Google',
        virustotal: 'ja_VirusTotal PDNS',
        sansiphistory: 'ja_SANS IP History',
        centralops: 'ja_CentralOps Whois for IPs and Hostnames',
        robtexipsearch: 'ja_Robtex IP Search',
        ipvoid: 'ja_IPVoid',
        urlvoid: 'ja_URLVoid',
        threatexpert: 'ja_ThreatExpert Search'
      }
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
    controls: {
      toggle: 'ja_Show/Hide Events Panel',
      togglePreferences: 'ja_Toggle Investigate Preferences'
    },
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
      endpoint: 'ja_Endpoint',
      network: 'ja_Network',
      log: 'ja_Log',
      correlation: 'ja_Correlation',
      undefined: 'ja_Unknown'
    },
    empty: {
      title: 'ja_No events found.',
      description: 'ja_Your filter criteria did not match any records.'
    },
    error: {
      title: 'ja_Unable to load data.',
      description: 'ja_An unexpected error occurred when attempting to fetch the data records.'
    },
    invalidQueryError: {
      title: 'ja_No events found.',
      description: 'ja_Your filter criteria is invalid. Examine query for syntax errors.'
    },
    meta: {
      title: 'ja_Meta',
      clickToOpen: 'ja_Click to open'
    },
    events: {
      title: 'ja_All Events',
      columnGroups: {
        custom: 'ja_Custom Column Group',
        default: 'ja_Default Column Group',
        searchPlaceholder: 'ja_Type to filter column group'
      },
      error: 'ja_An unexpected error occurred when executing this query.',
      shrink: 'ja_Shrink Events Panel',
      expand: 'ja_Expand Events Panel',
      close: 'ja_Close Events Panel'
    },
    services: {
      loading: 'ja_Loading list of available services',
      noData: 'ja_The selected service does not have any data',
      coreServiceNotUpdated: 'ja_Event Analysis requires all core services to be NetWitness 11.1. Connecting prior versions of services to the 11.1 NetWitness Server results in limited functionality (see \"Investigate in Mixed Mode\" in the Physical Host Upgrade Guide).',
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
  configure: {
    title: 'ja_Configure',
    liveContent: 'ja_Live Content',
    esaRules: 'ja_ESA Rules',
    respondNotifications: 'ja_Respond Notifications',
    incidentRulesTitle: 'ja_Incident Rules',
    subscriptions: 'ja_Subscriptions',
    customFeeds: 'ja_Custom Feeds',
    incidentRules: {
      confirm: 'ja_Are you sure you want to do this?',
      assignee: {
        none: 'ja_(Unassigned)'
      },
      priority: {
        LOW: 'ja_Low',
        MEDIUM: 'ja_Medium',
        HIGH: 'ja_High',
        CRITICAL: 'ja_Critical'
      },
      action: 'ja_Action',
      actionMessage: 'ja_Choose the action taken if the rule matches an alert',
      error: 'ja_There was a problem loading incident rules',
      noResults: 'ja_No incident rules were found',
      createRule: 'ja_Create Rule',
      cloneRule: 'ja_Clone',
      deleteRule: 'ja_Delete',
      select: 'ja_Select',
      order: 'ja_Order',
      enabled: 'ja_Enabled',
      name: 'ja_Name',
      namePlaceholder: 'ja_Provide a unique name for the rule',
      ruleNameRequired: 'ja_You must provide a rule name',
      description: 'ja_Description',
      descriptionPlaceholder: 'ja_Provide a description of the rule',
      lastMatched: 'ja_Last Matched',
      alertsMatchedCount: 'ja_Matched Alerts',
      incidentsCreatedCount: 'ja_Incidents',
      matchConditions: 'ja_Match Conditions',
      queryMode: 'ja_Query Mode',
      queryModes: {
        RULE_BUILDER: 'ja_Rule Builder',
        ADVANCED: 'ja_Advanced'
      },
      queryBuilderQuery: 'ja_Query Builder',
      advancedQuery: 'ja_Advanced',
      advancedQueryRequired: 'ja_Advanced Query cannot be empty',
      groupingOptions: 'ja_Grouping Options',
      groupBy: 'ja_Group By',
      groupByPlaceholder: 'ja_Choose a group-by field (required)',
      groupByError: 'ja_A minimum of one group-by field is required, and a maximum of two is allowed',
      timeWindow: 'ja_Time Window',
      incidentOptions: 'ja_Incident Options',
      incidentTitle: 'ja_Title',
      incidentTitleRequired: 'ja_You must provide a title for Incidents created from this rule',
      incidentTitlePlaceholder: 'ja_Enter a title for the incident created by this rule',
      incidentTitleHelp: 'ja_The Title template is used to create the Incident title. For ex- If rule has name Rule-01 and groupBy field is Severity, groupBy value is 50 and template is ${ruleName} for ${groupByValue1}, then incident will be created with name Rule-01 for 50.',
      incidentSummary: 'ja_Summary',
      incidentSummaryPlaceholder: 'ja_Enter a summary for the incident created by this rule',
      incidentCategories: 'ja_Categories',
      incidentCategoriesPlaceholder: 'ja_Choose a category (optional)',
      incidentAssignee: 'ja_Assignee',
      incidentAssigneePlaceholder: 'ja_Choose an assignee (optional)',
      incidentPriority: 'ja_Priority',
      incidentPriorityInstruction: 'ja_Use the following to set the priority for the incident',
      incidentPriorityAverage: 'ja_Average of Risk Score across all of the Alerts',
      incidentPriorityHighestScore: 'ja_Highest Risk Score available across all of the Alerts',
      incidentPriorityAlertCount: 'ja_Number of Alerts in the time window',
      priorityScoreError: 'ja_The priority score ranges are invalid',
      confirmQueryChange: 'ja_Confirm Query Change',
      confirmAdvancedQueryMessage: 'ja_Switching from Query Builder mode to Advanced mode will reset your match criteria.',
      confirmQueryBuilderMessage: 'ja_Switching from Advanced mode to Query Builder mode will reset your match criteria.',
      groupAction: 'ja_Group into an Incident',
      suppressAction: 'ja_Suppress the Alert',
      timeUnits: {
        DAY: 'ja_Days',
        HOUR: 'ja_Hours',
        MINUTE: 'ja_Minutes'
      },
      ruleBuilder: {
        addConditionGroup: 'ja_Add Group',
        removeConditionGroup: 'ja_Remove Group',
        addCondition: 'ja_Add Condition',
        field: 'ja_Field',
        operator: 'ja_Operator',
        operators: {
          '=': 'ja_is equal to',
          '!=': 'ja_is not equal to',
          'begins': 'ja_begins with',
          'ends': 'ja_ends with',
          'contains': 'ja_contains',
          'regex': 'ja_matches regex',
          'in': 'ja_in',
          'nin': 'ja_not in',
          '>': 'ja_is greater than',
          '>=': 'ja_is equal or greater than',
          '<': 'ja_is less than',
          '<=': 'ja_is equal or less than'
        },
        groupOperators: {
          and: 'ja_All of these',
          or: 'ja_Any of these',
          not: 'ja_None of these'
        },
        value: 'ja_Value',
        hasGroupsWithoutConditions: 'ja_All groups must have at least one condition',
        hasMissingConditionInfo: 'ja_One or more conditions is missing some information'
      },
      actionMessages: {
        deleteRuleConfirmation: 'ja_Are you sure you want to delete this rule? Once applied, this deletion cannot be reversed.',
        reorderSuccess: 'ja_You successfully changed the order of the rules',
        reorderFailure: 'ja_There was a problem changing the order of the rules',
        cloneSuccess: 'ja_You successfully cloned the selected rule',
        cloneFailure: 'ja_There was a problem cloning the selected rule',
        createSuccess: 'ja_You successfully created a new rule',
        createFailure: 'ja_There was a problem creating the new rule',
        deleteSuccess: 'ja_You successfully deleted the selected rule',
        deleteFailure: 'ja_There was a problem deleting the selected rule',
        saveSuccess: 'ja_The changes to the rule were successfully saved',
        saveFailure: 'ja_There was a problem saving the changes to the rule'
      },
      missingRequiredInfo: 'ja_There is required information missing from the incident rule'
    },
    notifications: {
      settings: 'ja_Respond Notification Settings',
      emailServer: 'ja_Email Server',
      socEmailAddresses: 'ja_SOC Manager Email Addresses',
      noSocEmails: 'ja_There are no SOC Manager emails configured',
      emailAddressPlaceholder: 'ja_Enter an email address to add',
      addEmail: 'ja_Add',
      notificationTypes: 'ja_Notification Types',
      type: 'ja_Type',
      sendToAssignee: 'ja_Send to Assignee',
      sendToSOCManagers: 'ja_Send to SOC Managers',
      types: {
        'incident-created': 'ja_Incident Created',
        'incident-state-changed': 'ja_Incident Updated'
      },
      hasUnsavedChanges: 'ja_You have unsaved changes. Click Apply to save.',
      emailServerSettings: 'ja_Email Server Settings',
      actionMessages: {
        fetchFailure: 'ja_There was a problem loading the Respond notification settings',
        updateSuccess: 'ja_You successfully updated the Respond notification settings',
        updateFailure: 'ja_There was a problem updating the Respond notification settings'
      }
    }
  },
  respond: {
    title: 'ja_Respond',
    common: {
      yes: 'ja_Yes',
      no: 'ja_No',
      true: 'ja_Yes',
      false: 'ja_No'
    },
    none: 'ja_None',
    select: 'ja_Select',
    close: 'ja_Close',
    empty: 'ja_(empty)',
    filters: 'ja_Filters',
    errorPage: {
      serviceDown: 'ja_Respond Server is not running',
      serviceDownDescription: 'ja_The Respond Server is not running or is inaccessible. Check with your administrator to resolve this issue.',
      fetchError: 'ja_An error has occurred. The Respond Server may be offline or inaccessible.'
    },
    timeframeOptions: {
      LAST_5_MINUTES: 'ja_Last 5 Minutes',
      LAST_10_MINUTES: 'ja_Last 10 Minutes',
      LAST_15_MINUTES: 'ja_Last 15 Minutes',
      LAST_30_MINUTES: 'ja_Last 30 Minutes',
      LAST_HOUR: 'ja_Last Hour',
      LAST_3_HOURS: 'ja_Last 3 Hours',
      LAST_6_HOURS: 'ja_Last 6 Hours',
      LAST_TWELVE_HOURS: 'ja_Last 12 Hours',
      LAST_TWENTY_FOUR_HOURS: 'ja_Last 24 Hours',
      LAST_FORTY_EIGHT_HOURS: 'ja_Last 2 Days',
      LAST_5_DAYS: 'ja_Last 5 Days',
      LAST_7_DAYS: 'ja_Last 7 Days',
      LAST_14_DAYS: 'ja_Last 14 Days',
      LAST_30_DAYS: 'ja_Last 30 Days',
      ALL_TIME: 'ja_All Data'
    },
    entities: {
      incidents: 'ja_Incidents',
      remediationTasks: 'ja_Tasks',
      alerts: 'ja_Alerts',
      actionMessages: {
        updateSuccess: 'ja_Your change was successful',
        updateFailure: 'ja_There was a problem updating the field for this record',
        createSuccess: 'ja_You successfully added a new record',
        createFailure: 'ja_There was a problem creating this record',
        deleteSuccess: 'ja_You successfully deleted this record',
        deleteFailure: 'ja_There was a problem deleting this record',
        saveSuccess: 'ja_Your changes were successfully saved',
        saveFailure: 'ja_There was a problem saving this record'
      },
      alert: 'ja_Alert'
    },
    explorer: {
      noResults: 'ja_No results were found. Try expanding your time range or adjusting existing filters to include more results.',
      confirmation: {
        updateTitle: 'ja_Confirm Update',
        deleteTitle: 'ja_Confirm Delete',
        bulkUpdateConfrimation: 'ja_You are about to make the following changes to more than one item',
        deleteConfirmation: 'ja_Are you sure you want to delete {{count}} record(s)? Once applied, this deletion cannot be reversed.',
        field: 'ja_Field',
        value: 'ja_Value',
        recordCountAffected: 'ja_Number of items'
      },
      filters: {
        timeRange: 'ja_Time Range',
        reset: 'ja_Reset Filters',
        customDateRange: 'ja_Custom Date Range',
        customStartDate: 'ja_Start Date',
        customEndDate: 'ja_End Date',
        customDateErrorStartAfterEnd: 'ja_The start date and time cannot be the same or later than the end date'
      },
      inspector: {
        overview: 'ja_Overview'
      },
      footer: 'ja_Showing {{count}} out of {{total}} items'
    },
    remediationTasks: {
      loading: 'ja_Loading Tasks',
      addNewTask: 'ja_Add New Task',
      noTasks: 'ja_There are no tasks for {{incidentId}}',
      openFor: 'ja_Opened',
      newTaskFor: 'ja_New Task for',
      delete: 'ja_Delete Task',
      noAccess: 'ja_You do not have permissions to view tasks',
      actions: {
        actionMessages: {
          deleteWarning: 'ja_Deleting a Task from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
          'to delete the task from any other applicable systems.'
        }
      },
      filters: {
        taskId: 'ja_Task ID',
        idFilterPlaceholder: 'ja_e.g., REM-123',
        idFilterError: 'ja_The ID must match the format: REM-###'
      },
      list: {
        priority: 'ja_Priority',
        select: 'ja_Select',
        id: 'ja_ID',
        name: 'ja_Name',
        createdDate: 'ja_Created',
        status: 'ja_Status',
        assignee: 'ja_Assignee',
        noResultsMessage: 'ja_No matching Tasks were found',
        incidentId: 'ja_Incident ID',
        targetQueue: 'ja_Target Queue',
        remediationType: 'ja_Type',
        escalated: 'ja_Escalated',
        lastUpdated: 'ja_Last Updated',
        description: 'ja_Description',
        createdBy: 'ja_Created By'
      },
      type: {
        QUARANTINE_HOST: 'ja_Quarantine Host',
        QUARANTINE_NETORK_DEVICE: 'ja_Quarantine Network Device',
        BLOCK_IP_PORT: 'ja_Block IP/Port',
        BLOCK_EXTERNAL_ACCESS_TO_DMZ: 'ja_Block External Access To DMZ',
        BLOCK_VPN_ACCESS: 'ja_Block VPN Access',
        REIMAGE_HOST: 'ja_Reimage Host',
        UPDATE_FIREWALL_POLICY: 'ja_Update Firewall Policy',
        UPDATE_IDS_IPS_POLICY: 'ja_Update IDS/IPS Policy',
        UPDATE_WEB_PROXY_POLICY: 'ja_Update Web Proxy Policy',
        UPDATE_ACCESS_POLICY: 'ja_Update Access Policy',
        UPDATE_VPN_POLICY: 'ja_Update VPN Policy',
        CUSTOM: 'ja_Custom',
        MITIGATE_RISK: 'ja_Mitigate Risk',
        MITIGATE_COMPLIANCE_VIOLATION: 'ja_Mitigate Compliance Violation',
        MITIGATE_VULNERABILITY_THREAT: 'ja_Mitigate Vulnerability/Threat',
        UPDATE_CORPORATE_BUSINESS_POLICY: 'ja_Update Corporate/Business Policy',
        NOTIFY_BC_DR_TEAM: 'ja_Notify BC/DR Team',
        UPDATE_RULES: 'ja_Update Rule(s)',
        UPDATE_FEEDS: 'ja_Update Feed(s)'
      },
      targetQueue: {
        OPERATIONS: 'ja_Operations',
        GRC: 'ja_GRC',
        CONTENT_IMPROVEMENT: 'ja_Content Improvement'
      },
      noDescription: 'ja_There is no description for this task'
    },
    incidents: {
      incidentName: 'ja_Incident Name',
      actions: {
        addEntryLabel: 'ja_Add Entry',
        confirmUpdateTitle: 'ja_Confirm update',
        changeAssignee: 'ja_Change Assignee',
        changePriority: 'ja_Change Priority',
        changeStatus: 'ja_Change Status',
        addJournalEntry: 'ja_Add Journal Entry',
        actionMessages: {
          deleteWarning: 'ja_Warning: You are about to delete one or more incidents which may have tasks and which may have been escalated. ' +
          'Deleting an incident from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
          'to delete the incident and its tasks from any other applicable systems.',
          addJournalEntrySuccess: 'ja_You added a journal entry to incident {{incidentId}}',
          addJournalEntryFailure: 'ja_There was a problem adding a journal entry to incident {{incidentId}}',
          incidentCreated: 'ja_You successfully created the incident {{incidentId}} from the selected alerts. The incident\'s priority has been set to LOW by default.',
          incidentCreationFailed: 'ja_There was a problem creating an incident from the selected alerts',
          createIncidentInstruction: 'ja_An incident will be created from the selected {{alertCount}} alert(s). Please provide a name for the incident.',
          addAlertToIncidentSucceeded: 'ja_You successfully added the selected alerts to {{incidentId}}.',
          addAlertToIncidentFailed: 'ja_There was a problem adding the selected alerts to this incident'
        },
        deselectAll: 'ja_Deselect all'
      },
      filters: {
        timeRange: 'ja_Time Range',
        incidentId: 'ja_Incident ID',
        idFilterPlaceholder: 'ja_e.g., INC-123',
        idFilterError: 'ja_The ID must match the format: INC-###',
        reset: 'ja_Reset Filters',
        customDateRange: 'ja_Custom Date Range',
        customStartDate: 'ja_Start Date',
        customEndDate: 'ja_End Date',
        customDateErrorStartAfterEnd: 'ja_The start date and time cannot be the same or later than the end date',
        showOnlyUnassigned: 'ja_Show only unassigned incidents'
      },
      label: 'ja_Incidents',
      selectionCount: 'ja_{{selectionCount}} selected',
      list: {
        select: 'ja_Select',
        id: 'ja_ID',
        name: 'ja_Name',
        createdDate: 'ja_Created',
        status: 'ja_Status',
        priority: 'ja_Priority',
        riskScore: 'ja_Risk Score',
        assignee: 'ja_Assignee',
        alertCount: 'ja_Alerts',
        sources: 'ja_Source',
        noResultsMessage: 'ja_No matching Incidents were found'
      },
      footer: 'ja_Showing {{count}} out of {{total}} incidents'
    },
    alerts: {
      createIncident: 'ja_Create Incident',
      addToIncident: 'ja_Add to Incident',
      incidentSearch: {
        searchInputLabel: 'ja_Search Open Incidents',
        searchInputPlaceholder: 'ja_Search by incident ID (e.g., INC-123) or incident name',
        noResults: 'ja_No open Incidents found',
        noQuery: 'ja_Use the search box above to search for open incidents by name or ID. Your search must contain at least (3) characters.',
        error: 'ja_There was a problem searching for incidents'
      },
      actions: {
        actionMessages: {
          deleteWarning: 'ja_Warning: You are about to delete one or more alerts that may be associated with incidents. ' +
          'Be aware that any associated incidents will be updated or deleted accordingly.'
        }
      },
      list: {
        receivedTime: 'ja_Created',
        severity: 'ja_Severity',
        numEvents: 'ja_# Events',
        id: 'ja_ID',
        name: 'ja_Name',
        status: 'ja_Status',
        source: 'ja_Source',
        incidentId: 'ja_Incident ID',
        partOfIncident: 'ja_Part of Incident',
        type: 'ja_Type',
        hostSummary: 'ja_Host Summary',
        userSummary: 'ja_User Summary'
      },
      notAssociatedWithIncident: 'ja_(None)',
      originalAlert: 'ja_Raw Alert',
      originalAlertLoading: 'ja_Loading raw alert',
      originalAlertError: 'ja_There was a problem loading the raw alert',
      alertNames: 'ja_Alert Names'
    },
    alert: {
      status: {
        GROUPED_IN_INCIDENT: 'ja_Grouped In Incident',
        NORMALIZED: 'ja_Normalized'
      },
      type: {
        Correlation: 'ja_Correlation',
        Log: 'ja_Log',
        Network: 'ja_Network',
        'Instant IOC': 'ja_Instant IOC',
        'Web Threat Detection Incident': 'ja_Web Threat Detection Incident',
        'File Share': 'ja_File Share',
        'Manual Upload': 'ja_Manual Upload',
        'On Demand': 'ja_On Demand',
        Resubmit: 'ja_Resubmit',
        Unknown: 'ja_Unknown'
      },
      source: {
        ECAT: 'ja_Endpoint',
        'Event Stream Analysis': 'ja_Event Stream Analysis',
        'Event Streaming Analytics': 'ja_Event Stream Analysis',
        'Security Analytics Investigator': 'ja_Security Analytics Investigator',
        'Web Threat Detection': 'ja_Web Threat Detection',
        'Malware Analysis': 'ja_Malware Analysis',
        'Reporting Engine': 'ja_Reporting Engine',
        'NetWitness Investigate': 'ja_NetWitness Investigate'
      },
      backToAlerts: 'ja_Back To Alerts'
    },
    incident: {
      created: 'ja_Created',
      status: 'ja_Status',
      priority: 'ja_Priority',
      riskScore: 'ja_Risk Score',
      assignee: 'ja_Assignee',
      alertCount: 'ja_Indicator(s)',
      eventCount: 'ja_Event(s)',
      catalystCount: 'ja_Catalysts',
      sealed: 'ja_Sealed',
      sealsAt: 'ja_Seals At',
      sources: 'ja_Sources',
      categories: 'ja_Categories',
      backToIncidents: 'ja_Back To Incidents',
      overview: 'ja_Overview',
      indicators: 'ja_Indicators',
      indicatorsCutoff: 'ja_Showing {{limit}} of {{expected}} indicators',
      events: 'ja_Events',
      loadingEvents: 'ja_Loading events...',
      view: {
        graph: 'ja_View: Graph',
        datasheet: 'ja_View: Datasheet'
      },
      journalTasksRelated: 'ja_Journal, Tasks, and Related',
      search: {
        tab: 'ja_Related',
        title: 'ja_Related Indicators',
        subtext: 'ja_Enter a value below and click the Find button to look for other indicators related to that value.',
        partOfThisIncident: 'ja_Part Of This Incident',
        types: {
          IP: 'ja_IP',
          MAC_ADDRESS: 'ja_MAC',
          HOST: 'ja_Host',
          DOMAIN: 'ja_Domain',
          FILE_NAME: 'ja_Filename',
          FILE_HASH: 'ja_Hash',
          USER: 'ja_User',
          label: 'ja_Find'
        },
        text: {
          label: 'ja_Value',
          placeholders: {
            IP: 'ja_Enter an IP address',
            MAC_ADDRESS: 'ja_Enter a MAC address',
            HOST: 'ja_Enter a hostname',
            DOMAIN: 'ja_Enter a domain name',
            FILE_NAME: 'ja_Enter a file name',
            FILE_HASH: 'ja_Enter a file hash',
            USER: 'ja_Enter a user name'
          }
        },
        timeframe: {
          label: 'ja_When'
        },
        devices: {
          source: 'ja_Source',
          destination: 'ja_Destination',
          detector: 'ja_Detector',
          domain: 'ja_Domain',
          label: 'ja_Look in'
        },
        results: {
          title: 'ja_Indicators for',
          openInNewWindow: 'ja_Open in new window'
        },
        actions: {
          search: 'ja_Find',
          cancel: 'ja_Cancel',
          addToIncident: 'ja_Add To Incident',
          addingAlert: 'ja_Adding Alert to Incident',
          unableToAddAlert: 'ja_Unable to add Alert to Incident.',
          pleaseTryAgain: 'ja_Please try again.'
        }
      }
    },
    storyline: {
      loading: 'ja_Loading incident storyline',
      error: 'ja_Unable to load incident storyline',
      catalystIndicator: 'ja_Catalyst Indicator',
      relatedIndicator: 'ja_Related Indicator',
      source: 'ja_Source',
      partOfIncident: 'ja_Part of Incident',
      relatedBy: 'ja_Related to Catalyst by',
      event: 'ja_event',
      events: 'ja_events'
    },
    details: {
      loading: 'ja_Loading incident details',
      error: 'ja_Unable to load incident details'
    },
    journal: {
      newEntry: 'ja_New Journal Entry',
      title: 'ja_Journal',
      close: 'ja_Close',
      milestone: 'ja_Milestone',
      loading: 'ja_Loading journal entries',
      noEntries: 'ja_There are no journal entries for {{incidentId}}',
      delete: 'ja_Delete Entry',
      deleteConfirmation: 'ja_Are you sure you want to delete this journal entry? This action cannot be reversed.',
      noAccess: 'ja_You do not have permissions to view journal entries'
    },
    milestones: {
      title: 'ja_Milestones',
      RECONNAISSANCE: 'ja_Reconnaissance',
      DELIVERY: 'ja_Delivery',
      EXPLOITATION: 'ja_Exploitation',
      INSTALLATION: 'ja_Installation',
      COMMAND_AND_CONTROL: 'ja_Command and Control',
      ACTION_ON_OBJECTIVE: 'ja_Action on Objective',
      CONTAINMENT: 'ja_Containment',
      ERADICATION: 'ja_Eradication',
      CLOSURE: 'ja_Closure'
    },
    eventDetails: {
      title: 'ja_Event Details',
      events: 'ja_events',
      in: 'ja_in',
      indicators: 'ja_indicators',
      type: {
        'Instant IOC': 'ja_Instant IOC',
        'Log': 'ja_Log',
        'Network': 'ja_Network',
        'Correlation': 'ja_Correlation',
        'Web Threat Detection': 'ja_Web Threat Detection',
        'Web Threat Detection Incident': 'ja_Web Threat Detection Incident',
        'Unknown': 'ja_Event',
        'File Share': 'ja_File Share',
        'Manual Upload': 'ja_Manual Upload',
        'On Demand': 'ja_On Demand',
        Resubmit: 'ja_Resubmit'
      },
      backToTable: 'ja_Back To Table',
      labels: {
        timestamp: 'ja_Timestamp',
        type: 'ja_Type',
        description: 'ja_Description',
        source: 'ja_Source',
        destination: 'ja_Destination',
        domain: 'ja_Domain/Host',
        detector: 'ja_Detector',
        device: 'ja_Device',
        ip_address: 'ja_IP Address',
        mac_address: 'ja_MAC Address',
        dns_hostname: 'ja_Host',
        dns_domain: 'ja_Domain',
        netbios_name: 'ja_NetBIOS Name',
        asset_type: 'ja_Asset Type',
        business_unit: 'ja_Business Unit',
        facility: 'ja_Facility',
        criticality: 'ja_Criticality',
        compliance_rating: 'ja_Compliance_rating',
        malicious: 'ja_Malicious',
        site_categorization: 'ja_Site Categorization',
        geolocation: 'ja_Geolocation',
        city: 'ja_City',
        country: 'ja_Country',
        longitude: 'ja_Longitude',
        latitude: 'ja_Latitude',
        organization: 'ja_Organization',
        device_class: 'ja_Device Class',
        product_name: 'Product Name',
        port: 'ja_Port',
        user: 'ja_User',
        username: 'ja_Username',
        ad_username: 'ja_Active Directory Username',
        ad_domain: 'ja_Active Directory Domain',
        email_address: 'ja_Email Address',
        os: 'ja_Operating System',
        size: 'ja_Size',
        data: 'ja_Data',
        filename: 'ja_Filename',
        hash: 'ja_Hash',
        av_hit: 'ja_AV Hit',
        extension: 'ja_Extension',
        mime_type: 'ja_MIME Type',
        original_path: 'ja_Original Path',
        av_aliases: 'ja_AV Aliases',
        networkScore: 'ja_Network Score',
        communityScore: 'ja_Community Score',
        staticScore: 'ja_Static Score',
        sandboxScore: 'ja_Sandbox Score',
        opswat_result: 'ja_OPSWAT Result',
        yara_result: 'ja_YARA Result',
        bit9_status: 'ja_Bit9 Status',
        module_signature: 'ja_Module Signature',
        related_links: 'ja_Related Links',
        url: 'ja_URL',
        ecat_agent_id: 'ja_NWE Agent ID',
        ldap_ou: 'ja_LDAP OU',
        last_scanned: 'ja_Last Scanned',
        enrichment: 'ja_Enrichment',
        enrichmentSections: {
          domain_registration: 'ja_Domain Registration',
          command_control_risk: 'ja_Command and Control',
          beaconing_behavior: 'ja_Beaconing',
          domain_age: 'ja_Domain Age',
          expiring_domain: 'ja_Expiring Domain',
          rare_domain: 'ja_Rare Domain',
          no_referers: 'ja_Referers',
          rare_user_agent: 'ja_Rare User Agent'
        },
        registrar_name: 'ja_Domain Registrar',
        registrant_organization: 'ja_Registrant Organization',
        registrant_name: 'ja_Registrant Name',
        registrant_email: 'ja_Registrant Email',
        registrant_telephone: 'ja_Registrant Telephone',
        registrant_street1: 'ja_Registrant Street Address',
        registrant_postal_code: 'ja_Registrant Postal Code',
        registrant_city: 'ja_Registrant City',
        registrant_state: 'ja_Registrant State',
        registrant_country: 'ja_Registrant Country',
        whois_created_dateNetWitness: 'ja_Registration Date',
        whois_updated_dateNetWitness: 'ja_Updated Date',
        whois_expires_dateNetWitness: 'ja_Expiration Date',
        whois_age_scoreNetWitness: 'ja_Domain Registration Age Score',
        whois_validity_scoreNetWitness: 'ja_Expiring Domain Score',
        whois_estimated_domain_age_daysNetWitness: 'ja_Domain Registration Age (in days)',
        whois_estimated_domain_validity_daysNetWitness: 'ja_Time To Expiration (in days)',
        command_control_aggregate: 'ja_Command and Control Risk Score',
        command_control_confidence: 'ja_Confidence',
        weighted_c2_referer_score: 'ja_Contribution of Rare Domain Score (This Network)',
        weighted_c2_referer_ratio_score: 'ja_Contribution of No Domain Referer Score',
        weighted_c2_ua_ratio_score: 'ja_Contribution of Rare User Agent Score',
        weighted_c2_whois_age_score: 'ja_Contribution of Domain Registration Age Score',
        weighted_c2_whois_validity_score: 'ja_Contribution of Expiring Domain Score',
        smooth_score: 'ja_Score',
        beaconing_period: 'ja_Period',
        newdomain_score: 'ja_Domain Age Score (This Network)',
        newdomain_age: 'ja_Domain Age (This Network)',
        referer_score: 'ja_Rare Score',
        referer_cardinality: 'ja_Rare Cardinality',
        referer_num_events: 'ja_Rare Events',
        referer_ratio: 'ja_Rare Ratio',
        referer_ratio_score: 'ja_Rare Ratio Score',
        referer_cond_cardinality: 'ja_Rare Conditional Cardinality',
        ua_num_events: 'ja_Occurences in the last week',
        ua_ratio: 'ja_Percentage of IPs With Rare User Agent',
        ua_ratio_score: 'ja_Rare User Agent Score',
        ua_cond_cardinality: 'ja_IPs with Rare User Agent'
      },
      periodValue: {
        hours: 'ja_hour(s)',
        minutes: 'ja_minute(s)',
        seconds: 'ja_second(s)'
      }
    },
    eventsTable: {
      time: 'ja_Time',
      type: 'ja_Type',
      sourceDomain: 'ja_Source Domain',
      destinationDomain: 'ja_Destination Domain',
      sourceHost: 'ja_Source Host',
      destinationHost: 'ja_Destination Host',
      sourceIP: 'ja_Source IP',
      destinationIP: 'ja_Destination IP',
      detectorIP: 'ja_Detector IP',
      sourcePort: 'ja_Source Port',
      destinationPort: 'ja_Destination Port',
      sourceMAC: 'ja_Source MAC',
      destinationMAC: 'ja_Destination MAC',
      sourceUser: 'ja_Source User',
      destinationUser: 'ja_Destination User',
      fileName: 'ja_File Name',
      fileHash: 'ja_File Hash',
      indicator: 'ja_Indicator'
    },
    entity: {
      legend: {
        user: 'ja_user(s)',
        host: 'ja_host(s)',
        ip: 'ja_IP(s)',
        domain: 'ja_domain(s)',
        mac_address: 'ja_MAC(s)',
        file_name: 'ja_file(s)',
        file_hash: 'ja_hash(es)',
        selection: {
          storyPoint: 'ja_in {{count}} selected indicator(s)',
          event: 'ja_in {{count}} selected event(s)'
        },
        selectionNotShown: 'ja_The selected nodes could not be shown due to size limits.',
        hasExceededNodeLimit: 'ja_Showing only first {{limit}} nodes.',
        showAll: 'ja_Show all data'
      }
    },
    enrichment: {
      uniformTimeIntervals: 'ja_The time intervals between communication events are very uniform.',
      newDomainToEnvironment: 'ja_Domain is relatively new to the environment.',
      rareDomainInEnvironment: 'ja_The domain is rare in this environment.',
      newDomainRegistration: 'ja_Domain is relatively new based on the registration date:',
      domainRegistrationExpires: 'ja_The domain registration will expire relatively soon:',
      rareUserAgent: 'ja_A high percentage of hosts connecting to the domain are using a rare or no user agent.',
      noReferers: 'ja_A high percentage of hosts connecting to the domain are not utilizing referrers.',
      highNumberServersAccessed: 'ja_Abnormally high number of servers accessed today.',
      highNumberNewServersAccessed: 'ja_Accessed an abnormally high number of new servers today.',
      highNumberNewDevicesAccessed: 'ja_Accessed an abnormally high number of new devices this week.',
      highNumberFailedLogins: 'ja_Abnormally high number of servers with failed logins today.',
      passTheHash: 'ja_Potential "pass the hash" attack indicated by a new device followed by a new server.',
      rareLogonType: 'ja_Accessed using a Windows logon type they have rarely used in the past.',
      authFromRareDevice: 'ja_Authenticated from a rare device.',
      authFromRareLocation: 'ja_Accessed from a rare location.',
      authFromRareServiceProvider: 'ja_Accessed using a rare service provider.',
      authFromNewServiceProvider: 'ja_Accessed using a new service provider.',
      highNumberVPNFailedLogins: 'ja_High number of VPN login failures.',
      daysAgo: 'ja_{{days}} day(s) ago',
      days: 'ja_{{days}} day(s)',
      domainIsWhitelisted: 'ja_Domain is whitelisted.',
      domainIsNotWhitelisted: 'ja_Domain is not whitelisted.'
    },
    sources: {
      'C2-Packet': 'ja_User Entity Behavioral Analytics',
      'C2-Log': 'ja_User Entity Behavioral Analytics',
      'UBA-WinAuth': 'ja_User Entity Behavioral Analytics',
      UbaCisco: 'ja_User Entity Behavioral Analytics',
      ESA: 'ja_Event Stream Analytics',
      'Event-Stream-Analysis': 'ja_Event Stream Analytics',
      RE: 'ja_Reporting Engine',
      'Reporting-Engine': 'ja_Reporting Engine',
      ModuleIOC: 'ja_Endpoint',
      ECAT: 'ja_Endpoint',
      generic: 'ja_NetWitness'
    },
    status: {
      NEW: 'ja_New',
      ASSIGNED: 'ja_Assigned',
      IN_PROGRESS: 'ja_In Progress',
      REMEDIATION_REQUESTED: 'ja_Task Requested',
      REMEDIATION_COMPLETE: 'ja_Task Complete',
      CLOSED: 'ja_Closed',
      CLOSED_FALSE_POSITIVE: 'ja_Closed - False Positive',
      REMEDIATED: 'ja_Remediated',
      RISK_ACCEPTED: 'ja_Risk Accepted',
      NOT_APPLICABLE: 'ja_Not Applicable'
    },
    priority: {
      LOW: 'ja_Low',
      MEDIUM: 'ja_Medium',
      HIGH: 'ja_High',
      CRITICAL: 'ja_Critical'
    },
    assignee: {
      none: 'ja_(Unassigned)'
    }
  },
  context: {
    noData: 'ja_No matching context is available',
    noResults: 'ja_(No Results)',
    notConfigured: 'ja_(Not Configured)',
    title: 'ja_Context',
    lastUpdated: 'ja_Last Updated:',
    timeWindow: 'ja_Time Window: ',
    iiocScore: 'ja_Iioc Score',
    IP: 'ja_IP',
    USER: 'ja_User',
    MAC_ADDRESS: 'ja_Mac Address',
    HOST: 'ja_Host',
    FILE_NAME: 'ja_File Name',
    FILE_HASH: 'ja_File Hash',
    DOMAIN: 'ja_Domain',
    noValues: 'ja_Context Sources with no values: ',
    dsNotConfigured: 'ja_Context Sources not configured: ',
    marketingText: 'ja_ is not a currently configured data source in Context Hub. Contact your Administrator to enable this feature. Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more sources on-demand. For more information , click Help.',
    lcMarketingText: 'ja_Live Connect collects, analyzes, and assesses Threat Intelligence data such as IP Addresses, Domains, and File Hashes collected from various sources. Live connect is not a default data source in Context Hub, you need to manually enable it. For more information , click Help.',
    timeUnit: {
      allData: 'ja_ALL DATA',
      HOUR: 'ja_HOUR',
      HOURS: 'ja_HOURS',
      MINUTE: 'ja_MINUTE',
      MINUTES: 'ja_MINUTES',
      DAY: 'ja_DAY',
      DAYS: 'ja_DAYS',
      MONTH: 'ja_MONTH',
      MONTHS: 'ja_MONTHS',
      YEAR: 'ja_YEAR',
      YEARS: 'ja_YEARS',
      WEEK: 'ja_WEEK',
      WEEKS: 'ja_WEEKS'
    },
    marketingDSType: {
      Users: 'ja_Active Directory',
      Alerts: 'ja_Respond(Alerts)',
      Incidents: 'ja_Respond(Incidents)',
      Machines: 'ja_Endpoint (Machines)',
      Modules: 'ja_Endpoint (Modules)',
      IOC: 'ja_Endpoint (IOC)',
      Archer: 'ja_Archer',
      LIST: 'ja_List'
    },
    header: {
      title: {
        archer: 'ja_Archer',
        users: 'ja_Active Directory',
        alerts: 'ja_Alerts',
        incidents: 'ja_Incidents',
        lIST: 'ja_Lists',
        endpoint: 'ja_NetWitness Endpoint',
        liveConnectIp: 'ja_Live Connect',
        liveConnectFile: 'ja_Live Connect',
        liveConnectDomain: 'ja_Live Connect'
      },
      archer: 'ja_Archer',
      overview: 'ja_overview',
      iioc: 'ja_IIOC',
      modules: 'ja_Modules',
      users: 'ja_Users',
      categoryTags: 'ja_category tags',
      incidents: 'ja_Incidents',
      alerts: 'ja_Alerts',
      files: 'ja_Files',
      lists: 'ja_Lists',
      feeds: 'ja_Feeds',
      endpoint: 'ja_Endpoint',
      liveConnect: 'ja_Live Connect',
      unsafe: 'ja_Unsafe',
      closeButton: {
        title: 'ja_Close Panel'
      },
      help: {
        title: 'ja_Help'
      }
    },
    toolbar: {
      investigate: 'ja_Investigate',
      endpoint: 'ja_NetWitness Endpoint',
      googleLookup: 'ja_Google Lookup',
      virusTotal: 'ja_VirusTotal Lookup',
      addToList: 'ja_Add to List'
    },
    hostSummary: {
      title: 'ja_Endpoint',
      riskScore: 'ja_Risk Score',
      modulesCount: 'ja_# of Modules',
      iioc0: 'ja_Iioc 0',
      iioc1: 'ja_Iioc 1',
      lastUpdated: 'ja_Last Updated',
      adminStatus: 'ja_Admin Status',
      lastLogin: 'ja_Last Login',
      macAddress: 'ja_MAC Address',
      operatingSystem: 'ja_Operating System',
      machineStatus: 'ja_Machine Status',
      ipAddress: 'ja_IPAddress',
      endpoint: 'ja_To Pivot to Endpoint, please install the NetWitness Endpoint client for Windows.'
    },
    ADdata: {
      title: 'ja_User Information',
      employeeID: 'ja_employeeID',
      department: 'ja_department',
      location: 'ja_location',
      manager: 'ja_manager',
      groups: 'ja_groups',
      company: 'ja_company',
      email: 'ja_Email',
      phone: 'ja_phone',
      jobTitle: 'ja_jobTitle',
      lastLogon: 'ja_lastLogon',
      lastLogonTimeStamp: 'ja_lastLogonTimeStamp',
      adUserID: 'ja_adUserID',
      distinguishedName: 'ja_Distinguished Name',
      displayName: 'ja_Display Name'
    },
    archer: {
      title: 'ja_Archer',
      criticalityRating: 'ja_Criticality Rating',
      riskRating: 'ja_Risk Rating',
      deviceName: 'ja_Device Name',
      hostName: 'ja_Hostname',
      deviceId: 'ja_Device Id',
      deviceType: 'ja_Device Type',
      deviceOwner: 'ja_Device Owner',
      deviceOwnerTitle: 'ja_Device Owner Title',
      businessUnit: 'ja_Business Unit',
      facility: 'ja_Facility',
      ipAddress: 'ja_Internal IP Address'
    },
    addToList: {
      title: 'ja_Add/Remove from List',
      create: 'ja_Create New List',
      metaValue: 'ja_Meta Value',
      newList: 'ja_Create New List',
      tabAll: 'ja_All',
      tabSelected: 'ja_Selected',
      tabUnselected: 'ja_Unselected',
      cancel: 'ja_Cancel',
      save: 'ja_Save',
      name: 'ja_List Name',
      listTitle: 'ja_List',
      descriptionTitle: 'ja_Description',
      filter: 'ja_Filter Results',
      listName: 'ja_Enter List Name',
      headerMessage: 'ja_Click on save to update the list(s). Refresh the page to view the updates.'
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
      title: 'ja_Machine IOC Levels',
      lastExecuted: 'ja_LastExecuted',
      description: 'ja_Description',
      iOCLevel: 'ja_IOC Level',
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
      risk_score: 'ja_Severity',
      source: 'ja_Source',
      name: 'ja_Name',
      numEvents: 'ja_# Events',
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
      notReviewed: 'ja_NOT REVIEWED',
      noFeedback1: 'ja_No Feedback Analysis Yet',
      noFeedback2: 'ja_ - Be an active member of the Live Connect Threat Community, provide your risk assessment',
      blankField: '-',
      modifiedDate: 'ja_Modified Date',
      reviewer: 'ja_Reviewer',
      riskConfirmation: 'ja_Risk Confirmation',
      safe: 'ja_Safe',
      unsafe: 'ja_Unsafe',
      unknown: 'ja_Unknown',
      suspicious: 'ja_Suspicious',
      highRisk: 'ja_High Risk',
      high: 'ja_High',
      med: 'ja_Medium',
      low: 'ja_Low',
      riskTags: 'ja_Risk Indicator Tags',
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
      organization: 'ja_Organization',
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
      whoisFax: 'ja_Fax',
      whoisEmail: 'ja_Email',
      domain: 'ja_Domain',
      ipAddress: 'ja_IP Address',
      errorMsg: 'ja_Could not fetch data from Live Connect: {{error}}',
      riskAssessment: 'ja_Live Connect Risk Assessment',
      riskReason: 'ja_Risk Reasons',
      highRiskDesc: 'ja_Indicator seen as high risk and deserves focused attention',
      safeRiskDesc: 'ja_Research and analysis shows indicators to be trusted resources',
      unsafeRiskDesc: 'ja_Research and analysis shows resource to be untrusted',
      unknownRiskDesc: 'ja_Inconclusive results from any available information, research, and analysis',
      suspiciousRiskDesc: 'ja_Research and analysis indicates potentially threatening activity',
      riskFeedback: 'ja_Risk Assessment Feedback',
      relatedFiles: 'ja_Related Files ',
      risk: 'ja_LC RISK RATING',
      importHashFunction: 'ja_API FUNCTION IMPORT HASH',
      compiledTime: 'ja_COMPILE DATE',
      relatedDomains: 'ja_Related Domains ',
      relatedIps: 'ja_Related IPs ',
      country: 'ja_Country',
      registeredDate: 'ja_Registered Date',
      expiredDate: 'ja_Expired Date',
      email: 'ja_Registrant email',
      asnShort: 'ja_ASN',
      confidenceLevel: 'ja_Confidence Level',
      select: 'ja_Select...',
      feedbackSubmitted: 'ja_Feedback has been submitted to Live Connect server',
      feedbackSubmissionFailed: 'ja_Could not submit feedback to Live Connect server',
      feedbackFormInvalid: 'ja_Select the \'Risk Confirmation\' and \'Confidence Level\'.',
      noTrendingCommunityActivity: 'ja_There is no new community activity in the past 30 days',
      noTrendingSubmissionActivity: 'ja_There are no new submissions in the past 30 days',
      skillLevel: 'ja_Analyst Skill Level',
      skillLevelPrefix: 'ja_Tier {{level}}',
      noRelatedData: 'ja_There are no Related {{entity}} for this entity.',
      ips: 'ja_IPs',
      files: 'ja_Files',
      domains: 'ja_Domains'
    },
    error: {
      error: 'ja_An unexpected error occurred when attempting to fetch the data.',
      noDataSource: 'ja_No data source configured/enabled.',
      dataSourcesFailed: 'ja_Unable to fetch data from the configured data sources.',
      dataSource: 'ja_An unexpected error occurred when attempting to fetch the data.',
      noData: 'ja_No context data is available for this DataSource.',
      listDuplicateName: 'ja_List name already exists!',
      listValidName: 'ja_Enter valid list name (Max length is 255 characters).',
      'mongo.error': 'ja_Unexpected database error occurred.',
      'total.entries.exceed.max': 'ja_List size exceeds the limit 100000.',
      'admin.error': 'ja_Admin service is not reachable. Check your service connectivity.',
      'datasource.disk.usage.high': 'ja_Low disk space. Delete unwanted data to free up space.',
      'context.service.timeout': 'ja_Context Hub service is not reachable. Check your service connectivity.',
      'get.mongo.connect.failed': 'ja_Database is not reachable. Retry after sometime.',
      'datasource.query.not.supported': 'ja_Context data lookup is not supported for this meta.',
      'transport.http.read.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.ad.read.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.init.failed': 'ja_Data source connection timed out.',
      'transport.not.found': 'ja_Context data is not available as the data source is not reachable.',
      'transport.create.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.refresh.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.connect.failed': 'ja_Context data is not available as the data source is not reachable.',
      'live.connect.private.ip.unsupported': 'ja_Only public IP addresses are supported by Live Connect.',
      'transport.http.error': 'ja_Context lookup failed for this datasource since it returned an error.',
      'transport.validation.error': 'ja_The data format is not supported for the data source.',
      'transport.http.auth.failed': 'ja_Could not fetch context from this data source - Authorization failed.'
    },
    footer: {
      viewAll: 'ja_VIEW All',
      title: {
        incidents: 'ja_Incident(s)',
        alerts: 'ja_Alert(s)',
        lIST: 'ja_List(s)',
        users: 'ja_User(s)',
        endpoint: 'ja_Host',
        archer: 'ja_Asset'
      },
      resultCount: 'ja_(First {{count}} Results)'
    },
    tooltip: {
      contextHighlights: 'ja_Context Highlights',
      viewOverview: 'ja_View Context',
      actions: 'ja_Actions',
      investigate: 'ja_Pivot to Investigate',
      addToList: 'ja_Add/Remove from List',
      virusTotal: 'ja_Virus Total Lookup',
      googleLookup: 'ja_Google Lookup',
      ecat: 'ja_Pivot to Endpoint',
      events: 'ja_Pivot to Events',
      contextUnavailable: 'ja_No context data available at this time.',
      dataSourceNames: {
        Incidents: 'ja_Incidents',
        Alerts: 'ja_Alerts',
        LIST: 'ja_Lists',
        Users: 'ja_Users',
        IOC: 'ja_IOCs',
        Machines: 'ja_Endpoint',
        Modules: 'ja_Modules',
        'LiveConnect-Ip': 'ja_LiveConnect',
        'LiveConnect-File': 'ja_LiveConnect',
        'LiveConnect-Domain': 'ja_LiveConnect'
      }
    }
  },
  preferences: {
    'investigate-events': {
      panelTitle: 'ja_Event Preferences',
      triggerTip: 'ja_Open/Hide Event Preferences',
      defaultEventView: 'ja_Default Event Analysis View',
      defaultLogFormat: 'ja_Default Log Format',
      defaultPacketFormat: 'ja_Default Packet Format',
      LOG: 'ja_Download Log',
      CSV: 'ja_Download CSV',
      XML: 'ja_Download XML',
      JSON: 'ja_Download JSON',
      PCAP: 'ja_Download PCAP',
      PAYLOAD: 'ja_Download All Payloads',
      PAYLOAD1: 'ja_Download Request Payload',
      PAYLOAD2: 'ja_Download Response Payload',
      FILE: 'ja_File Analysis',
      TEXT: 'ja_Text Analysis',
      PACKET: 'ja_Packet Analysis',
      queryTimeFormat: 'ja_Time format for query',
      DB: 'ja_Database Time',
      WALL: 'ja_Wall Clock Time',
      autoDownloadExtractedFiles: 'ja_Download extracted files automatically'
    },
    'endpoint-preferences': {
      visibleColumns: 'ja_Visible Columns',
      sortField: 'ja_Sort Field',
      sortOrder: 'ja_Sort Order',
      filter: 'ja_Filter'
    }
  },
  packager: {
    errorMessages: {
      invalidIP: 'ja_Please enter valid IP address',
      invalidPort: 'ja_Please enter valid port number',
      invalidName: 'ja_Please enter a valid name without special characters',
      passwordEmptyMessage: 'ja_Please enter certificate password',
      NAME_EMPTY: 'ja_Warning: The configuration name is empty.',
      SERVERS_EMPTY: 'ja_Warning: No servers are found.',
      EVENT_ID_INVALID: 'ja_Warning: Event ID is invalid.',
      CHANNEL_EMPTY: 'ja_Warning: Channel is empty.',
      FILTER_EMPTY: 'ja_Warning: Filter is empty.',
      FILTER_INVALID: 'ja_Warning: Filter is invalid.',
      INVALID_HOST: 'ja_Warning: Host is invalid.',
      CONFIG_NAME_INVALID: 'ja_Warning: The config name is invalid.',
      INVALID_PROTOCOL: 'ja_Warning: The protocol is invalid.',
      CHANNEL_NAME_INVALID: 'ja_Warning: The channel name is invalid.',
      EMPTY_CHANNELS: 'ja_Warning: The channel name is empty.',
      CHANNEL_FILTER_INVALID: 'ja_Warning: The channel filter is invalid.'
    },
    packagerTitle: 'ja_Packager',
    serviceName: 'ja_Service Name*',
    server: 'ja_Server*',
    port: 'ja_HTTPS Port*',
    certificateValidation: 'ja_Server Validation',
    certificatePassword: 'ja_Certificate Password*',
    none: 'ja_None',
    fullChain: 'ja_Full Chain',
    generateLogConfig: 'ja_Generate Log Configuration Only',
    loadExistingLogConfig: 'ja_Load Existing Configuration...',
    thumbprint: 'ja_Certificate Thumbprint',
    reset: 'ja_Reset',
    generateAgent: 'ja_Generate Agent',
    description: 'ja_Description',
    title: 'Packager',
    becon: 'ja_Becon',
    displayName: 'ja_Display Name*',
    upload: {
      success: 'ja_Configuration file loaded successfully.',
      failure: 'ja_Unable to upload Configuration file.'
    },
    error: {
      generic: 'ja_An unexpected error has occurred attempting to retrieve this data.'
    },
    autoUninstall: 'ja_Auto Uninstall',
    forceOverwrite: 'ja_Force Overwrite',
    windowsLogCollectionCongfig: 'ja_Windows Log Collection Configuration',
    enableWindowsLogCollection: 'ja_Enable Windows Log Collection',
    configurationName: 'ja_Configuration Name*',
    primaryLogDecoder: 'ja_Primary Log Decoder/Log collector*',
    secondaryLogDecoder: 'ja_Secondary Log Decoder/Log Collector',
    protocol: 'ja_Protocol',
    channels: 'ja_Channel filters',
    eventId: 'ja_event id to include/exclude (?)',
    heartbeatLogs: 'ja_Send Heartbeat logs',
    heartbeatFrequency: 'ja_Heartbeat Frequency',
    testLog: 'ja_Send Test Log',
    placeholder: 'ja_Make a selection',
    searchPlaceholder: 'ja_Enter the filter option',
    emptyName: 'ja_Configuration name is empty',
    channelFilter: 'ja_Channel Filters',
    specialCharacter: 'ja_Configuration name contains special character.',
    channel: {
      add: 'ja_Add a new channel',
      name: 'ja_CHANNEL NAME *',
      filter: 'ja_FILTER *',
      event: 'ja_EVENT ID *',
      empty: ''
    }
  },
  investigateFiles: {
    title: 'ja_Files',
    deleteTitle: 'ja_Confirm Delete',
    button: {
      exportToCSV: 'ja_Export to CSV',
      downloading: 'ja_Downloading',
      save: 'ja_Save',
      reset: 'ja_Reset',
      cancel: 'ja_Cancel'
    },
    message: {
      noResultsMessage: 'ja_No matching files were found'
    },
    errorPage: {
      serviceDown: 'Endpoint Server is offline',
      serviceDownDescription: 'The Endpoint Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
    },
    footer: 'ja_{{count}} of {{total}} {{label}}',
    filter: {
      filter: 'ja_Filter',
      filters: 'ja_Saved Filters',
      newFilter: 'ja_New Filter',
      windows: 'ja_WINDOWS',
      mac: 'ja_MAC',
      linux: 'ja_LINUX',
      favouriteFilters: 'ja_Favorite Filters',
      addMore: 'ja_Add More...',
      restrictionType: {
        moreThan: 'ja_Greater than',
        lessThan: 'ja_Less than',
        between: 'ja_Between',
        equals: 'ja_Equals',
        contains: 'ja_Contains'
      },
      customFilters: {
        save: {
          description: 'ja_Provide a name to the search to be saved. This name will appear in the search box list.',
          name: 'ja_Name *',
          errorHeader: 'ja_Unable to save search',
          header: 'ja_Save search',
          errorMessage: 'ja_The search cannot be saved. ',
          emptyMessage: 'ja_Name field is empty.',
          nameExistsMessage: 'ja_A saved search with the same name.',
          success: 'ja_Search query saved successfully.',
          filterFieldEmptyMessage: 'ja_Filter fields are empty',
          invalidInput: 'ja_Please enter a valid name (Only \'-\' and \'_\' special characters are allowed.)'
        },
        delete: {
          successMessage: 'ja_Query deleted successfully.',
          confirmMessage: 'ja_Are you sure you want to delete the selected query?'
        }
      }
    },
    fields: {
      panelTitle: 'ja_Files Preferences',
      triggerTip: 'ja_Open/Hide Files Preferences',
      id: 'ja_ID',
      companyName: 'ja_Company Name',
      checksumMd5: 'ja_MD5',
      checksumSha1: 'ja_SHA1',
      checksumSha256: 'ja_SHA256',
      machineOsType: 'ja_Operating System',
      elf: {
        classType: 'ja_ELF.Class Type',
        data: 'ja_ELF.Data',
        entryPoint: 'ja_ELF.Entry Point',
        features: 'ja_ELF.Features',
        type: 'ja_ELF.Type',
        sectionNames: 'ja_ELF.Section Names',
        importedLibraries: 'ja_ELF.Imported Libraries'
      },
      pe: {
        timeStamp: 'ja_PE.Timestamp',
        imageSize: 'ja_PE.Image Size',
        numberOfExportedFunctions: 'ja_PE.Exported Functions',
        numberOfNamesExported: 'ja_PE.Exported Names',
        numberOfExecuteWriteSections: 'ja_PE.Execute Write Sections',
        features: 'ja_PE.Features',
        sectionNames: 'ja_PE.Section Names',
        importedLibraries: 'ja_PE.Imported Librabries',
        resources: {
          originalFileName: 'ja_PE.Resources.Filename',
          company: 'ja_PE.Resources.Company',
          description: 'ja_PE.Resources.Description',
          version: 'ja_PE.Resources.Version'
        }
      },
      macho: {
        uuid: 'ja_MachO.Uuid',
        identifier: 'ja_MachO.Identifier',
        minOsxVersion: 'ja_MachO.Osx Version',
        features: 'ja_MachO.Features',
        flags: 'ja_MachO.Flags',
        numberOfLoadCommands: 'ja_MachO.Loaded Commands',
        version: 'ja_MachO.Version',
        sectionNames: 'ja_MachO.Section Names',
        importedLibraries: 'ja_MachO.Imported Libraries'
      },
      signature: {
        timeStamp: 'ja_Signature.Timestamp',
        thumbprint: 'ja_Signature.Thumbprint',
        features: 'ja_Signature',
        signer: 'ja_Signer'
      },
      owner: {
        userName: 'ja_Owner',
        groupName: 'ja_Owner Group'
      },
      rpm: {
        packageName: 'ja_Package'
      },
      path: 'ja_Path',
      entropy: 'ja_Entropy',
      fileName: 'ja_FileName',
      firstFileName: 'ja_FileName',
      firstSeenTime: 'ja_First Seen Time',
      timeCreated: 'ja_Created',
      format: 'ja_Format',
      sectionNames: 'ja_Section Names',
      importedLibraries: 'ja_Imported Libraries',
      size: 'ja_Size'

    },
    sort: {
      fileNameDescending: 'ja_Filename (descending)',
      fileNameAscending: 'ja_Filename (ascending)',
      sizeAscending: 'ja_Size (ascending)',
      sizeDescending: 'ja_Size (descending)',
      formatAscending: 'ja_Format (ascending)',
      formatDescending: 'ja_Format (descending)',
      signatureAscending: 'ja_Signature (ascending)',
      signatureDescending: 'ja_Signature (descending)'
    }
  },
  investigateHosts: {
    title: 'ja_Investigate',
    loading: 'ja_Loading',
    loadMore: 'ja_Load More',
    deleteTitle: 'ja_Confirm Delete',
    noSnapshotMessage: 'ja_No scan history were found.',
    common: {
      save: 'ja_Save',
      enable: 'ja_Enable',
      saveSuccess: 'ja_Saved successfully',
      emptyMessage: 'ja_No matching results'
    },
    errorPage: {
      serviceDown: 'js_Endpoint Server is offline',
      serviceDownDescription: 'ja_The Endpoint Server is not running or is inaccessible. Check with your administrator to resolve this issue.'
    },
    property: {
      file: {
        companyName: 'ja_Company Name',
        checksumMd5: 'ja_MD5',
        checksumSha1: 'ja_SHA1',
        checksumSha256: 'ja_SHA256',
        machineOsType: 'ja_Operating System',
        timeCreated: 'ja_Created',
        timeModified: 'ja_Modified',
        timeAccessed: 'ja_Accessed',
        createTime: 'ja_Process Created',
        pid: 'ja_PID',
        eprocess: 'ja_EPROCESS',
        path: 'ja_Full Path',
        sameDirectoryFileCounts: {
          nonExe: 'ja_# Non-Executables',
          exe: 'ja_# Executables',
          subFolder: 'ja_# Folder',
          exeSameCompany: 'ja_#Same company Executables'
        },
        elf: {
          classType: 'ja_Class Type',
          data: 'ja_Data',
          entryPoint: 'ja_Entry Point',
          features: 'ja_Features',
          type: 'ja_Type',
          sectionNames: 'ja_Section Names',
          importedLibraries: 'ja_Imported Libraries'
        },
        pe: {
          timeStamp: 'ja_Timestamp',
          imageSize: 'ja_Image Size',
          numberOfExportedFunctions: 'ja_Exported Functions',
          numberOfNamesExported: 'ja_Exported Names',
          numberOfExecuteWriteSections: 'ja_Execute Write Sections',
          features: 'ja_Features',
          sectionNames: 'ja_Section Names',
          importedLibraries: 'ja_Imported Libraries',
          resources: {
            originalFileName: 'ja_Filename',
            company: 'ja_Company',
            description: 'ja_Description',
            version: 'ja_Version'
          }
        },
        macho: {
          uuid: 'ja_Uuid',
          identifier: 'ja_Identifier',
          minOsxVersion: 'ja_Osx Version',
          features: 'ja_Features',
          flags: 'ja_Flags',
          numberOfLoadCommands: 'ja_Loaded Commands',
          version: 'ja_Version',
          sectionNames: 'ja_Section Names',
          importedLibraries: 'ja_Imported Libraries'
        },
        signature: {
          timeStamp: 'ja_Timestamp',
          thumbprint: 'ja_Thumbprint',
          features: 'ja_Signature',
          signer: 'ja_Signer'
        },
        process: {
          title: 'ja_Process',
          processName: 'ja_Process Name',
          eprocess: 'ja_EPROCESS',
          integrityLevel: 'ja_Integrity',
          parentPath: 'ja_Parent Path',
          threadCount: 'ja_Thread Count',
          owner: 'ja_Owner',
          sessionId: 'ja_Session ID',
          createUtcTime: 'ja_Created',
          imageBase: 'ja_Image Base',
          imageSize: 'ja_Image Size'
        },
        entropy: 'ja_Entropy',
        firstFileName: 'ja_FileName',
        format: 'ja_Format',
        sectionNames: 'ja_Section Names',
        importedLibraries: 'ja_Imported Libraries',
        size: 'ja_Size',
        imageBase: 'ja_Image Base',
        imageSize: 'ja_Image Size',
        loaded: 'ja_Loaded'
      }
    },
    tabs: {
      overview: 'ja_Overview',
      process: 'ja_Processes',
      autoruns: 'ja_Autoruns',
      files: 'ja_Files',
      drivers: 'ja_Drivers',
      systemInformation: 'ja_System Information',
      services: 'ja_Services',
      tasks: 'ja_Tasks',
      hostFileEntries: 'ja_Host File Entries',
      mountedPaths: 'ja_Mounted Paths',
      networkShares: 'ja_Network Shares',
      bashHistories: 'ja_Bash History',
      libraries: 'ja_Libraries',
      explore: 'ja_explore',
      securityProducts: 'ja_Security Products',
      windowsPatches: 'ja_Windows Patches'

    },
    systemInformation: {
      ipAddress: 'ja_IP Address',
      dnsName: 'ja_DNS Name',
      fileSystem: 'ja_File System',
      path: 'ja_Path',
      remotePath: 'ja_Remote Path',
      options: 'ja_Options',
      name: 'ja_Name',
      description: 'ja_Description',
      permissions: 'ja_Permissions',
      type: 'ja_Type',
      maxUses: 'ja_Max Users',
      currentUses: 'ja_Current Users',
      userName: 'ja_User Name',
      command: 'ja_Command',
      commandNote: 'ja_Note: Most recent commands are on the top',
      filterUser: 'ja_Type to filter user',
      filterBy: 'ja_Filter By User',
      patches: 'ja_Patches',
      securityProducts: {
        type: 'ja_Type',
        instance: 'ja_Instance',
        displayName: 'ja_Display Name',
        companyName: 'ja_Company Name',
        version: 'ja_Version',
        features: 'ja_Features'
      }
    },
    process: {
      title: 'ja_Process',
      processName: 'ja_Process Name',
      properties: 'ja_Process Properties',
      pid: 'ja_PID',
      parentId: 'ja_PPID',
      owner: 'ja_Owner',
      hostCount: 'ja_Host Count',
      creationTime: 'ja_Creation Time',
      hashlookup: 'ja_Hash Lookup',
      signature: 'ja_Signature',
      path: 'ja_Path',
      launchArguments: 'ja_Launch Arguments',
      message: {
        noResultsMessage: 'ja_No process information were found'
      },
      dll: {
        dllName: 'ja_DLL Name',
        filePath: 'ja_File Path',
        title: 'ja_Loaded Libraries',
        message: {
          noResultsMessage: 'ja_No loaded library information were found'
        },
        note: {
          windows: 'ja_Note: Displays libraries that are not signed by Microsoft',
          mac: 'ja_Note: Displays libraries that are not signed by Apple.'
        }
      }
    },
    hosts: {
      title: 'ja_Hosts',
      search: 'ja_Filter',
      button: {
        addMore: 'ja_Add more...',
        loadMore: 'Load More',
        exportCSV: 'ja_Export to CSV',
        export: 'ja_Export to JSON',
        exportTooltip: 'ja_Exports all scan data categories for the host.',
        downloading: 'ja_Downloading',
        initiateScan: 'ja_Start Scan',
        cancelScan: 'ja_Stop Scan',
        delete: 'ja_Delete',
        cancel: 'ja_Cancel',
        save: 'ja_Save',
        saveAs: 'ja_Save As...',
        clear: 'ja_Clear',
        search: 'ja_Search',
        ok: 'ja_Ok',
        moreActions: 'ja_More Actions',
        explore: 'ja_Explore',
        gearIcon: 'ja_Click here to manage columns',
        overview: 'ja_Show/Hide Overview Panel',
        settings: 'ja_Settings',
        meta: 'ja_Show/Hide Meta',
        close: 'ja_Close Reconstruction',
        shrink: 'ja_Contract View',
        update: 'ja_update',
        reset: 'ja_Reset'
      },
      autoruns: {
        services: {
          initd: 'ja_INIT.D',
          systemd: 'ja_SYSTEM.D'
        }
      },
      ranas: {
        ranas: 'ja_Ran as',
        categories: {
          Process: 'ja_Process',
          Libraries: 'ja_Library',
          Autorun: 'ja_Autorun',
          Service: 'ja_Service',
          Task: 'ja_Task',
          Driver: 'ja_Driver',
          Thread: 'ja_Thread'
        }
      },
      explore: {
        input: {
          placeholder: 'ja_Search by filename, path or hash'
        },
        noResultsFound: 'ja_No Results Found.',
        fileName: 'ja_Filename: ',
        path: 'ja_Path: ',
        hash: 'ja_Hash: ',
        search: {
          minimumtext: {
            required: 'ja_For filename or path, enter minimum 3 characters. For hash, enter entire SHA-256 hash string'
          }
        }
      },
      footerLabel: {
        autoruns: {
          autoruns: 'ja_autoruns',
          services: 'ja_services',
          tasks: 'ja_tasks'
        },
        files: 'ja_files',
        drivers: 'ja_drivers',
        libraries: 'ja_libraries'
      },
      summary: {
        snapshotTime: 'ja_Snapshot Time',
        overview: {
          typeToFilterOptions: 'ja_Enter the filter option',
          noSnapShots: 'ja_No snapshots available'
        },
        body: {
          ipAddresses: 'ja_IP Addresses ({{count}})',
          securityConfig: 'ja_Security Configuration',
          loggedUsers: 'ja_Logged-in Users ({{count}})',
          user: {
            administrator: 'ja_Administrator',
            sessionId: 'ja_Session ID',
            sessionType: 'ja_Session Type',
            groups: 'ja_Groups'
          }
        },
        securityConfig: {
          arrangeBy: 'ja_ARRANGE BY',
          alphabetical: 'ja_Alphabetical',
          status: 'ja_Status'
        }
      },
      selected: 'ja_selected ({{count}})',
      list: {
        noResultsMessage: 'ja_No Results Found'
      },
      filters: {
        systemFilter: 'ja_This search is system defined and cannot be edited.',
        since: 'ja_Since',
        customDateRange: 'ja_Custom Date Range',
        customStartDate: 'ja_Start Date',
        customEndDate: 'ja_End Date',
        customDate: 'ja_Custom Date',
        operator: 'ja_Operator',
        searchPlaceHolder: 'ja_Enter the filter option',
        mutlipleValuesNote: 'ja_Note: To search multiple values, use || as a separator',
        invalidFilterInput: 'ja_Invalid filter input',
        inTimeRange: 'ja_In',
        notInTimeRange: 'ja_Not In',
        agentStatus: {
          lastSeenTime: 'ja_Agent Not Seen Since'
        }
      },
      restrictionTypeOptions: {
        EQUALS: 'ja_equals',
        CONTAINS: 'ja_contains',
        GT: '>',
        LT: '<',
        GTE: '>=',
        LTE: '<=',
        NOT_EQ: '!=',
        LESS_THAN: 'ja_Less than',
        GREATER_THAN: 'ja_Greater than',
        BETWEEN: 'ja_Between',
        LAST_5_MINUTES: 'ja_Last 5 Minutes',
        LAST_10_MINUTES: 'ja_Last 10 Minutes',
        LAST_15_MINUTES: 'ja_Last 15 Minutes',
        LAST_30_MINUTES: 'ja_Last 30 Minutes',
        LAST_HOUR: 'ja_Last 1 Hour',
        LAST_3_HOURS: 'ja_Last 3 Hours',
        LAST_6_HOURS: 'ja_Last 6 Hours',
        LAST_TWELVE_HOURS: 'ja_Last 12 Hours',
        LAST_TWENTY_FOUR_HOURS: 'ja_Last 24 Hours',
        LAST_FORTY_EIGHT_HOURS: 'ja_Last 2 Days',
        LAST_5_DAYS: 'ja_Last 5 Days',
        LAST_7_DAYS: 'ja_Last 7 Days',
        LAST_14_DAYS: 'ja_Last 14 Days',
        LAST_30_DAYS: 'ja_Last 30 Days',
        LAST_HOUR_AGO: 'ja_1 Hour ago',
        LAST_TWENTY_FOUR_HOURS_AGO: 'ja_24 Hours ago',
        LAST_5_DAYS_AGO: 'ja_5 Days ago',
        ALL_TIME: 'ja_All Data'
      },
      footer: 'ja_showing {{count}} of {{total}} ja_hosts',
      column: {
        panelTitle: 'ja_Hosts Preferences',
        triggerTip: 'ja_Open/Hide Hosts Preferences',
        id: 'ja_Agent ID',
        analysisData: {
          iocs: 'ja_IOC Alerts',
          machineRiskScore: 'ja_Risk Score'
        },
        agentStatus: {
          scanStatus: 'ja_Agent Scan Status',
          lastSeenTime: 'ja_Agent Last Seen'
        },
        machine: {
          machineOsType: 'ja_Operating System',
          machineName: 'ja_Hostname',
          id: 'ja_Agent ID',
          agentVersion: 'ja_Agent Version',
          scanStartTime: 'ja_Last Scan Time',
          scanRequestTime: 'ja_Scan Request Time',
          scanType: 'ja_Scan Type',
          scanTrigger: 'ja_Scan Trigger',
          securityConfigurations: 'ja_Security Configurations',
          hostFileEntries: {
            ip: 'ja_Host File IP',
            hosts: 'ja_Host Entires'
          },
          users: {
            name: 'ja_Username',
            sessionId: 'ja_Users - Session ID',
            sessionType: 'ja_Users - Session Type',
            isAdministrator: 'ja_Users - Is Admin',
            groups: 'ja_Users - Groups',
            domainUserQualifiedName: 'ja_Users - QualifiedName',
            domainUserId: 'ja_Users - Domain UserID',
            domainUserOu: 'ja_Users - Domain User OU',
            domainUserCanonicalOu: 'ja_Users - Domain User Canonical OU',
            host: 'ja_Users - Host',
            deviceName: 'ja_Users - DeviceName'
          },
          errors: {
            time: 'ja_Error - Time',
            fileID: 'ja_Error - FileID',
            line: 'ja_Error - Line',
            number: 'ja_Error - Number',
            value: 'ja_Error - Value',
            param1: 'ja_Error - Param1',
            param2: 'ja_Error - Param2',
            param3: 'ja_Error - Param3',
            info: 'ja_Error - Info',
            level: 'ja_Error - Level',
            type: 'ja_Error - Type'
          },
          networkShares: {
            path: 'ja_NetworkShare - Path',
            name: 'ja_NetworkShare - Name',
            description: 'ja_NetworkShare - Description',
            type: 'ja_NetworkShare - Type',
            permissions: 'ja_NetworkShare - Permissions',
            maxUses: 'ja_NetworkShare - MaxUses',
            currentUses: 'ja_NetworkShare - CurrentUses'
          },
          mountedPaths: {
            path: 'ja_MountedPaths - Path',
            fileSystem: 'ja_MountedPaths - FileSystem',
            options: 'ja_MountedPaths - Options',
            remotePath: 'ja_MountedPaths - RemotePath'
          },
          securityProducts: {
            type: 'ja_SecurityProducts - Type',
            instance: 'ja_SecurityProducts - Instance',
            displayName: 'ja_SecurityProducts - DisplayName',
            companyName: 'ja_SecurityProducts - CompanyName',
            version: 'ja_SecurityProducts - Version',
            features: 'ja_SecurityProducts - Features'
          },
          networkInterfaces: {
            name: 'ja_NIC Name',
            macAddress: 'ja_NIC MAC Address',
            networkId: 'ja_NetworkInterface - NerworkID',
            ipv4: 'ja_IPv4',
            ipv6: 'ja_IPv6',
            gateway: 'ja_NetworkInterface - Gateway',
            dns: 'ja_NetworkInterface - DNS',
            promiscuous: 'ja_NIC Promiscous'
          }
        },
        riskScore: {
          moduleScore: 'ja_Module Score',
          highestScoringModules: 'ja_Highest Scoring Module'
        },
        machineIdentity: {
          machineName: 'ja_Hostname',
          group: 'ja_Agent Group',
          agentMode: 'ja_Agent Mode',
          agent: {
            exeCompileTime: 'ja_Agent - User Mode Compile Time',
            sysCompileTime: 'ja_Agent - Driver Compile Time',
            packageTime: 'ja_Agent - Package Time',
            installTime: 'ja_Agent - Install Time',
            serviceStartTime: 'ja_Agent - Service Start Time',
            serviceProcessId: 'ja_Agent - Service Process ID',
            serviceStatus: 'ja_Agent - Service Status',
            driverStatus: 'ja_Agent - Driver Status',
            blockingEnabled: 'ja_Agent - Blocking Enabled',
            blockingUpdateTime: 'ja_Agent - Blocking Update Time'
          },
          operatingSystem: {
            description: 'ja_OS - Description',
            buildNumber: 'ja_OS - Build Number',
            servicePack: 'ja_OS - Service Pack',
            directory: 'ja_OS - Directory',
            kernelId: 'ja_OS - Kernel Id',
            kernelName: 'ja_OS - Kernel Name',
            kernelRelease: 'ja_OS - Kernel Release',
            kernelVersion: 'ja_OS - Kernel Version',
            distribution: 'ja_OS - Distribution',
            domainComputerId: 'ja_OS - Domain ComputerID',
            domainComputerOu: 'ja_OS - Domain Computer OU',
            domainComputerCanonicalOu: 'ja_OS - Domain Computer Canonical OU',
            domainOrWorkgroup: 'ja_OS - DomainOrWorkgroup',
            domainRole: 'ja_OS - DomainRole',
            lastBootTime: 'ja_OS - Last BootTime'
          },
          hardware: {
            processorArchitecture: 'ja_Hardware - Processor Architecture',
            processorArchitectureBits: 'ja_Hardware - Processor Architecture Bits',
            processorCount: 'ja_Hardware - Processor Count',
            processorName: 'ja_Hardware - Processor Name',
            totalPhysicalMemory: 'ja_Hardware - Total Physical Memory',
            chassisType: 'ja_Hardware - ChassisType',
            manufacturer: 'ja_Hardware - Manufacturer',
            model: 'ja_Hardware - Model',
            serial: 'ja_Hardware - Serial',
            bios: 'ja_Hardware - Bios'
          },
          locale: {
            defaultLanguage: 'ja_Locale - Default Language',
            isoCountryCode: 'ja_Locale - Country Code',
            timeZone: 'ja_Locale - Time Zone'
          },
          knownFolder: {
            appData: 'ja_Folder - AppData',
            commonAdminTools: 'ja_Folder - Common Admin Tools',
            commonAppData: 'ja_Folder - Common App Data',
            commonDestop: 'ja_Folder - Common Desktop',
            commonDocuments: 'ja_Folder - Common Documents',
            commonProgramFiles: 'ja_Folder - Common Program Files',
            commonProgramFilesX86: 'ja_Folder - Common Program Files (x86)',
            commonPrograms: 'ja_Folder - Common Programs',
            commonStartMenu: 'ja_Folder - Common Start Menu',
            commonStartup: 'ja_Folder - Common Startup',
            desktop: 'ja_Folder - Desktop',
            localAppData: 'ja_Folder - LocalAppData',
            myDocuments: 'ja_Folder - My Documents',
            programFiles: 'ja_Folder - Program Files',
            programFilesX86: 'ja_Folder - Program Files (x86)',
            programs: 'ja_Folder - Programs',
            startMenu: 'ja_Folder - Start Menu',
            startup: 'ja_Folder - Startup',
            system: 'ja_Folder - System',
            systemX86: 'ja_Folder - System (x86)',
            windows: 'ja_Folder - Windows'
          }
        },
        markedForDeletion: 'ja_Marked For Deletion'
      },
      properties: {
        title: 'ja_Host Properties',
        filter: 'ja_Type to filter list',
        checkbox: 'ja_Show properties with values only',
        machine: {
          securityConfigurations: 'ja_Security Configurations',
          hostFileEntries: {
            title: 'ja_Host File Entries',
            ip: 'ja_Host File IP',
            hosts: 'ja_Host Entires'
          },
          users: {
            title: 'ja_User',
            name: 'ja_Name',
            sessionId: 'ja_Session ID',
            sessionType: 'ja_Session Type',
            isAdministrator: 'ja_Is Admin',
            administrator: 'ja_Is Admin',
            groups: 'ja_Groups',
            domainUserQualifiedName: 'ja_QualifiedName',
            domainUserId: 'ja_Domain UserID',
            domainUserOu: 'ja_Domain User OU',
            domainUserCanonicalOu: 'ja_Domain User Canonical OU',
            host: 'ja_Host',
            deviceName: 'ja_DeviceName'
          },
          networkInterfaces: {
            title: 'ja_Network Interfaces',
            name: 'ja_Name',
            macAddress: 'ja_MAC Address',
            networkId: 'ja_NerworkID',
            ipv4: 'ja_IPv4',
            ipv6: 'ja_IPv6',
            gateway: 'ja_Gateway',
            dns: 'ja_DNS',
            promiscuous: 'ja_Promiscous'
          }
        },
        machineIdentity: {
          agent: {
            agentId: 'ja_Agent ID',
            agentMode: 'ja_Agent Mode',
            agentVersion: 'ja_Agent Version',
            title: 'ja_Agent',
            exeCompileTime: 'ja_User Mode Compile Time',
            sysCompileTime: 'ja_Driver Compile Time',
            packageTime: 'ja_Package Time',
            installTime: 'ja_Install Time',
            serviceStartTime: 'ja_Service Start Time',
            serviceProcessId: 'ja_Service Process ID',
            serviceStatus: 'ja_Service Status',
            driverStatus: 'ja_Driver Status',
            blockingEnabled: 'ja_Blocking Enabled',
            blockingUpdateTime: 'ja_Blocking Update Time'
          },
          operatingSystem: {
            title: 'ja_Operating System',
            description: 'ja_Description',
            buildNumber: 'ja_Build Number',
            servicePack: 'ja_Service Pack',
            directory: 'ja_Directory',
            kernelId: 'ja_Kernel Id',
            kernelName: 'ja_Kernel Name',
            kernelRelease: 'ja_Kernel Release',
            kernelVersion: 'ja_Kernel Version',
            distribution: 'ja_Distribution',
            domainComputerId: 'ja_Domain ComputerID',
            domainComputerOu: 'ja_Domain Computer OU',
            domainComputerCanonicalOu: 'ja_Domain Computer Canonical OU',
            domainOrWorkgroup: 'ja_DomainOrWorkgroup',
            domainRole: 'ja_DomainRole',
            lastBootTime: 'ja_Last BootTime'
          },
          hardware: {
            title: 'ja_Hardware',
            processorArchitecture: 'ja_Processor Architecture',
            processorArchitectureBits: 'ja_Processor Architecture Bits',
            processorCount: 'ja_Processor Count',
            processorName: 'ja_Processor Name',
            totalPhysicalMemory: 'ja_Total Physical Memory',
            chassisType: 'ja_ChassisType',
            manufacturer: 'ja_Manufacturer',
            model: 'ja_Model',
            serial: 'ja_Serial',
            bios: 'ja_Bios'
          },
          locale: {
            title: 'ja_Locale',
            defaultLanguage: 'ja_Default Language',
            isoCountryCode: 'ja_Country Code',
            timeZone: 'ja_Time Zone'
          }
        }
      },
      propertyPanelTitles: {
        autoruns: {
          autorun: 'ja_Autoruns Properties',
          services: 'ja_Services Properties',
          tasks: 'ja_Tasks Properties'
        },
        files: 'ja_Files Properties',
        drivers: 'ja_Drivers Properties',
        libraries: 'ja_Libraries Properties'
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
      },
      customFilter: {
        save: {
          description: 'ja_Provide a name to the search. This name will appear in the search list.',
          name: 'ja_Name *',
          errorHeader: 'ja_Unable to save search',
          header: 'ja_Save search',
          errorMessage: 'ja_The search cannot be saved. ',
          emptyMessage: 'ja_Name field is empty.',
          nameExistsMessage: 'ja_A saved search with the same name.',
          success: 'ja_Search query saved successfully.',
          filterFieldEmptyMessage: 'ja_Filter fields are empty',
          invalidInput: 'ja_Only \'-\' and \'_\' special characters are allowed.'
        },
        update: {
          success: 'ja_Search query updated successfully.'
        }
      },
      initiateScan: {
        modal: {
          title: 'ja_Start Scan for {{count}} host(s)',
          modalTitle: 'ja_Start Scan for {{name}}',
          description: 'ja_Select the type of scan for the selected host(s).',
          error1: 'ja_*Select atleast one host',
          error2: 'ja_*Maximum 100 hosts allowed to start scan',
          infoMessage: 'ja_Some of the selected hosts are already being scanned, so a new scan will not be started for them.',
          ecatAgentMessage: 'ja_Some of the selected hosts are 4.4 agents, so a scan will not be started for them.',
          quickScan: {
            label: 'ja_Quick Scan (Default)',
            description: 'ja_Performs a quick scan of all executable modules loaded in memory. Takes approximately 10 minutes.'
          }
        },
        success: 'ja_Scan initiated successfully',
        error: 'ja_Scan initiate failed'
      },
      cancelScan: {
        modal: {
          title: 'ja_Stop Scan for {{count}} host(s)',
          description: 'ja_Are you sure, you want to stop scanning the selected host(s)?',
          error1: 'ja_*Select at least one host'
        },
        success: 'ja_Stop scan initiated successfully',
        error: 'ja_Stop Scan initiate failed'
      },
      deleteHosts: {
        modal: {
          title: 'ja_Delete {{count}} host(s)',
          message: 'ja_Delete the host if the agent is uninstalled or scan data is not required. ' +
          'Deletes all scan data related to the host. Are you sure you want to continue?'
        },
        success: 'ja_Host(s) are deleted successfully',
        error: 'ja_Host(s) deletion failed'
      },
      moreActions: {
        openIn: 'ja_Pivot to Endpoint',
        openInErrorMessage: 'ja_Select at least one host',
        notAnEcatAgent: 'ja_Select only the 4.4 agent(s)',
        cancelScan: 'ja_Stop Scan'
      }
    },
    savedQueries: {
      headerContent: 'ja_Select a saved query from the list to run it.You can also edit the name of the saved query by clicking the pencil icon next to the name,and set it as default by clicking the star icon.',
      deleteBtn: 'ja_Delete selected',
      runBtn: 'ja_Run selected',
      yesBtn: 'ja_Yes',
      noBtn: 'ja_No',
      delete: {
        successMessage: 'ja_Query deleted successfully.',
        confirmMessage: 'ja_Are you sure you want to delete the selected query?'
      },
      edit: {
        successMessage: 'ja_Query name updated successfully',
        errorMessage: 'ja_Query name updation failed',
        nameExistsMessage: 'ja_Query name already exists'
      }
    },
    files: {
      footer: '{{count}} of {{total}} {{label}}',
      filter: {
        filters: 'ja_Filters',
        newFilter: 'ja_New Filter',
        windows: 'ja_WINDOWS',
        mac: 'ja_MAC',
        linux: 'ja_LINUX',
        favouriteFilters: 'ja_Favorite Filters',
        restrictionType: {
          moreThan: 'ja_Greater than',
          lessThan: 'ja_Less than',
          between: 'ja_Between',
          equals: 'ja_Equals',
          contains: 'ja_Contains'
        },
        save: 'ja_Save',
        reset: 'ja_Reset',
        customFilters: {
          save: {
            description: 'ja_Provide a name to the search. This name will appear in the search list.',
            name: 'ja_Name *',
            errorHeader: 'ja_Unable to save search',
            header: 'ja_Save search',
            errorMessage: 'ja_The search cannot be saved. ',
            emptyMessage: 'ja_Name field is empty.',
            nameExistsMessage: 'ja_A saved search with the same name.',
            success: 'ja_Search query saved successfully.',
            filterFieldEmptyMessage: 'ja_Filter fields are empty',
            invalidInput: 'ja_Only \'-\' and \'_\' special characters are allowed.'
          }
        },
        button: {
          cancel: 'ja_Cancel',
          save: 'ja_Save'
        }
      },
      fields: {
        id: 'ja_ID',
        firstSeenTime: 'ja_First Seen Time',
        companyName: 'ja_Company Name',
        checksumMd5: 'ja_MD5',
        checksumSha1: 'ja_SHA1',
        checksumSha256: 'ja_SHA256',
        machineOsType: 'ja_Operating System',
        elf: {
          classType: 'ja_ELF.Class Type',
          data: 'ja_ELF.Data',
          entryPoint: 'ja_ELF.Entry Point',
          features: 'ja_ELF.Features',
          type: 'ja_ELF.Type',
          sectionNames: 'ja_ELF.Section Names',
          importedLibraries: 'ja_ELF.Imported Libraries'
        },
        pe: {
          timeStamp: 'ja_PE.Timestamp',
          imageSize: 'ja_PE.Image Size',
          numberOfExportedFunctions: 'ja_PE.Exported Functions',
          numberOfNamesExported: 'ja_PE.Exported Names',
          numberOfExecuteWriteSections: 'ja_PE.Execute Write Sections',
          features: 'ja_PE.Features',
          sectionNames: 'ja_PE.Section Names',
          importedLibraries: 'ja_PE.Imported Librabries',
          resources: {
            originalFileName: 'ja_PE.Resources.Filename',
            company: 'ja_PE.Resources.Company',
            description: 'ja_PE.Resources.Description',
            version: 'ja_PE.Resources.Version'
          }
        },
        macho: {
          uuid: 'ja_MachO.Uuid',
          identifier: 'ja_MachO.Identifier',
          minOsxVersion: 'ja_MachO.Osx Version',
          features: 'ja_MachO.Features',
          flags: 'ja_MachO.Flags',
          numberOfLoadCommands: 'ja_MachO.Loaded Commands',
          version: 'ja_MachO.Version',
          sectionNames: 'ja_MachO.Section Names',
          importedLibraries: 'ja_MachO.Imported Libraries'
        },
        signature: {
          timeStamp: 'ja_Signature.Timestamp',
          thumbprint: 'ja_Signature.Thumbprint',
          features: 'ja_Signature',
          signer: 'ja_Signer'
        },
        owner: {
          userName: 'ja_Owner',
          groupName: 'ja_Owner Group'
        },
        rpm: {
          packageName: 'ja_Package'
        },
        path: 'ja_Path',
        entropy: 'ja_Entropy',
        fileName: 'ja_FileName',
        firstFileName: 'ja_FileName',
        timeCreated: 'ja_Created',
        format: 'ja_Format',
        sectionNames: 'ja_Section Names',
        importedLibraries: 'ja_Imported Libraries',
        size: 'ja_Size'
      }
    },
    pivotToInvestigate: {
      title: 'ja_Select Service',
      buttonText: 'ja_Navigate',
      iconTitle: 'ja_Pivot to Investigate'
    }
  },
  hostsScanConfigure: {
    title: 'ja_Scan Schedule',
    save: 'js_Save',
    enable: 'ja_Enable',
    saveSuccess: 'ja_Saved successfully',
    startDate: 'ja_Start Date',
    recurrenceInterval: {
      title: 'ja_Recurrence Interval',
      options: {
        daily: 'ja_Daily',
        weekly: 'ja_Weekly',
        monthly: 'ja_Monthly'
      },
      every: 'ja_Every',
      on: 'ja_On',
      intervalText: {
        DAYS: 'ja_day(s)',
        WEEKS: 'ja_week(s)',
        MONTHS: 'ja_month(s)'
      },
      week: {
        monday: 'ja_M',
        tuesday: 'ja_T',
        wednesday: 'ja_W',
        thursday: 'ja_T',
        friday: 'ja_F',
        saturday: 'ja_S',
        sunday: 'ja_S'
      }
    },
    startTime: 'ja_Start Time',
    cpuThrottling: {
      title: 'ja_Agent CPU Throttling',
      cpuMax: 'ja_CPU Max',
      vmMax: 'ja_VM Max'
    }
  }
};
