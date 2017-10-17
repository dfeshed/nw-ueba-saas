import { route } from 'ember-redux';
import { getFields, getRule } from 'respond/actions/creators/aggregation-rule-creators';

const model = (dispatch) => {
  dispatch(getRule());
  dispatch(getFields());
};

export default route({ model })();