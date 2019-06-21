import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedProcess } from 'investigate-process-analysis/reducers/process-tree/selectors';
import computed from 'ember-computed-decorators';

import {
  propertyConfig,
  processExecutionConfig,
  hasProperties,
  processProperties,
  processDetails
} from 'investigate-process-analysis/reducers/process-properties/selectors';


const stateToComputed = (state) => ({
  propertyConfig: propertyConfig(state),
  executionConfig: processExecutionConfig(),
  hasProperties: hasProperties(state),
  propertyDetails: processProperties(state),
  processDetails: processDetails(selectedProcess(state))
});

const PropertyContainer = Component.extend({

  tagName: '',


  executionLabelPrefix: 'investigateProcessAnalysis.processExecutionDetails.',

  fileLabelPrefix: 'investigateProcessAnalysis.property.file.',

  @computed('processDetails')
  hasProcessDetails(processDetails) {
    if (processDetails.checksum) {
      return true;
    }
    return false;
  }
});

export default connect(stateToComputed)(PropertyContainer);
