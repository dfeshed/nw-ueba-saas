import Ember from 'ember';
import BaseTranslations from 'component-lib/locales/en/translations';

export default Ember.$.extend({}, BaseTranslations, {
  application: {
    title: 'Security Analytics',
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
  explore: {
    title: 'Investigate',
    details: 'Investigate contents go here.'
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
  incident: {
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
      riskScore: 'Risk Score',
      assignee: 'Assignee',
      alertCount: 'Alerts',
      sources: 'Source'
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
  list: {
    items: 'items',
    all: '(All)'
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
