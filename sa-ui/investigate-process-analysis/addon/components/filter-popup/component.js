import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setActiveFilterTab, resetActiveFilterTab } from 'investigate-process-analysis/actions/creators/filter-popup';
import { getFilterTabs } from 'investigate-process-analysis/reducers/filter-popup/selectors';
import { TAB_FILTER } from '../const';

const stateToComputed = (state) => ({
  activeTab: state.processAnalysis.filterPopup.activeFilterTab,
  tabsDetails: getFilterTabs(state)
});

const dispatchToActions = {
  setActiveFilterTab,
  resetActiveFilterTab
};

@classic
@classNames('process-filter-popup')
class PopupFilter extends Component {
  @computed('model.children', 'tabsDetails')
  get tabs() {
    return this.tabsDetails.map((tab) => ({
      ...tab,
      count: this.model?.children.filter((node) => {
        if (tab.name === 'all') {
          return this.model?.children;
        } else if (node.data.eventCategory) {
          return node.data.eventCategory[TAB_FILTER[tab.name]];
        }
      }).length
    }));
  }

  init() {
    super.init(...arguments);
    this.selectedProcess = this.selectedProcess || [];
  }

  @computed('selectedProcess.@each.selected', 'model.children')
  get selectedProcessCount() {
    return this.selectedProcess.length ? this.selectedProcess.filter((node) => node.selected).length : this.model?.children.filter((node) => node.selected).length;
  }

  @computed('selectedProcessCount')
  get isViewSelectedDisabled() {
    return !this.selectedProcessCount;
  }

  didReceiveAttrs() {
    super.didReceiveAttrs(...arguments);
    this.send('resetActiveFilterTab');
  }

  @action
  activate(tabName) {
    this.send('setActiveFilterTab', tabName);
  }

  @action
  onViewAll(d, hidePanelAction) {
    this.onView(d);
    hidePanelAction();
  }

  @action
  handleViewSelected(d, hidePanelAction) {
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
  }

  @action
  getSelectedItems(items) {
    this.set('selectedProcess', items);
  }

  @action
  onCancel(hidePanelAction) {
    hidePanelAction();
  }
}

export default connect(stateToComputed, dispatchToActions)(PopupFilter);
