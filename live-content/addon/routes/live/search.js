import Ember from 'ember';
import route from 'ember-redux/route';
import * as DataActions from 'live-content/actions/live-content/live-search-creators';

const { RSVP: { hash }, Route } = Ember;

const model = (dispatch) => {
  dispatch(DataActions.initializeDictionaries());
  dispatch(DataActions.search());
  return hash({ });
};

const SearchRoute = Route.extend({
});

export default route({ model })(SearchRoute);
