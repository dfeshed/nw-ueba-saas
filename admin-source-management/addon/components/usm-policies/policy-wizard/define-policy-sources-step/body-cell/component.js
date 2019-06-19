import layout from './template';
import computed from 'ember-computed-decorators';
import DataTableBody from '../component';
import { enableOnAgentConfig, dataCollectionConfig, encodingOptions } from './settings';
import { removeQuotes, arrToString } from 'admin-source-management/utils/string-util';

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
      return arrToString(filter);
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
      // abc\ndef becomes ["abc", "def"]
      // 'abc'\n'def' -> ["abc", "def"]
      // "abc"\n"def" -> ["abc", "def"]
      // "abc"\n'def' -> ["abc", "def"]
      // "abc*"\n"def-*" -> ["abc*", "def-*"]
      let arr = [];
      if (value.trim()) {
        arr = value.trim().split('\n').map((entry) => removeQuotes(entry));
      }
      this.set(`item.${column}`, arr);
      this.get('sourceUpdated')();
    }
  }
});
