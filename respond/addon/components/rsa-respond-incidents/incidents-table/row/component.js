import Ember from 'ember';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

const { isPresent } = Ember;

const stateToComputed = ({ respond: { incidents, dictionaries } }) => {
  return {
    focusedIncident: incidents.focusedIncident,
    priorityTypes: dictionaries.priorityTypes,
    statusTypes: dictionaries.statusTypes
  };
};
/**
 * Extension of the Data Table default row class to modify underlying template for incident actions
 * @public
 */
const IncidentsRow = DataTableBodyRow.extend({
  classNameBindings: ['hasFocus', 'actionDrawerActive:drawer-active:drawer-inactive'],
  isHovered: false,
  /**
   * True if the row item is the same as the "focusedIncident" in app state that tracks which incident is currently
   * focused / highlighted by the user
   * @property hasFocus
   * @public
   */
  @computed('focusedIncident', 'item')
  hasFocus(focusedIncident, item) {
    return focusedIncident === item;
  },

  @computed('focusedIncident', 'hasFocus')
  isAnotherRowFocused(focusedIncident, hasFocus) {
    return isPresent(focusedIncident) && !hasFocus;
  },

  @computed('hovered', 'hasFocus', 'isAnotherRowFocused')
  actionDrawerActive(isHovered, hasFocus, isAnotherRowFocused) {
    return hasFocus || (isHovered && !isAnotherRowFocused);
  },

  mouseEnter() {
    this.set('hovered', true);
  },

  mouseLeave() {
    this.set('hovered', false);
  }
});

export default connect(stateToComputed)(IncidentsRow);