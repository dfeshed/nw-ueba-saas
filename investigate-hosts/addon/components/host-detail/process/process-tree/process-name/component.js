import Component from '@ember/component';
import { get, set } from '@ember/object';
import computed, { alias } from 'ember-computed-decorators';
import { htmlSafe } from 'ember-string';

const BASE_PADDING = 30;

export default Component.extend({

  classNames: ['process-name-column'],

  /**
   * To show the icon in th UI
   * @public
   */
  @alias('item.expanded')
  isExpanded: false,

  /**
   * Calculate the padding for the row based on the `level` property. Using this to achieve tree structure in the UI.
   * For each row `level` property set which indicates the depth of tree node.
   * @param item
   * @returns {*}
   * @public
   */
  @computed('item')
  style(item) {
    const left = BASE_PADDING * item.level;
    return htmlSafe(`padding-left: ${left}px;`);
  },

  actions: {
    toggleExpand() {
      const { item, index } = this.getProperties('item', 'index');
      set(item, 'expanded', !get(item, 'expanded'));
      this.onToggleExpand(index, item.level, item);
    }
  }
});
