import EmberContextMenuItem from 'ember-context-menu/components/context-menu-item';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { get } from '@ember/object';
import layout from './template';

export default EmberContextMenuItem.extend({
  i18n: service(),
  layout,
  @computed('item.label', 'i18n.locale')
  localizedLabel(label) {
    const i18n = get(this, 'i18n');
    const prefix = get(this, 'item.prefix');
    const localeKey = `${prefix}${label}`;
    return i18n.exists(localeKey) ? i18n.t(localeKey) : label;
  }
});
