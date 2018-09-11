import Component from '@ember/component';
import { connect } from 'ember-redux';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { isProgressBarDisabled } from 'investigate-events/reducers/investigate/query-stats/selectors';

const stateToComputed = (state) => ({
  progress: state.investigate.queryStats.percent,
  isDisabled: isProgressBarDisabled(state)
});

const ProgressBar = Component.extend({
  classNames: ['progress-bar'],
  classNameBindings: ['isDisabled'],
  attributeBindings: ['title'],

  @computed('progress')
  style: (progress) => {
    return htmlSafe(`width:${progress}%;`);
  },

  @computed('progress', 'i18n')
  title: (progress, i18n) => {
    return i18n.t('investigate.queryStats.percentCompleted', { progress });
  }
});

export default connect(stateToComputed)(ProgressBar);
