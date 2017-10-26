import { route } from 'ember-redux';
import { getRules } from 'respond/actions/creators/aggregation-rule-creators';

const model = (dispatch) => {
  dispatch(getRules());
};

export default route({ model })();