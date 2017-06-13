import Component from 'ember-component';
import { scheduleOnce, debounce } from 'ember-runloop';
import computed, { gt } from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import layout from './template';
import { totalMetaToHighlight } from 'recon/reducers/text/selectors';

const stateToComputed = ({ recon }) => ({
  numberOfHighlightedMetas: totalMetaToHighlight(recon)
});

const MetaHighlightStatus = Component.extend({
  layout,

  tagName: 'div',

  classNames: ['meta-highlight-scroller'],

  index: 0,

  @gt('index', 0)
  showPrevious: false,

  @computed('index', 'numberOfHighlightedMetas')
  showNext: (index, numMetas) => index < numMetas - 1,

  @gt('numberOfHighlightedMetas', 0)
  hasMatches: false,

  // index is 0 based, so need to add 1, but just show 0 if no matches
  @computed('hasMatches', 'index')
  shownIndex: (hasMatches, index) => hasMatches ? index + 1 : 0,

  // when we first render, need to highlight meta,
  // but if the meta to highlight then changes we need to
  // redo all the checks
  didReceiveAttrs() {
    this._super(...arguments);
    scheduleOnce('afterRender', this, this._resetToFirstHighlightedMeta);
  },

  // When first rendered or when meta changes, need to
  // reset the index, then scroll to first meta
  _resetToFirstHighlightedMeta() {
    if (this.get('hasMatches')) {
      this.set('index', 0);
      this._scrollToMeta();
    }
  },

  _scrollToMeta() {
    const index = this.get('index');
    const $parent = this.$().parents().eq(3);
    const $scrollBox = $parent.find('.scroll-box');
    const $meta = $parent.find('.highlighted-meta').eq(index);

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

export default connect(stateToComputed)(MetaHighlightStatus);