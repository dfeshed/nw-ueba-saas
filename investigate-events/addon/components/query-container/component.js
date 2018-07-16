import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run } from '@ember/runloop';

import { isOnFreeForm, isOnNextGen } from 'investigate-events/reducers/investigate/query-node/selectors';
import { canQueryNextGen, pillsToFilters } from 'investigate-events/reducers/investigate/next-gen/selectors';
import { setQueryView, setQueryTimeRange, setService } from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  filters: pillsToFilters(state),
  requiredValuesToQuerySelector: canQueryNextGen(state),
  isOnFreeForm: isOnFreeForm(state),
  isOnNextGen: isOnNextGen(state),
  queryView: state.investigate.queryNode.queryView,
  startTime: state.investigate.queryNode.startTime,
  endTime: state.investigate.queryNode.endTime,
  services: state.investigate.services,
  serviceId: state.investigate.queryNode.serviceId
});

const dispatchToActions = {
  setQueryView,
  setQueryTimeRange,
  setService
};

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],
  tagName: 'nav',
  classNameBindings: ['queryView'],

  actions: {
    changeView(view) {
      this.send('setQueryView', view);
      run.next(() => {
        if (this.get('isOnFreeForm')) {
          this.$('.rsa-investigate-free-form-query-bar input').focus();
        } else if (this.get('isOnNextGen')) {
          this.$('.new-pill-template .pill-meta input').focus();
        }
      });
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
