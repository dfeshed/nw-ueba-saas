import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { equal } from '@ember/object/computed';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';
import SEARCHABLE_ENTITY_TYPES from './searchable-entity-types';
import safeCallback from 'component-lib/utils/safe-callback';
import { isEmpty } from '@ember/utils';

export default Component.extend({
  layout,
  classNames: ['rsa-alerts-search-form'],

  // list of available timeframe options
  timeFrameOptions: SINCE_WHEN_TYPES,

  // identifier name of the timeframe option selected in the UI
  selectedTimeFrameName: null,

  // the time frame option from the common-ranges dropdown that is to be selected
  selectedTimeFrameOption: computed('selectedTimeFrameName', 'timeFrameOptions', function() {
    return this.timeFrameOptions && this.selectedTimeFrameName && this.timeFrameOptions.findBy('name', this.selectedTimeFrameName);
  }),

  // list of available entity type options
  entityTypeOptions: SEARCHABLE_ENTITY_TYPES,

  // identifier name of the entity type option selected in the UI
  selectedEntityTypeName: null,

  // the entity type option from the dropdown that is to be selected
  selectedEntityTypeOption: computed('selectedEntityTypeName', 'entityTypeOptions', function() {
    return this.entityTypeOptions && this.selectedEntityTypeName && this.entityTypeOptions.findBy('name', this.selectedEntityTypeName);
  }),

  // inputted text value (e.g., "10.20.30.40") currently shown in the input text box
  inputText: '',

  // indicates whether a search is currently underway, thus the UI should be disabled, except for a Cancel button
  isSearchUnderway: false,

  // indicates whether the Submit button should be disabled
  isSubmitDisabled: computed(
    'inputText',
    'selectedEntityTypeOption',
    'selectedTimeFrameOption',
    function() {
      return isEmpty(this.inputText) || !this.selectedEntityTypeOption || !this.selectedTimeFrameOption;
    }
  ),

  // Handles user selection in time range dropdown
  onChangeEntityType() {},

  // Handles user selection in entity type dropdown
  onChangeTimeFrame() {},

  // Handles user click on Submit button
  onSubmit() {},

  // Handles user click on Cancel button
  onCancel() {},

  showDomainOption: equal('selectedEntityTypeName', 'HOST'),

  actions: {
    changeEntityType() {
      safeCallback(this.get('onChangeEntityType'), ...arguments);
    },

    // Collects user inputs and invokes configurable `onSubmit()`.
    search() {
      const {
        onSubmit,
        inputText,
        selectedEntityTypeName,
        selectedTimeFrameName
      } = this.getProperties('onSubmit', 'inputText', 'selectedEntityTypeName', 'selectedTimeFrameName');
      safeCallback(onSubmit, selectedEntityTypeName, inputText, selectedTimeFrameName);
    }
  }
});
