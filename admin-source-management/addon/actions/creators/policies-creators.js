import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const fetchPolicyList = () => ({
  type: ACTION_TYPES.FETCH_POLICY_LIST,
  promise: policyAPI.fetchPolicy()
});

export {
  fetchPolicyList
};
