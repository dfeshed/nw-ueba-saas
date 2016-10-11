import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/en/translations';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, {
  application: {
    title: 'NetWitness',
    version: '11.0.0',
    copyright: '&copy;2015 RSA Security LLC. All rights reserved.'
  },
  monitor: {
    title: 'Monitor',
    details: 'Monitor contents go here.'
  },
  respond: {
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
      instructions: 'Please select an Incident from the list on the left in order to view its information here.',
      summary: 'Summary',
      details: 'Details',
      journal: 'Incident Journal',
      by: 'by',
      remediationTasks: 'Remediation Tasks'
    }
  },
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
      title: 'Meta'
    },
    events: {
      title: 'Events'
    },
    services: {
      loading: 'Loading list of available services',
      empty: {
        title: 'Unable to find services.',
        description: 'No brokers, concentrators or other services were detected. This may be due to a configuration or connectivity issue.'
      },
      error: {
        title: 'Unable to load services.',
        description: 'Unexpected error loading list of brokers, concentrators and other services to investigate. This may be due to a configuration or connectivity issue.'
      }
    },
    customQuery: {
      title: 'Enter a query.'
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
  context: {
    title: 'Context',
    header: {
      hosts: 'Hosts',
      incidents: 'Incidents',
      alerts: 'Alerts',
      files: 'Files',
      lists: 'Lists',
      feeds: 'Feeds',
      liveConnect: 'Live Connect',
      unsafe: 'Unsafe'
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
      machineStatus: 'Machine Status'
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
      iiocLevel0: 'Iioc Level 0',
      iiocLevel1: 'Iioc Level 1',
      iiocLevel2: 'Iioc Level 2',
      iiocLevel3: 'Iioc Level 3'
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
      priority: 'Priority'
    },
    alerts: {
      title: 'Alerts',
      risk_score: 'Risk Score',
      source: 'Source',
      name: 'Name',
      numEvents: 'Events',
      severity: 'Severity',
      created: 'Created',
      id: 'Incident ID',
      timestamp: 'timestamp'
    },
    list: {
      title: 'list',
      createdByUser: 'Author',
      createdTimeStamp: 'Created',
      lastModifiedTimeStamp: 'Updated',
      dataSourceDescription: 'Description',
      dataSourceName: 'Name'
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
      commActivity: 'Community Activity (Last 30 Days)',
      firstSeen: 'First Seen',
      activitySS: 'Activity Snapshot',
      communityTrend: 'Trending Community Activity',
      submitTrend: 'Trending Submission Activity',
      communityActivityDesc1: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span> of the Community Seen {{value}}',
      communityActivityDesc2: 'Of the <span class="rsa-context-panel__liveconnect__comm-activity__desc__seen">{{seen}}%</span> seen, <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span> of the Community submitted feedback',
      submittedActivityDesc1: 'Of the <span class="rsa-context-panel__liveconnect__comm-activity__desc__submitted">{{submitted}}%</span> submitted',
      submittedActivityDesc2: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unsafe">{{unsafe}}%</span> marked unsafe',
      submittedActivityDesc3: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__safe">{{safe}}%</span> marked safe',
      submittedActivityDesc4: '<span class="rsa-context-panel__liveconnect__comm-activity__desc__unknown">{{unknown}}%</span> marked unknown',
      riskIndicators: 'Risk Indicators',
      percentageTagged: '% Community Tagged'
    }
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
      assigneeFirstLastName: 'Assignee',
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
      journal: 'Journal',
      events: 'Events',
      createdTimestamp: 'created ',
      updatedTimestamp: 'updated '
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
        reset: 'Reset Filters'
      }
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
        to: 'to'
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
    emptyInProgressMessage: 'No incidents being worked on'
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
});
