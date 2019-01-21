import Component from '@ember/component';
import EmberObject from '@ember/object';
import computed from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import { sendOperation, cancelOperation, updateOperationParams, updateCustomParameter } from 'ngcoreui/actions/actions';
import { selectedOperation, selectedOperationHelp } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  selectedOperation: selectedOperation(state),
  operationHelp: selectedOperationHelp(state),
  params: state.treeOperationParams,
  operationResponse: state.operationResponse
});

const dispatchToActions = {
  sendOperation,
  cancelOperation,
  updateOperationParams,
  updateCustomParameter
};

const treeViewOperationPanel = Component.extend({
  pendingCustomParameter: null,
  operationTypes: [
    'text', 'boolean', 'date-time'
  ],

  @computed('operationHelp')
  operationHelpText(helpText) {
    return htmlSafe(helpText);
  },

  @computed('params', 'selectedOperation')
  operationMessageObject: (params, selectedOperation) => ({
    message: selectedOperation.name,
    params
  }),

  @computed('operationResponse')
  hasPendingOperation: (operationResponse) => {
    return operationResponse && operationResponse.complete === false;
  },

  actions: {
    updateParams(update) {
      const { name, value } = update;
      const params = this.get('params');
      if (value === undefined && params[name]) {
        this.send('updateOperationParams', params.without(name));
      } else {
        this.send('updateOperationParams', params.set(name, value));
      }
    },

    newCustomParam() {
      this.set('pendingCustomParameter', EmberObject.create({
        name: '',
        displayName: '',
        description: 'Custom Parameter',
        type: 'text',
        optional: false,
        custom: true,
        method: 'add'
      }));
    },

    pushAndClearParam(param) {
      this.send('updateCustomParameter', param);
      this.set('pendingCustomParameter', null);
    },

    updateDisplayName() {
      const pendingCustomParameter = this.get('pendingCustomParameter');
      pendingCustomParameter.set('displayName', pendingCustomParameter.get('name'));
    },

    selectCustomParamType(type) {
      this.get('pendingCustomParameter').set('type', type);
    },

    cancelCustomParameter() {
      this.set('pendingCustomParameter', null);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(treeViewOperationPanel);
