import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { setDetailsTab, toggleEventPanelExpanded, toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { selectedTab } from 'investigate-process-analysis/reducers/process-visuals/selectors';


const stateToComputed = (state) => ({
  activeTab: selectedTab(state)
});

const dispatchToActions = {
  setDetailsTab,
  toggleEventPanelExpanded,
  toggleProcessDetailsVisibility
};

const processDetails = Component.extend({

  tagName: '',

  isEventExpanded: false,

  @alias('activeTab.component')
  tabComponent: '',

  @computed('isEventExpanded')
  toggleEventsClass: (isEventExpanded) => isEventExpanded ? 'shrink-diagonal-2' : 'expand-diagonal-4',

  actions: {
    toggleDetailsExpanded() {
      this.toggleProperty('isEventExpanded');
      this.send('toggleEventPanelExpanded');
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(processDetails);
