import classic from 'ember-classic-decorator';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';

@classic
export default class _BodyCell extends BodyCell {
  @service
  dateFormat;

  @service
  timeFormat;

  @service
  timezone;

  @computed('item')
  get isIsolated() {
    return this.item?.agentStatus?.isolationStatus?.isolated;
  }
}
