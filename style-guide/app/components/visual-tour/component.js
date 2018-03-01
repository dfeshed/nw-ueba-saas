import { computed } from '@ember/object';
import Component from 'ember-component';
import run from 'ember-runloop';
import config from 'ember-get-config';

export default Component.extend({

  activeResolution: null,

  classNameBindings: ['showDiffs'],

  classNames: ['images'],

  playState: 'pause',

  resolutions: [{
    width: 1920,
    height: 1080
  }, {
    width: 1680,
    height: 1050
  }, {
    width: 1440,
    height: 900
  }, {
    width: 1024,
    height: 768
  }],

  runs: null,

  showDiffs: false,

  showNetWitness: true,

  activeRun: computed('runs.@each.isActive', function() {
    if (this.get('runs')) {
      return this.get('runs').findBy('isActive', true);
    }
  }),

  allowPrevImage: computed('activeRun', 'runs.length', function() {
    const activeRun = this.get('activeRun');
    return this.get('runs').indexOf(activeRun) !== 0;
  }),

  allowPrevRun: computed('activeRun', 'runs.length', function() {
    const activeRun = this.get('activeRun');
    return this.get('runs').indexOf(activeRun) !== 0;
  }),

  allowNextImage: computed('activeRun', 'runs.length', function() {
    return this.get('runs').indexOf(this.get('activeRun')) !== (this.get('runs.length') - 1);
  }),

  allowNextRun: computed('activeRun', 'runs.length', function() {
    return this.get('runs').indexOf(this.get('activeRun')) !== (this.get('runs.length') - 1);
  }),

  imageRoot: computed('showDiffs', function() {
    const root = `${config.visualTourRootUrl}/images`;
    const folder = this.get('showDiffs') ? 'diffs' : 'new';

    return `${root}/${folder}`;
  }),

  filteredImages: computed('activeRun.timestamp', 'runs.length', 'playState', 'showNetWitness', 'activeRun.images.length', 'activeResolution.height', 'activeResolution.width', function() {
    if (!this.get('activeRun')) {
      return;
    }

    const images = this.get('activeRun.images').filter((image) => {
      const resolutionMatch = image.filename.indexOf(`${this.get('activeResolution.width')}x${this.get('activeResolution.height')}`) !== -1;
      const isStyleGuide = image.filename.indexOf('style-guide') !== -1;
      const contextMatch = this.get('showNetWitness') ? !isStyleGuide : isStyleGuide;
      return resolutionMatch && contextMatch;
    });

    run.schedule('afterRender', () => {
      this.get('tourGallery').forceUpdate();
    });

    return images;
  }),

  didInsertElement() {
    this.set('activeResolution', this.get('resolutions.firstObject'));
    this.set('activeRun', this.get('runs.lastObject'));
  },

  actions: {
    setResolution(resolution) {
      this.set('activeResolution', resolution);
    },

    updatePlayState() {
      if (this.get('playState') === 'play') {
        this.get('tourGallery.swiper').stopAutoplay();
        this.set('playState', 'pause');
      } else {
        this.get('tourGallery.swiper').startAutoplay();
        this.set('playState', 'play');
      }
    },

    updateRun(direction) {
      const activeIndex = this.get('runs').indexOf(this.get('activeRun'));
      const indexMod = direction === 'next' ? 1 : -1;
      const updatedIndex = activeIndex + indexMod;

      if ((updatedIndex < 0) || updatedIndex >= this.get('runs.length')) {
        return;
      }

      this.set('activeRun', this.get('runs').objectAt(updatedIndex));

      this.get('tourGallery').forceUpdate();
    },

    toggleNetWitness() {
      this.set('activeResolution', this.get('resolutions.firstObject'));
      this.toggleProperty('showNetWitness');
    }

  }

});
