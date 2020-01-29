import { helper } from '@ember/component/helper';

export function isLogicalOperator([selectedGroupRanking, groupRank]) {
  const className = 'editable';
  const isSelected = selectedGroupRanking === groupRank?.name;
  return isSelected ? `${className} is-selected` : className;
}

export default helper(isLogicalOperator);