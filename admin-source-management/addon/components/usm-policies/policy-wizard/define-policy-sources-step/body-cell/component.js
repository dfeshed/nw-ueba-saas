import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
// import DataTableBody from '../component';
import {
  removePolicyFileSource,
  updatePolicyFileSourceProperty
} from 'admin-source-management/actions/creators/policy-wizard-creators';
import {
  fileSourceById,
  fileSourceExclusionFilters,
  sourceNameValidator,
  exFilterValidator
} from 'admin-source-management/reducers/usm/policy-wizard/filePolicy/file-selectors';
import { enableOnAgentConfig, dataCollectionConfig, encodingOptions } from './settings';

const stateToComputed = (state, attrs) => ({
  item: fileSourceById(state, attrs.itemId),
  exclusionFilters: fileSourceExclusionFilters(state, attrs.itemId),
  invalidTableItem: sourceNameValidator(state).invalidTableItem,
  invalidPath: sourceNameValidator(state).invalidPath,
  dirPathEmptyMsg: sourceNameValidator(state).dirPathEmptyMsg,
  dirPathLength: sourceNameValidator(state).dirPathLength,
  errorMessage: sourceNameValidator(state).errorMessage,
  exFilterErrorMsg: exFilterValidator(state).errorMessage,
  exFilterErr: exFilterValidator(state).showError,
  exFilterInvalid: exFilterValidator(state).invalidFilter,
  exFilterInvalidIndex: exFilterValidator(state).invalidFilterIndex
});

const dispatchToActions = {
  removePolicyFileSource,
  updatePolicyFileSourceProperty
};

// const SourceBodyCell = DataTableBody.extend({
const SourceBodyCell = Component.extend({
  layout,
  classNames: 'child-source-container',
  encodingOptions,
  enableOnAgentConfig: enableOnAgentConfig(),
  dataCollectionConfig: dataCollectionConfig(),

  @computed()
  panelId() {
    return `fileTypeSourcesTooltip-${this.get('elementId')}`;
  },

  @computed('itemId')
  itemIdAsInt(itemId) {
    const idAsInt = parseInt(itemId, 10);
    return idAsInt;
  },

  @computed('exFilterInvalidIndex')
  exFiltersErrLineIndex(invalidIndex) {
    // if there is an invalid exclusion filter, add +1 to index to get the line number
    if (invalidIndex !== -1) {
      return ++invalidIndex;
    }
  },

  actions: {
    handleRemoveSource(/* index */) {

      this.send('removePolicyFileSource', this.get('itemIdAsInt'));
    },

    // power-selects & radio-buttons
    handleSelectionChange(column, value) {
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), column, value);
    },

    handleExclusionFiltersChange(column, event) {
      // capture the value from the textarea
      const { value } = event.target;
      // convert the entered string into an array delimited by a new line and store in state
      let arr = [];
      if (value.trim()) {
        arr = value.trim().split('\n');
      }
      // it's ok to send the whole array here
      // field/path built like 'policy.sources.0.exclusionFilters' (the 0 is the source index/id)
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), column, arr);
    },

    handleSourceNameChange(column, event) {
      // field/path built like 'policy.sources.0.sourceName' (the 0 is the source index/id)
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), column, event.target.value);
    },

    handlePathChange(column, index, event) {
      // field/path built like 'policy.sources.0.paths.0' (the 0's are array indexes)
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), `${column}.${index}`, event.target.value);
    },

    handleDeletePath(index) {
      // delete the path at the index
      const paths = this.get('item.paths').filter((e, i) => i !== index);
      // it's ok to send the whole array here
      // field/path built like 'policy.sources.0.paths' (the 0 is the source index/id)
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), 'paths', paths);
    },

    handleAddPath() {
      // field/path built like 'policy.sources.0.paths.0' (the 0's are array indexes)
      const index = this.get('item.paths').length;
      this.send('updatePolicyFileSourceProperty', this.get('itemIdAsInt'), `paths.${index}`, '');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SourceBodyCell);
