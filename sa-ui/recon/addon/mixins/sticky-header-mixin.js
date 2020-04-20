import Mixin from '@ember/object/mixin';
import { debounce, join } from '@ember/runloop';
import computed from 'ember-computed-decorators';

export default Mixin.create({
  // Internal mixin state, height of sticky, used for sliding
  // sticky out when new sticky hits the bottom of the stuck iteam
  heightOfCurrentSticky: 0,

  // Interal mixin state, which item in the list of stickables
  // is currently stuck
  indexAtTop: null,

  // Tracks whether the area being scrolled exists.
  // When it does exist, it needs to have scroll handlers attached
  hasScrollArea: false,

  // REQUIRED The key of the data, when paired with the index,
  // the result is the data used in the sticky component
  stickyContentKey: null,

  // OPTIONAL If stickySelector isn't the actual header
  // this selector can be used to find the header inside
  // items found using stickySelector
  stickyHeaderSelector: null,

  // REQUIRED The selector for all of the items to be stuck in a list
  // of items. This could be the headers themselves if that makes sense,
  // or it could be the parent of the header. If it is the parent of the header
  // then stickyHeaderSelector must be provided.
  stickySelector: null,

  @computed('indexAtTop')
  stickyContent(index) {
    // no scrolling has occurred so no content to stick
    if (!index || index === 0) {
      return;
    }

    const content = this.get(this.get('stickyContentKey'));

    // no content either, so no header to render
    if (!content || content.length === 0) {
      return;
    }

    return content[index - 1];
  },

  // didRender is called:
  // 1. each time another page of packets is returned from an API call and
  // rendered to the page (keep in mind they stream in from the API in batches).
  // 2. when sticky is stuck
  // 3. when data is toggled on/off in the view
  didRender() {

    // if this component isn't stuck itself...
    if (!this.get('isSticky')) {
      this._super(...arguments);

      const $scrollBox = this.$('.scroll-box');

      // If scroll box exists...
      if ($scrollBox.length > 0) {
        // ...and it didn't previously exist...
        if (this.get('hasScrollArea') === false) {
          // ...then attach scroll listener
          const scrollFunct = this._scrolled.bind(this);
          this.setProperties({ scrollFunct, 'hasScrollArea': true });
          $scrollBox.scroll(scrollFunct);
        }
      } else {
        // if it doesn't exist then track that,
        // so when it appears we can attach scroll handlers
        this.set('hasScrollArea', false);
      }

      // Need to reset the $headers, as each time didRender is called
      // there may be more/less of stickyable items
      const stickySelector = this.get('stickySelector');
      this.set('$headers', this.$(stickySelector));

      // didRender called when sticky is stuck, need to calculate
      // height of stuck sticky so smooth sliding out is possible
      const selector = this.get('stickyHeaderSelector') || '.is-sticky';
      const $stickyHeader = this.$(selector);
      if ($stickyHeader && $stickyHeader.length > 0) {
        this.set('heightOfCurrentSticky', $stickyHeader.outerHeight());
      }

      // Other factors can result in packets being removed from the page
      // like requests/responses being toggled.
      // If headers are eliminated, need to recalc the scroll sticky,
      // debounce as scroll can be finicky.
      debounce(() => {
        this._scrolled();
      }, 100);
    }
  },

  // Destroy the scroll handler
  willDestroyElement() {
    const scrollFunct = this.get('scrollFunct');
    if (scrollFunct) {
      const $scrollBox = this.$('.scroll-box');
      $scrollBox.off('scroll', scrollFunct);
    }
  },

  // Gotta run.join this to ensure it gets notified immediately,
  // header sticking needs to be faaaast
  _setIndex(index) {
    join(() => {
      if (!this.isDestroyed) {
        this.set('indexAtTop', index);
        if (index === null) {
          this.set('heightOfCurrentSticky', 0);
        }
      }
    });
  },

  _scrolled() {
    const $headers = this.get('$headers');

    if (!$headers || $headers.length === 0) {
      this._setIndex(null);
    } else {

      // fast eject, common use case, is just at the top
      // nothing to do here
      const positionTop = $headers.eq(0).position().top;
      if (positionTop === 0 || positionTop === 1) {
        this._setIndex(null);
        return;
      }

      const indexAtTop = this.get('indexAtTop');
      const heightOfCurrentSticky = this.get('heightOfCurrentSticky');

      // Process each of the headers from top of the page on down
      // to see if any of them need to be the stuck/top header.
      // A header gets stuck if it is in view and the header
      // before it has scrolled out of view
      let previousHeaderPosition = -1;
      $headers.each((i) => {
        const $currentHeader = $headers.eq(i);
        if (i > 0) {
          previousHeaderPosition = $headers.eq(i - 1).position().top;
        }
        const currentHeaderPosition = $currentHeader.position().top;

        // if the previous header is above the top of the view
        // and the current header is below the top of the view
        // then current header needs sticking!
        if (previousHeaderPosition - heightOfCurrentSticky < 0 && currentHeaderPosition - heightOfCurrentSticky > 0) {
          // only set if changing, ember checks if things change,
          // but why bother letting ember bother if we know up front
          if (indexAtTop !== i) {
            this._setIndex(i);
          }

          // the previous header is out of view and the current header is
          // in view, which means no more headers will be stickyable as
          // they will all have a header above them that is in view, so
          // now we can exit out of each loop, nothing else to do
          return false;
        } else {
          // Need to handle the case where the last item needs to be stuck
          if (indexAtTop === i && ($headers.length - 1) === i) {
            this._setIndex(i + 1);
          }
        }
      });
    }
  }
});
