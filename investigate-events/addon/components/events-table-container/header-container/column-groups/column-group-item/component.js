import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

export default Component.extend({
  tagName: 'span',
  classNames: ['option-name'],
  columnGroup: null,

  @computed('columnGroup')
  displayName(columnGroup) {
    return columnGroup.name ? _.truncate(columnGroup.name, { length: 32, omission: '...' }) : null;
  }

});

