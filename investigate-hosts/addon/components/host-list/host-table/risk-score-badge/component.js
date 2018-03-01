import Component from '@ember/component';
import computed, { equal } from 'ember-computed-decorators';

export default Component.extend({

  classNames: ['risk-score-badge'],

  classNameBindings: [
    'tooLow',
    'low',
    'high',
    'medium',
    'danger'
  ],

  @equal('score', 0)
  tooLow: false,

  @equal('score', 100)
  danger: false,

  @computed('score')
  high: (score) => score < 100 && score > 85,

  @computed('score')
  medium: (score) => score <= 85 && score > 40,

  @computed('score')
  low: (score) => score <= 40 && score > 0


});
