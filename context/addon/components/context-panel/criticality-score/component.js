import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';

const {
  Component
} = Ember;

const colorCode = {
  'NOT RATED': '',
  'LOW': ':#00FF3B',
  'MEDIUM LOW': ':#009DFF',
  'MEDIUM-LOW': ':#009DFF',
  'MEDIUM': ':#FFE900',
  'MEDIUM HIGH': ':#FFA100',
  'MEDIUM-HIGH': ':#FFA100',
  'HIGH': ':#FF0800'
};

const determineScore = (score) => {
  if (score && colorCode[score.toUpperCase()]) {
    return colorCode[score.toUpperCase()];
  } else {
    return '';
  }
};

export default Component.extend({

  layout,
  @computed('score')
  scoreColor(score) {
    return determineScore(score);
  }
});
