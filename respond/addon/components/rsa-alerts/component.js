import Component from '@ember/component';
import { connect } from 'ember-redux';
import columns from './columns';
import * as DictionaryActions from 'respond/actions/creators/dictionary-creators';

const dispatchToActions = (dispatch) => {
  return {
    bootstrap() {
      dispatch(DictionaryActions.getAllAlertTypes());
      dispatch(DictionaryActions.getAllAlertSources());
      dispatch(DictionaryActions.getAllAlertNames());
    }
  };
};

const Alerts = Component.extend({
  classNames: 'rsa-alerts',
  columns
});

export default connect(undefined, dispatchToActions)(Alerts);
