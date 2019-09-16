import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';
import { inject as service } from '@ember/service';

// Investigate TABS, order is important
const TABS = [
  {
    isClassic: true,
    localeKey: 'userPreferences.defaultInvestigatePage.navigate',
    url: '/investigation',
    label: 'Navigate'
  },
  {
    isClassic: true,
    url: '/investigation/events',
    localeKey: 'userPreferences.defaultInvestigatePage.events',
    label: 'Legacy Events'
  },
  {
    name: 'investigate.investigate-events',
    url: '/investigate/events',
    localeKey: 'userPreferences.defaultInvestigatePage.eventAnalysis',
    label: 'Events'
  },
  {
    name: 'investigate.investigate-hosts',
    localeKey: 'userPreferences.defaultInvestigatePage.hosts',
    label: 'Hosts'
  },
  {
    name: 'investigate.investigate-files',
    url: '/investigate/files',
    localeKey: 'userPreferences.defaultInvestigatePage.files',
    label: 'Files'
  },
  {
    name: 'investigate.investigate-users',
    url: '/investigate/entities',
    localeKey: 'userPreferences.defaultInvestigatePage.entities',
    label: 'Entities'
  },
  {
    isClassic: true,
    url: '/investigation/malware',
    localeKey: 'userPreferences.defaultInvestigatePage.malware',
    label: 'Malware Analysis'
  }
];

export default Component.extend({
  classNames: ['rsa-investigate js-test-investigate-root'],
  tagName: 'article',
  layout,
  accessControl: service(),
  investigatePage: service(),

  activeTab: 'investigate.investigate-events',
  iconBar: { isIconBar: true },
  main: { isMain: true },

  @computed('activeTab', 'accessControl.hasUEBAAccess', 'investigatePage.legacyEventsEnabled')
  tabs(activeTab, hasUEBAAccess, legacyEventsEnabled) {
    let tabs = TABS.map((t) => ({
      ...t,
      isActive: t.name === activeTab
    }));
    if (!hasUEBAAccess) {
      // remove the Users tab is the user does not have UEBA access
      tabs = tabs.filter((tab) => tab.name !== 'investigate.investigate-users');
    }

    if (!legacyEventsEnabled) {
      // remove the Legacy events
      tabs = tabs.filter((tab) => tab.label !== 'Legacy Events');
    }

    return tabs;
  }
});
