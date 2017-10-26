import { route } from 'ember-redux';
import { getFields, getRule } from 'respond/actions/creators/aggregation-rule-creators';

const model = (dispatch, { rule_id }) => {
  dispatch(getRule(rule_id));
  dispatch(getFields());
};

export default route({ model })();