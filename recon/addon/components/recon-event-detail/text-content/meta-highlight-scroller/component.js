import Component from 'ember-component';
import { schedule, debounce, later } from 'ember-runloop';
import computed, { gt, and } from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { totalMetaToHighlight } from 'recon/reducers/text/selectors';

const stateToComputed = ({ recon }) => ({
  numberOfHighlightedMetas: totalMetaToHighlight(recon)
});

const MetaHighlightScrollerComponent = Component.extend({
  classNames: ['meta-highlight-scroller'],
  layout,

  $highlightedMetas: [],
  index: 0,
  $parent: null,
  $scrollBox: null,

  @gt('index', 0)
  showPrevious: false,

  @computed('index', 'numberOfHighlightedMetas')
  showNext: (index, numMetas) => index < numMetas - 1,

  @and('showPrevious', 'showNext')
  showSeperator: false,

  @gt('numberOfHighlightedMetas', 0)
  hasMatches: false,

  // index is 0 based, so need to add 1, but just show 0 if no matches
  @computed('hasMatches', 'index')
  shownIndex: (hasMatches, index) => hasMatches ? index + 1 : 0,

  didUpdateAttrs() {
    // New meta has been selected for highlighting.
    // Wait until after rendering of new highlight meta
    // wrappers are complete, then reset scrolling.
    schedule('afterRender', this, this._resetToFirstHighlightedMeta);
  },

  didInsertElement() {
    this._super(...arguments);

    // First time rendered, save off parent/scroll-box
    // so we don't have to get them again
    const $parent = this.$().parent();
    const $scrollBox = $parent.find('.scroll-box');
    this.setProperties({ $parent, $scrollBox });
    this._resetToFirstHighlightedMeta();
  },

  // Meta can be revealed after initial rendering of the text entries.
  // They are revealed incrementally for performance reasons.
  // We know how many metas there should be, so keep attempting to find them
  // all until all of them are present in the DOM.
  _checkHighlightedMeta() {
    const { $highlightedMetas, $parent } = this.getProperties('$highlightedMetas', '$parent');
    if (this.get('numberOfHighlightedMetas') !== $highlightedMetas.length) {
      this.set('$highlightedMetas', $parent.find('.highlighted-meta'));
      later(this, this._checkHighlightedMeta, 500);
    }
  },

  // When first rendered or when meta changes, need to
  // get list of highlighted metas, save off, then scroll
  // to first meta
  _resetToFirstHighlightedMeta() {
    if (this.get('hasMatches')) {
      const $parent = this.get('$parent');
      const $highlightedMetas = $parent.find('.highlighted-meta');
      this.setProperties({ $highlightedMetas, index: 0 });
      this._checkHighlightedMeta();
      this._scrollToMeta();
    }
  },

  _scrollToMeta() {
    const { index, $highlightedMetas, $parent, $scrollBox } =
      this.getProperties('index', '$highlightedMetas', '$parent', '$scrollBox');

    const $meta = $highlightedMetas.eq(index);

    // calculate what the scroll top needs to be
    // Need the offset of the parent...
    const parentWindowOffset = $parent.offset().top;
    // ...and the offset of the item to be highlighted...
    const metaOffset = $meta.offset().top;
    // ...and the current locaton of where the scroll container is scrolled to...
    const currentScrollTop = $scrollBox.scrollTop();
    // And calculation is
    // WhereAreWeScrolledToNow
    // + LocationOfMeta (can be negative if first item is above current scroll position)
    // - WhereIsParentWindow
    // - BufferForReadability
    const scrollTop = currentScrollTop + metaOffset - parentWindowOffset - 60;

    $parent.find('.scroll-box').animate({ scrollTop }, 1000);
  },

  _moveIndex(mover) {
    this[mover]('index');
    // users can get spammy, debouce this to
    // fast scroll the list if they click a bunch
    // otherwise 20 clicks = 20 seconds as it animates each one
    debounce(this, this._scrollToMeta, 250);
  },

  actions: {
    next() {
      this._moveIndex('incrementProperty');
    },

    prev() {
      this._moveIndex('decrementProperty');
    }
  }
});

export default connect(stateToComputed)(MetaHighlightScrollerComponent);
