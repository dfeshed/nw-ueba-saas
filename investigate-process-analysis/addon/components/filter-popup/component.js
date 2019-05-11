import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setActiveFilterTab } from 'investigate-process-analysis/actions/creators/filter-popup';
import { getFilterTabs } from 'investigate-process-analysis/reducers/filter-popup/selectors';

const stateToComputed = (state) => ({
  activeTab: state.processAnalysis.filterPopup.activeFilterTab,
  tabs: getFilterTabs(state)
});

const dispatchToActions = {
  setActiveFilterTab
};

const PopupFilter = Component.extend({
  classNames: ['process-filter-popup'],
  selectedProcessCount: 0,

  actions: {
    activate(tabName) {
      this.send('setActiveFilterTab', tabName);
    },
    onViewAll(d, hidePanelAction) {
      this.onView(d);
      hidePanelAction();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PopupFilter);
