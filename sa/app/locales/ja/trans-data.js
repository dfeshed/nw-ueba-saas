// Do not alter name of file, build looks for 'trans-data' files
// to do translation checking between languages

export default {
  application: {
    title: 'ja_NetWitness Suite',
    copyright: 'ja_&copy;2015 RSA Security LLC. All rights reserved.'
  },
  errorPages: {
    notFound: {
      errorDescription: 'ja_Oops! The requested link was not found.',
      subtitle: 'ja_Page Not Found',
      title: 'ja_404'
    },
    error: {
      errorDescription: 'ja_We\'ve encountered an internal error and cannot proceed. Please contact an admin for further assistance.',
      subtitle: 'ja_Internal Error',
      title: 'ja_500'
    }
  },
  monitor: {
    title: 'ja_Monitor',
    details: 'ja_Monitor contents go here.'
  },
  responded: {
    title: 'ja_Respond',
    details: 'ja_Respond contents go here.',
    myQueue: 'ja_Incident Queue',
    myFilteredQueue: 'ja_Showing {{filteredCount}} of {{totalCount}}',
    allIncidents: 'ja_All Incidents',
    incidents: 'ja_Incidents',
    sort: 'ja_Sort',
    filter: 'ja_Filter',
    journal: {
      add: 'ja_Add Comment',
      placeholder: 'ja_Enter comment here'
    },
    incidentDetails: {
      title: 'ja_Incident Details',
      titleWithId: 'ja_{{id}} - Incident Details',
      instructions: 'ja_Please select an Incident from the list on the left in order to view its information here.',
      summary: 'ja_Summary',
      details: 'ja_Details',
      journal: 'ja_Incident Journal',
      by: 'ja_by',
      remediationTasks: 'ja_Remediation Tasks'
    },
    errors: {
      timeout: 'ja_Connection Timeout: Unable to connect to the Respond service.<br><br>Check your network connectivity. Contact your Administrator if the error persists.',
      unexpected: 'ja_Server Error: The Respond service cannot complete your request.<br><br>Contact your Administrator if the error persists.',
      unableToLoadData: 'ja_Unable to load data. Check your network connections.',
      unableToLoadModel: 'ja_Unable to load {{model}}. Check your network connections.'
    },
    models: {
      users: 'ja_users',
      events: 'ja_events',
      alerts: 'ja_alerts',
      categoryTags: 'ja_category tags',
      storyline: 'ja_storyline',
      coreService: 'ja_Core Services',
      incident: 'ja_the incident'
    }
  },
  config: {
    title: 'ja_Configure',
    details: 'ja_Config contents go here.'
  },
  admin: {
    title: 'ja_Admin',
    details: 'ja_Admin contents go here.'
  },
  about: {
    title: 'ja_About',
    appName: 'ja_App',
    appVersion: 'ja_Version'
  },
  journal: {
    title: 'ja_Notes',
    milestones: {
      RECONNAISSANCE: 'ja_Reconnaissance',
      DELIVERY: 'ja_Delivery',
      EXPLOITATION: 'ja_Exploitation',
      INSTALLATION: 'ja_Installation',
      COMMAND_AND_CONTROL: 'ja_Command and control',
      ACTION_ON_OBJECTIVE: 'ja_Action on objective',
      CONTAINMENT: 'ja_Containment',
      ERADICATION: 'ja_Eradication',
      CLOSURE: 'ja_Closure'
    },
    new: {
      notePlaceholder: 'ja_Add Note Here...',
      attachFile: 'ja_Attach file',
      milestone: 'ja_Investigation milestones',
      addNote: 'ja_Add Note'
    },
    delete: {
      title: 'ja_Delete Journal Entry',
      dialog: 'ja_Are you sure you want to delete the journal entry?'
    },
    sort: {
      title: 'ja_Show',
      myNotes: 'ja_My Notes',
      allNotes: 'ja_All Notes'
    }
  },
  incident: {
    sortFields: {
      sortByLabel: 'ja_Sort By:',
      alertCount: 'ja_Alerts',
      assigneeName: 'ja_Assignee',
      dateCreated: 'ja_Date Created',
      lastUpdated: 'ja_Date Updated',
      id: 'ja_Incident ID',
      priority: 'ja_Priority',
      riskScore: 'ja_Risk Score'
    },
    fields: {
      id: 'ja_ID',
      name: 'ja_Title',
      status: 'ja_Status',
      assignee: 'ja_Assignee',
      priority: 'ja_Priority',
      description: 'ja_Description',
      createdDate: 'ja_Created',
      createdBy: 'ja_Rule',
      lastUpdated: 'ja_Updated',
      riskScore: 'ja_Risk Score',
      averageAlertRiskScore: 'ja_Avg. Risk',
      alertCount: 'ja_Alerts',
      sources: 'ja_Sources',
      categories: 'ja_Categories',
      journal: 'ja_Journal',
      events: 'ja_Events',
      createdTimestamp: 'ja_created ',
      updatedTimestamp: 'ja_updated ',
      groupBySourceIp: 'ja_Source IP',
      groupByDestinationIp: 'ja_Dest IP'
    },
    list: {
      id: 'ja_ID',
      name: 'ja_Name',
      createdDate: 'ja_Date created',
      status: 'ja_Status',
      priority: 'ja_Priority',
      riskScore: 'ja_Risk Score',
      assignee: 'ja_Assignee',
      alertCount: 'ja_Alerts',
      sources: 'ja_Source',
      filters: {
        reset: 'ja_Reset Filters',
        sources: 'ja_Select sources to filter by',
        assignees: 'ja_Select assignees to filter by',
        dateTimeOptions: {
          0: 'ja_Today',
          1: 'ja_Last Hour',
          2: 'ja_Last 12 Hours',
          3: 'ja_Last 24 Hours',
          4: 'ja_Last 7 Days',
          5: 'ja_All Data',
          6: 'ja_Custom'
        },
        dateTimeFilterError: 'ja_Selected time cannot be greater than current time',
        dateTimeFilterStartError: 'ja_Start Time cannot be greater than End Time',
        dateTimeFilterEndError: 'ja_End Time cannot be less than Start Time',
        dateTimeFilterStartDate: 'ja_Start Date',
        dateTimeFilterEndDate: 'ja_End Date',
        dateTimeFilterPrompt: 'ja_Date & Time Range'
      }
    },
    queue: {
      myQueue: 'ja_My Incidents',
      allIncidents: 'ja_All Incidents'
    },
    details: {
      createdOn: 'ja_created on',
      updatedOn: 'ja_updated on',
      sourceIp: 'ja_Source IP',
      destinationIp: 'ja_Destination IP',
      actions: 'ja_Incident Actions',
      closeIncident: 'ja_Close Incident',
      escalateIncident: 'ja_Escalate Incident',
      alertsGrid: {
        title: 'ja_Alerts List ({{count}})',
        severity: 'ja_Severity',
        dateCreated: 'ja_Date Created',
        name: 'ja_Name',
        events: 'ja_Events',
        host: 'ja_Host',
        domain: 'ja_Domain',
        source: 'ja_Source'
      },
      storyline: {
        header: 'ja_Storyline',
        to: 'ja_to',
        lookup: {
          ip2host: 'ja_Host is related to source ip'
        },
        c2: {
          title: 'ja_Detected C&C communication',
          titleScore: 'ja_Domain risk score {{score}}',
          beaconingScore: 'ja_The time intervals between communication events are very uniform',
          newDomain: 'ja_Domain is relatively new to the environment',
          whoisAgeScore: 'ja_Domain is relatively new based on the registration date {{age}} days ago',
          expiringDomain: 'ja_The domain registration will expire relatively soon {{age}} days',
          rareDomain: 'ja_The domain is rare in this environment',
          referrer: 'ja_A high percentage of hosts connecting to the domain are not utilizing referrers',
          userAgent: 'ja_A high percentage of hosts connecting to the domain are using a rare or no user agent'
        },
        ecat: {
          title: 'ja_Risky process',
          riskScore: 'ja_Module Risk Score {{score}}'
        },
        winauth: {
          title: 'ja_Anomalous Windows authentication',
          titleScore: 'ja_User risk score {{score}}',
          highServerScore: 'ja_Abnormally high number of servers accessed today',
          newDeviceScore: 'ja_Accessed an abnormally high number of new devices this week',
          newServerScore: 'ja_Accessed an abnormally high number of new servers today',
          passTheHash: 'ja_Potential "pass the hash" attack indicated by a new device followed by a new server',
          failedServerScore: 'ja_Abnormally high number of servers with failed logins today',
          logonType: 'ja_Accessed using a WIndows logon type they have rarely used in the past',
          aggregation: 'ja_Accessed using a Windows logon type they have rarely used in the past'
        },
        vpn: {
          title: 'ja_Anomalous VPN authentication',
          titleScore: 'ja_User risk score {{score}}',
          manyLoginFailures: 'ja_High number of VPN login failures',
          rareDevice: 'ja_Authenticated from a rare device',
          rareLocation: 'ja_Accessed from a rare location',
          rareServiceProvider: 'ja_Accessed using a rare service provider',
          newServiceProvider: 'ja_Accessed using a new service provider'
        },
        generic: {
          title: 'ja_Related Indicator',
          titleScore: 'ja_Risk score {{score}}'
        }
      },
      eventOverview: {
        title: 'ja_Event Overview',
        content: 'ja_content',
        service: 'ja_service',
        type: 'ja_Type',
        domain: 'ja_domain',
        detector: 'ja_detector',
        detectorDeviceClass: 'ja_detector device class',
        detectorDeviceIpAddress: 'ja_detector ip address',
        detectorProductName: 'ja_detector product name',
        networkEvent: 'ja_Network Event',
        source: 'ja_source',
        destination: 'ja_destination',
        domainInfo: 'ja_domain information',
        meta: 'ja_event meta',
        devicePort: 'ja_device port',
        deviceMacAddress: 'ja_device mac address',
        geolocationInfo: 'ja_geolocation information',
        user: 'ja_user',
        country: 'ja_country',
        city: 'ja_city',
        organization: 'ja_organization',
        domainRegistrar: 'ja_domain registrar',
        registrantName: 'ja_registrant name',
        notAvailable: 'ja_Not Available',
        investigate: 'ja_Investigate',
        badges: {
          beaconBehavior: {
            label: 'ja_beacon behavior',
            description: 'ja_A high score indicates that the communications between this source IP and this domain are highly regular and therefore suspected Command and Control.'
          },
          domainAge: {
            label: 'ja_domain age',
            description: 'ja_A high score indicates that this domain is relatively new based on the registration date found at the registrar.'
          },
          expiringDomain: {
            label: 'ja_expiring Domain',
            description: 'ja_A high score means that the likelihood the domain will expire soon is high.'
          },
          rareDomain: {
            label: 'ja_rare domain',
            description: 'ja_A high score indicates that relatively few source IPs have connected to this domain on this network in the last week.'
          },
          noReferrers: {
            label: 'ja_no referers',
            description: 'ja_A high score indicates that a relatively low percentage of the IPs connecting to this domain have used referers.'
          },
          rareUserAgent: {
            label: 'ja_rare user agent',
            description: 'ja_A high score indicates that the domain has a high percentage of IPs using a rare user agent.'
          },
          manyServers: {
            label: 'ja_many servers',
            description: 'ja_This user has abnormally high number of servers accessed today'
          },
          manyNewServers: {
            label: 'ja_many new servers',
            description: 'ja_This user has accessed an abnormally high number of new servers today'
          },
          manyNewDevices: {
            label: 'ja_many new devices',
            description: 'ja_This user has authenticated to more new devices this week as compared to weekly average'
          },
          passTheHash: {
            label: 'ja_pass the hash',
            description: 'ja_This user has been involved in a potential "pass the hash” attack indicated by a new device followed by a new server'
          },
          manyFailedLogins: {
            label: 'ja_many failed logins',
            description: 'ja_This user had an abnormally high number of servers with failed logins today'
          },
          rareLogonType: {
            label: 'ja_rare logon type',
            description: 'ja_This user has accessed using a WIndows logon type they have rarely used in the past'
          },
          manyLoginFailures: {
            label: 'ja_many login failures',
            description: 'ja_This user has had an abnormally high number of VPN login failures today, compared with their daily average'
          },
          rareDevice: {
            label: 'ja_rare device',
            description: 'ja_This user authenticated from a device they have rarely used in the past'
          },
          rareLocation: {
            label: 'ja_rare location',
            description: 'ja_This user has accessed from a location they have rarely used in the past'
          },
          rareServiceProvider: {
            label: 'ja_rare service provider',
            description: 'ja_This user has accessed using a service provider they have rarely used in the past'
          },
          newServiceProvider: {
            label: 'ja_new service provider',
            description: 'ja_This user has accessed using a new service provider'
          }
        },
        relatedLinks: 'ja_related links',
        relatedLinkOptions: {
          investigate_destination_domain: 'ja_Investigate Destination Domain',
          investigate_original_event: 'ja_Investigate Original Event',
          investigate_dst_ip: 'ja_Investigate Destination IP Address',
          investigate_src_ip: 'ja_Investigate Source IP Address',
          investigate_device_ip: 'ja_Investigate Device IP Address',
          investigate_session: 'ja_Investigate Session'
        }
      },
      overview: {
        title: 'ja_Overview',
        about: 'ja_About Incident',
        categoryTags: 'ja_Category Tags',
        addCategoryTags: 'ja_Click to add category tags'
      }
    },
    status: {
      0: 'ja_New',
      1: 'ja_Assigned',
      2: 'ja_In Progress',
      3: 'ja_Remediation Requested',
      4: 'ja_Remediation Complete',
      5: 'ja_Closed',
      6: 'ja_False Positive'
    },
    priority: {
      0: 'ja_Low',
      1: 'ja_Medium',
      2: 'ja_High',
      3: 'ja_Critical'
    },
    assignee: {
      none: '(ja_Unassigned)'
    },
    description: {
      none: 'ja_None'
    },
    emptyNewMessage: 'ja_No new incidents',
    emptyMessage: 'ja_No incidents',
    emptyInProgressMessage: 'ja_No incidents being worked on',
    edit: {
      update: {
        bulkSuccessfulMessage: {
          one: 'ja_Update was successful. 1 incident was updated.',
          other: 'ja_Update was successful. {{count}} incidents were updated.'
        },
        singleSuccessfulMessage: 'ja_Incident was successfully updated.',
        errorMessage: 'ja_Unable to save changes. Check your network connections.'
      },
      delete: {
        confirmationTitle: 'ja_Are you sure?',
        confirmationBody: {
          one: 'ja_Please confirm you want to delete this incident. Once this incident is deleted, it cannot be recovered.',
          other: 'ja_Please confirm you want to delete these incidents.  Once these incidents are deleted, they cannot be recovered.'
        },
        bulkSuccessfulMessage: {
          one: 'ja_Delete was successful. 1 incident was deleted.',
          other: 'ja_Delete was successful. {{count}} incidents were deleted.'
        },
        errorMessage: 'ja_Unable to delete. Check your network connections.'
      },
      actions: {
        createRecord: 'ja_added',
        updateRecord: 'ja_updated',
        deleteRecord: 'ja_deleted'
      },
      attributeActionSuccessfulMessage: 'ja_{{attribute}} was successfully {{action}}.'
    }
  },
  sort: {
    ascending: 'ja_Ascending',
    descending: 'ja_Descending'
  },
  time: {
    abbrev: {
      hour: 'ja_H',
      day: 'ja_D',
      week: 'ja_W',
      month: 'ja_M'
    },
    lastHour: 'ja_Last Hour',
    last24Hours: 'ja_Last 24 Hours',
    last7Days: 'ja_Last 7 Days',
    last30Days: 'ja_Last 30 Days'
  }
};
