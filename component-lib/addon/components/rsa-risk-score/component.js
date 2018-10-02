import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed';

const CIRCLE_RADIUS = {
  small: 12,
  large: 25
};

const CIRCLE_SIZE_CLASS = {
  small: 'is-small',
  large: 'is-large'
};

export default Component.extend({
  layout,
  tagName: 'svg',
  classNames: ['rsa-risk-score'],
  attributeBindings: [
    'diameter:width',
    'diameter:height',
    'testId:test-id'
  ],
  score: null,
  radius: null,
  size: 'small',

  classNameBindings: [
    'circleStroke',
    'circleSize'
  ],

  circleStroke: computed('score', function() {
    const riskScore = this.get('score');
    if (riskScore <= 29) {
      return 'is-low';
    } else if (riskScore <= 59) {
      return 'is-medium';
    } else if (riskScore <= 99) {
      return 'is-high';
    } else {
      return 'is-danger';
    }
  }),

  circleSize: computed('size', function() {
    const riskSize = this.get('size');
    return CIRCLE_SIZE_CLASS[riskSize];
  }),

  circleRadius: computed('size', 'radius', function() {
    const radiusSize = this.get('size');
    const radiusLength = this.get('radius');
    return radiusLength ? radiusLength : CIRCLE_RADIUS[radiusSize];
  }),

  axis: computed('circleRadius', function() {
    return this.get('circleRadius') + 2;
  }),

  diameter: computed('axis', function() {
    return 2 * this.get('axis');
  }),

  textSize: computed('score', function() {
    const riskScore = this.get('score');
    return (riskScore > 99) ? '48%' : '50%';
  })
});

