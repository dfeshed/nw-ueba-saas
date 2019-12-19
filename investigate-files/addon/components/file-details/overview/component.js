import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from '../base-property-config';
import { toggleFilePropertyPanel } from 'investigate-files/actions/visual-creators';

import {
  setSelectedAlert,
  getUpdatedRiskScoreContext,
  expandEvent
} from 'investigate-shared/actions/data-creators/risk-creators';

import { riskState, getDataSourceTab } from 'investigate-files/reducers/visuals/selectors';

import { setDataSourceTab } from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  risk: riskState(state),
  getDataSourceTab: getDataSourceTab(state),
  activeDataSourceTab: state.files.visuals.activeDataSourceTab,
  fileProperty: state.files.fileList.selectedDetailFile,
  isFilePropertyPanelVisible: state.files.visuals.isFilePropertyPanelVisible,
  listOfServices: state.files.fileList.listOfServices
});

const dispatchToActions = {
  getUpdatedRiskScoreContext,
  setSelectedAlert,
  expandEvent,
  setDataSourceTab,
  toggleFilePropertyPanel
};

@classic
@tagName('box')
@classNames('file-overview')
class Overview extends Component {
  propertyConfig = CONFIG;

  @computed('getDataSourceTab')
  get dataSourceTabs() {
    return this.getDataSourceTab.filter((tab) => tab.name !== 'RISK_PROPERTIES');
  }

  @action
  expandEventAction(id) {
    if (this.get('isFilePropertyPanelVisible')) {
      this.send('toggleFilePropertyPanel');
    }
    this.send('expandEvent', id);
  }
}

export default connect(stateToComputed, dispatchToActions)(Overview);
