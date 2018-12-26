import { connect } from 'ember-redux';
import Component from '@ember/component';
import { inject as service } from '@ember/service';

import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

const stateToComputed = (state) => ({
  schemaLoading: state.endpoint.schema.schemaLoading,
  selectedServiceData: selectedServiceWithStatus(state)
});

const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone',

  pivot: service(),

  actions: {
    pivotToInvestigate(item, category) {
      this.get('pivot').pivotToInvestigate('machine.machineName', item, category);
    }
  }
});

export default connect(stateToComputed)(Container);
