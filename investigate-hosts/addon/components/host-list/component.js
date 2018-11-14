import { connect } from 'ember-redux';
import Component from '@ember/component';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

const stateToComputed = (state) => ({
  schemaLoading: state.endpoint.schema.schemaLoading,
  selectedServiceData: selectedServiceWithStatus(state)
});

const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone'

});

export default connect(stateToComputed)(Container);
