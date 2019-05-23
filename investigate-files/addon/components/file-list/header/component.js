import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { next } from '@ember/runloop';

export default RSADataTableHeader.extend({
  actions: {
    toggleColumn(column) {
      const field = column.get('field');
      // File name and Scores are required columns, don't allow to deselect
      if (field === 'firstFileName' || field === 'score') {
        return;
      }
      column.toggleProperty('selected');
      const columns = this.get('table.visibleColumns');
      // Checkbox are rendering twice, to avoid calling it in next run-loop
      next(() => {
        this.onToggleColumn(columns);
      });
    }
  }
});
