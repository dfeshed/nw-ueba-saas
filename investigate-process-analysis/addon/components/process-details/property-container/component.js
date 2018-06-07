import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  propertyConfig,
  processExecutionConfig,
  hasProperties,
  processProperties
} from 'investigate-process-analysis/reducers/process-properties/selectors';

import {
  selectedProcess
} from 'investigate-process-analysis/reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  propertyConfig: propertyConfig(state),
  executionConfig: processExecutionConfig(),
  hasProperties: hasProperties(state),
  propertyDetails: processProperties(state),
  processDetails: selectedProcess(state)
});

const PropertyContainer = Component.extend({

  tagName: 'vbox',

  classNames: 'property-container'

});

export default connect(stateToComputed)(PropertyContainer);
