import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedIndicatorId } from 'entity-details/reducers/indicators/selectors';

const stateToComputed = (state) => ({
  selectedIndicatorId: selectedIndicatorId(state)
});

const EntityDetailContainerBodyComponent = Component.extend({
  classNames: ['entity-details-container-body']
});

export default connect(stateToComputed)(EntityDetailContainerBodyComponent);