import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/ja/translations';

const { $ } = Ember;

export default $.extend({}, BaseTranslations, {
  application: {
    title: 'ja_NetWitness',
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
      title: 'ja_Meta'
    },
    events: {
      title: 'ja_Events'
    },
    services: {
      loading: 'ja_Loading list of available services',
      empty: {
        title: 'ja_Unable to find services.',
        description: 'ja_No brokers, concentrators or other services were detected. This may be due to a configuration or connectivity issue.'
      },
      error: {
        title: 'ja_Unable to load services.',
        description: 'ja_Unexpected error loading list of brokers, concentrators and other services to investigate. This may be due to a configuration or connectivity issue.'
      }
    },
    customQuery: {
      title: 'ja_Enter a query.'
    }
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
      assigneeFirstLastName: 'ja_Assignee',
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
      events: 'jp_Events',
      createdTimestamp: 'jp_created ',
      updatedTimestamp: 'jp_updated '
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
        header: 'ja-Storyline',
        to: 'ja-to'
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
      riskScore: 'ja_Risk Score',
      assignee: 'ja_Assignee',
      alertCount: 'ja_Alerts',
      sources: 'ja_Source'
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
    emptyInProgressMessage: 'ja_No incidents being worked on'
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
