import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  log: null,

  @computed('log')
  moduleClass: (log) => {
    switch (log.level) {
      case 'warning':
        return 'log-warning';
      case 'failure':
        return 'log-failure';
      default:
        return 'log-level-default';
    }
  },

  @computed('log')
  levelChar: (log) => log.level.substring(0, 1),

  @computed('log')
  dateTime: (log) => (new Date(log.time * 1000)).toLocaleString()
});
