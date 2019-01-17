import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { selectOperation } from 'direct-access/actions/actions';
import { filteredOperationNames, selectedOperation } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  filteredOperationNames: filteredOperationNames(state),
  selectedOperation: selectedOperation(state),
  operationResponse: state.operationResponse || {}
});

const dispatchToActions = {
  selectOperation
};

const treeViewOperations = Component.extend({

  @computed('operationResponse')
  responses: (operationResponse) => {
    return {
      ...operationResponse,
      progress: operationResponse.progress ? `${operationResponse.progress}% ` : null,
      status: operationResponse.status ? `${operationResponse.status}...` : null,
      hasError: !!operationResponse.error,
      hasPendingOperation: operationResponse.complete === false
    };
  },

  @computed('filteredOperationNames')
  hasOperations: (filteredOperationNames) => filteredOperationNames.length > 0,

  actions: {
    selectResponseText() {
      const [ text ] = this.$('.response-panel');
      let range, selection;

      if (document.body.createTextRange) {
        range = document.body.createTextRange();
        range.moveToElementText(text);
        range.select();
      } else if (window.getSelection) {
        selection = window.getSelection();
        range = document.createRange();
        range.selectNodeContents(text);
        selection.removeAllRanges();
        selection.addRange(range);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(treeViewOperations);
