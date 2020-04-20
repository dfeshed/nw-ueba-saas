import Helper from '@ember/component/helper';

export function isSelected(params) {
  const [selectedList, item ] = params;
  return selectedList && selectedList.some((li) => {
    if (li.id) {
      return li.id === item.id;
    }
    if (li.thumbprint) {
      return li.thumbprint === item.thumbprint;
    }
  });
}
export default Helper.helper(isSelected);
