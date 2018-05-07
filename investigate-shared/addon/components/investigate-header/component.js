import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';

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
    label: 'Event Analysis'
  },
  {
    name: 'investigate.investigate-hosts',
    label: 'Hosts'
  },
  {
    name: 'investigate.investigate-files',
    label: 'Files'
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

  activeTab: 'investigate.investigate-events',
  iconBar: { isIconBar: true },
  main: { isMain: true },

  @computed('activeTab')
  tabs(activeTab) {
    return TABS.map((t) => {
      return {
        ...t,
        isActive: t.name === activeTab
      };
    });
  }
});
