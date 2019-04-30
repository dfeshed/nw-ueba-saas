import EmberObject from '@ember/object';

const { log } = console; // eslint-disable-line no-unused-vars

/**
 * Accepts an Object that defines `list` as an Array of Objects with the
 * following properties: `label`, `disabled`, and `highlighted`.
 * @example
 * ```
 * const BL = BoundedList.create({
 *   list: [
 *     { label: 'Option 1', disabled: false, highlighted: false }
 *   ]
 * });
 * ```
 */
export default class BoundedList extends EmberObject {

  constructor(...args) {
    super(...args);
  }

  init() {
    this.list = this.list || [];
    this.limit = this.list.length - 1;

    // Set list up with current highlight index.
    // Allows list to be seeded with highlighted item
    // Note: if multiple items come in as highlighted
    // only the first will be marked as such
    let highlightedIndex = -1;
    for (let i = 0; i <= this.limit; i++) {
      if (this.list[i].highlighted === true) {
        highlightedIndex = i;
        break;
      }
    }
    this.highlightedIndex = highlightedIndex;
  }

  /**
   * Next possible index, limited to the length of the list.
   * @type number
   * @public
   */
  get nextHighlightIndex() {
    for (let i = this.highlightedIndex + 1; i <= this.limit; i++) {
      // If an item we encounter is not disabled,
      // we have one we can highlight
      if (!this.list[i].disabled) {
        return i;
      }
    }

    // Note: if NO next item is highlightable, then the
    // highlight index remains unchanged and the currently
    // highlighted item stays the same
    return this.highlightedIndex;
  }

  /**
   * Previous possible index. A return of -1 inidcates that the `list` is empty.
   * @type number
   * @public
   */
  get previousHighlightIndex() {
    for (let i = this.highlightedIndex - 1; i > -1; i--) {
      // If an item we encounter is not disabled,
      // we have one we can highlight
      if (!this.list[i].disabled) {
        return i;
      }
    }

    // Note: if NO previous item is highlightable,
    // then return -1 to indicate nothing should
    // be highlighted
    return -1;
  }

  /**
   * The currently highlighted item
   * @type {object}
   * @public
   */
  get highlightedItem() {
    return this.list[this.highlightedIndex];
  }

  /**
   * The index that is currently highlighted.
   * @type {number}
   * @public
   */
  get highlightIndex() {
    return this.highlightedIndex;
  }
  set highlightIndex(index) {
    this._highlightIndex(index);
  }

  /**
   * Returns the next index as long as that number does not go beyond the length
   * of the list.
   * @public
   */
  highlightNextIndex() {
    const index = this.nextHighlightIndex;
    if (index >= 0) {
      this._highlightIndex(index);
    }
    return this;
  }

  /**
   * Returns the previous index as long as that number does not go beyond the
   * beginning of the list.
   * @public
   */
  highlightPreviousIndex() {
    const index = this.previousHighlightIndex;
    if (index >= 0) {
      this._highlightIndex(index);
    }
    return this;
  }

  /**
   * Removes all highlighting.
   * @public
   */
  clearHighlight() {
    if (this.highlightedItem) {
      const newList = this.list.map((d) => {
        return { ...d, highlighted: false };
      });
      this.highlightedIndex = -1;
      this.set('list', newList);
    }
    return this;
  }

  /**
   * Replaces an item with a different item
   * based on label as locator
   * @public
   */
  replaceItemByLabel(label, item) {
    const newList = this.list.map((listItem) => {
      if (listItem.label === label) {
        listItem = { ...item };
      }
      return listItem;
    });
    this.set('list', newList);
    return this;
  }

  /**
   * Highlights a specific index, setting all others to not be highlighted.
   * @param {Number} index The index to highlight
   * @return {Array}
   * @private
   */
  _highlightIndex(index) {
    const newList = this.list.map((d, i) => ({
      ...d,
      highlighted: i === index
    }));
    this.highlightedIndex = index;
    this.set('list', newList);
  }
}