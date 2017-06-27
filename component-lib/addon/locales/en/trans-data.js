export default {
  pageTitle: '{{section}} - NetWitness Suite',
  empty: '',
  languages: {
    en: 'English',
    'en-us': 'English',
    ja: 'Japanese'
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
    save: 'Save'
  },
  tables: {
    noResults: 'No Results'
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
    userLocked: 'User account is locked',
    userDisabled: 'User account is disabled',
    userExpired: 'User account has expired',
    changePasswordSoon: 'Please note that your password to the RSA NetWitness Server will expire in {{daysRemaining}} day(s). We encourage you to change the password before it expires. To change your password, click the preferences button on the top right of the application window.',
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
  userPreferences: {
    preferences: 'User Preferences',
    personalize: 'Personalize your experience',
    signOut: 'Sign Out',
    version: 'Version',
    username: 'Username',
    email: 'Email',
    language: 'Language',
    timeZone: 'Time Zone',
    dateFormatError: 'There was an error attempting to save your Date Format selection. Please try again. If this issue persists, please contact your system admin.',
    landingPageError: 'There was an error attempting to save your Default Landing Page selection. Please try again. If this issue persists, please contact your system admin.',
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
    defaultLandingPage: {
      label: 'Default Landing Page',
      monitor: 'Monitor',
      investigate: 'Investigate',
      investigateClassic: 'Investigate',
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
    extractWarning: '<span>You have navigated away before the downloaded files could be attached to the browser tray. Your download will be available <a href="{{url}}" target="_blank">here</a>.</span>',
    titleBar: {
      titles: {
        endpoint: 'Endpoint Event Details',
        network: 'Network Event Details',
        log: 'Log Event Details'
      },
      views: {
        text: 'Text Analysis',
        packet: 'Packet Analysis',
        file: 'File Analysis'
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
      downloadEndpointEvent: 'Download Endpoint Event',
      downloadJson: 'Download JSON',
      downloadLog: 'Download Log',
      downloadXml: 'Download XML',
      headerShowing: 'Showing',
      isDownloading: 'Downloading...',
      maxPacketsReached: 'Rendered <span class="darker">{{maxPacketCount}} (Max)</span> of <span class="darker">{{packetTotal}}</span> packets',
      maxPacketsReachedTooltip: 'The limit of {{maxPacketCount}} packets to render a single event has been reached; no additional packets will be rendered for this event. The packet threshold ensures the best rendering experience.',
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
      headerDisplayLabel: '{{label}} = {{displayValue}}'
    },
    fileView: {
      downloadFile: 'Download File',
      downloadFiles: 'Download Files ({{fileCount}})',
      isDownloading: 'Downloading...',
      downloadWarning: 'Warning: Files contain the original raw unsecured content. Use caution when opening or downloading files; they may contain malicious data.'
    },
    error: {
      generic: 'An unexpected error has occurred attempting to retrieve this data.',
      missingRecon: 'This event (id = {{id}}) was not saved or has been rolled out of storage. No content to display.',
      noTextContentData: 'No text data was generated during content reconstruction. This could mean that the event data was corrupt or invalid. Check the other reconstruction views.'
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
      payloadSizeTooltip: 'The payload size in the summary header may be greater than the payload size in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      packetCountTooltip: 'The calculated packet count in the summary header may be different than the packet count in the meta details panel because the meta data is sometimes written before event parsing completes and may include packet duplicates.',
      deviceIp: 'Device IP',
      deviceType: 'Device Type',
      deviceClass: 'Device Class',
      eventCategory: 'Event Category',
      collectionTime: 'Collection Time',
      eventTime: 'Event Time'
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
    controls: {
      settings: 'Configure',
      toggle: 'Show/Hide Events Panel'
    },
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
      endpoint: 'Endpoint',
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
      title: 'All Events',
      error: 'An unexpected error occurred when executing this query.',
      shrink: 'Shrink Events Panel',
      expand: 'Expand Events Panel',
      close: 'Close Events Panel'
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
    filters: 'Filters',
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
      remediationTasks: 'Remediation Tasks',
      alerts: 'Alerts',
      actionMessages: {
        updateSuccess: 'Your change was successful',
        updateFailure: 'There was a problem updating the field for this record',
        createSuccess: 'You successfully added a new record',
        createFailure: 'There was a problem creating this record',
        deleteFailure: 'There was a problem deleting this record'
      }
    },
    explorer: {
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
      footer: 'Showing {{count}} out of {{total}} items'
    },
    remediationTasks: {
      loading: 'Loading Tasks',
      addNewTask: 'Add New Task',
      noTasks: 'There are no tasks for {{incidentId}}',
      openFor: 'Opened',
      newTaskFor: 'New Remediation Task for',
      delete: 'Delete Task',
      noAccess: 'You do not have permissions to view remediation tasks',
      actions: {
        actionMessages: {
          deleteWarning: 'Deleting a Remediation Task from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
          'to delete the task from any other applicable systems.'
        }
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
      actions: {
        addEntryLabel: 'Add Entry',
        confirmUpdateTitle: 'Confirm update',
        changeAssignee: 'Change Assignee',
        changePriority: 'Change Priority',
        changeStatus: 'Change Status',
        addJournalEntry: 'Add Journal Entry',
        actionMessages: {
          deleteWarning: 'Warning: You are about to delete one or more incidents which may have remediation tasks and which may have been escalated. ' +
          'Deleting an incident from NetWitness will not delete it from other systems. Please note that it will be your responsibility ' +
          'to delete the incident and its remediation tasks from any other applicable systems.',
          addJournalEntrySuccess: 'You added a journal entry to incident {{incidentId}}',
          addJournalEntryFailure: 'There was a problem adding a journal entry to incident {{incidentId}}'
        },
        deselectAll: 'Deselect all'
      },
      filters: {
        timeRange: 'Time Range',
        reset: 'Reset Filters',
        customDateRange: 'Custom Date Range',
        customStartDate: 'Start Date',
        customEndDate: 'End Date',
        customDateErrorStartAfterEnd: 'The start date and time cannot be the same or later than the end date'
      },
      selectionCount: '{{selectionCount}} selected',
      label: 'Incidents',
      list: {
        select: 'Select',
        id: 'ID',
        name: 'Name',
        createdDate: 'Created',
        status: 'Status',
        priority: 'Priority',
        riskScore: 'Risk Score',
        assignee: 'Assignee',
        alertCount: 'Alerts',
        sources: 'Source',
        noResultsMessage: 'No matching Incidents were found'
      },
      footer: 'Showing {{count}} out of {{total}} incidents'
    },
    alerts: {
      actions: {
        actionMessages: {
          deleteWarning: 'Warning: You are about to delete one or more alerts that may be associated with incidents. ' +
          'Be aware that any associated incidents will be updated or deleted accordingly.'
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
      originalAlertError: 'There was a problem loading the raw alert'
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
        ECAT: 'Endpoint',
        'Event Stream Analysis': 'Event Stream Analysis',
        'Event Streaming Analytics': 'Event Stream Analysis',
        'Security Analytics Investigator': 'Security Analytics Investigator',
        'Web Threat Detection': 'Web Threat Detection',
        'Malware Analysis': 'Malware Analysis',
        'Reporting Engine': 'Reporting Engine'
      },
      backToAlerts: 'Back To Alerts'
    },
    incident: {
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
      backToIncidents: 'Back To Incidents',
      overview: 'Overview',
      indicators: 'Indicators',
      events: 'Events',
      view: {
        graph: 'View: Graph',
        datasheet: 'View: Datasheet'
      },
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
            HOST: 'Enter a host name',
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
          label: 'Look in'
        },
        results: {
          title: 'Indicators for',
          openInNewWindow: 'Open in new window'
        },
        actions: {
          search: 'Find',
          cancel: 'Cancel',
          addToIncident: 'Add To Incident'
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
        }
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
      REMEDIATION_REQUESTED: 'Remediation Requested',
      REMEDIATION_COMPLETE: 'Remediation Complete',
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
    marketingText: ' is not a configured data source in Context Hub. Contact your Administrator to enable this feature. Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more on-demand.',
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
      title: {
        archer: 'Archer',
        users: 'Active Directory',
        alerts: 'Alerts',
        incidents: 'Incidents',
        lIST: 'Lists',
        endpoint: 'NetWitness Endpoint',
        liveConnectIp: 'Live Connect',
        liveConnectFile: 'Live Connect',
        liveConnectDomain: 'Live Connect'
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
      closeButton: {
        title: 'Close Panel'
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
      macAddress: 'Mac Address',
      operatingSystem: 'Operating System',
      machineStatus: 'Machine Status',
      ipAddress: 'IPAddress',
      endpoint: 'To view details in Endpoint, you must install NW Endpoint UI'
    },
    addToList: {
      title: 'Add to List',
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
      listName: 'Enter List Name'
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
      hostName: 'Host Name',
      deviceId: 'Device Id',
      deviceType: 'Device Type',
      deviceOwner: 'Device Owner',
      deviceOwnerTitle: 'Device Owner Title',
      businessUnit: 'Business Unit',
      facility: 'Facility',
      ipAddress: 'Internal IP Address'
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
      wohisFax: 'Fax',
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
      noDataMsg: 'No data from Live Connect',
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
      skillLevelPrefix: 'Tier {{level}}'
    },
    error: {
      error: 'An unexpected error occurred when attempting to fetch the data.',
      noDataSource: 'No data source configured/enabled.',
      dataSourcesFailed: 'Unable to fetch data from the configured data sources.',
      dataSource: 'An unexpected error occurred when attempting to fetch the data.',
      noData: 'No context data is available for this DataSource.',
      listDuplicateName: 'List name already exists!',
      listValidName: 'Enter valid list name (Max length is 255 characters)',
      createList: 'An unexpected error occurred while creating a new List.',
      'datasource.query.not.supported': 'Context data lookup is not supported for this meta.',
      'transport.http.read.failed': 'Context data is not available as the data source is not reachable.',
      'transport.ad.read.failed': 'Context data is not available as the data source is not reachable.',
      'transport.init.failed': 'Data source connection timed out.',
      'transport.not.found': 'Context data is not available as the data source is not reachable.',
      'transport.create.failed': 'Context data is not available as the data source is not reachable.',
      'transport.refresh.failed': 'Context data is not available as the data source is not reachable.',
      'transport.connect.failed': 'Context data is not available as the data source is not reachable.',
      'live.connect.private.ip.unsupported': 'Only public IP addresses are supported by Live Connect.'
    },
    footer: {
      viewAll: 'VIEW All',
      total: 'Showing {{count}}',
      title: {
        incidents: 'Incident(s)',
        alerts: 'Alert(s)',
        lIST: 'List(s)',
        users: 'User(s)',
        endpoint: 'Host',
        archer: 'Asset'
      }
    },
    tooltip: {
      contextHighlights: 'Context Highlights',
      viewOverview: 'View Context',
      actions: 'Actions',
      investigate: 'Pivot to Investigate',
      addToList: 'Add to list',
      virusTotal: 'Virus Total Lookup',
      googleLookup: 'Google Lookup',
      ecat: 'Pivot to Endpoint',
      events: 'Pivot to Events',
      contextUnavailable: 'No context data available at this time'
    }
  }
};
