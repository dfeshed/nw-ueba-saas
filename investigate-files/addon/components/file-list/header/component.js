import RSADataTableHeader from 'component-lib/components/rsa-data-table/header/component';
import { connect } from 'ember-redux';
import { updateColumnVisibility } from 'investigate-files/actions/data-creators';

const dispatchToActions = {
  updateColumnVisibility
};
const tableHeader = RSADataTableHeader.extend({
  actions: {
    toggleColumn(column) {
      column.toggleProperty('selected');
      const { field, selected } = column.getProperties('field', 'selected');
      this.send('updateColumnVisibility', { field, selected });
    }
  }
});
export default connect(undefined, dispatchToActions)(tableHeader);
