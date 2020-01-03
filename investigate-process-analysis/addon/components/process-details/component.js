import classic from 'ember-classic-decorator';
import { classNames, classNameBindings } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { alias } from '@ember/object/computed';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setDetailsTab, toggleEventPanelExpanded, toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { selectedTab, isEventsSelected } from 'investigate-process-analysis/reducers/process-visuals/selectors';


const stateToComputed = (state) => ({
  activeTab: selectedTab(state),
  isEventsSelected: isEventsSelected(state),
  isEventPanelExpanded: state.processAnalysis.processVisuals.isEventPanelExpanded
});

const dispatchToActions = {
  setDetailsTab,
  toggleEventPanelExpanded,
  toggleProcessDetailsVisibility
};

@classic
@classNames('process-details')
@classNameBindings('cssClassName')
class processDetails extends Component {
  isEventExpanded = false;

  @alias('activeTab.component')
  tabComponent;

  @computed('isEventsSelected', 'isEventPanelExpanded')
  get cssClassName() {
    if (this.isEventsSelected) {
      return this.isEventPanelExpanded ? 'expand' : 'collapse';
    }
    return null;
  }

  @computed('isEventExpanded')
  get toggleEventsClass() {
    return this.isEventExpanded ? 'shrink-diagonal-2' : 'expand-diagonal-4';
  }

  @action
  toggleDetailsExpanded() {
    this.toggleProperty('isEventExpanded');
    this.send('toggleEventPanelExpanded');
  }
}

export default connect(stateToComputed, dispatchToActions)(processDetails);
