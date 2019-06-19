import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

const emailsInitialState = Immutable.from({
  emails: null
});

const emailsReducer = handleActions({

}, emailsInitialState);

export default emailsReducer;
