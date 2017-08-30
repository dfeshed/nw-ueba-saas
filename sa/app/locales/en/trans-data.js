// Do not alter name of file, build looks for 'trans-data' files
// to do translation checking between languages

export default {
  application: {
    title: 'NetWitness Suite',
    copyright: '&copy;2015 RSA Security LLC. All rights reserved.'
  },
  errorPages: {
    notFound: {
      errorDescription: 'Oops! The requested link was not found.',
      subtitle: 'Page Not Found',
      title: '404'
    },
    error: {
      errorDescription: 'We\'ve encountered an internal error and cannot proceed. Please contact an admin for further assistance.',
      subtitle: 'Internal Error',
      title: '500'
    }
  },
  monitor: {
    title: 'Monitor',
    details: 'Monitor contents go here.'
  },
  responded: {
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
      titleWithId: '{{id}} - Incident Details',
      instructions: 'Please select an Incident from the list on the left in order to view its information here.',
      summary: 'Summary',
      details: 'Details',
      journal: 'Incident Journal',
      by: 'by',
      remediationTasks: 'Remediation Tasks'
    },
    errors: {
      timeout: 'Connection Timeout: Unable to connect to the Respond service.<br><br>Check your network connectivity. Contact your Administrator if the error persists.',
      unexpected: 'Server Error: The Respond service cannot complete your request.<br><br>Contact your Administrator if the error persists.',
      unableToLoadData: 'Unable to load data. Check your network connections.',
      unableToLoadModel: 'Unable to load {{model}}. Check your network connections.'
    },
    models: {
      users: 'users',
      events: 'events',
      alerts: 'alerts',
      categoryTags: 'category tags',
      storyline: 'storyline',
      coreService: 'Core Services',
      incident: 'the incident'
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
      title: 'Delete Journal Entry',
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
      journal: 'Note',
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
        assignees: 'Select assignees to filter by',
        dateTimeOptions: {
          0: 'Today',
          1: 'Last Hour',
          2: 'Last 12 Hours',
          3: 'Last 24 Hours',
          4: 'Last 7 Days',
          5: 'All Data',
          6: 'Custom'
        },
        dateTimeFilterError: 'Selected time cannot be greater than current time',
        dateTimeFilterStartError: 'Start Time cannot be greater than End Time',
        dateTimeFilterEndError: 'End Time cannot be less than Start Time',
        dateTimeFilterStartDate: 'Start Date',
        dateTimeFilterEndDate: 'End Date',
        dateTimeFilterPrompt: 'Date & Time Range'
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
        },
        vpn: {
          title: 'Anomalous VPN authentication',
          titleScore: 'User risk score {{score}}',
          manyLoginFailures: 'High number of VPN login failures',
          rareDevice: 'Authenticated from a rare device',
          rareLocation: 'Accessed from a rare location',
          rareServiceProvider: 'Accessed using a rare service provider',
          newServiceProvider: 'Accessed using a new service provider'
        },
        generic: {
          title: 'Related Indicator',
          titleScore: 'Risk score {{score}}'
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
          },
          manyServers: {
            label: 'many servers',
            description: 'This user has abnormally high number of servers accessed today'
          },
          manyNewServers: {
            label: 'many new servers',
            description: 'This user has accessed an abnormally high number of new servers today'
          },
          manyNewDevices: {
            label: 'many new devices',
            description: 'This user has authenticated to more new devices this week as compared to weekly average'
          },
          passTheHash: {
            label: 'pass the hash',
            description: 'This user has been involved in a potential "pass the hash” attack indicated by a new device followed by a new server'
          },
          manyFailedLogins: {
            label: 'many failed logins',
            description: 'This user had an abnormally high number of servers with failed logins today'
          },
          rareLogonType: {
            label: 'rare logon type',
            description: 'This user has accessed using a WIndows logon type they have rarely used in the past'
          },
          manyLoginFailures: {
            label: 'many login failures',
            description: 'This user has had an abnormally high number of VPN login failures today, compared with their daily average'
          },
          rareDevice: {
            label: 'rare device',
            description: 'This user authenticated from a device they have rarely used in the past'
          },
          rareLocation: {
            label: 'rare location',
            description: 'This user has accessed from a location they have rarely used in the past'
          },
          rareServiceProvider: {
            label: 'rare service provider',
            description: 'This user has accessed using a service provider they have rarely used in the past'
          },
          newServiceProvider: {
            label: 'new service provider',
            description: 'This user has accessed using a new service provider'
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
    edit: {
      update: {
        bulkSuccessfulMessage: {
          one: 'Update was successful. 1 incident was updated.',
          other: 'Update was successful. {{count}} incidents were updated.'
        },
        singleSuccessfulMessage: 'Incident was successfully updated.',
        errorMessage: 'Unable to save changes. Check your network connections.'
      },
      delete: {
        confirmationTitle: 'Are you sure?',
        confirmationBody: {
          one: 'Please confirm you want to delete this incident. Once this incident is deleted, it cannot be recovered.',
          other: 'Please confirm you want to delete these incidents.  Once these incidents are deleted, they cannot be recovered.'
        },
        bulkSuccessfulMessage: {
          one: 'Delete was successful. 1 incident was deleted.',
          other: 'Delete was successful. {{count}} incidents were deleted.'
        },
        errorMessage: 'Unable to delete. Check your network connections.'
      },
      actions: {
        createRecord: 'added',
        updateRecord: 'updated',
        deleteRecord: 'deleted'
      },
      attributeActionSuccessfulMessage: '{{attribute}} was successfully {{action}}.'
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
};
