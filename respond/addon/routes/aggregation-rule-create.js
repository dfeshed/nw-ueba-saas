import { route } from 'ember-redux';
import { getFields } from 'respond/actions/creators/aggregation-rule-creators';

const model = (dispatch) => {
  dispatch(getFields());
};

export default route({ model })();