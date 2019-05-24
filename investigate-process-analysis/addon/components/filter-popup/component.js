import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setActiveFilterTab } from 'investigate-process-analysis/actions/creators/filter-popup';
import { getFilterTabs } from 'investigate-process-analysis/reducers/filter-popup/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  activeTab: state.processAnalysis.filterPopup.activeFilterTab,
  tabs: getFilterTabs(state)
});

const dispatchToActions = {
  setActiveFilterTab
};

const PopupFilter = Component.extend({
  classNames: ['process-filter-popup'],
  selectedProcess: [],

  @computed('selectedProcess.@each.selected', 'model.children')
  selectedProcessCount(selectedChildren, children) {
    return selectedChildren.length ? selectedChildren.filter((node) => node.selected).length : children.filter((node) => node.selected).length;
  },
  @computed('selectedProcessCount')
  isViewSelectedDisabled(selectedProcessCount) {
    return !selectedProcessCount;
  },
  actions: {
    activate(tabName) {
      this.send('setActiveFilterTab', tabName);
    },
    onViewAll(d, hidePanelAction) {
      this.onView(d);
      hidePanelAction();
    },
    onViewSelected(d, hidePanelAction) {
      this.onViewSelected({ node: d.node, children: this.get('selectedProcess') });
      hidePanelAction();
    },
    getSelectedItems(items) {
      this.set('selectedProcess', items);
    },
    onCancel(hidePanelAction) {
      hidePanelAction();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PopupFilter);
