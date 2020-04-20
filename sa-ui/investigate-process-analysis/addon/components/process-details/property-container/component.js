import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedProcess } from 'investigate-process-analysis/reducers/process-tree/selectors';

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

@classic
@tagName('')
class PropertyContainer extends Component {
  executionLabelPrefix = 'investigateProcessAnalysis.processExecutionDetails.';
  fileLabelPrefix = 'investigateProcessAnalysis.property.file.';

  @computed('processDetails')
  get hasProcessDetails() {
    if (this.processDetails.checksum) {
      return true;
    }
    return false;
  }
}

export default connect(stateToComputed)(PropertyContainer);
