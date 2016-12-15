import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/ja/translations';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, {
  application: {
    title: 'ja_Netwitness Suite',
    version: 'ja_11.0.0',
    copyright: 'ja_&copy;2015 RSA Security LLC. All rights reserved.'
  },
  monitor: {
    title: 'ja_Monitor',
    details: 'ja_Monitor contents go here.'
  },
  respond: {
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
      instructions: 'ja_Please select an Incident from the list on the left in order to view its information here.',
      summary: 'ja_Summary',
      details: 'ja_Details',
      journal: 'ja_Incident Journal',
      by: 'ja_by',
      remediationTasks: 'ja_Remediation Tasks'
    }
  },
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
  config: {
    title: 'ja_Configure',
    details: 'ja_Config contents go here.'
  },
  admin: {
    title: 'ja_Admin',
    details: 'ja_Admin contents go here.'
  },
  context: {
    title: 'ja_Context',
    header: {
      hosts: 'ja_Hosts',
      incidents: 'ja_Incidents',
      alerts: 'ja_Alerts',
      files: 'ja_Files',
      lists: 'ja_Lists',
      feeds: 'ja_Feeds',
      liveConnect: 'ja_Live Connect',
      unsafe: 'ja_Unsafe'
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
      machineStatus: 'ja_Machine Status'
    },
    modules: {
      title: 'ja_Top Suspicious Modules',
      iiocScore: 'ja_Iioc Score',
      moduleName: 'ja_Module Name',
      analyticsScore: 'ja_Analytics Score',
      machineCount: 'ja_Machine Count',
      signature: 'ja_Signature',
      header: 'ja_(IIOC Score > 500)'
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
      error: 'ja_Error fetching Live Connect Data'
    }
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
        }
      },
      eventOverview: {
        title: 'ja_Event Overview',
        content: 'ja_content',
        setvice: 'ja_service',
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
        assignees: 'ja_Select assignees to filter by'
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
    bulkEdit: {
      statusSelectListLabel: 'ja_Status',
      assigneeSelectListLabel: 'ja_Assignee',
      prioritySelectListLabel: 'ja_Priority',
      saveButton: 'ja_Save',
      cancelButton: 'ja_Cancel',
      modalOkButtonLabel: 'ja_OK',
      successfulUpdateMessage: {
        'one': 'ja_{{totalFields}} record updated successfully',
        'other': 'ja_{{totalFields}} records updated successfully'
      },
      deleteConfirmation: 'Are you sure?',
      areYouSureConfirmation: {
        'one': 'ja_Please confirm you want to delete this incident. Once this incident is deleted, it cannot be recovered.',
        'other': 'ja_Please confirm you want to delete these incidents.  Once these incidents are deleted, they cannot be recovered.'
      },
      successfulDeleteMessage: {
        'one': 'ja_{{totalFields}} incident successfully deleted',
        'other': 'ja_{{totalFields}}  incidents successfully deleted'
      }
    }
  },
  list: {
    items: 'ja_items',
    all: 'ja_(All)',
    of: 'ja_of'
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
});
