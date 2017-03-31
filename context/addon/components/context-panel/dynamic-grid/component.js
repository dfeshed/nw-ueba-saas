import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';
import { riskScoreToBadgeLevel } from 'context/helpers/risk-score-to-badge-level';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  },

  @computed('data.IIOCScore')
  badgeLevel: (score) => {
    return riskScoreToBadgeLevel(score).badgeLevel;
  }
});
