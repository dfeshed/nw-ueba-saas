import classic from 'ember-classic-decorator';
import { layout as templateLayout } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import layout from './template';

@classic
@templateLayout(layout)
export default class _BodyCell extends BodyCell {
  @service
  dateFormat;

  @service
  timeFormat;

  @service
  timezone;
}