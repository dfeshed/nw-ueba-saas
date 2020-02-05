import Component from '@ember/component';
import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import {
  updatePolicyFileSourceProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  fileSourceById,
  isAdvancedSettingsCollapsed,
  sourceNameValidator
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';
import { encodingOptions } from '../cell-settings';

const stateToComputed = (state, attrs) => ({
  item: fileSourceById(state, attrs.itemId),
  isAdvancedSettingsCollapsed: isAdvancedSettingsCollapsed(state, attrs.itemId),
  invalidTableItem: sourceNameValidator(state).invalidTableItem,
  errorMessage: sourceNameValidator(state).errorMessage
});

const dispatchToActions = {
  updatePolicyFileSourceProperty
};

const SourceAdvancedSettingsCell = Component.extend({
  classNames: 'child-source-container',

  // multiple fields are nested so do this in the template
  // classNameBindings: ['column.field'],
  encodingOptions,

  isAdvancedSettingsAccordionCollapsed: true,

  didInsertElement() {
    this._super(...arguments);
    // doing this here so it only runs the first time it renders, otherwise,
    // it collapses if the advanced settings are set back to defaults by the user
    this.set('isAdvancedSettingsAccordionCollapsed', this.get('isAdvancedSettingsCollapsed'));
  },

  panelId: computed(function() {
    return `fileTypeSourcesTooltip-${this.get('elementId')}`;
  }),

  itemIdAsInt: computed('itemId', function() {
    const idAsInt = parseInt(this.itemId, 10);
    return idAsInt;
  }),

  actions: {
    // power-selects & radio-buttons
    handleSelectionChange(column, value) {
      // field/path built like 'policy.sources.0.someProp' (the 0 is the source index/id)
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), column, value);
    },

    handleSourceNameChange(column, event) {
      // field/path built like 'policy.sources.0.sourceName' (the 0 is the source index/id)
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), column, event.target.value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SourceAdvancedSettingsCell);
