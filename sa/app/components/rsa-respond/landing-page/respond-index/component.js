import Ember from 'ember';
import computed, { equal } from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  tagName: 'vbox',
  respondMode: service(),
  model: null,

  @equal('respondMode.selected', 'card') isCardMode: true,

  /*
   * Computed property that renders information about the number of displayed incidents based on filtered incidents
   */
  @computed('model.allIncidents.results.length', 'model.allIncidents.array.length')
  filteredIncidentCount: {
    get(count = 0, total = 0) {

      // Format output for display to show proper delimiter for large incident count (eg. 1000 will be 1,000 for en-US)
      let locale = this.get('i18n.locale');
      total = total.toLocaleString(locale);
      count = count.toLocaleString(locale);

      if (total === count) {
        return count;
      } else {
        let filteredCount = this.get('i18n').t('respond.myFilteredQueue', { filteredCount: count, totalCount: total });
        return filteredCount;
      }
    }
  }
});