import Component from '@ember/component';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import layout from './template';


const CATEGORIES = [
  {
    label: 'investigateShared.endpoint.fileActions.networkEvents',
    category: 'Network Event'
  },
  {
    label: 'investigateShared.endpoint.fileActions.fileEvents',
    category: 'File Event'
  },
  {
    label: 'investigateShared.endpoint.fileActions.processEvents',
    category: 'Process Event'
  },
  {
    label: 'investigateShared.endpoint.fileActions.registryEvents',
    category: 'Registry Event'
  }
];

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 1}px`);
  } else {
    return null;
  }
};

export default Component.extend({

  layout,

  classNames: ['pivot-to-event-analysis'],

  classNameBindings: ['isExpanded:expanded:collapsed'],

  isExpanded: false,

  offsetsStyle: null,

  categories: CATEGORIES,

  @computed('selections')
  isDisabled(selections = []) {
    return selections.length !== 1;
  },


  actions: {
    processEventAnalysis() {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
      this.pivotToInvestigate(this.get('selections')[0]);
    },

    processMenuItem(category) {
      this.toggleProperty('isExpanded');
      this.pivotToInvestigate(this.get('selections')[0], category);
    },

    clickOutside() {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
    },

    toggleExpand() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.toggleProperty('isExpanded');
    }
  }

});
