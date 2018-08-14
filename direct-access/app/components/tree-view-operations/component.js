import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import Immutable from 'seamless-immutable';
import { connect } from 'ember-redux';
import { selectOperation } from 'direct-access/actions/actions';
import { filteredOperationNames, selectedOperation } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  filteredOperationNames: filteredOperationNames(state),
  selectedOperation: selectedOperation(state),
  pendingOperation: state.pendingOperation,
  operationResponse: state.operationResponse
});

const dispatchToActions = {
  selectOperation
};

const treeViewOperations = Component.extend({
  @computed('operationResponse')
  response: (operationResponse) => {
    operationResponse = Immutable.without(operationResponse, ['path', 'route', 'flags']);
    const keys = Object.keys(operationResponse);
    if (keys.length === 1 && keys[0] === 'string') {
      return operationResponse.string
        .replace(/\n/g, '\n')
        .replace(/\t/g, '  ');
    } else {
      return JSON.stringify(operationResponse, null, 2);
    }
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
