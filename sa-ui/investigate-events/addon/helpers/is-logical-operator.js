import { helper } from '@ember/component/helper';
import {
  OPERATOR_AND,
  OPERATOR_OR
} from 'investigate-events/constants/pill';

export function isLogicalOperator([pillData = {}]) {
  const { type } = pillData;
  return type === OPERATOR_AND || type === OPERATOR_OR;
}

export default helper(isLogicalOperator);