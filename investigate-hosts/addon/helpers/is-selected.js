import Helper from '@ember/component/helper';

export function isSelected(params) {
  const [selectedList, item ] = params;
  return selectedList.some((li) => li.id === item.id);

}
export default Helper.helper(isSelected);