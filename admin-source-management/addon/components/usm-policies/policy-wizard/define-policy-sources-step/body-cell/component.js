import layout from './template';
import DataTableBody from '../component';
import { enableOnAgentConfig, dataCollectionConfig, encodingOptions } from './settings';

export default DataTableBody.extend({
  layout,
  classNames: 'child-source-container',
  encodingOptions,
  enableOnAgentConfig: enableOnAgentConfig(),
  dataCollectionConfig: dataCollectionConfig(),

  actions: {
    setSelected(column, value) {
      this.set(`item.${column}`, value);
      this.get('sourceUpdated')();
    }
  }
});
