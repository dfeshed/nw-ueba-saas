import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import columns from './columns';
import * as DictionaryActions from 'respond/actions/creators/dictionary-creators';

const dispatchToActions = (dispatch) => {
  return {
    bootstrap() {
      dispatch(DictionaryActions.getAllAlertTypes());
      dispatch(DictionaryActions.getAllAlertSources());
    }
  };
};

const Alerts = Component.extend({
  columns
});

export default connect(undefined, dispatchToActions)(Alerts);