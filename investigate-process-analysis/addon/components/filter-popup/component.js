import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setActiveFilterTab, resetActiveFilterTab } from 'investigate-process-analysis/actions/creators/filter-popup';
import { getFilterTabs } from 'investigate-process-analysis/reducers/filter-popup/selectors';
import computed from 'ember-computed-decorators';
import { TAB_FILTER } from '../const';

const stateToComputed = (state) => ({
  activeTab: state.processAnalysis.filterPopup.activeFilterTab,
  tabsDetails: getFilterTabs(state)
});

const dispatchToActions = {
  setActiveFilterTab,
  resetActiveFilterTab
};

const PopupFilter = Component.extend({
  classNames: ['process-filter-popup'],
  selectedProcess: [],

  @computed('model.children', 'tabsDetails')
  tabs(nodes, tabsDetails) {
    return tabsDetails.map((tab) => ({
      ...tab,
      count: nodes.filter((node) => {
        if (tab.name === 'all') {
          return nodes;
        } else if (node.data.eventCategory) {
          return node.data.eventCategory[TAB_FILTER[tab.name]];
        }
      }).length
    }));
  },

  @computed('selectedProcess.@each.selected', 'model.children')
  selectedProcessCount(selectedChildren, children) {
    return selectedChildren.length ? selectedChildren.filter((node) => node.selected).length : children.filter((node) => node.selected).length;
  },
  @computed('selectedProcessCount')
  isViewSelectedDisabled(selectedProcessCount) {
    return !selectedProcessCount;
  },
  didReceiveAttrs() {
    this._super(...arguments);
    this.send('resetActiveFilterTab');
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
      let selectedChildren = this.get('selectedProcess');
      if (selectedChildren.length === 0) {
        // If row selection is not done, initialize selected items
        selectedChildren = this.model.children;
      }
      d.children.forEach((child) => {
        const selectedChild = selectedChildren.find((node) => node.data.processId === child.data.processId);
        child.selected = selectedChild ? selectedChild.selected : false;
      });
      this.onViewSelected(d);
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
