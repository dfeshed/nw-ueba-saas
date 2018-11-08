import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';
import { inject as service } from '@ember/service';

// Investigate TABS, order is important
const TABS = [
  {
    isClassic: true,
    url: '/investigation',
    label: 'Navigate'
  },
  {
    isClassic: true,
    url: '/investigation/events',
    label: 'Events'
  },
  {
    name: 'investigate.investigate-events',
    url: '/investigate/events',
    label: 'Event Analysis'
  },
  {
    name: 'investigate.investigate-hosts',
    label: 'Hosts'
  },
  {
    name: 'investigate.investigate-files',
    url: '/investigate/files',
    label: 'Files'
  },
  {
    name: 'investigate.investigate-users',
    label: 'Users'
  },
  {
    isClassic: true,
    url: '/investigation/malware',
    label: 'Malware Analysis'
  }
];

export default Component.extend({
  classNames: ['rsa-investigate js-test-investigate-root'],
  tagName: 'article',
  layout,
  accessControl: service(),

  activeTab: 'investigate.investigate-events',
  iconBar: { isIconBar: true },
  main: { isMain: true },

  @computed('activeTab', 'accessControl.hasUEBAAccess')
  tabs(activeTab, hasUEBAAccess) {
    let tabs = TABS.map((t) => ({
      ...t,
      isActive: t.name === activeTab
    }));
    if (!hasUEBAAccess) {
      // remove the Users tab is the user does not have UEBA access
      tabs = tabs.filter((tab) => tab.name !== 'investigate.investigate-users');
    }
    return tabs;
  }
});
