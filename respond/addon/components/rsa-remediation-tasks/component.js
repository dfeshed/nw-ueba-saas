import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import * as DictionaryActions from 'respond/actions/creators/dictionary-creators';
import columns from './columns';

const dispatchToActions = (dispatch) => {
  return {
    bootstrap() {
      dispatch(DictionaryActions.getAllPriorityTypes());
      dispatch(DictionaryActions.getAllRemediationStatusTypes());
      dispatch(DictionaryActions.getAllRemediationTypes());
    }
  };
};

const RemediationTasks = Component.extend({
  columns
});

export default connect(undefined, dispatchToActions)(RemediationTasks);