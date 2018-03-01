import Component from '@ember/component';
import layout from './template';

/**
 * @class Alerts Table component
 * Presentational component that renders alerts & events in an `rsa-group-table`.
 * This component can be wired up to redux in order to show data for an Incident or an ad-hoc list of Alerts.
 * @public
 */
export default Component.extend({
  tagName: '',
  layout,


  // Passed down to child group table component.
  groups: null,
  selections: null,
  columnsConfig: [{
    field: 'summary',
    width: '100%'
  }],

  // Configurable stubs for actions.
  actions: {
    groupClickAction() {},
    groupShiftClickAction() {},
    groupCtrlClickAction() {},
    itemClickAction() {},
    itemShiftClickAction() {},
    itemCtrlClickAction() {}
  }
});
