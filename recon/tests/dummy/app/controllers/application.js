import Controller from '@ember/controller';
import { debug } from '@ember/debug';

export default Controller.extend({
  eventId: '12345678',
  index: 5,
  total: 107,
  linkToFileAction(file) {
    // Dummy handler, for troubleshooting
    debug(`linkToFileAction invoked with file: ${file}`);
  }
});
