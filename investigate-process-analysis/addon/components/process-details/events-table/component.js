import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage
} from 'investigate-process-analysis/reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state)
});

const EventsTableComponent = Component.extend({

  tagName: 'box',

  classNames: ['process-events-details'],

  classNameBindings: ['isShowFilter'],

  isShowFilter: false,

  actions: {
    toggleFilterPanel() {
      this.toggleProperty('isShowFilter');
    }
  }
});
export default connect(stateToComputed)(EventsTableComponent);
