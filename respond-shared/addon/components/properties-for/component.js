import layout from './template';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { isEmpty, typeOf } from '@ember/utils';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'propertiesFor',
  attributeBindings: ['testId:test-id', 'keyName:test-key-name'],
  classNames: ['properties-for'],

  @computed('item')
  itemType(item) {
    return typeOf(item);
  },

  @computed('item')
  members(item) {
    return item.map((value) => {
      const type = typeOf(value);
      return {
        value,
        type,
        isNestedValue: (type === 'object') || (type === 'array')
      };
    });
  },

  @computed('order', 'item', 'itemType', 'hidden')
  resolvedOrder(order, item, itemType, hidden) {
    const exclude = hidden || '';
    const ordering = order || '';
    const excludedKeys = exclude.split(',').map((str) => str.trim());
    const orderedKeys = ordering.split(',').map((str) => str.trim());
    const allKeys = (itemType === 'object') ? Object.keys(item) : [];

    const resolved = allKeys.filter((key) => {
      return !orderedKeys.includes(key);
    });

    return orderedKeys.concat(resolved).reject((key) => excludedKeys.includes(key));
  },

  @computed('item', 'resolvedOrder', 'itemPath')
  keys(item, resolvedOrder, itemPath) {
    if (!item) {
      return [];
    }

    return resolvedOrder
      .filter((name) => !isEmpty(item[name]))
      .map((name) => {
        const value = item[name];
        const type = typeOf(value);
        const fullPath = itemPath ? `${itemPath}.${name}` : name;
        return {
          name,
          fullPath,
          value,
          type,
          isNestedValue: (type === 'object') || (type === 'array')
        };
      });
  }
});
