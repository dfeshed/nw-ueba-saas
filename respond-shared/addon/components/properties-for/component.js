import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';
import { isEmpty, typeOf } from '@ember/utils';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'propertiesFor',
  attributeBindings: ['testId:test-id', 'keyName:test-key-name'],
  classNames: ['properties-for'],

  itemType: computed('item', function() {
    return typeOf(this.item);
  }),

  members: computed('item', function() {
    return this.item.map((value) => {
      const type = typeOf(value);
      return {
        value,
        type,
        isNestedValue: (type === 'object') || (type === 'array')
      };
    });
  }),

  resolvedOrder: computed('order', 'item', 'itemType', 'hidden', function() {
    const exclude = this.hidden || '';
    const ordering = this.order || '';
    const excludedKeys = exclude.split(',').map((str) => str.trim());
    const orderedKeys = ordering.split(',').map((str) => str.trim());
    const allKeys = (this.itemType === 'object') ? Object.keys(this.item) : [];

    const resolved = allKeys.filter((key) => {
      return !orderedKeys.includes(key);
    });

    return orderedKeys.concat(resolved).reject((key) => excludedKeys.includes(key));
  }),

  keys: computed('item', 'resolvedOrder', 'itemPath', function() {
    if (!this.item) {
      return [];
    }

    return this.resolvedOrder
      .filter((name) => !isEmpty(this.item[name]))
      .map((name) => {
        const value = this.item[name];
        const type = typeOf(value);
        const fullPath = this.itemPath ? `${this.itemPath}.${name}` : name;
        return {
          name,
          fullPath,
          value,
          type,
          isNestedValue: (type === 'object') || (type === 'array')
        };
      });
  })
});
