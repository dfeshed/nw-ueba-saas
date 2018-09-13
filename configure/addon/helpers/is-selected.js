import Helper from '@ember/component/helper';

export function isSelected(params) {
  const [selectedThumbList, item ] = params;
  return selectedThumbList.some((li) => li.thumbprint === item.thumbprint);

}
export default Helper.helper(isSelected);