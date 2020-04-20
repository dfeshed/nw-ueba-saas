import Component from '@ember/component';
import { connect } from 'ember-redux';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';

import { percentageOfEventsDataReturned } from 'investigate-events/reducers/investigate/event-results/selectors';

const stateToComputed = (state) => ({
  retrievalProgress: percentageOfEventsDataReturned(state)
});

const ProgressBar = Component.extend({
  classNames: ['progress-bar'],
  classNameBindings: ['areEventsStreaming'],
  attributeBindings: ['title'],

  @computed('retrievalProgress')
  style: (progress) => {
    return htmlSafe(`width:${progress}%;`);
  },

  @computed('retrievalProgress', 'i18n')
  title: (progress, i18n) => {
    return i18n.t('investigate.queryStats.percentCompleted', { progress });
  }
});

export default connect(stateToComputed)(ProgressBar);
