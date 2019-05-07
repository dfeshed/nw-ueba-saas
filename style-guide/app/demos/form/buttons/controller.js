import Controller from '@ember/controller';
import { htmlSafe } from '@ember/string';

export default Controller.extend({

  isExpanded: false,

  offsetsStyle: null,

  actions: {
    toggleExpand() {
      const element = document.querySelector('.button-menu-demo');

      if (element) {
        const elRect = element.getBoundingClientRect();
        this.set('offsetsStyle', htmlSafe(`top: ${elRect.height - 2}px`));
      }
      this.toggleProperty('isExpanded');
    }
  }
});
