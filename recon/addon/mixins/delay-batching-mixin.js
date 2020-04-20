import Mixin from '@ember/object/mixin';
import { debounce, throttle } from '@ember/runloop';

import { pauseBatching, resumeBatching } from 'recon/actions/util/batch-data-handler';

const throttlePause = () => throttle(pauseBatching, 150);
const debouncedResume = () => debounce(resumeBatching, 150);

export default Mixin.create({

  didInsertElement() {
    const $scrollBox = this.$('.scroll-box');
    $scrollBox.scroll(throttlePause);
    $scrollBox.scroll(debouncedResume);
  },

  willDestroyElement() {
    const $scrollBox = this.$('.scroll-box');
    $scrollBox.off('scroll', throttlePause);
    $scrollBox.off('scroll', debouncedResume);
  }
});
