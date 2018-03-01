import Component from '@ember/component';
import { set, get } from '@ember/object';
import computed, { notEmpty } from 'ember-computed-decorators';
import layout from './template';
import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';

export default Component.extend({
  layout,
  classNames: ['rsa-list'],
  classNameBindings: ['hasSelections:has-selections:has-no-selections'],

  /**
   * Array of data records to be rendered in the list.
   * @type Object[]
   * @public
   */
  items: null,

  /**
   * Name of attribute or attribute path that uniquely identifies each member of `items`.
   *
   * Note: The `itemIdField` is used in order to support selections. Without selections, it is not needed.
   *
   * @type {string}
   * @default 'id'
   * @public
   */
  itemIdField: 'id',

  /**
   * Configurable Ember Component class for rendering individual items.
   *
   * The component will be passed the following attrs:
   * `item`: {object} the list item data object;
   * `isSelected`: {boolean} indicates whether or not the item is currently in `selections`;
   * `isInSelectMode`: {boolean} indicates whether or not this component is currently in Select mode;
   * `clickAction`, `shiftClickAction`, `ctrlClickAction`: actions for handling various clicks on the item DOM.
   *
   * @type {string}
   * @public
   */
  itemComponentClass: 'rsa-list/item',

  /**
   * Specifies how this component's items should respond to clicks.
   *
   * If `isInSelectMode` is `false`, items will follow standard selection behavior when clicked.  Specifically, a clicked
   * item will become the one & only selected item in the list.  The previous selection (if any) is cleared.  Users can
   * select multiple items by using Shift+Click or Ctrl+Click.
   *
   * If `isInSelectMode` is `true`, items will follow checkbox-like selection behavior when clicked. Specifically, a
   * clicked item will become selected and remain selected even after other items are subsequently clicked.  The selected
   * item can be un-selected by clicking on it again.  This is essentially the same behavior that happens with Ctrl+Clicks
   * when `isInSelectMode` is `false`. The difference is that when `isInSelectMode` is `true`, the user doesn't need to
   * press the Ctrl key; simple clicking will suffice.
   *
   * The purpose of this feature is to support list UIs which expose an "Edit" or "Select" mode to the user. It enables
   * the user to select multiple list items with simple clicking and then typically choose an action to apply on
   * all the selected items.
   *
   * @type {boolean}
   * @public
   */
  isInSelectMode: false,

  /**
   * Array of item ids which correspond to items that are currently selected. A "selected" item is simply one
   * that is to be rendered with some sort of visual highlighting.
   *
   * The values of the `selections` array are each an item id. The item id is read from the attribute specified by
   * the configurable `itemIdField` property. If the `selections` array contain a given item's id, then that item is considered
   * selected; otherwise, it is considered not selected.
   *
   * Note that the items in `selections` are ids, not the actual item objects themselves.  This enables a caller to specify
   * a selection by id, even if the caller does not have a reference to the actual selected object.  This is useful in several
   * use cases; for example, if the selection id is a query param in the URL.
   *
   * @type {[]}
   * @public
   */
  selections: null,

  /**
   * Is `true` only if the `selections` hash contains at least one key. Used for CSS class name bindings.
   *
   * @type {boolean}
   * @private
   */
  @notEmpty('selections')
  hasSelections: null,

  /**
   * Hashtable of the values in `selections`.  Each hash key is a value from the `selections` array.  The hash values
   * are all set to `true`.  Values which are not in `selections` are not included in the hash keys.
   *
   * Parsing `selections` into a hash is useful for performance, because we can quickly check if a set of items
   * are selected by directly looking up their ids in the hash rather than searching for them by looping repeatedly hru an array.
   *
   * @type {object}
   * @private
   */
  @computed('selections.[]')
  selectionsHash(selections) {
    return arrayToHashKeys(selections);
  },

  /**
   * An array of objects, each of which wraps an item from `items`. The original item is stored in each object's
   * `raw` property.
   *
   * Note: additional properties get written to these wrapped objects elsewhere (@see `statefulWrappedItems`).
   * We don't include that logic here, because we don't want to re-wrap `items` unnecessarily every time.
   *
   * @type {{ raw: object }}
   * @public
   */
  @computed('items.[]')
  wrappedItems(items) {
    items = items || [];
    return items.map((item) => ({ data: item }));
  },

  /**
   * The same `wrappedItems` array, but each member is equipped with an `isSelected` attribute that specifies whether
   * or not the item is currently in `selections`.
   *
   * @type {object[]}
   * @private
   */
  @computed('wrappedItems.[]', 'itemIdField', 'selectionsHash')
  statefulWrappedItems(items, itemIdField, selectionsHash = {}) {
    items.forEach((item) => {
      const isSelected = get(item.data, itemIdField) in selectionsHash;
      set(item, 'isSelected', isSelected);
    });
    return items;
  }
});
