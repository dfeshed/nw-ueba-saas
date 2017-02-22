import Ember from 'ember';
import columnConfig from './columns';
import computed from 'ember-computed-decorators';
const {
    Component,
    inject: { service }
} = Ember;

/**
 * Container component that is responsible for orchestrating Respond Incidents layout and top-level components.
 * @public
 */
const IncidentsTable = Component.extend({
  tagName: 'section',
  classNames: 'rsa-respond-incidents-table',
  classNameBindings: ['isInSelectMode'],
  useLazyRendering: true,
  i18n: service(),

  isInSelectMode: false,

  incidentsSelected: [],

  @computed('i18n')
  noResultsMessage(i18n) {
    return i18n.t('respond.incidents.list.noResultsMessage');
  },

  initializeColumns: function() {
    this.set('columns', columnConfig);
  }.on('init'),

  actions: {
    handleRowClickAction(incident, /* jQueryEvent */) {
      this.sendAction('viewIncidentDetails', incident);
    }
  }
});


export default IncidentsTable;