import Component from '@ember/component';
import CellMixin from '../mixins/is-cell';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend(CellMixin, {
  layout,

  @computed('column.title', 'column.field', 'translateTitle')
  displayTitle(title, field, translateTitle) {
    if (title) {
      if (translateTitle) {
        return this.get('i18n').t(title);
      } else {
        return title;
      }
    } else {
      return field;
    }
  }
});
