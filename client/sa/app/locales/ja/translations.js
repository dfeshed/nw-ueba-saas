import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/ja/translations';

export default Ember.$.extend({}, BaseTranslations, {
  application: {
    title: 'ja_Security Analytics',
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
  explore: {
    title: 'ja_Investigate',
    details: 'ja_Investigate contents go here.'
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
  incident: {
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
    all: 'ja_(All)'
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
