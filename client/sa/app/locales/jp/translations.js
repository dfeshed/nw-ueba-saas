import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/jp/translations';

export default Ember.$.extend({}, BaseTranslations, {
  application: {
    title: 'jp_Security Analytics',
    version: 'jp_v11.0.0',
    copyright: 'jp_&copy;2015 RSA Security LLC. All rights reserved.'
  },
  monitor: {
    title: 'jp_Monitor',
    details: 'jp_Monitor contents go here.'
  },
  respond: {
    title: 'jp_Respond',
    details: 'jp_Respond contents go here.',
    myQueue: 'jp_My Queue',
    allIncidents: 'jp_All Incidents',
    incidents: 'jp_Incidents',
    sort: 'jp_Sort',
    filter: 'jp_Filter',
    journal: {
      add: 'jp_Add Comment',
      placeholder: 'jp_Enter comment here'
    },
    incidentDetails: {
      title: 'jp_Incident Details',
      instructions: 'jp_Please select an Incident from the list on the left in order to view its information here.',
      summary: 'jp_Summary',
      details: 'jp_Details',
      journal: 'jp_Incident Journal',
      by: 'jp_by',
      remediationTasks: 'jp_Remediation Tasks'
    }
  },
  explore: {
    title: 'jp_Investigate',
    details: 'jp_Investigate contents go here.'
  },
  admin: {
    title: 'jp_Admin',
    details: 'jp_Admin contents go here.'
  },
  about: {
    title: 'jp_About',
    appName: 'jp_App',
    appVersion: 'jp_Version'
  },
  incident: {
    fields: {
      id: 'jp_ID',
      name: 'jp_Title',
      status: 'jp_Status',
      assignee: 'jp_Assignee',
      priority: 'jp_Priority',
      description: 'jp_Description',
      createdDate: 'jp_Created',
      createdBy: 'jp_Rule',
      lastUpdated: 'jp_Updated',
      riskScore: 'jp_Risk Score',
      averageAlertRiskScore: 'jp_Avg. Risk',
      alertCount: 'jp_Alerts',
      sources: 'jp_Sources',
      categories: 'jp_Categories',
      journal: 'jp_Journal'
    },
    status: {
      0: 'jp_New',
      1: 'jp_Assigned',
      2: 'jp_In Progress',
      3: 'jp_Remediation Requested',
      4: 'jp_Remediation Complete',
      5: 'jp_Closed',
      6: 'jp_False Positive'
    },
    priority: {
      0: 'jp_Low',
      1: 'jp_Medium',
      2: 'jp_High',
      3: 'jp_Critical'
    },
    assignee: {
      none: 'jp_None'
    },
    description: {
      none: 'jp_None'
    }
  },
  list: {
    items: 'jp_items',
    all: 'jp_(All)'
  },
  sort: {
    ascending: 'jp_Ascending',
    descending: 'jp_Descending'
  },
  time: {
    abbrev: {
      hour: 'jp_H',
      day: 'jp_D',
      week: 'jp_W',
      month: 'jp_M'
    },
    lastHour: 'jp_Last Hour',
    last24Hours: 'jp_Last 24 Hours',
    last7Days: 'jp_Last 7 Days',
    last30Days: 'jp_Last 30 Days'
  }
});

