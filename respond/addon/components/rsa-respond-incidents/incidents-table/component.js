import Ember from 'ember';
import columnConfig from './columns';
import { empty } from 'ember-computed-decorators';

const {
  Component
} = Ember;

/**
 * Container component that is responsible for orchestrating Respond Incidents layout and top-level components.
 * @public
 */
export default Component.extend({
  tagName: 'section',
  classNames: 'rsa-respond-incidents-table',
  classNameBindings: ['isInSelectMode', 'rowIsFocused', 'hasNoFocusedIncidents::has-focused-incident'],
  useLazyRendering: true,
  focusedIncident: null,

  /**
   * @property hasNoFocusedIncidents
   * @type boolean
   * @public
   */
  @empty('focusedIncident') hasNoFocusedIncidents: true,

  /**
   * Whether the user is in bulk-edit selection mode, which allows the user to select the rows in the incidents table
   * @property isInSelectMode
   * @public
   */
  isInSelectMode: false,

  /**
   * The list of incident objects that are currently selected (when the user is in select mode)
   * @public
   * @property incidentsSelected
   */
  incidentsSelected: [],

  // On-init handler to set the column configuration on the instance at creation time rather than on the prototype
  // via .extend({})
  initializeColumns: function() {
    this.set('columns', columnConfig);
  }.on('init'),

  isIncidentFocused(incident) {
    return incident === this.get('focusedIncident');
  },

  actions: {
    handleRowClickAction(incident) {
      this.sendAction('select', incident);
    }
  }
});
