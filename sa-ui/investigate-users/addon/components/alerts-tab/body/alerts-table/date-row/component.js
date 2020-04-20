import Component from '@ember/component';

export default Component.extend({
  dateExpended: [],
  actions: {
    expandDate(dateClicked) {
      const expendedDates = this.get('dateExpended');
      if (expendedDates.includes(dateClicked)) {
        if (expendedDates.length === 1) {
          this.set('dateExpended', []);
        } else {
          this.set('dateExpended', expendedDates.splice(expendedDates.indexOf(dateClicked) - 1, 1));
        }
      } else {
        this.set('dateExpended', expendedDates.concat(dateClicked));
      }
    }
  }
});
