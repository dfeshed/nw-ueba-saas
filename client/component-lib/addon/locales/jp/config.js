export default {
  rtl: false,

  pluralForm(count) {
    if (count === 0) {
      return 'jp_zero';
    }
    if (count === 1) {
      return 'jp_one';
    }
    if (count === 2) {
      return 'jp_two';
    }
    if (count < 5) {
      return 'jp_few';
    }
    if (count >= 5) {
      return 'jp_many';
    }
    return 'other';
  }
};
