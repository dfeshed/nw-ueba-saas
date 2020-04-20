import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';

@classic
@tagName('box')
@classNames('table-footer')
export default class TableFooter extends Component {
 /**
  * Index of event.
  * @type Number
  * @default 0
  * @public
  */
 index = 0;

 /**
  * Total number of all events.
  * @type Number
  * @default 0
  * @public
  */
 total = 0;

 /**
  * tab name of the table
  * @type String
  * @default ''
  * @public
  */
 label = '';
}
