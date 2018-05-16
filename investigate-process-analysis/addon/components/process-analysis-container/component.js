import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage,
  queryInput
} from 'investigate-process-analysis/reducers/process-tree/selectors';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/creators/process-properties';
import {
  processProperties,
  propertyConfig
 } from 'investigate-process-analysis/reducers/process-properties/selectors';
import computed from 'ember-computed-decorators';

const dispatchToActions = {
  fetchProcessDetails
};

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state),
  queryInput: queryInput(state),
  propertyDetails: processProperties(state),
  config: propertyConfig(state)
});

const WrapperComponent = Component.extend({
  tagName: 'hbox',
  classNames: ['process-analysis-container', 'scrollable-panel-wrapper', 'col-xs-12'],
  @computed('propertyDetails')
  fileProperties(propertyDetails) {
    return propertyDetails ? propertyDetails[0] : [];
  }
});

export default connect(stateToComputed, dispatchToActions)(WrapperComponent);
