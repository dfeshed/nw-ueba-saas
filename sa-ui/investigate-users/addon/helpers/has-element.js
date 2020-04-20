import { helper } from '@ember/component/helper';

export default helper(function hasElement([arrayObj, element]) {
  return arrayObj.includes(element);
});
