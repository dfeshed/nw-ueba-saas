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
    save: 'ja_Save'
  },
  tables: {
    noResults: 'ja_No Results'
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
    userLocked: 'ja_User account is locked',
    userDisabled: 'ja_User account is disabled',
    userExpired: 'ja_User account has expired',
    changePasswordSoon: 'ja_Please note that your password to the RSA NetWitness Server will expire in {{daysRemaining}} day(s). We encourage you to change the password before it expires. To change your password, click the preferences button on the top right of the application window.',
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
    defaultLandingPage: {
      label: 'ja_Default Landing Page',
      monitor: 'ja_Monitor',
      investigate: 'ja_Investigate',
      investigateClassic: 'ja_Investigate',
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
    extractWarning: '<span>ja_You have navigated away before the downloaded files could be attached to the browser tray. Your download will be available <a href="{{url}}" target="_blank">here</a>.</span>',
    titleBar: {
      titles: {
        endpoint: 'ja_Endpoint Event Details',
        network: 'ja_Network Event Details',
        log: 'ja_Log Event Details'
      },
      views: {
        text: 'ja_Text Analysis',
        packet: 'ja_Packet Analysis',
        file: 'ja_File Analysis'
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
      noTextContentData: 'ja_No text data was generated during content reconstruction. This could mean that the event data was corrupt or invalid. Check the other reconstruction views.'
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
      nweCallbackId: 'ja_NWE Server',
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
      nweFileFilename: 'ja_File Filename',
      nweFileIiocScore: 'ja_File IIOC Score',
      nweProcessFilename: 'ja_Process Filename',
      nweProcessPath: 'ja_Process Path',
      nweDllFilename: 'ja_DLL Filename',
      nweDllPath: 'ja_DLL Path',
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
      toggle: 'ja_Show/Hide Events Panel'
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
      title: 'ja_All Events',
      error: 'ja_An unexpected error occurred when executing this query.',
      shrink: 'ja_Shrink Events Panel',
      expand: 'ja_Expand Events Panel',
      close: 'ja_Close Events Panel'
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
      remediationTasks: 'ja_Remediation Tasks',
      alerts: 'ja_Alerts',
      actionMessages: {
        updateSuccess: 'ja_Your change was successful',
        updateFailure: 'ja_There was a problem updating the field for this record',
        createSuccess: 'ja_You successfully added a new record',
        createFailure: 'ja_There was a problem creating this record',
        deleteFailure: 'ja_There was a problem deleting this record'
      },
      alert: 'ja_Alert'
    },
    explorer: {
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
      newTaskFor: 'ja_New Remediation Task for',
      delete: 'ja_Delete Task',
      noAccess: 'ja_You do not have permissions to view remediation tasks',
      actions: {
        actionMessages: {
          deleteWarning: 'ja_Deleting a Remediation Task from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
          'to delete the task from any other applicable systems.'
        }
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
      actions: {
        addEntryLabel: 'ja_Add Entry',
        confirmUpdateTitle: 'ja_Confirm update',
        changeAssignee: 'ja_Change Assignee',
        changePriority: 'ja_Change Priority',
        changeStatus: 'ja_Change Status',
        addJournalEntry: 'ja_Add Journal Entry',
        actionMessages: {
          deleteWarning: 'ja_Warning: You are about to delete one or more incidents which may have remediation tasks and which may have been escalated. ' +
          'Deleting an incident from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
          'to delete the incident and its remediation tasks from any other applicable systems.',
          addJournalEntrySuccess: 'ja_You added a journal entry to incident {{incidentId}}',
          addJournalEntryFailure: 'ja_There was a problem adding a journal entry to incident {{incidentId}}'
        },
        deselectAll: 'ja_Deselect all'
      },
      filters: {
        timeRange: 'ja_Time Range',
        reset: 'ja_Reset Filters',
        customDateRange: 'ja_Custom Date Range',
        customStartDate: 'ja_Start Date',
        customEndDate: 'ja_End Date',
        customDateErrorStartAfterEnd: 'ja_The start date and time cannot be the same or later than the end date'
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
        'Reporting Engine': 'ja_Reporting Engine'
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
      events: 'ja_Events',
      view: {
        graph: 'ja_View: Graph',
        datasheet: 'ja_View: Datasheet'
      },
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
            HOST: 'ja_Enter a host name',
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
          label: 'ja_Look in'
        },
        results: {
          title: 'ja_Indicators for',
          openInNewWindow: 'ja_Open in new window'
        },
        actions: {
          search: 'ja_Find',
          cancel: 'ja_Cancel',
          addToIncident: 'ja_Add To Incident'
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
        }
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
      REMEDIATION_REQUESTED: 'ja_Remediation Requested',
      REMEDIATION_COMPLETE: 'ja_Remediation Complete',
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
    marketingText: 'ja_ is not a currently configured data source in Context Hub. Contact your Administrator to enable this feature. Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more sources on-demand. For more information , click ',
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
      macAddress: 'ja_Mac Address',
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
      hostName: 'ja_Host Name',
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
      listName: 'ja_Enter List Name'
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
      wohisFax: 'ja_Fax',
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
      skillLevelPrefix: 'ja_Tier {{level}}'
    },
    error: {
      error: 'ja_An unexpected error occurred when attempting to fetch the data.',
      noDataSource: 'ja_No data source configured/enabled.',
      dataSourcesFailed: 'ja_Unable to fetch data from the configured data sources.',
      dataSource: 'ja_An unexpected error occurred when attempting to fetch the data.',
      noData: 'ja_No context data is available for this DataSource.',
      listDuplicateName: 'ja_List name already exists!',
      listValidName: 'ja_Enter valid list name (Max length is 255 characters)',
      createList: 'ja_An unexpected error occurred while creating a new List.',
      'datasource.query.not.supported': 'ja_Context data lookup is not supported for this meta.',
      'transport.http.read.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.ad.read.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.init.failed': 'ja_Data source connection timed out.',
      'transport.not.found': 'ja_Context data is not available as the data source is not reachable.',
      'transport.create.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.refresh.failed': 'ja_Context data is not available as the data source is not reachable.',
      'transport.connect.failed': 'ja_Context data is not available as the data source is not reachable.',
      'live.connect.private.ip.unsupported': 'ja_Only public IP addresses are supported by Live Connect.'
    },
    footer: {
      viewAll: 'ja_VIEW All',
      total: 'ja_Showing {{count}}',
      title: {
        incidents: 'ja_Incident(s)',
        alerts: 'ja_Alert(s)',
        lIST: 'ja_List(s)',
        users: 'ja_User(s)',
        endpoint: 'ja_Host',
        archer: 'ja_Asset'
      }
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
        LiveConnect: 'ja_LiveConnect'
      }
    }
  }
};
