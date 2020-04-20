import EmberContextMenuItem from 'ember-context-menu/components/context-menu-item';
import { inject as service } from '@ember/service';
import { get, computed } from '@ember/object';
import layout from './template';

export default EmberContextMenuItem.extend({
  classNameBindings: ['item.showDivider'],
  i18n: service(),
  layout,

  localizedLabel: computed('item.label', 'i18n.locale', function() {
    const i18n = get(this, 'i18n');
    const prefix = get(this, 'item.prefix');
    const localeKey = `${prefix}${this.item?.label}`;
    return i18n.exists(localeKey) ? i18n.t(localeKey) : this.item?.label;
  })
});
