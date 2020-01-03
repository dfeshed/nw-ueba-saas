import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';

@classic
@tagName('section')
@classNames('file-pager')
export default class HostTableFooter extends Component {
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
