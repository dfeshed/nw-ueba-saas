import Component from 'ember-component';
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
    name: 'protected.investigate.investigate-events',
    label: 'Events'
  },
  {
    name: 'protected.investigate.investigate-hosts',
    label: 'Hosts'
  },
  {
    name: 'protected.investigate.investigate-files',
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

  activeTab: 'protected.investigate.investigate-events',
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
