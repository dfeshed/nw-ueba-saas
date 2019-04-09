import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { selectOperation } from 'ngcoreui/actions/actions';
import { filteredOperationNames, selectedOperation, responses } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  filteredOperationNames: filteredOperationNames(state),
  selectedOperation: selectedOperation(state),
  responses: responses(state)
});

const dispatchToActions = {
  selectOperation
};

const treeViewOperations = Component.extend({

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
