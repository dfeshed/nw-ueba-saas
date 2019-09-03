import layout from './template';
import computed from 'ember-computed-decorators';
import DataTableBody from '../component';
import { enableOnAgentConfig, dataCollectionConfig, encodingOptions } from './settings';

export default DataTableBody.extend({
  layout,
  classNames: 'child-source-container',
  encodingOptions,
  enableOnAgentConfig: enableOnAgentConfig(),
  dataCollectionConfig: dataCollectionConfig(),

  @computed('item.exclusionFilters')
  exclusionFilters(filter) {
    // Since filter is stored as an array in state, convert to string and display it in the textarea.
    if (filter) {
      return filter.join('\n');
    }
  },

  @computed('exFilterInvalidIndex')
  exFiltersErrLineIndex(invalidIndex) {
    // if there is an invalid exclusion filter, add +1 to index to get the line number
    if (invalidIndex !== -1) {
      return ++invalidIndex;
    }
  },

  actions: {
    setSelected(column, value) {
      this.set(`item.${column}`, value);
      this.get('sourceUpdated')();
    },
    setexclusionFilters(column, option) {
      // capture the value from the textarea
      const { value } = option.target;
      // convert the entered string into an array delimited by a new line and store in state
      let arr = [];
      if (value.trim()) {
        // filter to remove empty/null/whitespace entries from array
        arr = value.trim().split('\n').filter((e) => Boolean(e.trim()));
      }
      this.set(`item.${column}`, arr);
      this.get('sourceUpdated')();
    },
    focusOut(index, value) {
      this.set(`item.paths.${index}`, value);
      this.get('sourceUpdated')();
    },
    deletePath(index) {
      // delete the path at the index
      const paths = this.get('item.paths').filter((e, i) => i !== index);
      this.set('item.paths', paths);
      this.get('sourceUpdated')();
    },
    addPath() {
      this.get('item.paths').push('');
      this.get('sourceUpdated')();
    }
  }
});
