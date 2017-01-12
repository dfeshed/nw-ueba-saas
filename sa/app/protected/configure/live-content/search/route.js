import Ember from 'ember';
import route from 'ember-redux/route';
import * as DataActions from 'sa/actions/live-content/live-search-creators';

const { RSVP: { hash }, Route } = Ember;

const model = (dispatch) => {
  dispatch(DataActions.initializeDictionaries());
  return hash({ });
};

const SearchRoute = Route.extend({
});

export default route({ model })(SearchRoute);
