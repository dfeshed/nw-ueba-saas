import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import * as DictionaryActions from 'respond/actions/creators/dictionary-creators';
import columns from './columns';
import { hasSelectedClosedIncidents } from 'respond/selectors/incidents';

const stateToComputed = (state) => {
  const { respond: { dictionaries, users } } = state;
  return {
    priorityTypes: dictionaries.priorityTypes,
    statusTypes: dictionaries.statusTypes,
    users: users.enabledUsers,
    hasSelectedClosedIncidents: hasSelectedClosedIncidents(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    bootstrap() {
      dispatch(DictionaryActions.getAllPriorityTypes());
      dispatch(DictionaryActions.getAllStatusTypes());
      dispatch(DictionaryActions.getAllEnabledUsers());
      dispatch(DictionaryActions.getAllCategories());
    }
  };
};

const Incidents = Component.extend({
  columns
});

export default connect(stateToComputed, dispatchToActions)(Incidents);